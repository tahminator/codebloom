migrate:
	dotenvx run -- mvn flyway:migrate -Dflyway.locations=filesystem:./db

migrate-prod:
	dotenvx run -f .env.production -- mvn flyway:migrate -Dflyway.locations=filesystem:./db/migration

java-dev:
	mvn spring-boot:run

js-dev:
	cd js && pnpm run dev

dev:
	pnpm i -g install && concurrently "make java-dev" "make js-dev"