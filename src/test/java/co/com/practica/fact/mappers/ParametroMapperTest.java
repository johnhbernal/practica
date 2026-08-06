package co.com.practica.fact.mappers;

import co.com.practica.fact.dto.ParametroDTO;
import co.com.practica.fact.entity.Parametro;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ParametroMapper")
class ParametroMapperTest {

    private final ParametroMapper mapper = new ParametroMapperImpl();

    @Test
    void toDTO_mapsFieldsAndFormatsDates() {
        Date now = new Date();
        Parametro entity = new Parametro();
        entity.setCodParametro(1L);
        entity.setNombreParametro("TIEMPO_SESION");
        entity.setCategoria("SISTEMA");
        entity.setValor("3600");
        entity.setDescripcion("desc");
        entity.setEstado("A");
        entity.setUsuarioCreacion("admin");
        entity.setFechaCreacion(now);
        entity.setUsuarioModificacion("admin");
        entity.setFechaModificacion(now);

        ParametroDTO dto = mapper.toDTO(entity);

        assertEquals(1L, dto.getParameterCode());
        assertEquals("TIEMPO_SESION", dto.getParameterName());
        assertEquals("SISTEMA", dto.getParameterCategory());
        assertEquals("3600", dto.getValue());
        assertEquals("A", dto.getStatus());
        assertNotNull(dto.getCreationDate());
    }

    @Test
    void toDTOList_mapsEachElement() {
        Parametro entity = new Parametro();
        entity.setCodParametro(2L);
        entity.setNombreParametro("X");

        List<ParametroDTO> list = mapper.toDTOList(Collections.singletonList(entity));

        assertEquals(1, list.size());
        assertEquals(2L, list.get(0).getParameterCode());
    }

    @Test
    void toEntity_ignoresAuditDates() {
        ParametroDTO dto = new ParametroDTO();
        dto.setParameterName("NUEVO");
        dto.setParameterCategory("NEGOCIO");
        dto.setValue("v");

        Parametro entity = mapper.toEntity(dto);

        assertEquals("NUEVO", entity.getNombreParametro());
        assertNull(entity.getFechaCreacion());
        assertNull(entity.getFechaModificacion());
    }

    @Test
    void formatDateTime_nullReturnsNull() {
        assertNull(ParametroMapper.formatDateTime(null));
    }
}
