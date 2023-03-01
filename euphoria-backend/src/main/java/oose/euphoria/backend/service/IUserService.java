package oose.euphoria.backend.service;

import oose.euphoria.backend.data.entities.User;
import oose.euphoria.backend.presentation.dto.Challenge;
import oose.euphoria.backend.presentation.dto.messages.responses.RegisterResponse;

import java.math.BigInteger;
import java.util.List;

public interface IUserService {
    RegisterResponse registerUser(String jwtToken, String publicKey, String companyID, boolean isSupport);

    void checkCompany(String companyID);

    String getUserPublicKey(String userID);

    Challenge generateChallenge(String senderID);

    BigInteger hexStringToBigInteger(String privateKey);

    BigInteger generateSharedKey(BigInteger userPublicKey, BigInteger serverPrivateKey);

    BigInteger generatePublicKey(BigInteger privateKeyInt);

    List<User> getAllUsersInCompany(String companyID);

    List<User> getListWithoutUser(List<User> users, String userID);

    User getUser(String receiverID);

    List<User> getUsersInSameCompany(String senderID);

    List<User> getSupportUsers(String senderID, String companyID);

    List<User> getUsersInContact(String senderID, String companyID);

    String generatePrivateKey();

    void checkChallenge(String encryptedSolution, String sharedKey, String challengeSolution);

    List<String> getUserSupportCompanies(String senderID);
}
