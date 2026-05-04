package co.com.practica.fact.repository;

import co.com.practica.fact.entity.Parametro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * ============================================================
 * ParametroRepository.java - CAPA DE ACCESO A DATOS (DAO)
 *
 * CONCEPTO DE REPOSITORY (Spring Data JPA):
 * Al extender JpaRepository, Spring genera AUTOMÁTICAMENTE la
 * implementación de los métodos CRUD y consultas básicas.
 * NO necesitamos escribir SQL ni implementar los métodos nosotros.
 *
 * JpaRepository<Parametro, Long> recibe:
 *   - Parametro: La entidad que maneja este repositorio
 *   - Long: El tipo de la clave primaria
 *
 * MÉTODOS HEREDADOS DE JPAREPOSITORY (gratis):
 * - save(entity)           → INSERT o UPDATE
 * - findById(id)           → SELECT WHERE id = ?
 * - findAll()              → SELECT * FROM tabla
 * - deleteById(id)         → DELETE WHERE id = ?
 * - count()                → SELECT COUNT(*)
 * - existsById(id)         → Verifica si existe
 *
 * QUERY METHODS (Spring los genera por el nombre del método):
 * Spring Data JPA lee el nombre del método y genera el SQL.
 *
 * Convenciones de nomenclatura:
 * - findBy[Campo]          → WHERE campo = ?
 * - findBy[Campo]Contains  → WHERE campo LIKE '%?%'
 * - findBy[C1]And[C2]      → WHERE c1 = ? AND c2 = ?
 * - findBy[C1]Or[C2]       → WHERE c1 = ? OR c2 = ?
 * - findBy[Campo]OrderBy[OtroCampo]Asc → ORDER BY otro_campo ASC
 * ============================================================
 */
@Repository  // Indica que esta interfaz es un componente de acceso a datos
public interface ParametroRepository extends JpaRepository<Parametro, Long> {

    /**
     * Busca todos los parámetros con un estado específico.
     * SQL generado: SELECT * FROM PARAMETROS WHERE ESTADO = ?
     *
     * @param estado "A" para activos, "I" para inactivos
     * @return Lista de parámetros con ese estado
     */
    List<Parametro> findByEstado(String estado);

    /**
     * Busca parámetros por nombre exacto.
     * SQL generado: SELECT * FROM PARAMETROS WHERE NOMBRE_PARAMETRO = ?
     *
     * @param nombreParametro Nombre exacto a buscar
     * @return Lista de coincidencias (puede haber varios con el mismo nombre)
     */
    List<Parametro> findByNombreParametro(String nombreParametro);

    /**
     * Busca parámetros por categoría.
     * SQL generado: SELECT * FROM PARAMETROS WHERE CATEGORIA = ?
     *
     * @param categoria Categoría del parámetro (SISTEMA, NEGOCIO, etc.)
     * @return Lista de parámetros de esa categoría
     */
    List<Parametro> findByCategoria(String categoria);

    /**
     * Busca parámetros cuyo nombre contiene el texto dado.
     * SQL generado: SELECT * FROM PARAMETROS WHERE NOMBRE_PARAMETRO LIKE '%?%'
     *
     * Útil para búsquedas tipo "autocompletar".
     *
     * @param texto Texto a buscar dentro del nombre
     * @return Lista de parámetros que contienen el texto
     */
    List<Parametro> findByNombreParametroContainingIgnoreCase(String texto);

    /**
     * Busca un parámetro por su valor exacto.
     * Optional<T>: Puede devolver el objeto o estar vacío (no null).
     * Evita NullPointerException al obligar a verificar si hay resultado.
     *
     * Uso:
     *   Optional<Parametro> p = repo.findByValor("valor");
     *   p.ifPresent(param -> log.info("Encontrado: {}", param.getNombreParametro()));
     *   Parametro param = p.orElseThrow(() -> new NotFoundException("No existe"));
     *
     * @param valor Valor exacto a buscar
     * @return Optional con el parámetro, o vacío si no existe
     */
    Optional<Parametro> findByValor(String valor);

    /**
     * Busca parámetros por categoría y estado.
     * SQL generado: SELECT * FROM PARAMETROS WHERE CATEGORIA = ? AND ESTADO = ?
     *
     * @param categoria Categoría a filtrar
     * @param estado Estado a filtrar
     * @return Lista filtrada
     */
    List<Parametro> findByCategoriaAndEstado(String categoria, String estado);
}
