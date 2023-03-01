package oose.euphoria.backend.configuration.database;

import org.hibernate.Session;

public interface IDatabaseConnection {
    void setup();

    Session openSession();
}
