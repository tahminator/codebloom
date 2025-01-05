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

## View database data (Dbeaver)

Use Dbeaver to view all the different parts of the database like tables, columns and their types, data rows, etc.

(If you are accessing the production database through this, you might delete data so excercise caution.)

https://dbeaver.io/download/ download from here (make it a link)

Once you have it installed, you can add a database by right clicking on the left sidebar and Create > Connection.

Click PostgreSQL > Next

Fill in:

- Host
- Post
- Check Show All Databases
- Username
- Password
- Click Connection details and name the connection (for example: localhostdb if it's your local machine, codebloom-prod if it's the Codebloom production database, etc.)

Click Finish.
