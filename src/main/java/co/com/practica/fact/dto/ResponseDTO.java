package co.com.practica.fact.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ============================================================
 * ResponseDTO.java - DTO DE RESPUESTA ESTÁNDAR
 *
 * PATRÓN: Wrapper de respuesta estándar para TODOS los endpoints.
 *
 * VENTAJA DE TENER UN RESPONSE ESTÁNDAR:
 * Los clientes (frontend, otros microservicios) siempre saben
 * qué esperar en la respuesta. La estructura es predecible:
 *
 * {
 *   "code": "200",
 *   "description": "OK",
 *   "data": { ... }   ← puede ser un objeto o una lista
 * }
 *
 * En caso de error:
 * {
 *   "code": "500",
 *   "description": "Error interno del servidor",
 *   "data": null      ← no se incluye por @JsonInclude(NON_NULL)
 * }
 *
 * CONSTRUCTOR PRINCIPAL:
 * ResponseDTO(String code, String description)  → para errores/éxito sin data
 * ResponseDTO(String code, String description, Object data) → con data
 * ============================================================
 */
@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseDTO {

    /** Código HTTP como String: "200", "400", "401", "404", "500" */
    private String code;

    /** Descripción del resultado: "OK", "NOT_FOUND", mensaje de error */
    private String description;

    /**
     * Datos de la respuesta.
     * Puede ser:
     * - Un objeto ParametroDTO
     * - Una lista List<ParametroDTO>
     * - null (en caso de error)
     *
     * Se define como Object para ser genérico y reutilizable
     * en cualquier endpoint del microservicio.
     */
    private Object data;

    /**
     * Constructor para respuestas SIN datos (errores o éxito simple).
     *
     * Ejemplo de uso:
     * return ResponseEntity.ok(new ResponseDTO("200", "OK"));
     */
    public ResponseDTO(String code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * Constructor para respuestas CON datos.
     *
     * Ejemplo de uso:
     * return ResponseEntity.ok(new ResponseDTO("200", "OK", listaParametros));
     */
    public ResponseDTO(String code, String description, Object data) {
        this.code = code;
        this.description = description;
        this.data = data;
    }
}
