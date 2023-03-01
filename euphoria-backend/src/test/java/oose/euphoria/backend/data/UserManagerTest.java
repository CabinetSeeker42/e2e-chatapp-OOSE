package oose.euphoria.backend.data;

import oose.euphoria.backend.configuration.database.IDatabaseConnection;
import oose.euphoria.backend.data.entities.User;
import oose.euphoria.backend.exceptions.DatabaseConnectionException;
import oose.euphoria.backend.exceptions.UserAlreadyExistsException;
import oose.euphoria.backend.exceptions.UserNotFoundException;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import javax.persistence.PersistenceException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class UserManagerTest {
    private static final User MOCK_USER = new User("TEST_USER_ID", "TEST_USER_NAME", "TEST_USER_KEY", "TEST_USER_COMPANY_ID", false);
    private static final String USER_ID_MOCK = "123";
    private static final String COMPANY_ID_MOCK = "321";

    @Spy
    @InjectMocks
    UserManager sut;
    @Mock
    private IDatabaseConnection connection;
    @Mock
    private Session session;
    @Mock
    private Transaction transaction;
    @Mock
    private Query query;

    @BeforeEach
    void setUp() {
        // This line is used to add a mock of @autowired classes into the sut.
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getUserTest() {
        // Arrange
        when(connection.openSession()).thenReturn(session);
        when(session.createQuery(anyString())).thenReturn(query);
        when(query.getResultStream()).thenReturn(List.of(MOCK_USER).stream());

        // Act
        User actual = sut.getUser("TEST_USER");

        // Assert
        assertNotNull(actual);
        assertEquals(MOCK_USER.getId(), actual.getId());
    }

    @Test
    void getUserThrowUserNotFoundExceptionTest() {
        // Arrange
        when(connection.openSession()).thenReturn(session);
        when(session.createQuery(anyString())).thenReturn(query);
        when(query.getResultStream()).thenThrow(UserNotFoundException.class);

        // Assert & Act
        assertThrows(UserNotFoundException.class, () -> sut.getUser(MOCK_USER.getId()));
    }

    @Test
    void getUserThrowHibernateExceptionTest() {
        // Arrange
        when(connection.openSession()).thenReturn(session);
        when(session.createQuery(anyString())).thenReturn(query);
        when(query.getResultStream()).thenThrow(HibernateException.class);

        // Assert & Act
        assertThrows(DatabaseConnectionException.class, () -> sut.getUser(MOCK_USER.getId()));
    }

    @Test
    void getAllUsersInCompanyTest() {
        // Arrange
        when(connection.openSession()).thenReturn(session);
        when(session.createQuery(anyString())).thenReturn(query);
        when(query.getResultStream()).thenReturn(List.of(MOCK_USER).stream());

        // Act
        List<User> actual = sut.getAllUsersInCompany(MOCK_USER.getCompanyID());

        // Assert
        assertNotNull(actual);
        assertEquals(1, actual.size());
        assertEquals(MOCK_USER.getId(), actual.get(0).getId());
    }

    @Test
    void getAllUsersInCompanyThrowHibernateExceptionTest() {
        // Arrange
        when(connection.openSession()).thenReturn(session);
        when(session.createQuery(anyString())).thenReturn(query);
        when(query.getResultStream()).thenThrow(HibernateException.class);

        // Assert & Act
        assertThrows(DatabaseConnectionException.class, () -> sut.getAllUsersInCompany(MOCK_USER.getCompanyID()));
    }

    @Test
    void getSupportUsersTest() {
        // Arrange
        List<String> expected = new ArrayList<>();

        when(connection.openSession()).thenReturn(session);
        when(session.createQuery(anyString())).thenReturn(query);

        // Act
        List<String> actual = sut.getSupportUsers(COMPANY_ID_MOCK);

        // Assert
        assertEquals(actual, expected);
    }

    @Test
    void getSupportUsersThrowHibernateExceptionTest() {
        // Arrange
        when(connection.openSession()).thenReturn(session);
        when(session.createQuery(anyString())).thenThrow(HibernateException.class);

        // Assert & Act
        assertThrows(DatabaseConnectionException.class, () -> sut.getSupportUsers(COMPANY_ID_MOCK));
    }

    @Test
    void createUserTest() {
        // Arrange
        when(connection.openSession()).thenReturn(session);
        when(session.beginTransaction()).thenReturn(transaction);
        when(session.save(any())).thenReturn(null);
        when(session.getTransaction()).thenReturn(transaction);

        // Act
        User actual = sut.createUser("TEST_ID", "TEST_USERNAME", "TEST_KEY", "TEST_COMPANY", false);

        // Assert
        assertEquals("TEST_ID", actual.getId());
    }

    @Test
    void createUserThrowUserAlreadyExistsExceptionTest() {
        // Arrange
        when(connection.openSession()).thenReturn(session);
        when(session.beginTransaction()).thenReturn(transaction);
        when(session.save(any())).thenThrow(PersistenceException.class);

        // Assert & Act
        assertThrows(UserAlreadyExistsException.class, () -> sut.createUser("TEST_ID", "TEST_USERNAME", "TEST_KEY", "TEST_COMPANY", false));
    }

    @Test
    void createUserThrowHibernateExceptionTest() {
        // Arrange
        when(connection.openSession()).thenReturn(session);
        when(session.beginTransaction()).thenThrow(HibernateException.class);

        // Assert & Act
        assertThrows(DatabaseConnectionException.class, () -> sut.createUser("TEST_ID", "TEST_USERNAME", "TEST_KEY", "TEST_COMPANY", false));
    }

    @Test
    void setCompanySupportTest() {
        // Arrange
        List<String> companyIDsMock = new ArrayList<>();
        companyIDsMock.add(COMPANY_ID_MOCK);

        when(connection.openSession()).thenReturn(session);
        when(session.createQuery(anyString())).thenReturn(query);
        when(session.beginTransaction()).thenReturn(transaction);

        // Act
        sut.setCompanySupport(companyIDsMock, USER_ID_MOCK);

        // Assert
        verify(transaction).commit();
    }

    @Test
    void setCompanySupportThrowHibernateExceptionTest() {
        // Arrange
        List<String> companyIDsMock = new ArrayList<>();
        companyIDsMock.add(COMPANY_ID_MOCK);

        when(connection.openSession()).thenReturn(session);
        when(session.beginTransaction()).thenThrow(HibernateException.class);

        // Assert & Act
        assertThrows(DatabaseConnectionException.class, () -> sut.setCompanySupport(companyIDsMock, USER_ID_MOCK));
    }

    @Test
    void getUserSupportCompaniesTest() {
        // Arrange
        when(connection.openSession()).thenReturn(session);
        when(session.beginTransaction()).thenReturn(transaction);
        when(session.getTransaction()).thenReturn(transaction);
        when(session.createQuery(anyString())).thenReturn(query);
        when(query.getResultStream()).thenReturn(Stream.of(MOCK_USER));
        when(session.save(any())).thenReturn(null);
        doNothing().when(transaction).commit();

        // Act
        List res = sut.getUserSupportCompanies("TEST_USER_ID");

        // Assert
        verify(session, times(1)).createQuery(anyString());
        assertEquals(1, res.size());
    }

    @Test
    void getUserSupportCompaniesThrowHibernateExceptionTest() {
        // Arrange
        when(connection.openSession()).thenReturn(session);
        when(session.createQuery(anyString())).thenReturn(query);
        when(query.getResultStream()).thenThrow(HibernateException.class);

        // Assert & Act
        assertThrows(DatabaseConnectionException.class, () -> sut.getUserSupportCompanies(MOCK_USER.getId()));
    }
}
