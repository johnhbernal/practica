package co.com.practica.fact.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * ============================================================
 * ParametroDTO.java - DATA TRANSFER OBJECT
 *
 * CONCEPTO DE DTO:
 * Un DTO es un objeto cuyo ÚNICO propósito es transferir datos
 * entre capas de la aplicación o entre sistemas.
 *
 * ¿POR QUÉ USAR DTOs EN LUGAR DE LAS ENTIDADES DIRECTAMENTE?
 *
 * 1. SEGURIDAD: La entidad puede tener campos sensibles que no
 *    queremos exponer en la API (contraseñas, claves internas).
 *    El DTO solo expone lo necesario.
 *
 * 2. DESACOPLAMIENTO: Si cambia la tabla en BD (columna renombrada),
 *    solo cambia la entidad y el mapper. La API (DTO) no cambia
 *    y los clientes no se ven afectados.
 *
 * 3. FORMATO: La BD puede tener Date, pero la API quiere String.
 *    El DTO puede tener el formato que el cliente necesita.
 *
 * 4. VALIDACIÓN: Las validaciones de la API van en el DTO,
 *    no en la entidad.
 *
 * FLUJO TÍPICO:
 *   JSON request → DTO (validado) → Mapper → Entity → BD
 *   BD → Entity → Mapper → DTO → JSON response
 *
 * ANOTACIONES DE VALIDACIÓN (javax.validation):
 * - @NotNull: El campo no puede ser null
 * - @NotBlank: No puede ser null ni cadena vacía/espacios
 * - @Size: Controla longitud mínima y máxima
 * - @Min / @Max: Para números
 * - @Email: Valida formato de email
 * ============================================================
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
// @JsonInclude: No incluir campos null en la respuesta JSON
// Así el JSON es más limpio (no envía "campo": null)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ParametroDTO {

    /** ID del parámetro (no requerido en creación, sí en actualización) */
    private Long parameterCode;

    /**
     * Nombre del parámetro.
     * @NotBlank: Obligatorio, no puede ser vacío
     * @Size: Entre 3 y 100 caracteres
     * El mensaje de error se muestra cuando la validación falla
     */
    @NotBlank(message = "El nombre del parámetro es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    private String parameterName;

    /** Categoría de agrupación del parámetro */
    private String parameterCategory;

    /** Valor del parámetro */
    private String value;

    /** Descripción del parámetro */
    private String description;

    /**
     * Estado del parámetro: "A" (Activo) o "I" (Inactivo).
     * En la respuesta al cliente usamos "A"/"I" en lugar de true/false
     * para ser coherentes con los valores de la BD.
     */
    private String status;

    /** Auditoría: quién lo creó */
    private String creationBy;

    /** Auditoría: fecha de creación en formato legible */
    private String creationDate;

    /** Auditoría: quién lo modificó */
    private String updateBy;

    /** Auditoría: fecha de modificación en formato legible */
    private String updateDate;
}
