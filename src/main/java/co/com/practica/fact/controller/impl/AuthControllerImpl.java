package co.com.practica.fact.controller.impl;

import co.com.practica.fact.controller.AuthController;
import co.com.practica.fact.dto.LoginRequestDTO;
import co.com.practica.fact.dto.LoginResponseDTO;
import co.com.practica.fact.dto.ResponseDTO;
import co.com.practica.fact.util.JwtValidationUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
public class AuthControllerImpl implements AuthController {

    @Value("${app.auth.username}")
    private String authUsername;

    @Value("${app.auth.password}")
    private String authPassword;

    private final JwtValidationUtil jwtValidationUtil;

    public AuthControllerImpl(JwtValidationUtil jwtValidationUtil) {
        this.jwtValidationUtil = jwtValidationUtil;
    }

    @Override
    public ResponseEntity<ResponseDTO> login(LoginRequestDTO loginRequest) {
        log.info("Intento de login para usuario: {}", loginRequest.getUsername());

        if (!authUsername.equals(loginRequest.getUsername())
                || !authPassword.equals(loginRequest.getPassword())) {
            log.warn("Credenciales inválidas para usuario: {}", loginRequest.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ResponseDTO("401", "Credenciales inválidas"));
        }

        String token = jwtValidationUtil.generarToken(loginRequest.getUsername());
        LoginResponseDTO loginResponse = new LoginResponseDTO(token, loginRequest.getUsername());
        log.info("Login exitoso para usuario: {}", loginRequest.getUsername());
        return ResponseEntity.ok(new ResponseDTO("200", "OK", loginResponse));
    }
}
