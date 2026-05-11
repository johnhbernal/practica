package co.com.practica.fact.controller;

import co.com.practica.fact.dto.ParametroDTO;
import co.com.practica.fact.dto.ResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * ============================================================
 * ParametroController.java - INTERFAZ DEL CONTROLADOR
 *
 * CONCEPTO: El controller es la PUERTA DE ENTRADA al microservicio.
 * Recibe las peticiones HTTP, valida los datos de entrada,
 * delega la lógica al Service y retorna la respuesta.
 *
 * ¿POR QUÉ SEPARAR INTERFAZ E IMPLEMENTACIÓN?
 * - La interfaz documenta los endpoints (contrato de la API)
 * - La implementación tiene la lógica
 * - Más limpio y fácil de navegar
 *
 * ANOTACIONES REST:
 * @RestController = @Controller + @ResponseBody
 *   Indica que todos los métodos retornan datos (JSON) directamente,
 *   no vistas HTML.
 *
 * @RequestMapping: Define la ruta base de TODOS los endpoints de este controller.
 *   Si definimos /parametros aquí, los métodos agregan sub-rutas:
 *   /parametros + /activos = GET /parametros/activos
 *
 * @Tag (Swagger): Agrupa los endpoints en la documentación Swagger UI
 *
 * HTTP METHODS:
 * - GET    → Consultar/leer datos (sin efectos secundarios)
 * - POST   → Crear un nuevo recurso
 * - PUT    → Actualizar un recurso completo
 * - PATCH  → Actualizar parcialmente un recurso
 * - DELETE → Eliminar un recurso
 * ============================================================
 */
@Tag(name = "Parámetros", description = "API para gestión de parámetros del sistema")
@SecurityRequirement(name = "BearerTokenAuth")
@RestController
@RequestMapping("/")
public interface ParametroController {

    /**
     * Endpoint: GET /parametros/activos
     * Retorna todos los parámetros con estado "A" (activos).
     *
     * @Operation: Documenta el endpoint en Swagger
     * @return ResponseEntity con lista de parámetros activos
     */
    @Operation(summary = "Obtener parámetros activos",
               description = "Retorna todos los parámetros con estado Activo")
    @GetMapping(value = "/parametros/activos", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<ResponseDTO> obtenerParametrosActivos();

    /**
     * Endpoint: GET /parametros
     * Retorna todos los parámetros (activos e inactivos).
     */
    @Operation(summary = "Obtener todos los parámetros (activos e inactivos)")
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping(value = "/parametros", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<ResponseDTO> obtenerTodos();

    /**
     * Endpoint: GET /parametros/{id}
     * Retorna un parámetro por su ID.
     *
     * @PathVariable: Extrae el valor de la URL. GET /parametros/42 → id = 42
     */
    @Operation(summary = "Buscar parámetro por ID")
    @GetMapping(value = "/parametros/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<ResponseDTO> obtenerPorId(@PathVariable Long id);

    /**
     * Endpoint: GET /parametros/buscar?nombre=texto
     * Búsqueda por nombre (parcial, case-insensitive).
     *
     * @RequestParam: Extrae parámetros de la query string.
     *   GET /parametros/buscar?nombre=CONFIG → nombre = "CONFIG"
     */
    @Operation(summary = "Buscar parámetros por nombre")
    @GetMapping(value = "/parametros/buscar", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<ResponseDTO> buscarPorNombre(@RequestParam String nombre);

    /**
     * Endpoint: POST /parametros
     * Crea un nuevo parámetro.
     *
     * @RequestBody: Deserializa el JSON del body a ParametroDTO
     * @Valid: Activa las validaciones (@NotBlank, @Size, etc.) del DTO
     *   Si alguna validación falla, Spring lanza MethodArgumentNotValidException
     *   que es capturada por ServicesException.
     *
     * BUENA PRÁCTICA: Los endpoints de creación retornan el recurso creado
     * con código HTTP 201 (Created), no 200 (OK).
     * Aquí retornamos 200 por consistencia con el patrón del proyecto base.
     */
    @Operation(summary = "Crear nuevo parámetro")
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping(value = "/parametros",
                 consumes = MediaType.APPLICATION_JSON_VALUE,
                 produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<ResponseDTO> crearParametro(@Valid @RequestBody ParametroDTO parametroDTO);

    /**
     * Endpoint: PUT /parametros/{id}
     * Actualiza un parámetro existente.
     */
    @Operation(summary = "Actualizar parámetro existente")
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping(value = "/parametros/{id}",
                consumes = MediaType.APPLICATION_JSON_VALUE,
                produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<ResponseDTO> actualizarParametro(@PathVariable Long id,
                                                     @Valid @RequestBody ParametroDTO parametroDTO);

    /**
     * Endpoint: DELETE /parametros/{id}
     * Desactiva un parámetro (borrado lógico).
     */
    @Operation(summary = "Desactivar parámetro (borrado lógico)")
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping(value = "/parametros/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<ResponseDTO> desactivarParametro(@PathVariable Long id);
}
