package oose.euphoria.backend.service;

import oose.euphoria.backend.data.CompanyManager;
import oose.euphoria.backend.data.IUserManager;
import oose.euphoria.backend.data.entities.CompanySupport;
import oose.euphoria.backend.data.entities.User;
import oose.euphoria.backend.exceptions.LoginRejectedException;
import oose.euphoria.backend.exceptions.UserNotFoundException;
import oose.euphoria.backend.presentation.coders.JWTTokenDecoder;
import oose.euphoria.backend.presentation.dto.Challenge;
import oose.euphoria.backend.presentation.dto.JWTToken;
import oose.euphoria.backend.presentation.dto.messages.responses.RegisterResponse;
import oose.euphoria.backend.presentation.dto.messages.staticmessage.GeneratorMessage;
import oose.euphoria.backend.utilities.AES;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class UserServiceTest {
    private static final String TOKEN_MOCK = "TEST_TOKEN";
    private static final User MOCK_USER =
            new User("TEST_USER_ID", "TEST_USER_NAME", "TEST_USER_KEY", "TEST_USER_COMPANY_ID", false);
    private static final User MOCK_SUPPORT =
            new User("TEST_SUPPORT_ID", null, "TEST_SUPPORT_KEY", "SUPPORT", true);
    private static final Challenge CHALLENGE_MOCK =
            new Challenge("TEST_CHALLENGE_ENCRYPTED", "TEST_CHALLENGE_SOLVED", "12345", "54321");
    private static final String ENCRYPTED_SOLUTION_MOCK = "1";
    private static final String PRIVATE_KEY_MOCK = "123";
    private static final String PUBLIC_KEY_MOCK = "321";
    private static final String SHARED_KEY_MOCK = "2";
    private static final String CHALLENGE_SOLUTION_MOCK = "3";
    private static final String SENDER_ID_MOCK = "11";
    private static final String COMPANY_ID_MOCK = "22";
    private static final String USER_ID_1 = "1000";
    private static final String USER_ID_2 = "1001";
    private static MockedStatic<AES> AESMock;
    private static MockedStatic<JWTTokenDecoder> JWTTokenDecoderMock;

    @Spy
    @InjectMocks
    UserService sut;
    @Mock
    IUserManager userManagerMock;
    @Mock
    GeneratorMessage generator;
    @Mock
    CompanyManager companyManagerMock;

    @BeforeEach
    void setup() {
        // This line is used to add a mock of @autowired classes into the sut.
        MockitoAnnotations.openMocks(this);
        // Makes static functions in the AES mockable. Closes after each test.
        AESMock = mockStatic(AES.class);
        JWTTokenDecoderMock = mockStatic(JWTTokenDecoder.class);
    }

    @AfterEach
    void close() {
        AESMock.close();
        JWTTokenDecoderMock.close();
    }

    @Test
    void registerUserTest() {
        // Arrange
        JWTToken jwtToken = new JWTToken();
        JWTToken.JDIDetails jdiDetails = new JWTToken.JDIDetails();
        List<String> allSupportIDsMock = new ArrayList<>(Arrays.asList(""));
        jdiDetails.setSupportIDs(allSupportIDsMock);
        jdiDetails.setUserId("TEST_USER_ID");
        jdiDetails.setAccountName("TEST_ACCOUNT_NAME");
        jwtToken.setDetails(jdiDetails);

        when(userManagerMock.createUser(anyString(), anyString(), anyString(),
                anyString(), anyBoolean())).thenReturn(new User());

        JWTTokenDecoderMock.when(() -> JWTTokenDecoder.decodeJWTToken(anyString())).thenReturn(jwtToken);
        doReturn(MOCK_USER).when(userManagerMock)
                .createUser(anyString(), anyString(), anyString(), anyString(), anyBoolean());
        doNothing().when(sut).checkCompany(anyString());

        // Act
        RegisterResponse result =
                sut.registerUser(TOKEN_MOCK, MOCK_USER.getPublicKey(), MOCK_USER.getCompanyID(), MOCK_USER.isSupport());

        // Assert
        verify(userManagerMock, times(1)).createUser(anyString(), anyString(), anyString(),
                anyString(), anyBoolean());
        assertNotNull(result);
        assertEquals(MOCK_USER.getId(), result.getUser().getId());
        assertEquals(MOCK_USER.getCompanyID(), result.getUser().getCompanyID());
        assertEquals(MOCK_USER.getPublicKey(), result.getUser().getPublicKey());
        assertEquals(MOCK_USER.getUsername(), result.getUser().getUsername());
    }

    @Test
    void registerUserAsSupportTest() {
        // Arrange
        JWTToken jwtToken = new JWTToken();
        JWTToken.JDIDetails jdiDetails = new JWTToken.JDIDetails();
        jdiDetails.setUserId("TEST_USER_ID");
        jdiDetails.setAccountName("TEST_ACCOUNT_NAME");
        jdiDetails.setSupportIDs(new ArrayList<>(Arrays.asList("TEST_SUPPORT_ID")));
        jwtToken.setDetails(jdiDetails);

        when(userManagerMock.createUser(anyString(), anyString(), anyString(),
                any(), anyBoolean())).thenReturn(new User());
        JWTTokenDecoderMock.when(() -> JWTTokenDecoder.decodeJWTToken(anyString())).thenReturn(jwtToken);
        doReturn(MOCK_SUPPORT).when(userManagerMock)
                .createUser(anyString(), anyString(), anyString(), any(), anyBoolean());
        doNothing().when(sut).checkCompany(anyString());

        // Act
        RegisterResponse result =
                sut.registerUser(TOKEN_MOCK, MOCK_SUPPORT.getPublicKey(), MOCK_SUPPORT.getCompanyID(), true);

        // Assert
        verify(userManagerMock, times(1)).createUser(anyString(), anyString(), anyString(),
                any(), anyBoolean());
        assertNotNull(result);
        assertEquals(MOCK_SUPPORT.getId(), result.getUser().getId());
        assertEquals(MOCK_SUPPORT.getPublicKey(), result.getUser().getPublicKey());
        assertEquals(MOCK_SUPPORT.getUsername(), result.getUser().getUsername());
        assertEquals(true, result.getUser().isSupport());
    }

    @Test
    void checkCompanyTest() {
        // Arrange
        doReturn(true).when(companyManagerMock).companyExists(anyString());

        // Act
        sut.checkCompany("TEST_COMPANY_ID");

        // Assert
        verify(companyManagerMock, times(1)).companyExists(anyString());
    }

    @Test
    void checkCompanyFalseTest() {
        // Arrange
        doReturn(false).when(companyManagerMock).companyExists(anyString());
        doNothing().when(companyManagerMock).createCompany(anyString());

        // Act
        sut.checkCompany("TEST_COMPANY_ID");

        // Assert
        verify(companyManagerMock, times(1)).companyExists(anyString());
    }

    @Test
    void getUserPublicKeyTest() {
        // Arrange
        when(userManagerMock.getUser(anyString())).thenReturn(MOCK_USER);

        // Act
        String result = sut.getUserPublicKey(MOCK_USER.getId());

        // Assert
        verify(userManagerMock, times(1)).getUser(anyString());
        assertEquals(MOCK_USER.getPublicKey(), result);
    }

    @Test
    void getUserPublicKeyThrowUserNotFoundExceptionTest() {
        //Arrange
        doReturn(new User()).when(userManagerMock).getUser(anyString());

        //Assert & Act
        assertThrows(UserNotFoundException.class,
                () -> sut.getUserPublicKey(MOCK_USER.getPublicKey()));
    }

    @Test
    void generateChallengeTest() {
        // Arrange
        String senderIDMock = "1";
        User userMock = new User();
        userMock.setPublicKey(PUBLIC_KEY_MOCK);
        String privateKeyMock = PRIVATE_KEY_MOCK;
        BigInteger bigIntegerMock = new BigInteger(userMock.getPublicKey());

        String expected = bigIntegerMock.toString();
        String expectedAES = "AES";

        BDDMockito.given(AES.encrypt(anyString(), anyString())).willReturn(expectedAES);

        when(userManagerMock.getUser(anyString())).thenReturn(userMock);
        when(sut.generatePrivateKey()).thenReturn(privateKeyMock);
        when(sut.hexStringToBigInteger(privateKeyMock)).thenReturn(bigIntegerMock);
        doReturn(bigIntegerMock).when(sut).generatePublicKey(bigIntegerMock);
        doReturn(bigIntegerMock).when(sut).generateSharedKey(bigIntegerMock, bigIntegerMock);

        // Act
        Challenge actual = sut.generateChallenge(senderIDMock);

        // Assert
        assertEquals(expected, actual.getChallengeSolved());
        assertEquals(expected, actual.getPublicKey());
        assertEquals(expectedAES, actual.getChallengeEncrypted());
    }

    @Test
    void hexStringToBigIntegerTest() {
        // Arrange
        String hex1 = "10";
        String integer1 = "16";
        String hex2 = "FF";
        String integer2 = "255";

        // Act
        BigInteger actual1 = sut.hexStringToBigInteger(hex1);
        BigInteger actual2 = sut.hexStringToBigInteger(hex2);

        // Assert
        assertEquals(actual1.toString(), integer1);
        assertEquals(actual2.toString(), integer2);
    }

    @Test
    void generateSharedKeyTest() {
        // Arrange
        BigInteger userPublicKeyMock = new BigInteger(PUBLIC_KEY_MOCK);
        BigInteger serverPrivateKeyMock = new BigInteger(PRIVATE_KEY_MOCK);

        when(generator.getN()).thenReturn("2");

        // Act
        BigInteger actual = sut.generateSharedKey(userPublicKeyMock, serverPrivateKeyMock);

        // Assert
        assertEquals("1", actual.toString());
    }

    @Test
    void generatorPublicKeyTest() {
        // Arrange
        BigInteger privateKeyIntMock = new BigInteger(PRIVATE_KEY_MOCK);

        when(generator.getG()).thenReturn("2");
        when(generator.getN()).thenReturn("2");

        // Act
        BigInteger actual = sut.generatePublicKey(privateKeyIntMock);

        // Assert
        assertEquals("0", actual.toString());
    }

    @Test
    void getAllUsersInCompanyTest() {
        // Arrange
        when(userManagerMock.getAllUsersInCompany(anyString())).thenReturn(null);

        // Act
        sut.getAllUsersInCompany(MOCK_USER.getCompanyID());

        // Assert
        verify(userManagerMock, times(1)).getAllUsersInCompany(anyString());
    }

    @Test
    void getUsersInSameCompanyTest() {
        // Arrange
        when(userManagerMock.getUser(anyString())).thenReturn(MOCK_USER);

        // Act
        List<User> result = sut.getUsersInSameCompany(MOCK_USER.getId());

        // Assert
        verify(userManagerMock, times(1)).getUser(anyString());
        assertEquals(
                0, result.toArray().length);
    }

    @Test
    void getUsersInSameCompanyAsSupportTest() {
        // Arrange
        when(userManagerMock.getUser(anyString())).thenReturn(MOCK_SUPPORT);

        // Act
        List<User> result = sut.getUsersInSameCompany(MOCK_SUPPORT.getId());

        // Assert
        verify(userManagerMock, times(1)).getUser(anyString());
        assertEquals(
                0, result.toArray().length);
    }

    @Test
    void getSupportUsersTest() {
        // Arrange
        List<String> userListMock = new ArrayList<>();
        List<User> removedUserList = new ArrayList<>();
        User user1 = new User();
        user1.setId(USER_ID_1);
        User user2 = new User();
        user2.setId(USER_ID_2);
        userListMock.add(user1.getId());
        userListMock.add(user2.getId());
        removedUserList.add(user2);

        when(userManagerMock.getSupportUsers(anyString())).thenReturn(userListMock);
        when(sut.getListWithoutUser(anyList(), anyString())).thenReturn(removedUserList);

        // Act
        List<User> actual = sut.getSupportUsers(SENDER_ID_MOCK, COMPANY_ID_MOCK);

        // Assert
        assertEquals(1, actual.size());
    }

    @Test
    void getUsersInContactTest() {
        // Arrange
        List<User> userListMock1 = new ArrayList<>();
        List<User> userListMock2 = new ArrayList<>();
        User user1 = new User();
        user1.setId(USER_ID_1);
        User user2 = new User();
        user2.setId(USER_ID_2);
        userListMock1.add(user1);
        userListMock2.add(user2);

        doReturn(userListMock1).when(sut).getUsersInSameCompany(SENDER_ID_MOCK);
        doReturn(userListMock2).when(sut).getSupportUsers(SENDER_ID_MOCK, COMPANY_ID_MOCK);

        // Act
        List<User> actual = sut.getUsersInContact(SENDER_ID_MOCK, COMPANY_ID_MOCK);

        // Assert
        assertEquals(2, actual.size());
    }

    @Test
    void getListWithoutUserTest() {
        // Arrange
        List<User> userListMock = new ArrayList<>();
        User user1 = new User();
        user1.setId(USER_ID_1);
        User user2 = new User();
        user2.setId(USER_ID_2);
        userListMock.add(user1);
        userListMock.add(user2);

        // Act
        List<User> actual = sut.getListWithoutUser(userListMock, USER_ID_1);

        // Assert
        assertFalse(actual.contains(user1));
        assertTrue(actual.contains(user2));
    }

    @Test
    void getUserTest() {
        // Arrange
        when(userManagerMock.getUser(anyString())).thenReturn(null);

        // Act
        sut.getUser(MOCK_USER.getCompanyID());

        // Assert
        verify(userManagerMock, times(1)).getUser(anyString());
    }

    @Test
    void generatePrivateKeyTest() {
        // Arrange
        UUID uuid = mock(UUID.class);
        mockStatic(UUID.class);

        when(generator.getG()).thenReturn("2");
        when(generator.getN()).thenReturn("2");
        when(UUID.randomUUID()).thenReturn(uuid);
        when(uuid.toString()).thenReturn("1");

        // Act
        String actual = sut.generatePrivateKey();

        // Assert
        assertEquals("6b86b273ff34fce19d6b804eff5a3f5747ada4eaa22f1d49c01e52ddb7875b4b", actual);
    }

    @Test
    void checkChallengeTest() {
        // Arrange
        BDDMockito.given(AES.decrypt(ENCRYPTED_SOLUTION_MOCK, SHARED_KEY_MOCK)).willReturn("SOLVED_" + CHALLENGE_SOLUTION_MOCK);

        // Act
        sut.checkChallenge(ENCRYPTED_SOLUTION_MOCK, SHARED_KEY_MOCK, CHALLENGE_SOLUTION_MOCK);

        // Assert
        assertDoesNotThrow(LoginRejectedException::new);
    }

    @Test
    void checkChallengeThrowLoginRejectedExceptionTest() {
        // Arrange
        String wrongSolutionMock = "4";

        BDDMockito.given(AES.decrypt(ENCRYPTED_SOLUTION_MOCK, SHARED_KEY_MOCK)).willReturn("SOLVED_" + wrongSolutionMock);

        // Assert & Act
        assertThrows(LoginRejectedException.class, () -> sut.checkChallenge(ENCRYPTED_SOLUTION_MOCK, SHARED_KEY_MOCK, CHALLENGE_SOLUTION_MOCK));
    }

    @Test
    void getUserSupportCompaniesTest() {
        // Arrange
        List<CompanySupport> companySupports = new ArrayList<>(
                Arrays.asList(new CompanySupport("TEST_COMPANY_ID_1", "TEST_COMPANY_NAME_1"),
                        new CompanySupport("TEST_COMPANY_ID_2", "TEST_COMPANY_NAME_2")));

        doReturn(companySupports).when(userManagerMock).getUserSupportCompanies(anyString());

        // Act
        List act = sut.getUserSupportCompanies("TEST_USER_ID");

        // Assert
        verify(userManagerMock, times(1)).getUserSupportCompanies(anyString());
        assertEquals(act.size(), 2);
    }
}