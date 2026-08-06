# Spring Boot

- **Version:** **2.7.18** (javax namespace, not jakarta).
- **Packaging:** WAR (`ServletInitializer` for external Tomcat; Boot plugin for executable).
- **Profiles:** activate explicitly (`dev` / `test` / `prod` / `stack`). Base `application.properties` does **not** default `spring.profiles.active=dev`.
  - `dev` / `stack`: seed via `DataInitializer`
  - `prod`: Postgres + Flyway; combine with `stack` in practica-stack compose (`prod,stack`)
- Maven profiles `dev`/`prod` only set a property hint; they do **not** copy YAML files.
- OpenAPI: springdoc 1.7 (`/swagger-ui.html` under context path).
- Method security: `@EnableGlobalMethodSecurity(prePostEnabled = true)` → use `hasRole(...)`.
