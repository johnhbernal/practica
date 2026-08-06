package co.com.practica.fact.config;

import co.com.practica.fact.constantes.Constantes;
import co.com.practica.fact.filter.JwtAuthFilter;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Security configuration aligned with ms-auth session JWTs.
 * Use {@code @PreAuthorize("hasRole('ADMIN')")} — authorities are ROLE_*.
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

    @Value("${spring.h2.console.enabled:false}")
    private boolean h2ConsoleEnabled;

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(Arrays.asList(allowedOrigins.split(",\\s*")));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept"));
        config.setAllowCredentials(false);
        config.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        List<String> permitAll = new ArrayList<String>(Arrays.asList(
                "/actuator/health",
                "/actuator/health/**",
                "/swagger-ui/**",
                "/swagger-ui.html",
                "/v3/api-docs/**",
                "/error"
        ));
        if (h2ConsoleEnabled) {
            permitAll.add("/h2-console/**");
        }

        http.cors().and()
                .csrf().disable();

        if (h2ConsoleEnabled) {
            http.headers().frameOptions().sameOrigin();
        } else {
            http.headers().frameOptions().deny();
        }

        http.headers()
                .contentTypeOptions().and()
                .xssProtection().and()
                .httpStrictTransportSecurity()
                    .includeSubDomains(true)
                    .maxAgeInSeconds(31536000);

        return http
                .authorizeRequests()
                    .antMatchers(permitAll.toArray(new String[0])).permitAll()
                    .antMatchers("/parametros/**").authenticated()
                    .anyRequest().denyAll()
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
                    .accessDeniedHandler((req, res, ex) -> {
                        res.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        res.setContentType(MediaType.APPLICATION_JSON_VALUE);
                        res.getWriter().write(
                            "{\"code\":\"403\",\"description\":\"" + Constantes.MSG_FORBIDDEN + "\"}");
                    })
                .and()
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
