package co.com.practica.fact.config;

import co.com.practica.fact.constantes.Constantes;
import co.com.practica.fact.entity.Parametro;
import co.com.practica.fact.repository.ParametroRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * ============================================================
 * DataInitializer.java - CARGADOR DE DATOS INICIALES
 *
 * CONCEPTO CommandLineRunner:
 * Interfaz de Spring Boot que ejecuta código al INICIAR la aplicación,
 * después de que el contexto de Spring está completamente configurado.
 *
 * Ideal para:
 * - Cargar datos de prueba en ambientes de desarrollo
 * - Ejecutar migraciones de datos
 * - Validar configuraciones al inicio
 *
 * @Profile("dev"): Solo se activa en el perfil de desarrollo.
 * En QA, UAT y producción, esta clase no carga datos.
 * Así evitamos llenar la BD de producción con datos de prueba.
 *
 * Si no existe el @Profile, se ejecutaría en TODOS los ambientes.
 * ============================================================
 */
@Log4j2
@Component
@Profile("dev")  // Solo en desarrollo
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private ParametroRepository parametroRepository;

    /**
     * Este método se ejecuta automáticamente al iniciar la app.
     * En modo DEV, carga parámetros de ejemplo en la BD H2.
     */
    @Override
    public void run(String... args) {
        log.info("═══════════════════════════════════════════════");
        log.info("  Cargando datos iniciales de desarrollo...");
        log.info("═══════════════════════════════════════════════");

        // Solo cargar si la tabla está vacía
        if (parametroRepository.count() == 0) {
            List<Parametro> parametrosIniciales = Arrays.asList(

                    crearParametro("TIEMPO_SESION", "SISTEMA",
                            "3600",
                            "Tiempo de sesión en segundos (1 hora)"),

                    crearParametro("MAX_INTENTOS_LOGIN", "SEGURIDAD",
                            "3",
                            "Máximo de intentos fallidos de login antes de bloquear"),

                    crearParametro("EMAIL_SOPORTE", "CONTACTO",
                            "soporte@empresa.com",
                            "Email del equipo de soporte técnico"),

                    crearParametro("VERSION_API", "SISTEMA",
                            "1.0.0",
                            "Versión actual de la API"),

                    crearParametro("MONEDA_DEFAULT", "NEGOCIO",
                            "COP",
                            "Moneda por defecto del sistema (Peso Colombiano)"),

                    crearParametro("IVA_PORCENTAJE", "NEGOCIO",
                            "19",
                            "Porcentaje de IVA aplicado a los productos"),

                    crearParametro("PARAMETRO_INACTIVO", "SISTEMA",
                            "valor-inactivo",
                            "Este parámetro está inactivo (para probar el filtro de estado)",
                            Constantes.ESTADO_INACTIVO)
            );

            parametroRepository.saveAll(parametrosIniciales);

            log.info("  ✓ {} parámetros de prueba cargados exitosamente",
                    parametrosIniciales.size());
            log.info("═══════════════════════════════════════════════");
            log.info("  Consola H2: http://localhost:8080/api/h2-console");
            log.info("  Swagger UI: http://localhost:8080/api/swagger-ui.html");
            log.info("  API Base:   http://localhost:8080/api/parametros");
            log.info("═══════════════════════════════════════════════");
        } else {
            log.info("  Ya existen {} parámetros en BD. Omitiendo carga inicial.",
                    parametroRepository.count());
        }
    }

    /** Helper para crear parámetros activos */
    private Parametro crearParametro(String nombre, String categoria,
                                      String valor, String descripcion) {
        return crearParametro(nombre, categoria, valor, descripcion, Constantes.ESTADO_ACTIVO);
    }

    /** Helper para crear parámetros con estado personalizado */
    private Parametro crearParametro(String nombre, String categoria,
                                      String valor, String descripcion, String estado) {
        Parametro p = new Parametro();
        p.setNombreParametro(nombre);
        p.setCategoria(categoria);
        p.setValor(valor);
        p.setDescripcion(descripcion);
        p.setEstado(estado);
        p.setUsuarioCreacion("DATA_INITIALIZER");
        p.setFechaCreacion(new Date());
        p.setUsuarioModificacion("DATA_INITIALIZER");
        p.setFechaModificacion(new Date());
        return p;
    }
}
