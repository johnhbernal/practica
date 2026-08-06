# Database

## Dev / CI

- H2 in-memory via `application-dev.properties`:
  - URL: `jdbc:h2:mem:practica_dev;...`
  - User `sa`, empty password
  - `ddl-auto=update`
- No Postgres credentials required for local run or GitHub Actions.

## Production

- Documented env vars in `application.properties` comments / `.env.example`:
  - `SPRING_DATASOURCE_URL`
  - `SPRING_DATASOURCE_USERNAME`
  - `SPRING_DATASOURCE_PASSWORD`
- Do not enable Postgres in the default profile without credentials.
