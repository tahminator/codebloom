migrate:
	dotenvx run -- mvn flyway:migrate -Dflyway.locations=filesystem:./db

migrate-prod:
	dotenvx run -f .env.production -- mvn flyway:migrate -Dflyway.locations=filesystem:./db/migration

java-dev:
	dotenvx run -- mvn spring-boot:run

js-dev:
	cd js && pnpm run dev

dev:
	pnpm i -g concurrently && concurrently "make java-dev" "make js-dev"

backend-install:
	dotenvx run -- mvn install -DskipTests=true

backend-test:
	dotenvx run -- mvn test
