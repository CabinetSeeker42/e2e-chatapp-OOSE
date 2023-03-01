package oose.euphoria.backend.data;

import oose.euphoria.backend.configuration.database.IDatabaseConnection;
import oose.euphoria.backend.data.entities.Company;
import oose.euphoria.backend.exceptions.DatabaseConnectionException;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class CompanyManagerTest {
    private static final String COMPANY_ID_MOCK = "1";
    @Spy
    @InjectMocks
    CompanyManager sut;
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
    void companyExistsTest() {
        // Arrange
        Company company = new Company("ID", "NAME");

        when(connection.openSession()).thenReturn(session);
        when(session.createQuery(anyString())).thenReturn(query);
        when(query.getResultStream()).thenReturn(List.of(company).stream());

        // Act
        boolean actual = sut.companyExists(COMPANY_ID_MOCK);

        // Assert
        assertTrue(actual);
    }

    @Test
    void companyExistsThrowHibernateExceptionTest() {
        // Arrange
        when(connection.openSession()).thenReturn(session);
        when(session.createQuery(anyString())).thenThrow(HibernateException.class);

        // Assert & Act
        assertThrows(DatabaseConnectionException.class, () -> sut.companyExists(COMPANY_ID_MOCK));
    }

    @Test
    void createCompanyTest() {
        // Arrange
        when(connection.openSession()).thenReturn(session);
        when(session.createQuery(anyString())).thenReturn(query);
        when(session.getTransaction()).thenReturn(transaction);
        doNothing().when(transaction).commit();

        // Act
        sut.createCompany(COMPANY_ID_MOCK);

        // Assert
        verify(session.getTransaction()).commit();
    }

    @Test
    void createCompanyThrowHibernateExceptionTest() {
        // Arrange
        when(connection.openSession()).thenThrow(HibernateException.class);

        // Assert & Act
        assertThrows(DatabaseConnectionException.class, () -> sut.createCompany(COMPANY_ID_MOCK));
    }
}
