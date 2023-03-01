package oose.euphoria.backend.presentation.coders;

import com.google.gson.Gson;
import oose.euphoria.backend.exceptions.JWTTokenDecoderException;
import oose.euphoria.backend.presentation.dto.JWTToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class JWTTokenDecoderTest {
    private final String MOCK_TOKEN_STRING = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJkZXRhaWxzIjp7InVzZXJJZCI6IjEiLCJhY2NvdW50TmFtZSI6IkpvaG4ifX0.ya1z6r7O676UXYlE4yFPMWrJk2jUiTyLki82fIrX7ic";
    private final JWTToken EXPECTED = new JWTToken();
    private final JWTToken.JDIDetails JDI_DETAILS = new JWTToken.JDIDetails();
    JWTTokenDecoder sut;
    Gson gson;

    @BeforeEach
    void setUp() {
        //Not used as annotations, this is necessary for mocking GSON class
        sut = Mockito.spy(new JWTTokenDecoder());
        gson = Mockito.mock(Gson.class);
    }

    @Test
    void decodeJWTTokenTest() {
        // Arrange
        JDI_DETAILS.setUserId("1");
        JDI_DETAILS.setAccountName("John");
        JDI_DETAILS.setSupportIDs(new ArrayList<>());
        EXPECTED.setDetails(JDI_DETAILS);
        when(gson.fromJson(anyString(), any())).thenReturn(EXPECTED);

        // Act
        JWTToken act = sut.decodeJWTToken(MOCK_TOKEN_STRING);

        // Assert
        assertEquals(EXPECTED.getDetails().getUserId(), act.getDetails().getUserId());
    }

    @Test
    void decodeJWTTokenThrowExceptionTest() {
        // Act & Arrange
        String InvalidToken = "<script> Alert('Invalid Token')</script>";

        // Assert
        assertThrows(JWTTokenDecoderException.class, () -> sut.decodeJWTToken(InvalidToken));
    }

}