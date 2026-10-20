# CI

Workflow: `.github/workflows/ci.yml`

| Job | Command |
|-----|---------|
| build | `./mvnw -B package -DskipTests` → upload `target/*.war` |
| test | `./mvnw -B verify` (JUnit + JaCoCo line coverage **≥ 0.70**) |
| docker-build | `docker build .` (needs test) |

- Java 17 Temurin; `pom.xml` declares `java.version` **17** (`source`/`target` from that property).
- `FORCE_JAVASCRIPT_ACTIONS_TO_NODE24: true`
- `APP_JWT_SECRET_SESSION` set in workflow env for consistency (dev profile also has a local default).
- Package must succeed without gitignored `properties/application.*.yml` files.
