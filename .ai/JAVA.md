# Java

- **Language level:** Java **17** (`java.version=17` in pom — parity with ms-auth).
- **CI JDK:** Temurin 17.
- Prefer APIs available on Java 17; keep style consistent with existing code.
- Lombok 1.18.30 + MapStruct 1.5.5 — processor order: Lombok **before** MapStruct.
- Keep public methods short; validate at controller/DTO boundaries (`javax.validation`).
