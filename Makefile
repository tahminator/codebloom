migrate:
	dotenvx run -- ./mvnw flyway:migrate -Dflyway.locations=filesystem:./db

migrate-ci:
	dotenvx run -f .env.ci -- ./mvnw flyway:migrate -Dflyway.locations=filesystem:./db

migrate-prod:
	dotenvx run -f .env.production -- ./mvnw flyway:migrate -Dflyway.locations=filesystem:./db/migration

migrate-prod-no-env:
	./mvnw flyway:migrate -Dflyway.locations=filesystem:./db

java-dev:
	dotenvx run -- ./mvnw -Dspring-boot.run.profiles=dev spring-boot:run

js-dev:
	cd js && pnpm run dev

types-dev:
	cd js && pnpm run generate

dev:
	pnpm i -g concurrently && concurrently "make java-dev" "make js-dev"

backend-install:
	dotenvx run -- ./mvnw install -DskipTests=true

backend-test:
	dotenvx run -- ./mvnw checkstyle:check test -Dspring.profiles.active=ci
