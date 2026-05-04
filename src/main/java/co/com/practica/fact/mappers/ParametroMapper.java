package co.com.practica.fact.mappers;

import co.com.practica.fact.dto.ParametroDTO;
import co.com.practica.fact.entity.Parametro;
import org.mapstruct.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * ============================================================
 * ParametroMapper.java - MAPSTRUCT MAPPER
 *
 * CONCEPTO DE MAPPER (MapStruct):
 * Convierte objetos de un tipo a otro automáticamente.
 * MapStruct GENERA el código de conversión en tiempo de
 * COMPILACIÓN (no reflexión), haciéndolo muy eficiente.
 *
 * @Mapper(componentModel = "spring"):
 *   - Genera una implementación de esta interfaz automáticamente
 *   - La implementación es un @Component de Spring, inyectable con @Autowired
 *   - El archivo generado está en: target/generated-sources/annotations/
 *
 * @Mappings: Define cómo mapear campos con nombres diferentes.
 *   Si los nombres son iguales, MapStruct los mapea automáticamente.
 *
 * EJEMPLO DE CÓDIGO GENERADO POR MAPSTRUCT:
 * public ParametroDTO toDTO(Parametro source) {
 *     ParametroDTO dto = new ParametroDTO();
 *     dto.setParameterCode(source.getCodParametro()); // @Mapping source→target
 *     dto.setParameterName(source.getNombreParametro());
 *     // ... etc.
 *     return dto;
 * }
 * ============================================================
 */
@Mapper(componentModel = "spring")
public interface ParametroMapper {

    /**
     * Convierte Entity → DTO (para respuestas al cliente)
     *
     * @Mapping: source = nombre del campo en Entity (Parametro)
     *           target = nombre del campo en DTO (ParametroDTO)
     *
     * Para fechas usamos expression con el método formatDateTime()
     * definido abajo, porque MapStruct no convierte Date→String automáticamente.
     */
    @Mappings({
            @Mapping(source = "codParametro",       target = "parameterCode"),
            @Mapping(source = "nombreParametro",    target = "parameterName"),
            @Mapping(source = "categoria",          target = "parameterCategory"),
            @Mapping(source = "valor",              target = "value"),
            @Mapping(source = "descripcion",        target = "description"),
            @Mapping(source = "estado",             target = "status"),
            @Mapping(source = "usuarioCreacion",    target = "creationBy"),
            @Mapping(source = "usuarioModificacion",target = "updateBy"),
            // Para fechas usamos una expresión Java personalizada
            @Mapping(expression = "java(ParametroMapper.formatDateTime(source.getFechaCreacion()))",
                    target = "creationDate"),
            @Mapping(expression = "java(ParametroMapper.formatDateTime(source.getFechaModificacion()))",
                    target = "updateDate")
    })
    ParametroDTO toDTO(Parametro source);

    /**
     * Convierte una lista de Entities → lista de DTOs.
     * MapStruct genera automáticamente este método usando toDTO() para cada elemento.
     *
     * @param source Lista de entidades
     * @return Lista de DTOs
     */
    List<ParametroDTO> toDTOList(List<Parametro> source);

    /**
     * Convierte DTO → Entity (para guardar en BD).
     *
     * @InheritInverseConfiguration: Hereda la configuración inversa de toDTO().
     * No necesitamos repetir todos los @Mapping, solo los que sean distintos.
     *
     * Ignoramos creationDate y updateDate porque en la entidad son Date,
     * no String (las fechas las maneja el servidor, no el cliente).
     */
    @InheritInverseConfiguration
    @Mappings({
            @Mapping(target = "fechaCreacion",    ignore = true),
            @Mapping(target = "fechaModificacion",ignore = true)
    })
    Parametro toEntity(ParametroDTO parametroDTO);

    /**
     * Método utilitario para formatear fechas.
     *
     * Es STATIC porque MapStruct lo llama directamente en las expresiones
     * de @Mapping sin tener una instancia del mapper.
     *
     * @param fecha Fecha a formatear (puede ser null)
     * @return String con formato "yyyy-MM-dd HH:mm:ss" o null si la fecha es null
     */
    static String formatDateTime(Date fecha) {
        if (fecha == null) return null;
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(fecha);
    }
}
