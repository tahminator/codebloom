# Installation and Setup Guide

## Required Packages

### Core Requirements

- **OpenJDK 21**: Java Runtime to run our backend Java code
- **Maven**: Package manager to manage all our Java dependencies
- **Dotenvx**: Run commands using the root .env file instead of permanently saving environment variables into ENV (which would be risky). One place we use it is for database migrations via Flyway.
- **Node.js**: Javascript runtime to run our frontend TypeScript code
- **Pnpm**: Package manager that works faster than the default `npm` package manager

## Installation Instructions

### MacOS

#### Prerequisites

Before starting, you'll need Homebrew installed. Get it from https://brew.sh/

#### Installation Steps

1. Install OpenJDK 21:

   ```zsh
   brew install openjdk@21
   ```

2. Install Maven:

   ```zsh
   brew install maven
   ```

3. Install Node.js:

   ```zsh
   brew install node
   ```

4. Install pnpm:

   ```zsh
   corepack enable pnpm
   ```

   Note: Corepack is a tool that helps manage package manager versions. [Learn more about Corepack](https://medium.com/@rohitdeshpande9922/corepack-managing-the-package-managers-d3d4d82f05c2)
   <br />

5. Install dotenvx:

   ```zsh
   brew install dotenvx/brew/dotenvx
   ```

6. Install frontend packages:
   ```zsh
   cd js && pnpm install
   ```

### Windows

Install the same packages in the same order, but download them from their respective websites instead of using Homebrew.

## VSCode Setup

While you can use any editor, VSCode is recommended as it can handle multiple languages effectively. Here are the recommended extensions:

### Required Extensions

- **Prettier**: Javascript formatter
  - Helps maintain consistent styling
  - Configure format on save [following these instructions](https://stackoverflow.com/questions/39494277/how-do-you-format-code-on-save-in-vs-code)
- **ESLint**: Javascript linter
  - Integrates with your project's ESLint configuration
- **Babel JavaScript**: Improves JSX syntax highlighting
- **Docker**: Provides Dockerfile IntelliSense
- **DotENV**: .env file syntax highlighting
- **Pretty Typescript Errors**: Simplifies complex TypeScript error messages
- **Extension Pack for Java**:
  - Includes debuggers, formatters, and managers
  - Supports format on save
- **Spring Boot Extension Pack**: Additional Spring Boot-specific tooling
- **SQL Formatter**: Formats SQL files with format on save
- **Tailwind CSS IntelliSense**: Provides intelligent suggestions for Tailwind classes
- **XML by RedHat**:
  - Official XML language support and formatter
  - Important for editing Java XML files like pom.xml

## Running the Project

### Backend

1. Install dependencies:

   ```bash
   mvn clean install
   ```

2. Start development server:

   - Open `CodebloomApplication.java`
   - Click "Run"

3. Environment Setup:
   - Populate your .env file with required values
   - Contact Tahmid if you need access to certain secrets
   - For local database setup, refer to `/local-database.md`
   - Find more information about the backend in the `backend` folder which you can go to by clicking [here](https://github.com/tahminator/tree/main/docs/backend).

### Frontend

1. Important: Always ensure you're in the `js` folder when working with frontend packages

   ```bash
   # As an example
   cd js
   pnpm install zod
   ```

2. Start development server:

   ```bash
   pnpm run dev
   ```

3. Additional frontend documentation can be found in the `frontend` folder [here](https://github.com/tahminator/codebloom/tree/main/docs/frontend).
