# CI

Workflow: `.github/workflows/ci.yml`

| Job | Command |
|-----|---------|
| build | `mvn -B package -DskipTests` → upload `target/*.war` |
| test | `mvn -B test` (needs build) |

- Java 17 Temurin; project `java.version` remains 1.8.
- `FORCE_JAVASCRIPT_ACTIONS_TO_NODE24: true`
- `APP_JWT_SECRET_SESSION` set in workflow env for consistency (dev profile also has a local default).
- Package must succeed without gitignored `properties/application.*.yml` files.
