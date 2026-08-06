package co.com.practica.fact.constantes;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Constantes")
class ConstantesTest {

    @Test
    void privateConstructor_throwsIllegalStateException() throws Exception {
        Constructor<Constantes> ctor = Constantes.class.getDeclaredConstructor();
        ctor.setAccessible(true);
        InvocationTargetException ex = assertThrows(InvocationTargetException.class, ctor::newInstance);
        assertTrue(ex.getCause() instanceof IllegalStateException);
    }

    @Test
    void tokenTypeConstants_matchMsAuth() {
        assertEquals("SESSION", Constantes.TOKEN_TYPE_SESSION);
        assertEquals("MASTER", Constantes.TOKEN_TYPE_MASTER);
    }
}
