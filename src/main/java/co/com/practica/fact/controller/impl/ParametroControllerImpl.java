package co.com.practica.fact.controller.impl;

import co.com.practica.fact.constantes.Constantes;
import co.com.practica.fact.controller.ParametroController;
import co.com.practica.fact.dto.ParametroDTO;
import co.com.practica.fact.dto.ResponseDTO;
import co.com.practica.fact.service.ParametroService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * ============================================================
 * ParametroControllerImpl.java - IMPLEMENTACIÓN DEL CONTROLLER
 *
 * RESPONSABILIDADES DE ESTA CLASE:
 * 1. Delegar la lógica al Service (JWT validado por JwtAuthFilter antes de llegar aquí)
 * 2. Construir la respuesta estándar (ResponseDTO)
 * 3. Manejar errores y retornar el código HTTP correcto
 *
 * PATRÓN: Esta clase NUNCA accede directamente al Repository.
 * La cadena es siempre: Controller → Service → Repository → BD
 *
 * CÓDIGOS HTTP USADOS:
 * - 200 OK: Operación exitosa
 * - 404 NOT_FOUND: Recurso no encontrado
 * - 500 INTERNAL_SERVER_ERROR: Error inesperado del servidor
 * ============================================================
 */
@Log4j2
@RestController
public class ParametroControllerImpl implements ParametroController {

    @Autowired
    private ParametroService parametroService;

    @Override
    public ResponseEntity<ResponseDTO> obtenerParametrosActivos() {
        log.info("GET /parametros/activos - Iniciando consulta");

        try {
            // Delegar al servicio
            List<ParametroDTO> parametros = parametroService.obtenerParametrosActivos();

            // 3. Construir respuesta exitosa
            log.info("GET /parametros/activos - Retornando {} parámetros", parametros.size());
            return ResponseEntity.ok(new ResponseDTO(
                    String.valueOf(HttpStatus.OK.value()),
                    HttpStatus.OK.name(),
                    parametros));

        } catch (Exception e) {
            // 4. Error inesperado
            log.error("Error en GET /parametros/activos: {}", e.getMessage(), e);
            ResponseDTO errorResponse = new ResponseDTO(
                    String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()),
                    Constantes.MSG_FAIL);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @Override
    public ResponseEntity<ResponseDTO> obtenerTodos() {
        log.info("GET /parametros - Consultando todos los parámetros");

        try {
            List<ParametroDTO> parametros = parametroService.obtenerTodosLosParametros();
            return ResponseEntity.ok(new ResponseDTO(
                    String.valueOf(HttpStatus.OK.value()),
                    HttpStatus.OK.name(),
                    parametros));
        } catch (Exception e) {
            log.error("Error en GET /parametros: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO(
                            String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()),
                            Constantes.MSG_FAIL));
        }
    }

    @Override
    public ResponseEntity<ResponseDTO> obtenerPorId(Long id) {
        log.info("GET /parametros/{} - Buscando parámetro", id);

        try {
            ParametroDTO parametro = parametroService.obtenerParametroPorId(id);
            return ResponseEntity.ok(new ResponseDTO(
                    String.valueOf(HttpStatus.OK.value()),
                    HttpStatus.OK.name(),
                    parametro));
        } catch (co.com.practica.fact.exception.ResourceNotFoundException e) {
            // ResourceNotFoundException → 404 NOT FOUND
            log.warn("Parámetro {} no encontrado", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseDTO(
                            String.valueOf(HttpStatus.NOT_FOUND.value()),
                            e.getMessage()));
        } catch (Exception e) {
            log.error("Error buscando parámetro {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO(
                            String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()),
                            Constantes.MSG_FAIL));
        }
    }

    @Override
    public ResponseEntity<ResponseDTO> buscarPorNombre(String nombre) {
        log.info("GET /parametros/buscar");

        if (nombre == null || nombre.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new ResponseDTO("400", "El término de búsqueda es obligatorio"));
        }
        if (nombre.length() > 50) {
            return ResponseEntity.badRequest()
                    .body(new ResponseDTO("400", "El término de búsqueda no puede superar 50 caracteres"));
        }
        if (!nombre.matches(co.com.practica.fact.constantes.Constantes.NOMBRE_PARAMETRO_PATTERN)) {
            return ResponseEntity.badRequest()
                    .body(new ResponseDTO("400", "El término de búsqueda contiene caracteres no permitidos"));
        }

        try {
            List<ParametroDTO> resultado = parametroService.buscarPorNombre(nombre);
            return ResponseEntity.ok(new ResponseDTO(
                    String.valueOf(HttpStatus.OK.value()),
                    HttpStatus.OK.name(),
                    resultado));
        } catch (Exception e) {
            log.error("Error en búsqueda por nombre: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO(
                            String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()),
                            Constantes.MSG_FAIL));
        }
    }

    @Override
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ResponseDTO> crearParametro(ParametroDTO parametroDTO) {
        log.info("POST /parametros - Creando parámetro: {}", parametroDTO.getParameterName());

        try {
            ParametroDTO creado = parametroService.crearParametro(parametroDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseDTO(
                    String.valueOf(HttpStatus.CREATED.value()),
                    "Parámetro creado exitosamente",
                    creado));
        } catch (Exception e) {
            log.error("Error creando parámetro: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO(
                            String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()),
                            Constantes.MSG_FAIL));
        }
    }

    @Override
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ResponseDTO> actualizarParametro(Long id, ParametroDTO parametroDTO) {
        log.info("PUT /parametros/{} - Actualizando", id);

        try {
            ParametroDTO actualizado = parametroService.actualizarParametro(id, parametroDTO);
            return ResponseEntity.ok(new ResponseDTO(
                    String.valueOf(HttpStatus.OK.value()),
                    "Parámetro actualizado exitosamente",
                    actualizado));
        } catch (co.com.practica.fact.exception.ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseDTO(
                            String.valueOf(HttpStatus.NOT_FOUND.value()),
                            e.getMessage()));
        } catch (Exception e) {
            log.error("Error actualizando parámetro {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO(
                            String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()),
                            Constantes.MSG_FAIL));
        }
    }

    @Override
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ResponseDTO> desactivarParametro(Long id) {
        log.info("DELETE /parametros/{} - Desactivando (borrado lógico)", id);

        try {
            parametroService.desactivarParametro(id);
            return ResponseEntity.ok(new ResponseDTO(
                    String.valueOf(HttpStatus.OK.value()),
                    "Parámetro desactivado exitosamente"));
        } catch (co.com.practica.fact.exception.ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseDTO(
                            String.valueOf(HttpStatus.NOT_FOUND.value()),
                            e.getMessage()));
        } catch (Exception e) {
            log.error("Error desactivando parámetro {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO(
                            String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()),
                            Constantes.MSG_FAIL));
        }
    }
}
