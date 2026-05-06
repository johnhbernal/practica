package co.com.practica.fact.controller;

import co.com.practica.fact.dto.LoginRequestDTO;
import co.com.practica.fact.dto.ResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth", description = "Autenticación de usuarios")
@RestController
@RequestMapping("/auth")
public interface AuthController {

    @Operation(summary = "Iniciar sesión", description = "Valida credenciales y retorna un JWT")
    @PostMapping(value = "/login",
                 consumes = MediaType.APPLICATION_JSON_VALUE,
                 produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<ResponseDTO> login(@RequestBody LoginRequestDTO loginRequest);
}
