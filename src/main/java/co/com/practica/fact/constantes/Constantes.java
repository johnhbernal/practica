package co.com.practica.fact.constantes;

/**
 * ============================================================
 * Constantes.java
 * Clase que centraliza todas las constantes del microservicio.
 *
 * BUENA PRÁCTICA: Nunca usar "magic strings" o "magic numbers"
 * directamente en el código. En su lugar, definirlos aquí como
 * constantes (static final) para:
 *   1. Facilitar el mantenimiento (cambiar en un solo lugar)
 *   2. Evitar errores de tipeo
 *   3. Hacer el código más legible
 *
 * CONVENCIÓN: nombres en SCREAMING_SNAKE_CASE para constantes
 * ============================================================
 */
public class Constantes {

    // ── MENSAJES DE RESPUESTA ──────────────────────────────────────
    /** Mensaje de éxito estándar */
    public static final String MSG_OK = "Operación realizada exitosamente";

    /** Mensaje de error estándar */
    public static final String MSG_FAIL = "Ha ocurrido un error en la operación";

    /** Mensaje cuando el token es inválido */
    public static final String MSG_UNAUTHORIZED = "Token inválido o expirado";

    /** Mensaje cuando no se encuentran recursos */
    public static final String MSG_NOT_FOUND = "Recurso no encontrado";

    // ── LOGGING ───────────────────────────────────────────────────
    /**
     * Prefijo para los logs de transacción.
     * Permite filtrar logs de una transacción específica en herramientas
     * como Kibana, Splunk, etc.
     * Formato: [TRX-ID: abc123] Mensaje del log
     */
    public static final String LOG_ID_TRANSACTION = "[TRX-ID: ";
    public static final String MSG_NAME_APP_LOG = "microservicio-practica";

    // ── PARÁMETROS ────────────────────────────────────────────────
    /** Valor especial que indica "traer todos los parámetros" */
    public static final int FIND_ALL_PARAMETER = 1;

    /** Estado activo en la base de datos */
    public static final String ESTADO_ACTIVO = "A";

    /** Estado inactivo en la base de datos */
    public static final String ESTADO_INACTIVO = "I";

    // ── SEGURIDAD ─────────────────────────────────────────────────
    /** Prefijo del header Authorization: "Bearer <token>" */
    public static final String BEARER_PREFIX = "Bearer ";

    /** Nombre del header de autorización */
    public static final String AUTHORIZATION_HEADER = "Authorization";

    // ── CATEGORÍAS DE PARÁMETROS ──────────────────────────────────
    /** Categoría para parámetros de configuración del sistema */
    public static final String CATEGORIA_SISTEMA = "SISTEMA";

    /** Categoría para parámetros de negocio */
    public static final String CATEGORIA_NEGOCIO = "NEGOCIO";

    // ── VALIDACIÓN ────────────────────────────────────────────────
    /** Patrón permitido para nombres de parámetros y búsquedas */
    public static final String NOMBRE_PARAMETRO_PATTERN = "^[a-zA-Z0-9_\\-\\s]+$";

    // Constructor privado: Esta clase NO debe instanciarse
    // Solo contiene constantes estáticas
    private Constantes() {
        throw new IllegalStateException("Clase de constantes, no instanciar");
    }
}
