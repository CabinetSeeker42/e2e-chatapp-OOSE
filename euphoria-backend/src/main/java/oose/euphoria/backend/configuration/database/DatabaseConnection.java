package oose.euphoria.backend.configuration.database;

import oose.euphoria.backend.exceptions.DatabaseSessionException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.springframework.stereotype.Component;

@Component
public class DatabaseConnection implements IDatabaseConnection {
    protected SessionFactory sessionFactory;

    /**
     * Initializes database connection
     */
    public DatabaseConnection() {
        setup();
    }

    /**
     * Sets up a new registry
     */
    @Override
    public void setup() {
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure()
                .build();
        try {
            sessionFactory = new MetadataSources(registry)
                    .buildMetadata()
                    .buildSessionFactory();
        } catch (Exception e) {
            StandardServiceRegistryBuilder
                    .destroy(registry);
            throw new DatabaseSessionException(e);
        }
    }

    /**
     * Returns a new database session
     *
     * @return Session
     */
    @Override
    public Session openSession() {
        return sessionFactory.openSession();
    }
}
