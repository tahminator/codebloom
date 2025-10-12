set shell := ["bash", "-uc"]

# Migrate to local DB
migrate *args:
  dotenvx run -- ./mvnw flyway:migrate -Dflyway.locations=filesystem:./db {{args}}

# Migrate to CI DB
migrate-ci *args:
  dotenvx run -f .env.ci -- ./mvnw flyway:migrate -Dflyway.locations=filesystem:./db {{args}}

# Migrate to PROD DB (CAREFUL!!!)
migrate-prod *args:
  dotenvx run -f .env.production -- ./mvnw flyway:migrate -Dflyway.locations=filesystem:./db/migration {{args}}

# Run the backend Spring server
java-dev *args:
  dotenvx run -- ./mvnw -Dspring-boot.run.profiles=dev spring-boot:run {{args}}

# Run the backend Spring server with an exposed debugger at :5005
java-dev-debug *args:
  dotenvx run -- ./mvnw \
    -Dspring-boot.run.profiles=dev \
    -Dspring-boot.run.jvmArguments="-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=*:5005" \
    spring-boot:run {{args}}

# Run the frontend 
js-dev *args:
  cd js && pnpm run dev {{args}}

# Run the email development server
email-dev *args:
  cd email && pnpm i && pnpm email dev --dir emails {{args}}

# Generate HTML output of email and copy to Spring server
email-gen *args:
  ./email.sh {{args}}

# Generate types through OpenAPI
types-dev *args:
  cd js && pnpm run generate {{args}}

# Run the developmental server (backend & frontend)
dev *args:
  pnpm i -g concurrently && concurrently "just java-dev" "just js-dev" {{args}}

# Run the developmental server (backend & frontend) but the backend will launch a debugger server.
devd *args:
  pnpm i -g concurrently && concurrently "just java-dev-debug" "just js-dev" {{args}}

# Builds and installs Spring backend
backend-install *args:
  dotenvx run -- ./mvnw install -DskipTests=true {{args}}

# Run backend tests
backend-test *args:
  dotenvx run -- ./mvnw checkstyle:check test -Dspring.profiles.active=ci {{args}}
