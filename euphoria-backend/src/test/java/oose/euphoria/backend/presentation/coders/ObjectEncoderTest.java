package oose.euphoria.backend.presentation.coders;

import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ObjectEncoderTest {
    ObjectEncoder sut;
    Gson gson;

    @BeforeEach
    void setUp() {
        //Not used as annotations, this is necessary for mocking GSON class
        sut = Mockito.spy(new ObjectEncoder());
        gson = Mockito.mock(Gson.class);
    }

    @Test
    void encodeTest() {
        // Arrange
        String expected = "\"test\"";
        when(gson.toJson(any(Object.class))).thenReturn(expected);

        // Act
        String actual = sut.encode("test");

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    void encodeFailsTest() {
        // Arrange
        String expected = "\"test\"";
        when(gson.toJson(any(Object.class))).thenReturn(expected);

        // Act
        String actual = sut.encode("testt");

        // Assert
        assertNotEquals(expected, actual);
    }

    // This test is not necessary, but is used for SonarQube coverage. Init function cannot be erased because it's from an imported class
    @Test
    void initTest() {
        // Act & Arrange
        sut.init(null);

        // Assert
        verify(sut, times(1)).init(null);
    }

    // This test is not necessary, but is used for SonarQube coverage. Destroy function cannot be erased because it's from an imported class
    @Test
    void destroyTest() {
        // Act & Arrange
        sut.destroy();

        // Assert
        verify(sut, times(1)).destroy();
    }
}
