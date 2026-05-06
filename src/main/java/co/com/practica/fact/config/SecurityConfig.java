package co.com.practica.fact.config;

import co.com.practica.fact.filter.JwtAuthFilter;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

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
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf().disable()
                .headers().frameOptions().disable()  // H2 console uses iframes
                .and()
                .authorizeRequests()
                    .antMatchers(
                        "/h2-console/**",
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
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
