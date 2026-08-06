package co.com.practica.fact.exception;

import co.com.practica.fact.dto.ParametroDTO;
import co.com.practica.fact.dto.ResponseDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.ServletWebRequest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@DisplayName("ServicesException")
class ServicesExceptionTest {

    private final ServicesException handler = new ServicesException();

    @Test
    void handleResourceNotFoundException_returns404() {
        ResponseEntity<ResponseDTO> response = handler.handleResourceNotFoundException(
                new ResourceNotFoundException("Parámetro no encontrado"), mock(ServletWebRequest.class));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("404", response.getBody().getCode());
        assertEquals("Parámetro no encontrado", response.getBody().getDescription());
    }

    @Test
    void handleMethodArgumentNotValid_returns400WithFieldErrors() throws Exception {
        BeanPropertyBindingResult bindingResult =
                new BeanPropertyBindingResult(new ParametroDTO(), "parametroDTO");
        bindingResult.addError(new FieldError("parametroDTO", "parameterName", "required"));

        MethodParameter parameter = new MethodParameter(String.class.getMethod("toString"), -1);
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(parameter, bindingResult);

        ResponseEntity<Object> response = handler.handleMethodArgumentNotValid(
                ex, null, HttpStatus.BAD_REQUEST, mock(ServletWebRequest.class));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ResponseDTO body = (ResponseDTO) response.getBody();
        assertNotNull(body);
        assertEquals("400", body.getCode());
        assertNotNull(body.getData());
    }

    @Test
    void handleIllegalArgumentException_returns400() {
        ResponseEntity<ResponseDTO> response =
                handler.handleIllegalArgumentException(new IllegalArgumentException("invalid"));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("invalid", response.getBody().getDescription());
    }

    @Test
    void handleGenericException_returns500() {
        ResponseEntity<ResponseDTO> response =
                handler.handleGenericException(new RuntimeException("boom"));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("500", response.getBody().getCode());
        assertTrue(response.getBody().getDescription().contains("administrador"));
    }
}
