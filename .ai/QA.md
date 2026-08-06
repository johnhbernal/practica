# QA — microservicio-practica (lecciones Kilele / consejo Practica)

## Mandato

- JUnit/MockMvc prueban **AuthN JWT** + AuthZ por **rol primario** (`hasRole('ADMIN')`).
- **No** acreditan UI React — eso es Playwright en `ms-frontend`.
- Fine-grained `permissions[]` / módulos viven en **ms-auth** (demo inventario); este servicio **no** mapea `PERM_*`.
- Cobertura JaCoCo ≥ 0.70 en `verify` — techo honesto.

## Suites canónicas

| Clase | Qué prueba |
|-------|------------|
| `JwtValidationUtilTest` | session/master/expiry/tokenType/role |
| `JwtAuthFilterTest` | `ROLE_` prefix |
| `ActuatorSecurityIntegrationTest` | health público |
| `ParametroAuthzIntegrationTest` | JWT estilo ms-auth (`permissions`/`groups` ignorados); USER 403 POST; ADMIN 201 |

## Consejo

Orquestador: `../practica-stack/.ai/AGENTS.md` · acta AuthZ: `../practica-stack/docs/auditorias/COUNCIL-AUTHZ-2026-08-06.md`.

## Comando

```powershell
.\mvnw.cmd -B "-Dtest=JwtValidationUtilTest,JwtAuthFilterTest,ActuatorSecurityIntegrationTest,ParametroAuthzIntegrationTest" test
.\mvnw.cmd -B verify
```
