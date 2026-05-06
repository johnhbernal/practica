package co.com.practica.fact.filter;

import co.com.practica.fact.constantes.Constantes;
import co.com.practica.fact.util.JwtValidationUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

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

        String rawToken = request.getHeader(Constantes.AUTHORIZATION_HEADER)
                                 .substring(Constantes.BEARER_PREFIX.length());
        String subject = jwtValidationUtil.extraerSubject(rawToken);

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(subject, null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(auth);
        log.debug("JWT válido para subject: {}", subject);

        filterChain.doFilter(request, response);
    }
}
