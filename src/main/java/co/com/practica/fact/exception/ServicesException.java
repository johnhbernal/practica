package co.com.practica.fact.exception;

import co.com.practica.fact.dto.ResponseDTO;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * ============================================================
 * ServicesException.java - MANEJADOR GLOBAL DE EXCEPCIONES
 *
 * CONCEPTO: @RestControllerAdvice + @ExceptionHandler implementan
 * el patrón "Global Exception Handler". En lugar de manejar
 * excepciones en cada método del controller (try/catch repetitivo),
 * las centralizamos aquí.
 *
 * @RestControllerAdvice: Intercepta excepciones de TODOS los
 *   controllers. Combina @ControllerAdvice + @ResponseBody.
 *
 * @ExceptionHandler(TipoExcepcion.class): Indica qué tipo de
 *   excepción maneja cada método.
 *
 * FLUJO CUANDO OCURRE UNA EXCEPCIÓN:
 * 1. Service lanza ResourceNotFoundException("Parámetro no encontrado")
 * 2. Spring busca un @ExceptionHandler para ResourceNotFoundException
 * 3. Encuentra handleResourceNotFoundException() aquí
 * 4. Lo ejecuta y retorna la respuesta 404 al cliente
 * 5. El cliente recibe JSON: { "code": "404", "description": "Parámetro no encontrado" }
 *
 * VENTAJA: Sin este handler, Spring retornaría un HTML de error
 * genérico. Con el handler, siempre retornamos JSON estructurado.
 * ============================================================
 */
@Log4j2
@RestControllerAdvice
public class ServicesException extends ResponseEntityExceptionHandler {

    /**
     * Maneja ResourceNotFoundException → HTTP 404
     *
     * Se activa cuando el Service lanza:
     * throw new ResourceNotFoundException("mensaje");
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ResponseDTO> handleResourceNotFoundException(
            ResourceNotFoundException ex, WebRequest request) {

        log.warn("Recurso no encontrado: {}", ex.getMessage());

        ResponseDTO response = new ResponseDTO(
                String.valueOf(HttpStatus.NOT_FOUND.value()),
                ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /**
     * Maneja errores de VALIDACIÓN → HTTP 400 BAD REQUEST
     *
     * Se activa cuando @Valid en el controller falla.
     * Ejemplo: si @NotBlank del DTO no se cumple, Spring lanza
     * MethodArgumentNotValidException automáticamente.
     *
     * Retorna un mapa con todos los errores de validación:
     * {
     *   "code": "400",
     *   "description": "Error de validación",
     *   "data": {
     *     "parameterName": "El nombre del parámetro es obligatorio",
     *     "value": "El valor no puede estar vacío"
     *   }
     * }
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {

        log.warn("Error de validación en el request: {}", ex.getMessage());

        // Recolectar todos los errores de validación campo por campo
        Map<String, String> errores = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String campo = ((FieldError) error).getField();
            String mensaje = error.getDefaultMessage();
            errores.put(campo, mensaje);
        });

        ResponseDTO response = new ResponseDTO(
                String.valueOf(HttpStatus.BAD_REQUEST.value()),
                "Error de validación en los datos enviados",
                errores);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Maneja IllegalArgumentException → HTTP 400
     *
     * Se activa cuando el código lanza:
     * throw new IllegalArgumentException("Parámetro inválido");
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ResponseDTO> handleIllegalArgumentException(
            IllegalArgumentException ex) {

        log.warn("Argumento inválido: {}", ex.getMessage());

        ResponseDTO response = new ResponseDTO(
                String.valueOf(HttpStatus.BAD_REQUEST.value()),
                ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Manejador GENÉRICO para cualquier otra excepción → HTTP 500
     *
     * Es el "catch-all": captura cualquier excepción no manejada
     * específicamente por los otros @ExceptionHandler.
     *
     * IMPORTANTE: En producción, NO retornar el mensaje interno de la
     * excepción al cliente (puede exponer información sensible).
     * Aquí lo hacemos por simplicidad educativa.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDTO> handleGenericException(Exception ex) {

        log.error("Error interno del servidor: {}", ex.getMessage(), ex);

        ResponseDTO response = new ResponseDTO(
                String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()),
                "Error interno del servidor. Por favor contacte al administrador.");

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
