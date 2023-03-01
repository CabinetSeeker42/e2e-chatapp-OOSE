package oose.euphoria.backend;

import org.hibernate.cfg.Configuration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

import java.util.Timer;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class BackendApplication {
    static final int SIX_HOURS_IN_MILLISECONDS = 1000 * 60 * 60 * 6;
    static final String HIBERNATE_CONFIG = "hibernate.cfg.xml";
    static DeletionTimer dTimer;

    /**
     * Starts the application!
     *
     * @param args
     */
    public static void main(String[] args) {
        Configuration configuration = new Configuration();
        configuration.configure(HIBERNATE_CONFIG);
        SpringApplication.run(BackendApplication.class, args);

        Timer timer = new Timer();
        DeletionTimer dTimer = new DeletionTimer();
        timer.scheduleAtFixedRate(dTimer, 0, SIX_HOURS_IN_MILLISECONDS);
    }
}


