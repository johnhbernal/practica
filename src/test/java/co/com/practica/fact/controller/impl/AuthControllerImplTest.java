package co.com.practica.fact.controller.impl;

import co.com.practica.fact.util.JwtValidationUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthControllerImpl.class)
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = {
        "app.auth.username=admin",
        "app.auth.password=admin123"
})
@DisplayName("Pruebas del AuthControllerImpl")
class AuthControllerImplTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JwtValidationUtil jwtValidationUtil;

    @Test
    @DisplayName("login con credenciales válidas retorna 200 y token")
    void login_credencialesValidas_retorna200() throws Exception {
        when(jwtValidationUtil.generarToken("admin")).thenReturn("eyJ.mock.token");

        String body = objectMapper.writeValueAsString(
                Map.of("username", "admin", "password", "admin123"));

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.data.token").value("eyJ.mock.token"))
                .andExpect(jsonPath("$.data.username").value("admin"));
    }

    @Test
    @DisplayName("login con contraseña incorrecta retorna 401")
    void login_passwordIncorrecto_retorna401() throws Exception {
        String body = objectMapper.writeValueAsString(
                Map.of("username", "admin", "password", "wrong"));

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("401"))
                .andExpect(jsonPath("$.description").value("Credenciales inválidas"));
    }

    @Test
    @DisplayName("login con usuario incorrecto retorna 401")
    void login_usuarioIncorrecto_retorna401() throws Exception {
        String body = objectMapper.writeValueAsString(
                Map.of("username", "other", "password", "admin123"));

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("401"))
                .andExpect(jsonPath("$.description").value("Credenciales inválidas"));
    }
}
