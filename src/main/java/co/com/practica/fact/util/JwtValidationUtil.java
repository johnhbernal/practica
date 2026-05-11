package co.com.practica.fact.util;

import co.com.practica.fact.constantes.Constantes;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * ============================================================
 * JwtValidationUtil.java - UTILIDAD DE VALIDACIÓN JWT
 *
 * CONCEPTO JWT (JSON Web Token):
 * Un token JWT tiene 3 partes separadas por puntos:
 *   [Header].[Payload].[Signature]
 *
 * Ejemplo decodificado:
 * Header:  { "alg": "HS256", "typ": "JWT" }
 * Payload: { "sub": "usuario123", "iat": 1700000000, "exp": 1700003600 }
 * Signature: HMACSHA256(base64(header) + "." + base64(payload), secretKey)
 *
 * FLUJO DE VALIDACIÓN:
 * 1. Cliente envía: Authorization: Bearer eyJhbGc...
 * 2. Extraemos el token (quitando "Bearer ")
 * 3. Verificamos la firma con nuestra clave secreta
 * 4. Verificamos que no esté expirado
 * 5. Si todo OK, permitimos el acceso
 *
 * @Component: Registra esta clase como un bean de Spring (inyectable).
 * @Value: Inyecta valores del application.yml en atributos.
 * @PostConstruct: El método se ejecuta después de que Spring inyecta
 *   las dependencias, perfecto para inicializaciones.
 * ============================================================
 */
@Log4j2
@Component
public class JwtValidationUtil {

    /**
     * @Value("${app.jwt.secret}"): Lee el valor de app.jwt.secret
     * del application.yml activo.
     * El valor por defecto (después del :) se usa si la propiedad no existe.
     */
    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration-ms:3600000}")
    private long jwtExpirationMs;

    /** Clave criptográfica derivada del secret, inicializada en @PostConstruct */
    private SecretKey secretKey;

    /**
     * @PostConstruct: Se ejecuta UNA VEZ después de que Spring inyecta
     * todas las dependencias. Usamos para inicializar la clave.
     */
    @PostConstruct
    public void init() {
        // Creamos la clave HMAC-SHA256 a partir del secret en texto
        // La clave debe tener al menos 256 bits (32 bytes) para HS256
        this.secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        log.info("JwtValidationUtil inicializado correctamente");
    }

    /**
     * Valida si la petición tiene un token JWT válido.
     *
     * PROCESO:
     * 1. Extraer el header Authorization
     * 2. Verificar que tenga el prefijo "Bearer "
     * 3. Extraer el token puro
     * 4. Validar el token
     *
     * @param request Petición HTTP entrante
     * @return true si el token es válido, false si no
     */
    public boolean isValidToken(HttpServletRequest request) {
        try {
            // 1. Obtener el header de autorización
            String authHeader = request.getHeader(Constantes.AUTHORIZATION_HEADER);

            // 2. Verificar que exista y tenga el formato correcto
            if (authHeader == null || !authHeader.startsWith(Constantes.BEARER_PREFIX)) {
                log.warn("Header Authorization ausente o con formato incorrecto");
                return false;
            }

            // 3. Extraer solo el token (quitar "Bearer ")
            String token = authHeader.substring(Constantes.BEARER_PREFIX.length());

            // 4. Parsear y validar el token
            // Si el token es inválido o expirado, lanza una excepción
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);

            log.debug("Token JWT válido");
            return true;

        } catch (ExpiredJwtException e) {
            log.warn("Token JWT expirado: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.warn("Token JWT malformado: {}", e.getMessage());
        } catch (SignatureException e) {
            log.warn("Firma del token JWT inválida");
        } catch (Exception e) {
            log.warn("Error validando token JWT: {}", e.getMessage());
        }
        return false;
    }

    /**
     * Genera un nuevo token JWT.
     * Útil para testing o para un endpoint de login.
     *
     * @param subject Identificador del usuario (email, username, etc.)
     * @return Token JWT como String
     */
    public String generarToken(String subject) {
        Date ahora = new Date();
        Date expiracion = new Date(ahora.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .setSubject(subject)           // "sub": quien es el usuario
                .setIssuedAt(ahora)             // "iat": cuándo se creó
                .setExpiration(expiracion)       // "exp": cuándo expira
                .signWith(secretKey)             // Firma con HMAC-SHA256
                .compact();                      // Genera el String final
    }

    /**
     * Extrae el subject (usuario) del token.
     *
     * @param token Token JWT (sin el prefijo "Bearer ")
     * @return Subject del token (email/username del usuario)
     */
    public String extraerSubject(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public String extractRole(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .get("role", String.class);
        } catch (Exception e) {
            return null;
        }
    }
}
