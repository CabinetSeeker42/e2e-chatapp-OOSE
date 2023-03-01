package oose.euphoria.backend.data;

import oose.euphoria.backend.data.entities.CompanySupport;
import oose.euphoria.backend.data.entities.User;

import java.util.List;

public interface IUserManager {
    User getUser(String userID);

    List<User> getAllUsersInCompany(String companyID);

    List<String> getSupportUsers(String companyID);

    User createUser(String userID, String userName, String publicKey, String companyID, boolean isSupport);

    void setCompanySupport(List<String> companyIDs, String userID);

    List<CompanySupport> getUserSupportCompanies(String senderID);
}
