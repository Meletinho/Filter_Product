# Filter Product — Spring Boot

- Projeto inicial em Spring Boot 3.3.x, Java 21.
- Estrutura de pacotes: `domain`, `application`, `integration`, `infrastructure`.
- Configuração via `application.yml` com variáveis `DB_URL`, `DB_USER`, `DB_PASS`.

## Executar
- `mvn clean package -DskipTests`
- `java -jar target/filter-product-0.0.1-SNAPSHOT.jar`

## Migrations
- Coloque scripts em `src/main/resources/db/migration`.

## Observabilidade
- Endpoints: `GET /actuator/health`, `GET /actuator/prometheus`.
