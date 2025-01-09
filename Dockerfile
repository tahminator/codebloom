# ==== Frontend Build Stage ====
FROM node:20 AS frontend-build

WORKDIR /js

# Copy frontend source code
COPY js/package.json js/pnpm-lock.yaml ./

# Install dependencies
RUN corepack enable pnpm && pnpm i --frozen-lockfile

# Copy the rest of the frontend files
COPY js/ ./

# Build the frontend
RUN npm run build

# ==== Backend Build Stage ====
FROM openjdk:21-jdk AS backend-build

WORKDIR /app

# Copy Maven Wrapper and pom.xml
COPY mvnw pom.xml ./
COPY .mvn/ .mvn/

# Download dependencies
RUN ./mvnw dependency:go-offline -B

# Copy the backend source code
COPY src ./src

# Copy frontend build output to backend static resources
COPY --from=frontend-build /js/dist/ src/main/resources/static/

# Package the application
RUN ./mvnw clean package -DskipTests


# ==== Runtime Stage ====
FROM openjdk:21-jdk

WORKDIR /app

# Copy the built JAR file
COPY --from=backend-build /app/target/*.jar app.jar

# Expose backend port
EXPOSE 8080

# Start the application
ENTRYPOINT ["java", "-jar", "app.jar"]