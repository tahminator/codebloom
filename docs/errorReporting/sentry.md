# Error Reporting with Sentry

We use [Sentry](https://sentry.io) to monitor and report errors in both our frontend and backend applications. This enables us to track, investigate, and resolve issues quickly, improving reliability and user experience.

## Overview

Sentry is configured as follows:

-   **Frontend (React + Vite)**: Captures client-side errors, including rendering issues, unhandled exceptions, and performance bottlenecks.
-   **Backend (Spring Boot)**: Captures server-side exceptions, request errors, and system failures.

---

## Frontend Configuration (React + Vite)

### Setup

The frontend uses Sentry’s official React SDK, configured for a Vite-based application.

## Backend Configuration (Spring Boot)

### Setup

The backend uses Sentry’s Java SDK, integrated with Spring Boot.
