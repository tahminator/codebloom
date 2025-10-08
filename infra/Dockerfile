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

# ==== Backend Build Stage ====
FROM eclipse-temurin:21-jdk-jammy AS backend-build

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

# Copy react email output to backend static resources
RUN corepack enable pnpm && cd email && pnpm i --frozen-lockfile && cd ..
RUN ./email.sh

# Package the application
RUN ./mvnw clean package -DskipTests


# ==== Runtime Stage ====
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# Copy the built JAR file
COPY --from=backend-build /app/target/*.jar app.jar

# Download any needed Playwright configurations.
COPY mvnw pom.xml ./
COPY .mvn/ .mvn/
RUN ./mvnw exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install --with-deps firefox"

# Expose backend port
EXPOSE 8080

# Start the application
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "app.jar"]
