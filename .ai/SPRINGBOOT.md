# Spring Boot

- **Version:** 2.7.15 (javax namespace, not jakarta).
- **Packaging:** WAR (`ServletInitializer` for external Tomcat; Boot plugin for executable).
- **Profiles:** `spring.profiles.active=dev` by default → `application-dev.properties`.
- Maven profiles `dev`/`prod` only set a property hint; they do **not** copy YAML files.
- OpenAPI: springdoc 1.7 (`/swagger-ui.html` under context path).
- Method security: `@EnableGlobalMethodSecurity(prePostEnabled = true)` → use `hasRole(...)`.
