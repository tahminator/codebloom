set shell := ["bash", "-uc"]

# Migrate to local DB
migrate *args:
  dotenvx run -- ./mvnw flyway:migrate -Dflyway.locations=filesystem:./db {{args}}

# Manually migrate PROD DB if needed (CAREFUL!!!)
migrate-prod *args:
  dotenvx run -f .env.production -- ./mvnw flyway:migrate -Dflyway.locations=filesystem:./db/migration {{args}}

# Manually copy production db to staging. (WILL DROP ALL STAGING DATA!!!) Check wiki for more details.
copy-stg *args:
  dotenvx run -f infra/.env.production -- bash infra/copy-prod-db.sh

# Run the backend Spring server
backend-dev *args:
  dotenvx run -- ./mvnw -Dspring-boot.run.profiles=dev spring-boot:run {{args}}

# Run the backend Spring server with an exposed debugger at :5005
backend-dev-debug *args:
  dotenvx run -- ./mvnw \
    -Dspring-boot.run.profiles=dev \
    -Dspring-boot.run.jvmArguments="-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=*:5005" \
    spring-boot:run {{args}}

# Builds and installs Spring backend
backend-install *args:
  ./mvnw install -DskipTests=true {{args}}

# Run backend tests
backend-test *args:
  dotenvx run -- ./mvnw checkstyle:check verify -Dspring.profiles.active=ci {{args}}

backend-coverage *args:
  just backend-test && open target/site/jacoco/index.html

# Run the frontend 
frontend-dev *args:
  cd js && pnpm run dev {{args}}

# Builds and installs frontend packages
frontend-install *args:
  cd js && pnpm i 

# Frontend tests 
frontend-test *args:
  cd js && pnpm run test

# Generate types through OpenAPI
type-gen *args:
  cd js && pnpm run generate {{args}}

# Run the react-email development server
email-dev *args:
  cd email && pnpm i && pnpm email dev --dir emails {{args}}

# Generate HTML output of react-email and copy to backend static folder
email-gen *args:
  cd email && bash email.sh {{args}}

# Run the dev servers (backend & frontend)
dev *args:
  pnpm i -g concurrently && concurrently "just backend-dev" "just frontend-dev" {{args}}

# Run the dev servers (backend & frontend) but the backend will launch a debugger server.
devd *args:
  pnpm i -g concurrently && concurrently "just backend-dev-debug" "just frontend-dev" {{args}}

# Triggers a deploy to staging command for the following environment. Must be authenticated on gh.
# NOTE: Triggering staging deployment this way can cause a buildup of tasks (broken concurrency group).
# As such, this should only be used in one branch at a time.
ci-test-stg pr_name pr_id:
  gh workflow run .github/workflows/deploy-stg.yml --ref {{pr_name}} --field prId={{pr_id}}

