package co.com.practica.fact;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ============================================================
 * PracticaApplication.java
 * Clase principal del microservicio.
 *
 * @SpringBootApplication es una anotación compuesta que incluye:
 *   - @Configuration: Esta clase define beans de Spring
 *   - @EnableAutoConfiguration: Spring Boot configura automáticamente
 *     los componentes según las dependencias del classpath
 *   - @ComponentScan: Escanea todos los @Component, @Service,
 *     @Repository, @Controller en el paquete actual y sus sub-paquetes
 *
 * FLUJO DE INICIO:
 * 1. SpringApplication.run() arranca el contexto de Spring
 * 2. Se escanean todos los componentes del paquete co.com.practica
 * 3. Spring Boot auto-configura DataSource, JPA, Security, etc.
 * 4. Se levantan los endpoints REST
 * 5. La aplicación queda lista para recibir peticiones
 * ============================================================
 */
@SpringBootApplication
public class PracticaApplication {

    public static void main(String[] args) {
        SpringApplication.run(PracticaApplication.class, args);
    }
}
