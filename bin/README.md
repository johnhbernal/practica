# 🚀 Guía Completa: Microservicios en Java con Spring Boot

## Proyecto de práctica: `microservicio-practica`

---

## 📋 Tabla de Contenidos

1. [¿Qué es un microservicio?](#que-es-un-microservicio)
2. [Arquitectura del proyecto](#arquitectura)
3. [Tecnologías utilizadas](#tecnologias)
4. [Cómo ejecutar el proyecto](#como-ejecutar)
5. [Flujo de una petición HTTP](#flujo)
6. [Endpoints disponibles](#endpoints)
7. [Cómo probar con Postman](#postman)
8. [Conceptos clave explicados](#conceptos)
9. [Cómo crear un nuevo módulo](#nuevo-modulo)

---

## 1. ¿Qué es un Microservicio? {#que-es-un-microservicio}

Un **microservicio** es una aplicación pequeña e independiente que hace **una sola cosa** muy bien. En lugar de tener una aplicación monolítica gigante, dividimos el sistema en servicios pequeños:

```
┌─────────────────────────────────────────────────────┐
│                  SISTEMA COMPLETO                    │
├─────────────┬──────────────┬───────────────────────┤
│   MS-AUTH   │  MS-FACTURA  │     MS-PARAMETROS      │
│  (Autenticar│  (Facturas)  │  (Este proyecto ✓)     │
│   usuarios) │              │                         │
└─────────────┴──────────────┴───────────────────────┘
```

Cada microservicio:
- Tiene su **propia base de datos**
- Se **despliega independientemente**
- Se comunica con otros via **REST API** o mensajería
- Puede **escalar independientemente**

---

## 2. Arquitectura del Proyecto {#arquitectura}

Este proyecto usa **arquitectura en capas** (Layered Architecture):

```
Cliente (Postman/Frontend)
         │
         │ HTTP Request (JSON)
         ▼
┌─────────────────────────────────────────────────────┐
│  CONTROLLER (co.com.practica.fact.controller)       │
│  • Recibe la petición HTTP                          │
│  • Valida el token JWT                              │
│  • Delega la lógica al Service                      │
│  • Retorna la respuesta HTTP con ResponseDTO        │
└───────────────────────┬─────────────────────────────┘
                        │
                        ▼
┌─────────────────────────────────────────────────────┐
│  SERVICE (co.com.practica.fact.service)             │
│  • Contiene la LÓGICA DE NEGOCIO                    │
│  • Valida reglas de negocio                         │
│  • Llama al Repository para acceder a BD            │
│  • Usa Mapper para convertir Entity ↔ DTO           │
└───────────────────────┬─────────────────────────────┘
                        │
                        ▼
┌─────────────────────────────────────────────────────┐
│  REPOSITORY (co.com.practica.fact.repository)       │
│  • Acceso a la BASE DE DATOS                        │
│  • Spring Data JPA genera el SQL automáticamente    │
│  • Solo métodos de consulta/escritura en BD         │
└───────────────────────┬─────────────────────────────┘
                        │
                        ▼
┌─────────────────────────────────────────────────────┐
│  BASE DE DATOS (H2 en DEV / PostgreSQL en PROD)     │
└─────────────────────────────────────────────────────┘
```

### Estructura de carpetas:

```
src/main/java/co/com/practica/fact/
├── config/
│   ├── SecurityConfig.java      ← Configuración Spring Security + JWT
│   └── DataInitializer.java     ← Carga datos de prueba en DEV
├── constantes/
│   └── Constantes.java          ← Todas las constantes del proyecto
├── controller/
│   ├── ParametroController.java      ← Interfaz (contrato de la API)
│   └── impl/
│       └── ParametroControllerImpl.java  ← Implementación
├── dto/
│   ├── ParametroDTO.java        ← Objeto de transferencia de datos
│   └── ResponseDTO.java         ← Respuesta estándar de la API
├── entity/
│   └── Parametro.java           ← Entidad JPA (mapea a tabla BD)
├── exception/
│   ├── ResourceNotFoundException.java  ← Excepción personalizada 404
│   └── ServicesException.java          ← Handler global de excepciones
├── mappers/
│   └── ParametroMapper.java     ← Conversión Entity ↔ DTO (MapStruct)
├── repository/
│   └── ParametroRepository.java ← Acceso a BD (Spring Data JPA)
├── service/
│   ├── ParametroService.java    ← Interfaz del servicio
│   └── impl/
│       └── ParametroServiceImpl.java  ← Implementación con lógica
└── util/
    └── JwtValidationUtil.java   ← Validación y generación de JWT
```

---

## 3. Tecnologías Utilizadas {#tecnologias}

| Tecnología | Versión | Para qué sirve |
|-----------|---------|----------------|
| Java | 1.8 | Lenguaje base |
| Spring Boot | 2.7.15 | Framework principal |
| Spring Data JPA | incluido | Acceso a BD sin SQL manual |
| Spring Security | incluido | Autenticación/Autorización |
| JJWT | 0.11.5 | Manejo de tokens JWT |
| MapStruct | 1.5.5 | Mapeo Entity ↔ DTO |
| Lombok | 1.18.30 | Reduce código boilerplate |
| H2 | incluido | BD en memoria (solo DEV) |
| PostgreSQL | incluido | BD en producción |
| Springdoc OpenAPI | 1.7.0 | Documentación Swagger |
| Log4j2 | incluido | Sistema de logging |
| JUnit 5 + Mockito | incluido | Pruebas unitarias |

---

## 4. Cómo Ejecutar el Proyecto {#como-ejecutar}

### Prerrequisitos:
- Java 8 o superior instalado
- Maven 3.6 o superior instalado
- IDE: IntelliJ IDEA (recomendado) o Eclipse

### Pasos:

```bash
# 1. Clonar o descargar el proyecto
cd microservicio-practica

# 2. Compilar (descarga dependencias automáticamente)
mvn clean compile

# 3. Ejecutar en modo desarrollo (perfil DEV)
mvn spring-boot:run

# O ejecutar con perfil específico:
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# 4. Compilar como WAR para despliegue:
mvn clean package -P prod
```

### URLs disponibles al iniciar:
- **API Base:** `http://localhost:8080/api`
- **Swagger UI:** `http://localhost:8080/api/swagger-ui.html`
- **H2 Console:** `http://localhost:8080/api/h2-console`
  - JDBC URL: `jdbc:h2:mem:practica_dev`
  - User: `sa` | Password: (vacío)

---

## 5. Flujo de una Petición HTTP {#flujo}

Ejemplo: `GET /api/parametros/activos`

```
1. Cliente envía:
   GET http://localhost:8080/api/parametros/activos
   Header: Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...

2. Spring Security intercepta:
   → Verifica que la ruta está permitida (sí, está en permitAll)
   → Deja pasar la petición al controller

3. ParametroControllerImpl.obtenerParametrosActivos() ejecuta:
   a. validateToken(request) → verifica el JWT
      - Si inválido → retorna 401 UNAUTHORIZED inmediatamente
      - Si válido → continúa
   b. parametroService.obtenerParametrosActivos()

4. ParametroServiceImpl.obtenerParametrosActivos() ejecuta:
   a. parametroRepository.findByEstado("A")
      → SQL generado: SELECT * FROM PARAMETROS WHERE ESTADO = 'A'
   b. parametroMapper.toDTOList(listaEntidades)
      → Convierte List<Parametro> → List<ParametroDTO>
   c. Retorna List<ParametroDTO>

5. Controller construye la respuesta:
   ResponseDTO {
     code: "200",
     description: "OK",
     data: [{ parameterCode: 1, parameterName: "TIEMPO_SESION", ... }]
   }

6. Spring serializa a JSON y retorna al cliente:
   HTTP 200 OK
   Content-Type: application/json
   {
     "code": "200",
     "description": "OK",
     "data": [...]
   }
```

---

## 6. Endpoints Disponibles {#endpoints}

Todos los endpoints requieren el header: `Authorization: Bearer <token>`

| Método | URL | Descripción |
|--------|-----|-------------|
| GET | `/api/parametros/activos` | Listar parámetros activos |
| GET | `/api/parametros` | Listar todos los parámetros |
| GET | `/api/parametros/{id}` | Buscar por ID |
| GET | `/api/parametros/buscar?nombre=X` | Buscar por nombre |
| POST | `/api/parametros` | Crear nuevo parámetro |
| PUT | `/api/parametros/{id}` | Actualizar parámetro |
| DELETE | `/api/parametros/{id}` | Desactivar parámetro |

---

## 7. Cómo Probar con Postman {#postman}

### Paso 1: Generar un Token JWT

Abre la consola H2 o escribe un test que llame a `JwtValidationUtil.generarToken("usuario-prueba")`.

Como atajo para desarrollo, el token de prueba generado con la clave del `application.dev.yml` es válido por 1 hora.

### Paso 2: Configurar la colección en Postman

**Variables de colección:**
```
base_url = http://localhost:8080/api
token = <tu token JWT>
```

**Header en todas las peticiones:**
```
Authorization: Bearer {{token}}
```

### Paso 3: Ejemplos de peticiones

**Listar parámetros activos:**
```
GET {{base_url}}/parametros/activos
```

**Crear un parámetro:**
```
POST {{base_url}}/parametros
Content-Type: application/json

{
  "parameterName": "NUEVO_PARAMETRO",
  "parameterCategory": "NEGOCIO",
  "value": "mi-valor",
  "description": "Descripción del nuevo parámetro"
}
```

**Respuesta exitosa:**
```json
{
  "code": "200",
  "description": "Parámetro creado exitosamente",
  "data": {
    "parameterCode": 8,
    "parameterName": "NUEVO_PARAMETRO",
    "parameterCategory": "NEGOCIO",
    "value": "mi-valor",
    "description": "Descripción del nuevo parámetro",
    "status": "A",
    "creationBy": "SISTEMA",
    "creationDate": "2024-04-24 15:30:00"
  }
}
```

**Respuesta de error (token inválido):**
```json
{
  "code": "401",
  "description": "Token inválido o expirado"
}
```

---

## 8. Conceptos Clave Explicados {#conceptos}

### 8.1 ¿Qué es Lombok?

```java
// SIN Lombok (mucho código repetitivo):
public class Parametro {
    private Long codParametro;
    private String nombreParametro;

    public Parametro() {}

    public Long getCodParametro() { return codParametro; }
    public void setCodParametro(Long codParametro) { this.codParametro = codParametro; }
    public String getNombreParametro() { return nombreParametro; }
    public void setNombreParametro(String n) { this.nombreParametro = n; }
    // + toString(), equals(), hashCode()...
}

// CON Lombok (limpio y conciso):
@Data           // genera getters, setters, toString, equals, hashCode
@NoArgsConstructor  // genera constructor vacío
@AllArgsConstructor // genera constructor con todos los campos
public class Parametro {
    private Long codParametro;
    private String nombreParametro;
}
```

### 8.2 ¿Qué es MapStruct?

MapStruct genera automáticamente código para copiar datos entre objetos:

```java
// MapStruct genera esto automáticamente por nosotros:
public ParametroDTO toDTO(Parametro source) {
    ParametroDTO dto = new ParametroDTO();
    dto.setParameterCode(source.getCodParametro());    // @Mapping
    dto.setParameterName(source.getNombreParametro()); // @Mapping
    dto.setStatus(source.getEstado());                 // @Mapping
    return dto;
}
```

### 8.3 ¿Qué es el Token JWT?

```
Token = eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c3VhcmlvIn0.FIRMA

Decodificado:
├── Header:  { "alg": "HS256", "typ": "JWT" }
├── Payload: { "sub": "usuario", "iat": 1700000000, "exp": 1700003600 }
└── Firma:   HMACSHA256(header + "." + payload, clave-secreta)

La firma garantiza que nadie modificó el token.
Si alguien cambia el payload, la firma ya no coincide → inválido.
```

### 8.4 ¿Qué es @Transactional?

```java
@Transactional  // Si algo falla dentro → ROLLBACK automático
public ParametroDTO crearParametro(ParametroDTO dto) {
    // operación 1: guardar parámetro
    parametroRepository.save(parametro);

    // operación 2: registrar auditoría
    auditoriaRepository.save(auditoria);

    // Si operación 2 falla → operación 1 también se deshace (ROLLBACK)
    // Sin @Transactional → operación 1 quedaría guardada aunque 2 falle
}
```

---

## 9. Cómo Crear un Nuevo Módulo {#nuevo-modulo}

Ejemplo: Agregar gestión de **Empleados**.

### Paso 1: Crear la Entidad
```java
// entity/Empleado.java
@Data @Entity @Table(name = "EMPLEADOS")
public class Empleado implements Serializable {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_EMPLEADO")
    private Long idEmpleado;

    @Column(name = "NOMBRE")
    private String nombre;
    // ... más campos
}
```

### Paso 2: Crear el DTO
```java
// dto/EmpleadoDTO.java
@Data @JsonInclude(JsonInclude.Include.NON_NULL)
public class EmpleadoDTO {
    private Long employeeId;
    @NotBlank private String name;
}
```

### Paso 3: Crear el Mapper
```java
// mappers/EmpleadoMapper.java
@Mapper(componentModel = "spring")
public interface EmpleadoMapper {
    @Mapping(source = "idEmpleado", target = "employeeId")
    @Mapping(source = "nombre", target = "name")
    EmpleadoDTO toDTO(Empleado source);
    List<EmpleadoDTO> toDTOList(List<Empleado> source);
}
```

### Paso 4: Crear el Repository
```java
// repository/EmpleadoRepository.java
@Repository
public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {
    List<Empleado> findByNombre(String nombre);
}
```

### Paso 5: Crear el Service
```java
// service/EmpleadoService.java (interfaz)
public interface EmpleadoService {
    List<EmpleadoDTO> obtenerTodos();
}

// service/impl/EmpleadoServiceImpl.java
@Service
public class EmpleadoServiceImpl implements EmpleadoService {
    @Autowired private EmpleadoRepository repo;
    @Autowired private EmpleadoMapper mapper;

    @Transactional(readOnly = true)
    public List<EmpleadoDTO> obtenerTodos() {
        return mapper.toDTOList(repo.findAll());
    }
}
```

### Paso 6: Crear el Controller
```java
// controller/EmpleadoController.java (interfaz)
@RestController @RequestMapping("/")
public interface EmpleadoController {
    @GetMapping("/empleados")
    ResponseEntity<ResponseDTO> obtenerTodos(HttpServletRequest request);
}

// controller/impl/EmpleadoControllerImpl.java
@RestController
public class EmpleadoControllerImpl implements EmpleadoController {
    @Autowired private EmpleadoService service;
    @Autowired private JwtValidationUtil jwtUtil;

    public ResponseEntity<ResponseDTO> obtenerTodos(HttpServletRequest request) {
        if (!jwtUtil.isValidToken(request))
            return ResponseEntity.status(401).body(new ResponseDTO("401", "No autorizado"));
        return ResponseEntity.ok(new ResponseDTO("200", "OK", service.obtenerTodos()));
    }
}
```

### Paso 7: Escribir pruebas unitarias
Copiar `ParametroServiceImplTest.java` como base y adaptar para `EmpleadoServiceImpl`.

---

## ✅ Lista de Verificación (Checklist)

Antes de considerar un módulo completo, verificar:

- [ ] Entidad con `@Entity`, `@Table`, `@Id` y campos de auditoría
- [ ] DTO con validaciones (`@NotBlank`, `@Size`, etc.) y `@JsonInclude`
- [ ] Mapper con `@Mapper(componentModel = "spring")` y mapeos correctos
- [ ] Repository con `JpaRepository` y métodos de consulta necesarios
- [ ] Service (interfaz + implementación) con `@Transactional`
- [ ] Controller (interfaz + implementación) con validación JWT
- [ ] Exception handler actualizado si hay nuevas excepciones
- [ ] Pruebas unitarias para el Service (mínimo happy path + error path)
- [ ] Datos de prueba en `DataInitializer` para el nuevo módulo

---

*Proyecto de práctica - Microservicios Java Spring Boot*
