package co.com.practica.fact.util;

import co.com.practica.fact.constantes.Constantes;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Validates JWTs issued by ms-auth:
 * <ul>
 *   <li>SESSION — signed with {@code APP_JWT_SECRET_SESSION}</li>
 *   <li>MASTER — signed with {@code APP_JWT_SECRET_MASTER} (Feign service calls)</li>
 * </ul>
 */
@Log4j2
@Component
public class JwtValidationUtil {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.secret-master:}")
    private String jwtSecretMaster;

    @Value("${app.jwt.expiration-ms:3600000}")
    private long jwtExpirationMs;

    private SecretKey sessionKey;
    private SecretKey masterKey;

    @PostConstruct
    public void init() {
        this.sessionKey = requireKey(jwtSecret, "app.jwt.secret / APP_JWT_SECRET_SESSION");
        if (jwtSecretMaster != null && !jwtSecretMaster.trim().isEmpty()) {
            this.masterKey = requireKey(jwtSecretMaster, "app.jwt.secret-master / APP_JWT_SECRET_MASTER");
        }
        log.info("JwtValidationUtil inicializado (masterKey={})", masterKey != null);
    }

    public boolean isValidToken(HttpServletRequest request) {
        try {
            String authHeader = request.getHeader(Constantes.AUTHORIZATION_HEADER);
            if (authHeader == null || !authHeader.startsWith(Constantes.BEARER_PREFIX)) {
                log.debug("Authorization header absent or malformed");
                return false;
            }
            String token = authHeader.substring(Constantes.BEARER_PREFIX.length());
            Claims claims = parseClaims(token);
            if (!isAcceptableTokenType(claims)) {
                log.debug("JWT rejected: unsupported tokenType");
                return false;
            }
            log.debug("Token JWT válido");
            return true;
        } catch (ExpiredJwtException e) {
            log.debug("Token JWT expirado");
        } catch (MalformedJwtException e) {
            log.debug("Token JWT malformado");
        } catch (SignatureException e) {
            log.debug("Firma del token JWT inválida");
        } catch (Exception e) {
            log.debug("Error validando token JWT");
        }
        return false;
    }

    public String generarToken(String subject) {
        Date ahora = new Date();
        Date expiracion = new Date(ahora.getTime() + jwtExpirationMs);
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(ahora)
                .setExpiration(expiracion)
                .signWith(sessionKey)
                .compact();
    }

    public String extraerSubject(String token) {
        return parseClaims(token).getSubject();
    }

    public String extractRole(String token) {
        try {
            return parseClaims(token).get(Constantes.CLAIM_ROLE, String.class);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Tries session key first, then master key (Feign MASTER tokens from ms-auth).
     */
    private Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(sessionKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (SignatureException | MalformedJwtException primary) {
            if (masterKey == null) {
                throw primary;
            }
            return Jwts.parserBuilder()
                    .setSigningKey(masterKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        }
    }

    /**
     * Accept SESSION and MASTER when claim is present; missing claim = legacy OK.
     */
    private boolean isAcceptableTokenType(Claims claims) {
        String tokenType = claims.get(Constantes.CLAIM_TOKEN_TYPE, String.class);
        if (tokenType == null) {
            return true;
        }
        return Constantes.TOKEN_TYPE_SESSION.equals(tokenType)
                || Constantes.TOKEN_TYPE_MASTER.equals(tokenType);
    }

    private static SecretKey requireKey(String secret, String label) {
        if (secret == null || secret.trim().isEmpty()) {
            throw new IllegalStateException(label + " must not be null or empty");
        }
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            throw new IllegalStateException(label + " must be at least 32 bytes for HS256");
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
