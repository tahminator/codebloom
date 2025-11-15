# ==== Frontend Build Stage ====
FROM node:20 AS frontend-build

WORKDIR /js

# Copy frontend source code
COPY js/package.json js/pnpm-lock.yaml ./

# Fix a bug with corepack by installing corepack globally
RUN npm i -g corepack@latest

# Install dependencies
RUN corepack enable pnpm && pnpm i --frozen-lockfile

# Copy the rest of the frontend files
COPY js/ ./

# Build the frontend
RUN pnpm run build

# ==== Email Build Stage ====
FROM node:20 AS email-build

WORKDIR /email

COPY email/package.json email/pnpm-lock.yaml ./

RUN npm i -g corepack@latest

RUN corepack enable pnpm && pnpm i --frozen-lockfile

COPY email/ ./

# Export HTML to a directory
RUN pnpm email export --dir emails --outDir out

# ==== Backend Build Stage ====
FROM eclipse-temurin:25-jdk-jammy AS backend-build

WORKDIR /app

# Copy Maven Wrapper and pom.xml
COPY mvnw pom.xml ./
COPY .mvn/ .mvn/

ARG SENTRY_AUTH_TOKEN
ARG SENTRY_DSN
ARG SKIP
ARG VITE_DSN

RUN echo "SENTRY_AUTH_TOKEN is: ${SENTRY_AUTH_TOKEN:+set}" \
    && echo "SENTRY_DSN is: ${SENTRY_DSN:+set}" \
    && echo "SKIP is: ${SKIP:+set}" \
    && echo "VITE_DSN is: ${VITE_DSN:+set}"

# Download dependencies
RUN ./mvnw dependency:resolve -B


# Copy the backend source code
COPY src ./src

# Copy frontend build output to backend static resources
COPY --from=frontend-build /js/dist/ src/main/resources/static/

# Ensure email static directory exists and reset it
RUN mkdir -p src/main/resources/static/email 
RUN rm -f src/main/resources/static/email/*.html

# Copy email build output to backend static resources
COPY --from=email-build /email/out/*.html src/main/resources/static/email/

# Package the application
RUN ./mvnw clean package -DskipTests


# ==== Runtime Stage ====
FROM eclipse-temurin:25-jre-jammy

WORKDIR /app

# Copy the built JAR file
COPY --from=backend-build /app/target/*.jar app.jar

# Download any needed Playwright configurations.
COPY mvnw pom.xml ./
COPY .mvn/ .mvn/
RUN ./mvnw -B exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install-deps"
RUN ./mvnw -B exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install"

ARG SERVER_PROFILES=prod

# Expose backend port
EXPOSE 8080

# Start the application
ENTRYPOINT java -Dspring.profiles.active=${SERVER_PROFILES} -jar app.jar
