package oose.euphoria.backend.utilities;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeUtils {

    private TimeUtils() {

    }

    public static String changeDate(LocalDateTime ldt, int days) {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                .format(ldt.plusDays(days));
    }

    public static String oneDay() {
        LocalDateTime ldt = getLocalDateTime();
        return changeDate(ldt, -1);
    }

    public static String oneWeek() {
        LocalDateTime ldt = getLocalDateTime();
        return changeDate(ldt, -7);
    }

    public static LocalDateTime getLocalDateTime() {
        return LocalDateTime.now();
    }
}
