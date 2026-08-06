# Security

## JWT (ms-auth alignment)

- Session secret: `app.jwt.secret=${APP_JWT_SECRET_SESSION}` (no nested fallback — breaks under Docker env)
- Master secret: `app.jwt.secret-master=${APP_JWT_SECRET_MASTER:}` (optional; required for Feign)
- Validate HS256: try session key first, then master key.
- Claim `role` (e.g. `ADMIN`) → authority `ROLE_ADMIN` in `JwtAuthFilter`.
- Use `@PreAuthorize("hasRole('ADMIN')")` (not raw `hasAuthority('ADMIN')`).
- If claim `tokenType` is present, accept `SESSION` or `MASTER`; missing claim = accept (compat).
- MASTER tokens are for ms-auth Feign (`createParameter`); SPA uses SESSION.
- Extra ms-auth claims (`roles[]`, `permissions[]`, `groups[]`) are **ignored** — JJWT accepts them; this service does **not** map them to `PERM_*` authorities. Module AuthZ demo lives in ms-auth (`/api/demo/inventario`).

## HTTP security

- Stateless session; JWT filter before `UsernamePasswordAuthenticationFilter`.
- `/parametros/**` authenticated; Swagger/docs permitAll; others denyAll.
- 401 entry point (no/invalid JWT) · 403 accessDeniedHandler + `@ExceptionHandler(AccessDeniedException)` for `@PreAuthorize` denials (must not surface as 500).
- H2 console permitAll + `frameOptions().sameOrigin()` only when `spring.h2.console.enabled=true` (dev).
