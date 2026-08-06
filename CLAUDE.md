# practica — Claude / agent configuration

Spring Boot **2.7.18** · Java **17** WAR · parámetros MS. Interoperates with **ms-auth** (session JWT + RBAC claims) and **ms-frontend**.

## Agent docs (source of truth)

| Doc | Topic |
|-----|--------|
| [`.ai/AGENTS.md`](.ai/AGENTS.md) | Roles, scope, how to work this repo |
| [`.ai/QA.md`](.ai/QA.md) | Canonical suites · council pointer |
| [`.ai/JAVA.md`](.ai/JAVA.md) | Java 17, Maven, Lombok/MapStruct |
| [`.ai/SPRINGBOOT.md`](.ai/SPRINGBOOT.md) | Boot 2.7.18, profiles, packaging |
| [`.ai/SECURITY.md`](.ai/SECURITY.md) | JWT session alignment with ms-auth |
| [`.ai/DATABASE.md`](.ai/DATABASE.md) | H2 (dev) / Postgres (prod) |
| [`.ai/ACTIVE-DIRECTORY.md`](.ai/ACTIVE-DIRECTORY.md) | AD — out of scope; see ms-auth simulated AD |
| [`.ai/CI.md`](.ai/CI.md) | GitHub Actions build/test |

Full-stack council: `../practica-stack/.ai/AGENTS.md`.

## Rules

- Prefer editing existing files; do not invent YAML under `properties/` for Maven copy.
- Keep JWT secret shared with ms-auth via `APP_JWT_SECRET_SESSION`.
- Default port: **8082**, context-path `/api`.
- AuthZ here = primary `role` only; do not invent module `PERM_*` enforcement without product decision.
