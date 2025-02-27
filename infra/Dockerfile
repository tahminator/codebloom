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
FROM sapmachine:21-jdk-ubuntu-24.04 AS backend-build

WORKDIR /app

# Copy Maven Wrapper and pom.xml
COPY mvnw pom.xml ./
COPY .mvn/ .mvn/

# Download dependencies
RUN ./mvnw dependency:resolve -B


# Copy the backend source code
COPY src ./src

# Copy frontend build output to backend static resources
COPY --from=frontend-build /js/dist/ src/main/resources/static/

# Package the application
RUN ./mvnw clean package -DskipTests


# ==== Runtime Stage ====
FROM sapmachine:21-jdk-ubuntu-24.04

WORKDIR /app

# Copy the built JAR file
COPY --from=backend-build /app/target/*.jar app.jar

# Download any needed Playwright configurations.
COPY mvnw pom.xml ./
COPY .mvn/ .mvn/
RUN ./mvnw exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install --with-deps"



# Expose backend port
EXPOSE 8080

# Start the application
ENTRYPOINT ["java", "-jar", "app.jar"]
