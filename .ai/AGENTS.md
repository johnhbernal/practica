# Agents — practica

## Identity

- **Service:** microservicio-practica (parámetros)
- **Stack:** Spring Boot 2.7.15, Java 8, WAR, Spring Security + JJWT
- **Peers:** ms-auth (issues session JWT), ms-frontend (Bearer consumer)

## Scope

| In scope | Out of scope |
|----------|----------------|
| Parámetros CRUD API | User auth / login (ms-auth) |
| JWT validation of ms-auth session tokens | Issuing tokens |
| H2 local / Postgres prod config | Active Directory / LDAP |

## Working agreements

1. Read `.ai/SECURITY.md` before changing auth filters or `@PreAuthorize`.
2. Spring config lives in `application*.properties` — do not restore antrun YAML copy.
3. Keep Maven compiler annotation processors (Lombok then MapStruct) in root `<build>`.
4. Port **8082**, context `/api`.
