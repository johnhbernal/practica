# practica — Claude / agent configuration

Spring Boot 2.7.15 · Java 8 WAR · parámetros MS. Interoperates with **ms-auth** (session JWT) and **ms-frontend**.

## Agent docs (source of truth)

| Doc | Topic |
|-----|--------|
| [`.ai/AGENTS.md`](.ai/AGENTS.md) | Roles, scope, how to work this repo |
| [`.ai/JAVA.md`](.ai/JAVA.md) | Java 8 constraints, Maven, Lombok/MapStruct |
| [`.ai/SPRINGBOOT.md`](.ai/SPRINGBOOT.md) | Boot 2.7, profiles, packaging |
| [`.ai/SECURITY.md`](.ai/SECURITY.md) | JWT session alignment with ms-auth |
| [`.ai/DATABASE.md`](.ai/DATABASE.md) | H2 (dev) / Postgres (prod) |
| [`.ai/ACTIVE-DIRECTORY.md`](.ai/ACTIVE-DIRECTORY.md) | AD/LDAP — out of scope here |
| [`.ai/CI.md`](.ai/CI.md) | GitHub Actions build/test |

## Rules

- Prefer editing existing files; do not invent YAML under `properties/` for Maven copy.
- Keep JWT secret shared with ms-auth via `APP_JWT_SECRET_SESSION`.
- Default port: **8082**, context-path `/api`.
