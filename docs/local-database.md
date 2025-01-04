# PostgreSQL Local Environment Setup

When developing on your local machine, it's important to avoid working directly with production data that users rely on. Setting up a local database on your machine provides a safe environment for development and testing.

## MacOS Setup with Postgres.app

The simplest way to set up PostgreSQL on macOS is using Postgres.app.

### Installation
1. Download and install from [https://postgresapp.com/](https://postgresapp.com/)
2. Follow the installation instructions on the website

### Authentication
Postgres.app uses "trust" authentication, which means:
- The password you enter in your application doesn't matter
- On first connection attempt, Postgres.app will prompt you to trust the specific program
- After allowing access once, it won't ask again for that program

## Docker Setup (MacOS or Windows)

If you prefer not to use Postgres.app or want to use Docker instead, refer to Patina's detailed guide on Docker setup:
[https://github.com/arklian/patina/blob/main/docs/postgres-on-docker.md](https://github.com/arklian/patina/blob/main/docs/postgres-on-docker.md)