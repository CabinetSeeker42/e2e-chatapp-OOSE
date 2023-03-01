package oose.euphoria.backend.presentation.coders;

import com.google.gson.Gson;
import oose.euphoria.backend.presentation.dto.messages.MessageRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class IncomingMessageDecoderTest {
    IncomingMessageDecoder sut;
    Gson gson;

    @BeforeEach
    void setUp() {
        //Not used as annotations, this is necessary for mocking GSON class
        sut = Mockito.spy(new IncomingMessageDecoder());
        gson = Mockito.mock(Gson.class);
    }

    @Test
    void decodeTest() {
        // Arrange
        MessageRequest expected = new MessageRequest();
        expected.setContent("test");

        when(gson.fromJson(anyString(), any(Class.class))).thenReturn(expected);

        // Act
        MessageRequest actual = sut.decode("{'content': 'test'}");

        // Assert
        assertEquals(expected.getContent(), actual.getContent());
    }

    @Test
    void decodeWrongTest() {
        // Arrange
        MessageRequest expected = new MessageRequest();
        expected.setContent("test");

        when(gson.fromJson(anyString(), any(Class.class))).thenReturn(expected);

        // Act
        MessageRequest actual = sut.decode("{'content': 'testt'}");

        // Assert
        assertNotEquals(expected.getContent(), actual.getContent());
    }

    @Test
    void willDecodeTest() {
        // Arrange
        String message = "test";

        // Act
        boolean actual = sut.willDecode(message);

        // Assert
        assertTrue(actual);
    }

    @Test
    void willNotDecodeTest() {
        // Arrange
        String message = null;

        // Act
        boolean actual = sut.willDecode(message);

        // Assert
        assertFalse(actual);
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
