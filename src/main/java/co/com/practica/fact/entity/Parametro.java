package co.com.practica.fact.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * ============================================================
 * Parametro.java - ENTIDAD JPA
 *
 * CONCEPTO DE ENTIDAD:
 * Una entidad representa una tabla en la base de datos.
 * Cada instancia de esta clase = una fila de la tabla.
 *
 * ANOTACIONES JPA CLAVE:
 * - @Entity: Le dice a Hibernate que esta clase mapea a una tabla
 * - @Table: Especifica el nombre de la tabla en BD
 * - @Id: Identifica la clave primaria
 * - @GeneratedValue: Define cómo se genera el ID automáticamente
 * - @Column: Mapea el atributo a una columna específica de la tabla
 *
 * ANOTACIONES LOMBOK:
 * - @Data: Genera getters, setters, toString, equals, hashCode
 * - @NoArgsConstructor: Genera constructor sin argumentos (requerido por JPA)
 * - @AllArgsConstructor: Genera constructor con todos los argumentos
 *
 * TABLA EN BD:
 * CREATE TABLE PARAMETROS (
 *   COD_PARAMETRO    BIGINT PRIMARY KEY AUTO_INCREMENT,
 *   NOMBRE_PARAMETRO VARCHAR(100) NOT NULL,
 *   CATEGORIA        VARCHAR(50),
 *   VALOR            VARCHAR(500),
 *   DESCRIPCION      VARCHAR(200),
 *   ESTADO           VARCHAR(1),
 *   USUARIO_CREACION VARCHAR(50),
 *   FECHA_CREACION   TIMESTAMP,
 *   USUARIO_MOD      VARCHAR(50),
 *   FECHA_MOD        TIMESTAMP
 * );
 * ============================================================
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "PARAMETROS")
public class Parametro implements Serializable {

    // serialVersionUID: Necesario para la serialización Java
    // Garantiza compatibilidad al deserializar objetos entre versiones
    private static final long serialVersionUID = 1L;

    /**
     * Clave primaria de la tabla.
     *
     * @GeneratedValue con IDENTITY usa el AUTO_INCREMENT de la BD.
     * Alternativas:
     *   - SEQUENCE: Usa una secuencia de BD (Oracle)
     *   - TABLE: Usa una tabla auxiliar para los IDs
     *   - AUTO: Hibernate elige según la BD
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "COD_PARAMETRO")
    private Long codParametro;

    /**
     * Nombre identificador del parámetro.
     * nullable=false = NOT NULL en la BD
     * length=100 = VARCHAR(100)
     */
    @Column(name = "NOMBRE_PARAMETRO", nullable = false, length = 100)
    private String nombreParametro;

    /** Agrupación del parámetro (ej: SISTEMA, NEGOCIO, CORREO) */
    @Column(name = "CATEGORIA", length = 50)
    private String categoria;

    /** Valor del parámetro (puede ser texto, número, JSON, etc.) */
    @Column(name = "VALOR", length = 500)
    private String valor;

    /** Descripción legible del propósito del parámetro */
    @Column(name = "DESCRIPCION", length = 200)
    private String descripcion;

    /**
     * Estado del registro: 'A' = Activo, 'I' = Inactivo.
     * PATRÓN COMÚN: Nunca borrar físicamente registros en BD empresarial,
     * solo desactivarlos (borrado lógico).
     */
    @Column(name = "ESTADO", length = 1)
    private String estado;

    /** Auditoría: quién creó el registro */
    @Column(name = "USUARIO_CREACION", length = 50)
    private String usuarioCreacion;

    /**
     * Auditoría: cuándo se creó.
     * @Temporal(DATE) = solo fecha, TIMESTAMP = fecha y hora
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "FECHA_CREACION")
    private Date fechaCreacion;

    /** Auditoría: quién modificó el registro por última vez */
    @Column(name = "USUARIO_MOD", length = 50)
    private String usuarioModificacion;

    /** Auditoría: cuándo se modificó por última vez */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "FECHA_MOD")
    private Date fechaModificacion;
}
