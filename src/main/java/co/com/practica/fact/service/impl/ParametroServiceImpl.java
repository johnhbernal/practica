package co.com.practica.fact.service.impl;

import co.com.practica.fact.constantes.Constantes;
import co.com.practica.fact.dto.ParametroDTO;
import co.com.practica.fact.entity.Parametro;
import co.com.practica.fact.exception.ResourceNotFoundException;
import co.com.practica.fact.mappers.ParametroMapper;
import co.com.practica.fact.repository.ParametroRepository;
import co.com.practica.fact.service.ParametroService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * ============================================================
 * ParametroServiceImpl.java - IMPLEMENTACIÓN DEL SERVICIO
 *
 * ANOTACIONES CLAVE:
 *
 * @Service: Marca esta clase como un componente de CAPA DE NEGOCIO.
 *   Spring la registra en el contexto y permite inyectarla.
 *   Funcionalmente igual a @Component, pero semánticamente más
 *   descriptivo (indica que contiene lógica de negocio).
 *
 * @Log4j2: Inyecta un logger Log4j2. Equivalente a escribir:
 *   private static final Logger log = LogManager.getLogger(ParametroServiceImpl.class);
 *
 * @Transactional: Envuelve los métodos en una transacción de BD.
 *   Si algo falla, hace ROLLBACK automático.
 *   - readOnly=true: Optimización para consultas (no genera snapshots)
 *   - Sin readOnly: Para operaciones de escritura (INSERT, UPDATE, DELETE)
 *
 * @Autowired: Inyección de dependencias. Spring busca un bean del tipo
 *   declarado y lo inyecta automáticamente.
 *
 * PATRÓN: Toda la LÓGICA DE NEGOCIO va en el Service.
 * El Controller solo recibe/valida el request y delega al Service.
 * El Service delega el acceso a BD al Repository.
 * ============================================================
 */
@Log4j2
@Service
public class ParametroServiceImpl implements ParametroService {

    /**
     * Inyección del repositorio.
     * Spring inyecta la implementación generada automáticamente.
     *
     * ALTERNATIVA MODERNA: Usar inyección por constructor (más testeable):
     * public ParametroServiceImpl(ParametroRepository repo, ParametroMapper mapper) {
     *     this.parametroRepository = repo;
     *     this.parametroMapper = mapper;
     * }
     */
    @Autowired
    private ParametroRepository parametroRepository;

    @Autowired
    private ParametroMapper parametroMapper;

    /**
     * @Transactional(readOnly = true): Optimización para consultas.
     * Spring informa a Hibernate que no habrá escrituras,
     * lo que mejora el rendimiento en escenarios de alta concurrencia.
     */
    @Override
    @Transactional(readOnly = true)
    public List<ParametroDTO> obtenerParametrosActivos() {
        log.info("Consultando parámetros activos");

        // 1. Consulta a BD mediante el repository
        List<Parametro> parametros = parametroRepository.findByEstado(Constantes.ESTADO_ACTIVO);

        log.info("Se encontraron {} parámetros activos", parametros.size());

        // 2. Convierte List<Parametro> → List<ParametroDTO> usando MapStruct
        return parametroMapper.toDTOList(parametros);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParametroDTO> obtenerTodosLosParametros() {
        log.info("Consultando todos los parámetros");
        // findAll() viene heredado de JpaRepository
        return parametroMapper.toDTOList(parametroRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public ParametroDTO obtenerParametroPorId(Long id) {
        log.info("Buscando parámetro con ID: {}", id);

        // findById retorna Optional<Parametro>
        // orElseThrow: Si no existe, lanza ResourceNotFoundException
        // Esta excepción es capturada por ServicesException (handler global)
        Parametro parametro = parametroRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Parámetro con ID {} no encontrado", id);
                    return new ResourceNotFoundException(
                            "Parámetro no encontrado con ID: " + id);
                });

        return parametroMapper.toDTO(parametro);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParametroDTO> buscarPorNombre(String nombre) {
        log.info("Búsqueda de parámetros por nombre");
        List<Parametro> resultado = parametroRepository
                .findByNombreParametroContainingIgnoreCase(nombre);
        log.info("Búsqueda completada: {} resultado(s)", resultado.size());
        return parametroMapper.toDTOList(resultado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParametroDTO> obtenerPorCategoria(String categoria) {
        log.info("Buscando parámetros de categoría: {}", categoria);
        return parametroMapper.toDTOList(
                parametroRepository.findByCategoria(categoria));
    }

    /**
     * Creación de un nuevo parámetro.
     * Sin readOnly porque escribe en BD.
     *
     * FLUJO:
     * 1. DTO → Entity (con mapper)
     * 2. Asignar valores de auditoría (fechas, usuario)
     * 3. Guardar en BD (repository.save)
     * 4. Entity guardada → DTO (con el ID generado)
     * 5. Retornar DTO
     */
    @Override
    @Transactional
    public ParametroDTO crearParametro(ParametroDTO parametroDTO) {
        log.info("Creando nuevo parámetro: {}", parametroDTO.getParameterName());

        // 1. Convertir DTO a Entity
        Parametro parametro = parametroMapper.toEntity(parametroDTO);

        // 2. Asignar valores de auditoría
        Date ahora = new Date();
        parametro.setFechaCreacion(ahora);
        parametro.setFechaModificacion(ahora);
        parametro.setEstado(Constantes.ESTADO_ACTIVO);

        String currentUser = currentUsername();
        parametro.setUsuarioCreacion(currentUser);
        parametro.setUsuarioModificacion(currentUser);

        // 3. Guardar en BD. save() hace INSERT si el ID es null, UPDATE si tiene ID
        Parametro guardado = parametroRepository.save(parametro);

        log.info("Parámetro creado con ID: {}", guardado.getCodParametro());

        // 4. Retornar el DTO del objeto guardado (ya tiene el ID generado por BD)
        return parametroMapper.toDTO(guardado);
    }

    @Override
    @Transactional
    public ParametroDTO actualizarParametro(Long id, ParametroDTO parametroDTO) {
        log.info("Actualizando parámetro con ID: {}", id);

        // Verificar que exista antes de actualizar
        Parametro existente = parametroRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se puede actualizar. Parámetro no encontrado con ID: " + id));

        // Actualizar solo los campos editables (no tocamos auditoría de creación)
        existente.setNombreParametro(parametroDTO.getParameterName());
        existente.setCategoria(parametroDTO.getParameterCategory());
        existente.setValor(parametroDTO.getValue());
        existente.setDescripcion(parametroDTO.getDescription());
        existente.setFechaModificacion(new Date());
        existente.setUsuarioModificacion(currentUsername());

        Parametro actualizado = parametroRepository.save(existente);
        log.info("Parámetro {} actualizado exitosamente", id);

        return parametroMapper.toDTO(actualizado);
    }

    @Override
    @Transactional
    public void desactivarParametro(Long id) {
        log.info("Desactivando parámetro con ID: {}", id);

        Parametro parametro = parametroRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se puede desactivar. Parámetro no encontrado con ID: " + id));

        // BORRADO LÓGICO: Solo cambiamos el estado a "I"
        // NUNCA llamamos parametroRepository.delete()
        parametro.setEstado(Constantes.ESTADO_INACTIVO);
        parametro.setFechaModificacion(new Date());
        parametroRepository.save(parametro);

        log.info("Parámetro {} desactivado exitosamente", id);
    }

    private String currentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (auth != null && auth.isAuthenticated()) ? auth.getName() : "SISTEMA";
    }
}
