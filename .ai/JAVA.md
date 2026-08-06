# Java

- **Language level:** Java 8 (`java.version=1.8` in pom).
- **CI JDK:** Temurin 17 compiling with source/target 1.8 (Boot 2.7 supports this).
- Avoid Java 9+ APIs (`List.of`, `var`, `.isBlank()`, etc.).
- Lombok 1.18.30 + MapStruct 1.5.5 — processor order: Lombok **before** MapStruct.
- Keep public methods short; validate at controller/DTO boundaries (`javax.validation`).
