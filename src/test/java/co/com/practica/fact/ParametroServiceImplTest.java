package co.com.practica.fact;

import co.com.practica.fact.dto.ParametroDTO;
import co.com.practica.fact.entity.Parametro;
import co.com.practica.fact.exception.ResourceNotFoundException;
import co.com.practica.fact.mappers.ParametroMapper;
import co.com.practica.fact.repository.ParametroRepository;
import co.com.practica.fact.service.impl.ParametroServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * ============================================================
 * ParametroServiceImplTest.java - PRUEBAS UNITARIAS
 *
 * CONCEPTO DE PRUEBAS UNITARIAS:
 * Prueban una clase de forma AISLADA, sin dependencias reales.
 * Las dependencias se reemplazan por "Mocks" (objetos falsos controlables).
 *
 * VENTAJAS:
 * - Rápidas (no necesitan BD, servidor, red)
 * - Deterministas (siempre el mismo resultado)
 * - Documentan el comportamiento esperado del código
 *
 * FRAMEWORK: JUnit 5 + Mockito
 *
 * @ExtendWith(MockitoExtension.class): Integra Mockito con JUnit 5.
 *   Procesa automáticamente @Mock e @InjectMocks.
 *
 * @Mock: Crea un objeto falso del tipo especificado.
 *   Podemos programar qué debe retornar con when().thenReturn()
 *
 * @InjectMocks: Crea la clase bajo prueba e inyecta los @Mock automáticamente.
 *   Equivale a hacer: new ParametroServiceImpl(mockRepository, mockMapper)
 *
 * PATRÓN AAA (Arrange-Act-Assert):
 * - Arrange (Given): Preparar los datos y configurar los mocks
 * - Act (When): Ejecutar el método bajo prueba
 * - Assert (Then): Verificar que el resultado es el esperado
 * ============================================================
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas del ParametroService")
class ParametroServiceImplTest {

    // ── MOCKS (dependencias falsas) ────────────────────────────────
    @Mock
    private ParametroRepository parametroRepository;

    @Mock
    private ParametroMapper parametroMapper;

    // ── CLASE BAJO PRUEBA ─────────────────────────────────────────
    @InjectMocks
    private ParametroServiceImpl parametroService;

    // ── DATOS DE PRUEBA ───────────────────────────────────────────
    private Parametro parametroActivo;
    private ParametroDTO parametroDTOActivo;

    /**
     * @BeforeEach: Se ejecuta ANTES de cada método @Test.
     * Inicializa los datos de prueba para tenerlos listos.
     */
    @BeforeEach
    void setUp() {
        // Crear entidad de prueba
        parametroActivo = new Parametro();
        parametroActivo.setCodParametro(1L);
        parametroActivo.setNombreParametro("TIEMPO_SESION");
        parametroActivo.setCategoria("SISTEMA");
        parametroActivo.setValor("3600");
        parametroActivo.setEstado("A");

        // Crear DTO de prueba correspondiente
        parametroDTOActivo = new ParametroDTO();
        parametroDTOActivo.setParameterCode(1L);
        parametroDTOActivo.setParameterName("TIEMPO_SESION");
        parametroDTOActivo.setParameterCategory("SISTEMA");
        parametroDTOActivo.setValue("3600");
        parametroDTOActivo.setStatus("A");
    }

    // ═══════════════════════════════════════════════════════════════
    // PRUEBAS DE obtenerParametrosActivos()
    // ═══════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Debe retornar lista de parámetros activos cuando existen")
    void debeRetornarParametrosActivos_cuandoExisten() {
        // ── ARRANGE (Given) ──────────────────────────────────────
        // Programamos el mock: cuando se llame findByEstado("A"), retornar lista con 1 elemento
        List<Parametro> parametrosEnBD = Arrays.asList(parametroActivo);
        when(parametroRepository.findByEstado("A")).thenReturn(parametrosEnBD);

        // Programamos el mapper mock
        List<ParametroDTO> dtoEsperados = Arrays.asList(parametroDTOActivo);
        when(parametroMapper.toDTOList(parametrosEnBD)).thenReturn(dtoEsperados);

        // ── ACT (When) ────────────────────────────────────────────
        List<ParametroDTO> resultado = parametroService.obtenerParametrosActivos();

        // ── ASSERT (Then) ─────────────────────────────────────────
        assertNotNull(resultado, "El resultado no debe ser null");
        assertEquals(1, resultado.size(), "Debe retornar exactamente 1 parámetro");
        assertEquals("TIEMPO_SESION", resultado.get(0).getParameterName());

        // Verificar que se llamaron los métodos esperados
        verify(parametroRepository, times(1)).findByEstado("A");
        verify(parametroMapper, times(1)).toDTOList(parametrosEnBD);
    }

    @Test
    @DisplayName("Debe retornar lista vacía cuando no hay parámetros activos")
    void debeRetornarListaVacia_cuandoNoHayParametrosActivos() {
        // ARRANGE
        when(parametroRepository.findByEstado("A")).thenReturn(Collections.emptyList());
        when(parametroMapper.toDTOList(Collections.emptyList())).thenReturn(Collections.emptyList());

        // ACT
        List<ParametroDTO> resultado = parametroService.obtenerParametrosActivos();

        // ASSERT
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty(), "La lista debe estar vacía");
    }

    // ═══════════════════════════════════════════════════════════════
    // PRUEBAS DE obtenerParametroPorId()
    // ═══════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Debe retornar el parámetro cuando existe el ID")
    void debeRetornarParametro_cuandoExisteElId() {
        // ARRANGE
        when(parametroRepository.findById(1L)).thenReturn(Optional.of(parametroActivo));
        when(parametroMapper.toDTO(parametroActivo)).thenReturn(parametroDTOActivo);

        // ACT
        ParametroDTO resultado = parametroService.obtenerParametroPorId(1L);

        // ASSERT
        assertNotNull(resultado);
        assertEquals(1L, resultado.getParameterCode());
        assertEquals("TIEMPO_SESION", resultado.getParameterName());
    }

    @Test
    @DisplayName("Debe lanzar ResourceNotFoundException cuando el ID no existe")
    void debeLanzarExcepcion_cuandoIdNoExiste() {
        // ARRANGE: Simulamos que la BD no encuentra nada
        when(parametroRepository.findById(999L)).thenReturn(Optional.empty());

        // ACT + ASSERT: Verificamos que se lanza la excepción correcta
        ResourceNotFoundException excepcion = assertThrows(
                ResourceNotFoundException.class,
                () -> parametroService.obtenerParametroPorId(999L),
                "Debe lanzar ResourceNotFoundException para ID inexistente"
        );

        // Verificar el mensaje de la excepción
        assertTrue(excepcion.getMessage().contains("999"),
                "El mensaje debe contener el ID buscado");
    }

    // ═══════════════════════════════════════════════════════════════
    // PRUEBAS DE crearParametro()
    // ═══════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Debe crear y retornar el parámetro con ID generado")
    void debeCrearParametroExitosamente() {
        // ARRANGE
        ParametroDTO dtoParaCrear = new ParametroDTO();
        dtoParaCrear.setParameterName("NUEVO_PARAMETRO");
        dtoParaCrear.setParameterCategory("NEGOCIO");
        dtoParaCrear.setValue("valor-nuevo");

        Parametro entidadAGuardar = new Parametro();
        entidadAGuardar.setNombreParametro("NUEVO_PARAMETRO");

        Parametro entidadGuardada = new Parametro();
        entidadGuardada.setCodParametro(10L);  // ID asignado por BD
        entidadGuardada.setNombreParametro("NUEVO_PARAMETRO");

        ParametroDTO dtoResultado = new ParametroDTO();
        dtoResultado.setParameterCode(10L);
        dtoResultado.setParameterName("NUEVO_PARAMETRO");

        when(parametroMapper.toEntity(dtoParaCrear)).thenReturn(entidadAGuardar);
        when(parametroRepository.save(any(Parametro.class))).thenReturn(entidadGuardada);
        when(parametroMapper.toDTO(entidadGuardada)).thenReturn(dtoResultado);

        // ACT
        ParametroDTO resultado = parametroService.crearParametro(dtoParaCrear);

        // ASSERT
        assertNotNull(resultado);
        assertEquals(10L, resultado.getParameterCode(),
                "El ID debe ser el asignado por la BD");
        assertEquals("NUEVO_PARAMETRO", resultado.getParameterName());

        // Verificar que se llamó save() exactamente 1 vez
        verify(parametroRepository, times(1)).save(any(Parametro.class));
    }

    // ═══════════════════════════════════════════════════════════════
    // PRUEBAS DE desactivarParametro()
    // ═══════════════════════════════════════════════════════════════

    @Test
    @DisplayName("Debe desactivar el parámetro (borrado lógico)")
    void debeDesactivarParametro_cuandoExiste() {
        // ARRANGE
        when(parametroRepository.findById(1L)).thenReturn(Optional.of(parametroActivo));
        when(parametroRepository.save(any(Parametro.class))).thenReturn(parametroActivo);

        // ACT
        parametroService.desactivarParametro(1L);

        // ASSERT: Verificar que se guardó con estado "I"
        verify(parametroRepository, times(1)).save(argThat(p ->
                "I".equals(p.getEstado())  // Verificar el borrado lógico
        ));

        // Verificar que NUNCA se llamó delete() (no borrado físico)
        verify(parametroRepository, never()).delete(any());
        verify(parametroRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Debe lanzar excepción al desactivar un ID inexistente")
    void debeLanzarExcepcion_alDesactivarIdInexistente() {
        // ARRANGE
        when(parametroRepository.findById(999L)).thenReturn(Optional.empty());

        // ACT + ASSERT
        assertThrows(ResourceNotFoundException.class,
                () -> parametroService.desactivarParametro(999L));

        // Verificar que NUNCA se guardó nada
        verify(parametroRepository, never()).save(any());
    }
}
