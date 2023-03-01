package oose.euphoria.backend.data;

import oose.euphoria.backend.configuration.database.IDatabaseConnection;
import oose.euphoria.backend.data.entities.CompanySupport;
import oose.euphoria.backend.data.entities.User;
import oose.euphoria.backend.exceptions.DatabaseConnectionException;
import oose.euphoria.backend.exceptions.UserAlreadyExistsException;
import oose.euphoria.backend.exceptions.UserNotFoundException;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.PersistenceException;
import java.util.List;
import java.util.stream.Collectors;

import static oose.euphoria.backend.configuration.database.Queries.*;

@Component
public class UserManager implements IUserManager {
    @Autowired
    IDatabaseConnection connection;

    /**
     * Returns the user which has the same ID as userID.
     *
     * @param userID The ID of the user that needs to be gotten
     * @return Userobject of the found user
     */
    @Override
    public User getUser(String userID) {
        try (Session session = connection.openSession()) {
            Query<User> query = session.createQuery(SELECT_USER);
            query.setParameter(USER_ID_PARAMETER, userID);
            return query
                    .getResultStream()
                    .findAny()
                    .orElseThrow(() -> new UserNotFoundException(userID));
        } catch (HibernateException e) {
            throw new DatabaseConnectionException(e);
        }
    }

    /**
     * Returns all the users in a specific company.
     *
     * @param companyID The ID of the specific company
     * @return User objects of all the users that are in the specific company
     */
    @Override
    public List<User> getAllUsersInCompany(String companyID) {
        try (Session session = connection.openSession()) {
            Query<User> query = session.createQuery(SELECT_USERS_IN_COMPANY);
            query.setParameter(COMPANY_ID_PARAMETER, companyID);
            return query.getResultStream().collect(Collectors.toList());
        } catch (HibernateException e) {
            throw new DatabaseConnectionException(e);
        }
    }


    /**
     * Returns all the user IDs of users that are support in a specific company.
     *
     * @param companyID The company that's being checked for supports
     * @return IDs of the support users that are in the specific company
     */
    @Override
    public List<String> getSupportUsers(String companyID) {
        try (Session session = connection.openSession()) {
            Query<String> query = session.createQuery(SELECT_SUPPORT_USERS);
            query.setParameter(COMPANY_ID_PARAMETER, companyID);
            return query.getResultStream().collect(Collectors.toList());
        } catch (HibernateException e) {
            throw new DatabaseConnectionException(e);
        }
    }

    /**
     * Creates a new user
     *
     * @param userID    ID of the new user
     * @param userName  Name of the new user
     * @param publicKey The public key of the new user
     * @param companyID The ID of the company to which the user belongs
     * @param isSupport Is true if the user is a support
     * @return User object of the created user
     */
    @Override
    public User createUser(String userID, String userName, String publicKey, String companyID, boolean isSupport) {
        User user = new User(
                userID,
                companyID,
                publicKey,
                userName,
                isSupport
        );
        try (Session session = connection.openSession()) {
            session.beginTransaction();
            session.save(user);
            session.getTransaction().commit();
        } catch (HibernateException e) {
            throw new DatabaseConnectionException(e);
        } catch (PersistenceException e) {
            throw new UserAlreadyExistsException(e);
        }
        return user;
    }

    /**
     * Adds new rows in the CompanySupport table for a user
     *
     * @param companyIDs The IDs of companies that are being added
     * @param userID     The user that wants to be added to the companies
     */
    @Override
    public void setCompanySupport(List<String> companyIDs, String userID) {
        try (Session session = connection.openSession()) {
            Transaction transaction = session.beginTransaction();
            for (String companyID : companyIDs) {
                CompanySupport userCompany = new CompanySupport(
                        userID,
                        companyID
                );
                session.save(userCompany);
            }
            transaction.commit();
        } catch (HibernateException e) {
            throw new DatabaseConnectionException(e);
        }
    }


    /**
     * Get all the support companies from a user.
     *
     * @param senderID The ID of the user
     * @return A list of CompanySupport objects
     */
    @Override
    public List<CompanySupport> getUserSupportCompanies(String senderID) {
        try (Session session = connection.openSession()) {
            Query<CompanySupport> query = session.createQuery(SELECT_USER_SUPPORT_COMPANY_IDS);
            query.setParameter(USER_ID_PARAMETER, senderID);
            return query.getResultStream().collect(Collectors.toList());
        } catch (HibernateException e) {
            throw new DatabaseConnectionException(e);
        }
    }
}
