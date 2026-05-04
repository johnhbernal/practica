package co.com.practica.fact.exception;

/**
 * ============================================================
 * ResourceNotFoundException.java
 *
 * EXCEPCIÓN PERSONALIZADA para cuando un recurso no se encuentra en BD.
 * Lanzada por el Service, capturada por ServicesException (handler global).
 *
 * Al extender RuntimeException, es una excepción NO CHECKED:
 * - No obliga a declarar "throws" en los métodos que la lanzan
 * - Spring puede hacer rollback automático de la transacción
 *
 * HTTP STATUS: 404 NOT FOUND
 * ============================================================
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
