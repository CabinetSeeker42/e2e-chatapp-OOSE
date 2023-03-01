package oose.euphoria.backend.service;

import com.google.common.hash.Hashing;
import oose.euphoria.backend.data.ICompanyManager;
import oose.euphoria.backend.data.IUserManager;
import oose.euphoria.backend.data.entities.User;
import oose.euphoria.backend.exceptions.LoginRejectedException;
import oose.euphoria.backend.exceptions.UserNotFoundException;
import oose.euphoria.backend.presentation.coders.JWTTokenDecoder;
import oose.euphoria.backend.presentation.dto.Challenge;
import oose.euphoria.backend.presentation.dto.JWTToken;
import oose.euphoria.backend.presentation.dto.messages.responses.RegisterResponse;
import oose.euphoria.backend.presentation.dto.messages.staticmessage.GeneratorMessage;
import oose.euphoria.backend.utilities.AES;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class UserService implements IUserService {
    @Autowired
    IUserManager userManager;

    @Autowired
    ICompanyManager companyManager;

    @Autowired
    GeneratorMessage generator;

    /**
     * Decompiles the token and registers the user
     *
     * @param jwtToken  JWTToken from JDI
     * @param publicKey Publickey generated from private key/password
     * @param companyID Company that the user is in
     * @return RegisterResponse with the user object
     */
    @Override
    public RegisterResponse registerUser(String jwtToken, String publicKey, String companyID, boolean isSupport) {
        JWTToken.JDIDetails tokenDetails = JWTTokenDecoder.decodeJWTToken(jwtToken).getDetails();
        List<String> allSupportIDs = tokenDetails.getSupportIDs();
        if (isSupport) {
            companyID = null;
        } else {
            checkCompany(companyID);
        }
        User user = userManager.createUser(tokenDetails.getUserId(), tokenDetails.getAccountName(), publicKey, companyID, isSupport);
        allSupportIDs.forEach(this::checkCompany);
        userManager.setCompanySupport(allSupportIDs, user.getId());
        return new RegisterResponse(user);
    }

    /**
     * Checks if a company doesn't exist. If the company doesn't exist, it adds it to the database.
     *
     * @param companyID The ID of the company being checked
     */
    @Override
    public void checkCompany(String companyID) {
        if (!companyManager.companyExists(companyID)) {
            companyManager.createCompany(companyID);
        }
    }

    /**
     * Gets the publickey from userID
     *
     * @param userID UserID that should be found
     * @return PublicKey String
     */
    @Override
    public String getUserPublicKey(String userID) {
        User user = userManager.getUser(userID);
        if (user.getPublicKey() == null) {
            throw new UserNotFoundException(userID);
        }
        return user.getPublicKey();
    }

    /**
     * @param senderID the sender id of the user that wants to login
     * @return a challenge object
     */
    @Override
    public Challenge generateChallenge(String senderID) {
        User user = userManager.getUser(senderID);
        BigInteger userPublicKey = new BigInteger(user.getPublicKey());

        BigInteger serverPrivateKey = hexStringToBigInteger(generatePrivateKey());
        BigInteger serverPublicKey = generatePublicKey(serverPrivateKey);

        String sharedKey = generateSharedKey(userPublicKey, serverPrivateKey).toString(16);

        String challengeSolution = hexStringToBigInteger(generatePrivateKey()).toString();
        String challenge = AES.encrypt(challengeSolution, sharedKey);

        return new Challenge(challenge, challengeSolution, serverPublicKey.toString(), sharedKey);
    }

    /**
     * Gets the user object from userID
     *
     * @param privateKey the private key of the server
     * @return the public key of the server
     */
    @Override
    public BigInteger hexStringToBigInteger(String privateKey) {
        return new BigInteger(privateKey, 16);
    }

    /**
     * Generates a private key
     *
     * @param userPublicKey    the public key of the user
     * @param serverPrivateKey the private key of the server
     * @return the shared key
     */
    @Override
    public BigInteger generateSharedKey(BigInteger userPublicKey, BigInteger serverPrivateKey) {
        return userPublicKey.modPow(serverPrivateKey, new BigInteger(generator.getN()));
    }

    /**
     * Generates a public key
     *
     * @param privateKeyInt the private key of the user
     * @return the public key of the server
     */
    @Override
    public BigInteger generatePublicKey(BigInteger privateKeyInt) {
        return new BigInteger(generator.getG()).modPow(privateKeyInt, new BigInteger(generator.getN()));
    }

    /**
     * Returns a list of users that are in CompanyID
     *
     * @param companyID CompanyID that should list all users
     * @return List of users in company
     */
    @Override
    public List<User> getAllUsersInCompany(String companyID) {
        return userManager.getAllUsersInCompany(companyID);
    }

    /**
     * Get List of user objects that are in the same company as senderID
     *
     * @param senderID Get list of user objects within the same company as senderID
     * @return List of user objects
     */
    @Override
    public List<User> getUsersInSameCompany(String senderID) {
        User sender = userManager.getUser(senderID);
        if (sender.getCompanyID() == null) return new ArrayList<>();
        List<User> users = this.getAllUsersInCompany(sender.getCompanyID());
        return getListWithoutUser(users, senderID);
    }


    /**
     * Get List of user objects that are designated as support.
     *
     * @param senderID  ID of the sender
     * @param companyID Company where the support users should come from
     * @return List of user objects
     */
    @Override
    public List<User> getSupportUsers(String senderID, String companyID) {
        List<User> users = new ArrayList<>();
        userManager.getSupportUsers(companyID)
                .forEach(user -> users.add(userManager.getUser(user)));
        return getListWithoutUser(users, senderID);
    }

    /**
     * Get List of user objects that are in the same company as senderID
     *
     * @param senderID  ID of the sender
     * @param companyID ID of the company
     * @return List of user objects
     */
    @Override
    public List<User> getUsersInContact(String senderID, String companyID) {
        List<User> usersCompany = getUsersInSameCompany(senderID);
        usersCompany.addAll(getSupportUsers(senderID, companyID));

        userManager.getUserSupportCompanies(senderID)
                .forEach(companySupport -> usersCompany.addAll(userManager.getAllUsersInCompany(companySupport.getCompanyID())));
        return usersCompany;
    }

    /**
     * Returns the list users without the user with userID
     *
     * @param users  list of users
     * @param userID ID of the user that should be removed
     * @return List of users without the user with userID
     */
    @Override
    public List<User> getListWithoutUser(List<User> users, String userID) {
        users.removeIf(user -> user.getId().equals(userID));
        return users;
    }

    /**
     * Returns a specific User object from the database
     *
     * @param receiverID Get User object with receiverID
     * @return User object
     */
    @Override
    public User getUser(String receiverID) {
        return userManager.getUser(receiverID);
    }

    /**
     * Generates a private key
     *
     * @return a random private key
     */
    @Override
    public String generatePrivateKey() {
        String randomString = UUID.randomUUID().toString();
        return Hashing.sha256()
                .hashString(randomString, StandardCharsets.UTF_8)
                .toString();
    }

    /**
     * Checks if the challenge solution from the frontend is the same as the one in the backend.
     *
     * @param encryptedSolution Challenge solution from the frontend that's encrypted
     * @param sharedKey         The shared key between the user and the backend
     * @param challengeSolution Challenge solution from the backend
     */
    @Override
    public void checkChallenge(String encryptedSolution, String sharedKey, String challengeSolution) {
        String decryptedSolution = AES.decrypt(encryptedSolution, sharedKey);
        if (!decryptedSolution.equals("SOLVED_" + challengeSolution)) {
            throw new LoginRejectedException();
        }
    }

    /**
     * Returns a list of all companyID's of support companies from a user.
     *
     * @param senderID The ID of the sender
     * @return A list of companyID's
     */
    @Override
    public List<String> getUserSupportCompanies(String senderID) {
        List<String> companyIDs = new ArrayList<>();
        userManager.getUserSupportCompanies(senderID)
                .forEach(company -> companyIDs.add(company.getCompanyID()));
        return companyIDs;
    }
}
