migrate:
	dotenvx run -- ./mvnw flyway:migrate -Dflyway.locations=filesystem:./db

migrate-ci:
	dotenvx run -f .env.ci -- ./mvnw flyway:migrate -Dflyway.locations=filesystem:./db

migrate-prod:
	dotenvx run -f .env.production -- ./mvnw flyway:migrate -Dflyway.locations=filesystem:./db/migration

java-dev:
	dotenvx run -- ./mvnw spring-boot:run

js-dev:
	cd js && pnpm run dev

dev:
	pnpm i -g concurrently && concurrently "make java-dev" "make js-dev"

backend-install:
	dotenvx run -- ./mvnw install -DskipTests=true

backend-test:
	dotenvx run -- ./mvnw checkstyle:check test -Dspring.profiles.active=ci
