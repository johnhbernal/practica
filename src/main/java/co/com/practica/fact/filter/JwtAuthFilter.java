package co.com.practica.fact.filter;

import co.com.practica.fact.constantes.Constantes;
import co.com.practica.fact.util.JwtValidationUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Log4j2
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtValidationUtil jwtValidationUtil;

    public JwtAuthFilter(JwtValidationUtil jwtValidationUtil) {
        this.jwtValidationUtil = jwtValidationUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (!jwtValidationUtil.isValidToken(request)) {
            SecurityContextHolder.clearContext();
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader(Constantes.AUTHORIZATION_HEADER);
        if (authHeader == null) {
            filterChain.doFilter(request, response);
            return;
        }
        String rawToken = authHeader.substring(Constantes.BEARER_PREFIX.length());
        String subject = jwtValidationUtil.extraerSubject(rawToken);
        String role = jwtValidationUtil.extractRole(rawToken);
        List<GrantedAuthority> authorities = toAuthorities(role);

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(subject, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(auth);
        log.debug("JWT válido — autenticación establecida con authorities={}", authorities);

        filterChain.doFilter(request, response);
    }

    /**
     * Maps JWT role claim to Spring authority with ROLE_ prefix
     * so {@code hasRole('ADMIN')} matches ms-auth (ADMIN → ROLE_ADMIN).
     */
    static List<GrantedAuthority> toAuthorities(String role) {
        if (role == null || role.trim().isEmpty()) {
            return Collections.emptyList();
        }
        String authority = role.startsWith(Constantes.ROLE_PREFIX)
                ? role
                : Constantes.ROLE_PREFIX + role;
        return Collections.singletonList(new SimpleGrantedAuthority(authority));
    }
}
