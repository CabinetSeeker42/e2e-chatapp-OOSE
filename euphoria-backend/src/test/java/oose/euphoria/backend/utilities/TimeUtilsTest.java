package oose.euphoria.backend.utilities;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.MockedStatic;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mockStatic;

class TimeUtilsTest {
    private static final LocalDateTime TIME_MOCK = LocalDateTime.now();
    private static MockedStatic<LocalDateTime> LDTMock;

    @BeforeEach
    void setup() {
        // Makes static functions in the AES mockable. Closes after each test.
        LDTMock = mockStatic(LocalDateTime.class);
    }

    @AfterEach
    void close() {
        LDTMock.close();
    }

    @Test
    void getLocalDateTimeTest() {
        // Arrange
        BDDMockito.given(LocalDateTime.now()).willReturn(TIME_MOCK);

        // Act
        LocalDateTime actual = TimeUtils.getLocalDateTime();

        // Assert
        assertEquals(TIME_MOCK, actual);
    }
}
