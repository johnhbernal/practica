# Active Directory / LDAP

**Out of scope** for this service.

Identity, simulated AD (groups → roles → permissions), and module RBAC belong to **ms-auth**. See `../ms-auth/.ai/ACTIVE-DIRECTORY.md`.

practica only validates the session/master JWT that ms-auth issues and enforces `@PreAuthorize("hasRole('ADMIN')")` on write/list-all parameter endpoints.
