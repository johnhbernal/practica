package co.com.practica.fact.controller.impl;

import co.com.practica.fact.constantes.Constantes;
import co.com.practica.fact.controller.ParametroController;
import co.com.practica.fact.dto.ParametroDTO;
import co.com.practica.fact.dto.ResponseDTO;
import co.com.practica.fact.service.ParametroService;
import co.com.practica.fact.util.JwtValidationUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * ============================================================
 * ParametroControllerImpl.java - IMPLEMENTACIÓN DEL CONTROLLER
 *
 * RESPONSABILIDADES DE ESTA CLASE:
 * 1. Validar el token JWT antes de procesar cualquier petición
 * 2. Delegar la lógica al Service
 * 3. Construir la respuesta estándar (ResponseDTO)
 * 4. Manejar errores y retornar el código HTTP correcto
 *
 * PATRÓN: Esta clase NUNCA accede directamente al Repository.
 * La cadena es siempre: Controller → Service → Repository → BD
 *
 * CÓDIGOS HTTP USADOS:
 * - 200 OK: Operación exitosa
 * - 401 UNAUTHORIZED: Token inválido o ausente
 * - 404 NOT_FOUND: Recurso no encontrado
 * - 500 INTERNAL_SERVER_ERROR: Error inesperado del servidor
 * ============================================================
 */
@Log4j2
@RestController
public class ParametroControllerImpl implements ParametroController {

    @Autowired
    private ParametroService parametroService;

    @Autowired
    private JwtValidationUtil jwtValidationUtil;

    /**
     * MÉTODO PRIVADO: validateToken()
     *
     * PATRÓN: Método de validación de seguridad que se reutiliza
     * en TODOS los endpoints. Si el token no es válido, retorna
     * 401 UNAUTHORIZED inmediatamente, sin procesar la petición.
     *
     * @param request Petición HTTP con el header Authorization
     * @return ResponseEntity con error 401, o null si el token es válido
     */
    private ResponseEntity<ResponseDTO> validateToken(HttpServletRequest request) {
        if (!jwtValidationUtil.isValidToken(request)) {
            log.warn("Token inválido o ausente en la petición");
            ResponseDTO response = new ResponseDTO(
                    String.valueOf(HttpStatus.UNAUTHORIZED.value()),
                    Constantes.MSG_UNAUTHORIZED);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        return null; // null = token válido, continúar procesando
    }

    @Override
    public ResponseEntity<ResponseDTO> obtenerParametrosActivos(HttpServletRequest request) {
        log.info("GET /parametros/activos - Iniciando consulta");

        // 1. Validar token
        ResponseEntity<ResponseDTO> tokenValidation = validateToken(request);
        if (tokenValidation != null) return tokenValidation;

        try {
            // 2. Delegar al servicio
            List<ParametroDTO> parametros = parametroService.obtenerParametrosActivos();

            // 3. Construir respuesta exitosa
            ResponseDTO response = new ResponseDTO(
                    String.valueOf(HttpStatus.OK.value()),
                    HttpStatus.OK.name(),
                    parametros);

            log.info("GET /parametros/activos - Retornando {} parámetros", parametros.size());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            // 4. Error inesperado
            log.error("Error en GET /parametros/activos: {}", e.getMessage(), e);
            ResponseDTO errorResponse = new ResponseDTO(
                    String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()),
                    "Error al consultar parámetros: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @Override
    public ResponseEntity<ResponseDTO> obtenerTodos(HttpServletRequest request) {
        log.info("GET /parametros - Consultando todos los parámetros");

        ResponseEntity<ResponseDTO> tokenValidation = validateToken(request);
        if (tokenValidation != null) return tokenValidation;

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
                            e.getMessage()));
        }
    }

    @Override
    public ResponseEntity<ResponseDTO> obtenerPorId(HttpServletRequest request, Long id) {
        log.info("GET /parametros/{} - Buscando parámetro", id);

        ResponseEntity<ResponseDTO> tokenValidation = validateToken(request);
        if (tokenValidation != null) return tokenValidation;

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
                            e.getMessage()));
        }
    }

    @Override
    public ResponseEntity<ResponseDTO> buscarPorNombre(HttpServletRequest request, String nombre) {
        log.info("GET /parametros/buscar?nombre={}", nombre);

        ResponseEntity<ResponseDTO> tokenValidation = validateToken(request);
        if (tokenValidation != null) return tokenValidation;

        try {
            List<ParametroDTO> resultado = parametroService.buscarPorNombre(nombre);
            return ResponseEntity.ok(new ResponseDTO(
                    String.valueOf(HttpStatus.OK.value()),
                    HttpStatus.OK.name(),
                    resultado));
        } catch (Exception e) {
            log.error("Error buscando por nombre '{}': {}", nombre, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO(
                            String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()),
                            e.getMessage()));
        }
    }

    @Override
    public ResponseEntity<ResponseDTO> crearParametro(HttpServletRequest request,
                                                        ParametroDTO parametroDTO) {
        log.info("POST /parametros - Creando parámetro: {}", parametroDTO.getParameterName());

        ResponseEntity<ResponseDTO> tokenValidation = validateToken(request);
        if (tokenValidation != null) return tokenValidation;

        try {
            ParametroDTO creado = parametroService.crearParametro(parametroDTO);
            return ResponseEntity.ok(new ResponseDTO(
                    String.valueOf(HttpStatus.OK.value()),
                    "Parámetro creado exitosamente",
                    creado));
        } catch (Exception e) {
            log.error("Error creando parámetro: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO(
                            String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()),
                            e.getMessage()));
        }
    }

    @Override
    public ResponseEntity<ResponseDTO> actualizarParametro(HttpServletRequest request,
                                                             Long id,
                                                             ParametroDTO parametroDTO) {
        log.info("PUT /parametros/{} - Actualizando", id);

        ResponseEntity<ResponseDTO> tokenValidation = validateToken(request);
        if (tokenValidation != null) return tokenValidation;

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
                            e.getMessage()));
        }
    }

    @Override
    public ResponseEntity<ResponseDTO> desactivarParametro(HttpServletRequest request, Long id) {
        log.info("DELETE /parametros/{} - Desactivando (borrado lógico)", id);

        ResponseEntity<ResponseDTO> tokenValidation = validateToken(request);
        if (tokenValidation != null) return tokenValidation;

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
                            e.getMessage()));
        }
    }
}
