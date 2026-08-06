# Agents — practica

## Identity

- **Service:** microservicio-practica (parámetros)
- **Stack:** Spring Boot **2.7.18**, Java **17**, WAR, Spring Security + JJWT
- **Peers:** ms-auth (issues session JWT + RBAC claims), ms-frontend (Bearer consumer)
- **Council:** `../practica-stack/.ai/AGENTS.md` (fan-out Security · Spring · React · E2E)

## Scope

| In scope | Out of scope |
|----------|----------------|
| Parámetros CRUD API | Login / issuing tokens (ms-auth) |
| JWT validation of ms-auth session/master tokens | Mapping `permissions[]` → `PERM_*` |
| AuthZ via primary claim `role` → `ROLE_*` + `@PreAuthorize("hasRole('ADMIN')")` | Module RBAC admin UI (ms-auth + frontend) |
| H2 local / Postgres prod (+ profile `stack` seed) | Active Directory / LDAP (simulated in ms-auth) |

## Working agreements

1. Read `.ai/SECURITY.md` before changing auth filters or `@PreAuthorize`.
2. Spring config lives in `application*.properties` — do not restore antrun YAML copy.
3. Keep Maven compiler annotation processors (Lombok then MapStruct) in root `<build>`.
4. Port **8082**, context `/api`.
5. Do **not** claim this service enforces module permissions — that demo is ms-auth inventario.
