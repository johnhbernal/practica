package co.com.practica.fact.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * ============================================================
 * SecurityConfig.java - CONFIGURACIÓN DE SPRING SECURITY
 *
 * @SecurityScheme: Configura la documentación Swagger para que
 *   muestre el botón "Authorize" y permita enviar el JWT.
 *   type = HTTP con scheme = "bearer" indica que el token va
 *   en el header: Authorization: Bearer <token>
 *
 * @Configuration: Indica que esta clase define beans de Spring.
 *   Los métodos anotados con @Bean crean objetos gestionados por Spring.
 *
 * FLUJO DE SEGURIDAD:
 * 1. La petición llega al servidor
 * 2. Spring Security intercepta ANTES de llegar al Controller
 * 3. Verifica si la ruta está permitida o requiere autenticación
 * 4. En este proyecto: TODAS las rutas están permitidas aquí
 *    porque la validación del JWT la hacemos MANUALMENTE en el Controller
 *    mediante JwtValidationUtil.isValidToken()
 *
 * ¿POR QUÉ HACER LA VALIDACIÓN MANUAL EN EL CONTROLLER?
 * Es el patrón del proyecto base. La alternativa más común es
 * usar un JwtAuthenticationFilter que valide automáticamente,
 * pero este enfoque es más explícito y controlable.
 * ============================================================
 */
@SecurityScheme(
        type = SecuritySchemeType.HTTP,
        name = "BearerTokenAuth",
        scheme = "bearer"
)
@Configuration
public class SecurityConfig {

    /**
     * Configura las reglas de seguridad HTTP.
     *
     * REGLAS CONFIGURADAS:
     * - csrf().disable(): Deshabilitamos CSRF porque usamos JWT (stateless)
     *   CSRF protege contra ataques en aplicaciones con sesiones de servidor.
     *   Con JWT no hay sesión, por eso no aplica.
     *
     * - antMatchers("/**").permitAll(): Permite todas las rutas sin autenticación.
     *   La validación del JWT se hace en el Controller.
     *
     * - anyRequest().authenticated(): Requeriría autenticación para rutas no listadas.
     *   Aquí lo omitimos porque .permitAll() ya cubre todo.
     *
     * - sessionManagement STATELESS: No crear sesiones HTTP.
     *   Cada petición es independiente y se autentica con el token.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf().disable()
                .authorizeRequests()
                    .antMatchers("/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .build();
    }
}
