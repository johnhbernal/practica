package co.com.practica.fact.service;

import co.com.practica.fact.dto.ParametroDTO;

import java.util.List;

/**
 * ============================================================
 * ParametroService.java - INTERFAZ DE SERVICIO
 *
 * PATRÓN: Interface + Implementation (Separación de contrato e implementación)
 *
 * ¿POR QUÉ USAR UNA INTERFAZ EN LUGAR DE UNA CLASE DIRECTAMENTE?
 *
 * 1. PRINCIPIO DE INVERSIÓN DE DEPENDENCIAS (SOLID - D):
 *    Los controllers dependen de la INTERFAZ (abstracción),
 *    no de la implementación concreta. Esto facilita:
 *    - Cambiar la implementación sin modificar el controller
 *    - Crear implementaciones alternativas (ej: para testing)
 *
 * 2. TESTABILIDAD:
 *    En los tests podemos inyectar un Mock de la interfaz
 *    en lugar de la implementación real, aislando las pruebas.
 *
 * 3. DOCUMENTACIÓN:
 *    La interfaz sirve como contrato: documenta qué puede hacer
 *    el servicio sin revelar CÓMO lo hace.
 *
 * FLUJO DE DEPENDENCIAS (inyección):
 *   ParametroController
 *        ↓ @Autowired ParametroService (interfaz)
 *        ↓ Spring inyecta ParametroServiceImpl (implementación)
 *   ParametroServiceImpl
 *        ↓ @Autowired ParametroRepository
 *        ↓ Spring inyecta el repositorio generado
 * ============================================================
 */
public interface ParametroService {

    /**
     * Retorna todos los parámetros activos.
     *
     * @return Lista de DTOs de parámetros con estado "A"
     */
    List<ParametroDTO> obtenerParametrosActivos();

    /**
     * Retorna TODOS los parámetros sin importar el estado.
     *
     * @return Lista completa de parámetros
     */
    List<ParametroDTO> obtenerTodosLosParametros();

    /**
     * Busca un parámetro por su ID.
     *
     * @param id Identificador único del parámetro
     * @return DTO del parámetro encontrado
     * @throws co.com.practica.fact.exception.ResourceNotFoundException si no existe
     */
    ParametroDTO obtenerParametroPorId(Long id);

    /**
     * Busca parámetros por nombre (búsqueda parcial, no sensible a mayúsculas).
     *
     * @param nombre Texto a buscar en el nombre del parámetro
     * @return Lista de parámetros que contienen el texto en su nombre
     */
    List<ParametroDTO> buscarPorNombre(String nombre);

    /**
     * Busca todos los parámetros de una categoría específica.
     *
     * @param categoria Categoría a filtrar (SISTEMA, NEGOCIO, etc.)
     * @return Lista de parámetros de esa categoría
     */
    List<ParametroDTO> obtenerPorCategoria(String categoria);

    /**
     * Crea un nuevo parámetro en la base de datos.
     *
     * @param parametroDTO DTO con los datos del nuevo parámetro
     * @return DTO del parámetro creado (con el ID generado)
     */
    ParametroDTO crearParametro(ParametroDTO parametroDTO);

    /**
     * Actualiza un parámetro existente.
     *
     * @param id ID del parámetro a actualizar
     * @param parametroDTO DTO con los nuevos valores
     * @return DTO del parámetro actualizado
     * @throws co.com.practica.fact.exception.ResourceNotFoundException si no existe
     */
    ParametroDTO actualizarParametro(Long id, ParametroDTO parametroDTO);

    /**
     * Desactiva un parámetro (borrado lógico: cambia estado a "I").
     * NUNCA borra físicamente el registro de la BD.
     *
     * @param id ID del parámetro a desactivar
     */
    void desactivarParametro(Long id);
}
