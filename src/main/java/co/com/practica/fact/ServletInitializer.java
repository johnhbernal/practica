package co.com.practica.fact;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * ============================================================
 * ServletInitializer.java
 * Clase necesaria para desplegar el proyecto como archivo WAR
 * en un servidor de aplicaciones externo (Tomcat, JBoss, WebLogic).
 *
 * ¿POR QUÉ EXISTE ESTO?
 * Cuando empaquetamos como WAR (no JAR), el servidor de aplicaciones
 * necesita saber cómo inicializar nuestra aplicación Spring Boot.
 * Esta clase es el "punto de entrada" para ese escenario.
 *
 * DIFERENCIA JAR vs WAR:
 * - JAR: Tiene Tomcat embebido, se ejecuta con: java -jar app.jar
 * - WAR: Se despliega en un Tomcat/servidor externo
 *
 * En el pom.xml tenemos <packaging>war</packaging> por eso necesitamos esta clase.
 * ============================================================
 */
public class ServletInitializer extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        // Le decimos a Spring Boot cuál es nuestra clase principal
        return application.sources(PracticaApplication.class);
    }
}
