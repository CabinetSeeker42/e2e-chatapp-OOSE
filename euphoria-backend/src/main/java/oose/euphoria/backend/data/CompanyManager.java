package oose.euphoria.backend.data;

import oose.euphoria.backend.configuration.database.IDatabaseConnection;
import oose.euphoria.backend.data.entities.Company;
import oose.euphoria.backend.exceptions.DatabaseConnectionException;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

import static oose.euphoria.backend.configuration.database.Queries.COMPANY_ID_PARAMETER;
import static oose.euphoria.backend.configuration.database.Queries.SELECT_SPECIFIC_COMPANY;

@Component
public class CompanyManager implements ICompanyManager {

    @Autowired
    IDatabaseConnection connection;


    /**
     * Checks if a company exists.
     *
     * @param companyID ID of the company being checked
     * @return boolean that is true if the company already exists
     */
    @Override
    public boolean companyExists(String companyID) {
        try (Session session = connection.openSession()) {
            Query<Company> query = session.createQuery(SELECT_SPECIFIC_COMPANY);
            query.setParameter(COMPANY_ID_PARAMETER, companyID);
            Stream<Company> companyStream = query.getResultStream();
            return companyStream.findAny().isPresent();
        } catch (HibernateException e) {
            throw new DatabaseConnectionException(e);
        }
    }

    /**
     * Create's a new company from ID.
     *
     * @param companyID ID of the company being checked
     */
    @Override
    public void createCompany(String companyID) {
        Company company = new Company(
                companyID,
                null
        );
        try (Session session = connection.openSession()) {
            session.beginTransaction();
            session.save(company);
            session.getTransaction().commit();
        } catch (HibernateException e) {
            throw new DatabaseConnectionException(e);
        }
    }
}
