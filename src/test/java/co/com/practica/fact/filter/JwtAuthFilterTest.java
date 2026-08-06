package co.com.practica.fact.filter;

import co.com.practica.fact.constantes.Constantes;
import co.com.practica.fact.util.JwtValidationUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas del JwtAuthFilter")
class JwtAuthFilterTest {

    @Mock
    private JwtValidationUtil jwtValidationUtil;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthFilter jwtAuthFilter;

    @AfterEach
    void limpiarContexto() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Token válido: debe setear autenticación con ROLE_ prefix y continuar")
    void tokenValido_debeAutenticarYContinuar() throws Exception {
        String rawToken = "eyJhbGciOiJIUzI1NiJ9.payload.signature";
        when(jwtValidationUtil.isValidToken(request)).thenReturn(true);
        when(request.getHeader(Constantes.AUTHORIZATION_HEADER))
                .thenReturn(Constantes.BEARER_PREFIX + rawToken);
        when(jwtValidationUtil.extraerSubject(rawToken)).thenReturn("usuario@test.com");
        when(jwtValidationUtil.extractRole(rawToken)).thenReturn("ADMIN");

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth, "Debe haber autenticación en el contexto");
        assertEquals("usuario@test.com", auth.getPrincipal());
        assertTrue(auth.isAuthenticated());
        assertTrue(auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("Rol ya con ROLE_ prefix: no duplicar prefijo")
    void roleConPrefijo_noDuplica() {
        assertEquals("ROLE_ADMIN",
                JwtAuthFilter.toAuthorities("ROLE_ADMIN").get(0).getAuthority());
        assertEquals("ROLE_ADMIN",
                JwtAuthFilter.toAuthorities("ADMIN").get(0).getAuthority());
    }

    @Test
    @DisplayName("Token inválido: no debe autenticar, pero sí continuar la cadena")
    void tokenInvalido_noDebeAutenticarPeroSiContinuar() throws Exception {
        when(jwtValidationUtil.isValidToken(request)).thenReturn(false);

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication(),
                "No debe haber autenticación con token inválido");
        verify(filterChain, times(1)).doFilter(request, response);
        verify(jwtValidationUtil, never()).extraerSubject(anyString());
    }

    @Test
    @DisplayName("Sin header Authorization: no debe autenticar, pero sí continuar la cadena")
    void sinHeader_noDebeAutenticarPeroSiContinuar() throws Exception {
        when(jwtValidationUtil.isValidToken(request)).thenReturn(false);

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain, times(1)).doFilter(request, response);
        verify(jwtValidationUtil, never()).extraerSubject(anyString());
    }
}
