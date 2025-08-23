set shell := ["bash", "-uc"]

migrate *args:
  dotenvx run -- ./mvnw flyway:migrate -Dflyway.locations=filesystem:./db {{args}}

migrate-ci *args:
  dotenvx run -f .env.ci -- ./mvnw flyway:migrate -Dflyway.locations=filesystem:./db {{args}}

migrate-prod *args:
  dotenvx run -f .env.production -- ./mvnw flyway:migrate -Dflyway.locations=filesystem:./db/migration {{args}}

java-dev *args:
  dotenvx run -- ./mvnw -Dspring-boot.run.profiles=dev spring-boot:run {{args}}

js-dev *args:
  cd js && pnpm run dev {{args}}

types-dev *args:
  cd js && pnpm run generate {{args}}

dev *args:
  pnpm i -g concurrently && concurrently "just java-dev" "just js-dev" {{args}}

backend-install *args:
  dotenvx run -- ./mvnw install -DskipTests=true {{args}}

backend-test *args:
  dotenvx run -- ./mvnw checkstyle:check test -Dspring.profiles.active=ci {{args}}