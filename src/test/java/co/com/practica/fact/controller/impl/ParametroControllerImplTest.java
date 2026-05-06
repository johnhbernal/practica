package co.com.practica.fact.controller.impl;

import co.com.practica.fact.dto.ParametroDTO;
import co.com.practica.fact.exception.ResourceNotFoundException;
import co.com.practica.fact.service.ParametroService;
import co.com.practica.fact.util.JwtValidationUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas unitarias del controller: verifica routing, delegación al servicio
 * y estructura de ResponseDTO. La seguridad (JWT) se prueba en JwtAuthFilterTest.
 * addFilters=false desactiva la cadena de filtros para aislar el controller.
 */
@WebMvcTest(ParametroControllerImpl.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("Pruebas del ParametroControllerImpl")
class ParametroControllerImplTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ParametroService parametroService;

    // Requerido por JwtAuthFilter aunque los filtros estén desactivados
    @MockBean
    private JwtValidationUtil jwtValidationUtil;

    private ParametroDTO parametroDTO;

    @BeforeEach
    void setUp() {
        parametroDTO = new ParametroDTO();
        parametroDTO.setParameterCode(1L);
        parametroDTO.setParameterName("TIEMPO_SESION");
        parametroDTO.setParameterCategory("SISTEMA");
        parametroDTO.setValue("3600");
        parametroDTO.setStatus("A");
    }

    // ═══════════════════════════════════════════════════════════════
    // GET /parametros/activos
    // ═══════════════════════════════════════════════════════════════

    @Test
    @DisplayName("GET /parametros/activos - retorna 200 con lista")
    void obtenerActivos_retorna200() throws Exception {
        when(parametroService.obtenerParametrosActivos()).thenReturn(Arrays.asList(parametroDTO));

        mockMvc.perform(get("/parametros/activos"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.code").value("200"))
               .andExpect(jsonPath("$.data[0].parameterName").value("TIEMPO_SESION"));
    }

    @Test
    @DisplayName("GET /parametros/activos - lista vacía retorna 200")
    void obtenerActivos_listaVacia_retorna200() throws Exception {
        when(parametroService.obtenerParametrosActivos()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/parametros/activos"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.code").value("200"))
               .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @DisplayName("GET /parametros/activos - error en servicio retorna 500")
    void obtenerActivos_errorServicio_retorna500() throws Exception {
        when(parametroService.obtenerParametrosActivos()).thenThrow(new RuntimeException("DB error"));

        mockMvc.perform(get("/parametros/activos"))
               .andExpect(status().isInternalServerError())
               .andExpect(jsonPath("$.code").value("500"));
    }

    // ═══════════════════════════════════════════════════════════════
    // GET /parametros
    // ═══════════════════════════════════════════════════════════════

    @Test
    @DisplayName("GET /parametros - retorna 200 con lista")
    void obtenerTodos_retorna200() throws Exception {
        when(parametroService.obtenerTodosLosParametros()).thenReturn(Arrays.asList(parametroDTO));

        mockMvc.perform(get("/parametros"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.code").value("200"))
               .andExpect(jsonPath("$.data[0].parameterName").value("TIEMPO_SESION"));
    }

    @Test
    @DisplayName("GET /parametros - error en servicio retorna 500")
    void obtenerTodos_errorServicio_retorna500() throws Exception {
        when(parametroService.obtenerTodosLosParametros()).thenThrow(new RuntimeException("DB error"));

        mockMvc.perform(get("/parametros"))
               .andExpect(status().isInternalServerError())
               .andExpect(jsonPath("$.code").value("500"));
    }

    // ═══════════════════════════════════════════════════════════════
    // GET /parametros/{id}
    // ═══════════════════════════════════════════════════════════════

    @Test
    @DisplayName("GET /parametros/{id} - ID existente retorna 200")
    void obtenerPorId_idExiste_retorna200() throws Exception {
        when(parametroService.obtenerParametroPorId(1L)).thenReturn(parametroDTO);

        mockMvc.perform(get("/parametros/1"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.code").value("200"))
               .andExpect(jsonPath("$.data.parameterName").value("TIEMPO_SESION"));
    }

    @Test
    @DisplayName("GET /parametros/{id} - ID inexistente retorna 404")
    void obtenerPorId_idNoExiste_retorna404() throws Exception {
        when(parametroService.obtenerParametroPorId(999L))
                .thenThrow(new ResourceNotFoundException("Parámetro 999 no encontrado"));

        mockMvc.perform(get("/parametros/999"))
               .andExpect(status().isNotFound())
               .andExpect(jsonPath("$.code").value("404"));
    }

    @Test
    @DisplayName("GET /parametros/{id} - error en servicio retorna 500")
    void obtenerPorId_errorServicio_retorna500() throws Exception {
        when(parametroService.obtenerParametroPorId(1L)).thenThrow(new RuntimeException("DB error"));

        mockMvc.perform(get("/parametros/1"))
               .andExpect(status().isInternalServerError())
               .andExpect(jsonPath("$.code").value("500"));
    }

    // ═══════════════════════════════════════════════════════════════
    // GET /parametros/buscar
    // ═══════════════════════════════════════════════════════════════

    @Test
    @DisplayName("GET /parametros/buscar - retorna 200 con resultados")
    void buscarPorNombre_retorna200() throws Exception {
        when(parametroService.buscarPorNombre("TIEMPO")).thenReturn(Arrays.asList(parametroDTO));

        mockMvc.perform(get("/parametros/buscar").param("nombre", "TIEMPO"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.code").value("200"))
               .andExpect(jsonPath("$.data[0].parameterName").value("TIEMPO_SESION"));
    }

    @Test
    @DisplayName("GET /parametros/buscar - error en servicio retorna 500")
    void buscarPorNombre_errorServicio_retorna500() throws Exception {
        when(parametroService.buscarPorNombre(anyString())).thenThrow(new RuntimeException("DB error"));

        mockMvc.perform(get("/parametros/buscar").param("nombre", "TIEMPO"))
               .andExpect(status().isInternalServerError())
               .andExpect(jsonPath("$.code").value("500"));
    }

    // ═══════════════════════════════════════════════════════════════
    // POST /parametros
    // ═══════════════════════════════════════════════════════════════

    @Test
    @DisplayName("POST /parametros - crea y retorna 200")
    void crearParametro_retorna200() throws Exception {
        when(parametroService.crearParametro(any(ParametroDTO.class))).thenReturn(parametroDTO);

        mockMvc.perform(post("/parametros")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(parametroDTO)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.code").value("200"))
               .andExpect(jsonPath("$.data.parameterName").value("TIEMPO_SESION"));
    }

    @Test
    @DisplayName("POST /parametros - error en servicio retorna 500")
    void crearParametro_errorServicio_retorna500() throws Exception {
        when(parametroService.crearParametro(any(ParametroDTO.class)))
                .thenThrow(new RuntimeException("DB error"));

        mockMvc.perform(post("/parametros")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(parametroDTO)))
               .andExpect(status().isInternalServerError())
               .andExpect(jsonPath("$.code").value("500"));
    }

    // ═══════════════════════════════════════════════════════════════
    // PUT /parametros/{id}
    // ═══════════════════════════════════════════════════════════════

    @Test
    @DisplayName("PUT /parametros/{id} - actualiza y retorna 200")
    void actualizarParametro_retorna200() throws Exception {
        when(parametroService.actualizarParametro(eq(1L), any(ParametroDTO.class)))
                .thenReturn(parametroDTO);

        mockMvc.perform(put("/parametros/1")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(parametroDTO)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.code").value("200"));
    }

    @Test
    @DisplayName("PUT /parametros/{id} - ID inexistente retorna 404")
    void actualizarParametro_idNoExiste_retorna404() throws Exception {
        when(parametroService.actualizarParametro(eq(999L), any(ParametroDTO.class)))
                .thenThrow(new ResourceNotFoundException("Parámetro 999 no encontrado"));

        mockMvc.perform(put("/parametros/999")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(parametroDTO)))
               .andExpect(status().isNotFound())
               .andExpect(jsonPath("$.code").value("404"));
    }

    @Test
    @DisplayName("PUT /parametros/{id} - error en servicio retorna 500")
    void actualizarParametro_errorServicio_retorna500() throws Exception {
        when(parametroService.actualizarParametro(eq(1L), any(ParametroDTO.class)))
                .thenThrow(new RuntimeException("DB error"));

        mockMvc.perform(put("/parametros/1")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(parametroDTO)))
               .andExpect(status().isInternalServerError())
               .andExpect(jsonPath("$.code").value("500"));
    }

    // ═══════════════════════════════════════════════════════════════
    // DELETE /parametros/{id}
    // ═══════════════════════════════════════════════════════════════

    @Test
    @DisplayName("DELETE /parametros/{id} - desactiva y retorna 200")
    void desactivarParametro_retorna200() throws Exception {
        mockMvc.perform(delete("/parametros/1"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.code").value("200"));
    }

    @Test
    @DisplayName("DELETE /parametros/{id} - ID inexistente retorna 404")
    void desactivarParametro_idNoExiste_retorna404() throws Exception {
        doThrow(new ResourceNotFoundException("Parámetro 999 no encontrado"))
                .when(parametroService).desactivarParametro(999L);

        mockMvc.perform(delete("/parametros/999"))
               .andExpect(status().isNotFound())
               .andExpect(jsonPath("$.code").value("404"));
    }

    @Test
    @DisplayName("DELETE /parametros/{id} - error en servicio retorna 500")
    void desactivarParametro_errorServicio_retorna500() throws Exception {
        doThrow(new RuntimeException("DB error"))
                .when(parametroService).desactivarParametro(1L);

        mockMvc.perform(delete("/parametros/1"))
               .andExpect(status().isInternalServerError())
               .andExpect(jsonPath("$.code").value("500"));
    }
}
