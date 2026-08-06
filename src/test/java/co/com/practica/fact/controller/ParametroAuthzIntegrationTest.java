package co.com.practica.fact.controller;

import co.com.practica.fact.constantes.Constantes;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * AuthZ with filters ON — JWT shaped like current ms-auth (role + roles/permissions/groups).
 * practica ignores fine-grained permissions and uses primary role only.
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Parametro AuthZ integration (ms-auth JWT shape)")
class ParametroAuthzIntegrationTest {

    private static final String SESSION_SECRET = "test-session-secret-key-at-least-32-chars!!";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void sellerShapedJwt_canReadActivos_butCannotListAll() throws Exception {
        String token = msAuthShapedToken("seller", "VENDEDOR",
                List.of("VENDEDOR"),
                List.of("INVENTARIO_PRECIO_READ"),
                List.of("CN=Sales,OU=Practica,DC=demo,DC=local"));

        mockMvc.perform(get("/parametros/activos")
                        .header(Constantes.AUTHORIZATION_HEADER, Constantes.BEARER_PREFIX + token))
                .andExpect(status().isOk());

        mockMvc.perform(get("/parametros")
                        .header(Constantes.AUTHORIZATION_HEADER, Constantes.BEARER_PREFIX + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminShapedJwt_canCreateParametro() throws Exception {
        String token = msAuthShapedToken("admin", "ADMIN",
                List.of("ADMIN"),
                List.of("GROUP_ADMIN", "USER_ADMIN", "INVENTARIO_PRECIO_WRITE"),
                List.of("CN=Admins,OU=Practica,DC=demo,DC=local"));

        String body = "{"
                + "\"parameterName\":\"AUTHZ_IT_PARAM\","
                + "\"parameterCategory\":\"TEST\","
                + "\"value\":\"1\","
                + "\"status\":\"A\""
                + "}";

        mockMvc.perform(post("/parametros")
                        .header(Constantes.AUTHORIZATION_HEADER, Constantes.BEARER_PREFIX + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());
    }

    @Test
    void userShapedJwt_cannotCreateParametro() throws Exception {
        String token = msAuthShapedToken("user", "USER",
                List.of("USER"),
                List.of(),
                List.of());

        String body = "{"
                + "\"parameterName\":\"SHOULD_FAIL\","
                + "\"parameterCategory\":\"TEST\","
                + "\"value\":\"1\","
                + "\"status\":\"A\""
                + "}";

        mockMvc.perform(post("/parametros")
                        .header(Constantes.AUTHORIZATION_HEADER, Constantes.BEARER_PREFIX + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isForbidden());
    }

    private static String msAuthShapedToken(String sub, String role,
                                            List<String> roles, List<String> permissions, List<String> groups) {
        SecretKeySpec key = new SecretKeySpec(SESSION_SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        return Jwts.builder()
                .setSubject(sub)
                .claim(Constantes.CLAIM_ROLE, role)
                .claim(Constantes.CLAIM_TOKEN_TYPE, Constantes.TOKEN_TYPE_SESSION)
                .claim("roles", roles)
                .claim("permissions", permissions)
                .claim("groups", groups)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3_600_000))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}
