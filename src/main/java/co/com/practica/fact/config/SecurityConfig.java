package co.com.practica.fact.config;

import co.com.practica.fact.filter.JwtAuthFilter;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import co.com.practica.fact.constantes.Constantes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

/**
 * ============================================================
 * SecurityConfig.java - CONFIGURACIÓN DE SPRING SECURITY
 *
 * FLUJO DE SEGURIDAD:
 * 1. La petición llega al servidor
 * 2. JwtAuthFilter valida el token ANTES de UsernamePasswordAuthenticationFilter
 * 3. Si el token es válido, setea el contexto de autenticación
 * 4. Spring Security verifica si la ruta requiere autenticación
 *
 * RUTAS ABIERTAS: /h2-console, /swagger-ui, /v3/api-docs, /error
 * RUTAS PROTEGIDAS: /parametros/** → requieren JWT válido
 * ============================================================
 */
@SecurityScheme(
        type = SecuritySchemeType.HTTP,
        name = "BearerTokenAuth",
        scheme = "bearer"
)
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Value("${app.cors.allowed-origins:http://localhost:3000}")
    private String allowedOrigins;

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(Arrays.asList(allowedOrigins.split(",\\s*")));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept"));
        config.setAllowCredentials(false);
        config.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors().and()
                .csrf().disable()
                .headers()
                    .frameOptions().deny()
                    .contentTypeOptions().and()
                    .xssProtection().and()
                    .httpStrictTransportSecurity()
                        .includeSubDomains(true)
                        .maxAgeInSeconds(31536000)
                    .and()
                .and()
                .authorizeRequests()
                    .antMatchers(
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/v3/api-docs/**",
                        "/error"
                    ).permitAll()
                    .antMatchers("/parametros/**").authenticated()
                .and()
                .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .exceptionHandling()
                    .authenticationEntryPoint((req, res, ex) -> {
                        res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        res.setContentType(MediaType.APPLICATION_JSON_VALUE);
                        res.getWriter().write(
                            "{\"code\":\"401\",\"description\":\"" + Constantes.MSG_UNAUTHORIZED + "\"}");
                    })
                .and()
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
