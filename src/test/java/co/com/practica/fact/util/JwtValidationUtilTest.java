package co.com.practica.fact.util;

import co.com.practica.fact.constantes.Constantes;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JwtValidationUtil")
class JwtValidationUtilTest {

    private static final String SESSION_SECRET = "test-session-secret-key-at-least-32-chars!";
    private static final String MASTER_SECRET  = "test-master-secret-key-at-least-32-chars!!";

    private JwtValidationUtil jwtValidationUtil;

    @BeforeEach
    void setUp() {
        jwtValidationUtil = new JwtValidationUtil();
        ReflectionTestUtils.setField(jwtValidationUtil, "jwtSecret", SESSION_SECRET);
        ReflectionTestUtils.setField(jwtValidationUtil, "jwtSecretMaster", MASTER_SECRET);
        ReflectionTestUtils.setField(jwtValidationUtil, "jwtExpirationMs", 3600000L);
        jwtValidationUtil.init();
    }

    @Test
    void init_blankSecret_throwsIllegalStateException() {
        JwtValidationUtil util = new JwtValidationUtil();
        ReflectionTestUtils.setField(util, "jwtSecret", "");
        ReflectionTestUtils.setField(util, "jwtSecretMaster", MASTER_SECRET);
        assertThrows(IllegalStateException.class, util::init);
    }

    @Test
    void init_shortSecret_throwsIllegalStateException() {
        JwtValidationUtil util = new JwtValidationUtil();
        ReflectionTestUtils.setField(util, "jwtSecret", "too-short");
        ReflectionTestUtils.setField(util, "jwtSecretMaster", MASTER_SECRET);
        assertThrows(IllegalStateException.class, util::init);
    }

    @Test
    void isValidToken_validSessionToken_returnsTrue() {
        String token = jwtValidationUtil.generarToken("user@test.com");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(Constantes.AUTHORIZATION_HEADER, Constantes.BEARER_PREFIX + token);

        assertTrue(jwtValidationUtil.isValidToken(request));
    }

    @Test
    void isValidToken_missingHeader_returnsFalse() {
        assertFalse(jwtValidationUtil.isValidToken(new MockHttpServletRequest()));
    }

    @Test
    void isValidToken_malformedHeader_returnsFalse() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(Constantes.AUTHORIZATION_HEADER, "NotBearer token");
        assertFalse(jwtValidationUtil.isValidToken(request));
    }

    @Test
    void isValidToken_expiredToken_returnsFalse() {
        String expired = buildExpiredToken(SESSION_SECRET);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(Constantes.AUTHORIZATION_HEADER, Constantes.BEARER_PREFIX + expired);
        assertFalse(jwtValidationUtil.isValidToken(request));
    }

    @Test
    void isValidToken_masterToken_returnsTrue() {
        String master = buildToken(MASTER_SECRET, Constantes.TOKEN_TYPE_MASTER);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(Constantes.AUTHORIZATION_HEADER, Constantes.BEARER_PREFIX + master);
        assertTrue(jwtValidationUtil.isValidToken(request));
    }

    @Test
    void isValidToken_unsupportedTokenType_returnsFalse() {
        String token = buildToken(SESSION_SECRET, "UNKNOWN");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(Constantes.AUTHORIZATION_HEADER, Constantes.BEARER_PREFIX + token);
        assertFalse(jwtValidationUtil.isValidToken(request));
    }

    @Test
    void generarToken_andExtraerSubject_roundTrip() {
        String token = jwtValidationUtil.generarToken("usuario");
        assertEquals("usuario", jwtValidationUtil.extraerSubject(token));
    }

    @Test
    void extractRole_returnsClaimValue() {
        String token = buildTokenWithRole(SESSION_SECRET, "ADMIN");
        assertEquals("ADMIN", jwtValidationUtil.extractRole(token));
    }

    @Test
    void extractRole_invalidToken_returnsNull() {
        assertNull(jwtValidationUtil.extractRole("not-a-jwt"));
    }

    private static String buildToken(String secret, String tokenType) {
        SecretKeySpec key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        return Jwts.builder()
                .setSubject("svc")
                .claim(Constantes.CLAIM_TOKEN_TYPE, tokenType)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    private static String buildTokenWithRole(String secret, String role) {
        SecretKeySpec key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        return Jwts.builder()
                .setSubject("user")
                .claim(Constantes.CLAIM_ROLE, role)
                .claim(Constantes.CLAIM_TOKEN_TYPE, Constantes.TOKEN_TYPE_SESSION)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    private static String buildExpiredToken(String secret) {
        SecretKeySpec key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        return Jwts.builder()
                .setSubject("user")
                .setIssuedAt(new Date(System.currentTimeMillis() - 7200000))
                .setExpiration(new Date(System.currentTimeMillis() - 3600000))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}
