# Database Migrations

**TLDR**: If you are looking for the command to run migrations on your local machine, you can either run

`./migrate.sh`

or

just migrate

or

`dotenvx run -- mvn flyway:migrate -Dflyway.locations=filesystem:./db/migration`.

To run production migrations (BE CAREFUL WITH THIS IF YOU HAVE PRODUCTION ACCESS), it is:

`dotenvx run -f .env.production -- mvn flyway:migrate -Dflyway.locations=filesystem:./db/migration`

[You must manually fill .env.production yourself if you have the required access to the secrets.]

We use Flyway to handle our database migrations. However, we perform migrations manually, so it's crucial to exercise extreme caution when dealing with data.

View the current migrations folder [here](https://github.com/tahminator/codebloom/tree/main/db)

## Mock Data

There is a repeatable script inside of the db folder that should run if your database is completely empty.

## Migration File Naming

Migration files must follow this naming pattern:

```
V{version}__{description}.sql
```

Examples:

-   `V1__create_users_table.sql`
-   `V2__add_email_column_to_users.sql`
-   `V3__create_posts_table.sql`

### Naming Requirements

-   Version numbers must be sequential and unique
-   Double underscores (`__`) separate the version from the description
-   Use underscores (`_`) instead of spaces in descriptions
-   Files must have `.sql` extension

## Types of Migrations

### Version Migrations (V)

-   Standard version migration
-   Runs exactly once

### Repeatable Migrations (R)

-   TODO(Tahmid)

### Undo Migrations (U)

-   Revert from the standard version migrations if needed

## Best Practices

Do not feel pressured to squeeze multiple changes into a single .sql file. It's better to have multiple focused migration files than one large file with many changes.

**Important:** Always test migrations in a development environment first, and **do not** commit migrations to production without discussing your changes with another team member.

## Running Migrations

### Using Maven

You can run Flyway commands through Maven (mvn) like this:

```bash
# Runs any pending migrations
mvn flyway:migrate
```

### Using Environment Variables

Always pipe in the .env file when running migrations using dotenx:

```bash
# dotenvx will automatically find the .env file as long as you are typing commands in the root directory
dotenvx run -- mvn flyway:migrate -X
# -X shows debug information
```

If you are missing dotenvx, please check `local.md` to make sure all of your tools are up to date.

## Common Commands

### Check Migration Status

```bash
mvn flyway:info
```

### Apply All Pending Migrations

```bash
mvn flyway:migrate
```

### Revert Last Migration

```bash
mvn flyway:undo
```

### Clean Database (Use with Caution!)

```bash
mvn flyway:clean
```

### Validate Migrations

```bash
mvn flyway:validate
```

## Transactions

Flyway automatically wraps the .sql files in transactions. This means that if any part of a .sql file fails to execute, none of the changes in that file will be applied. This helps maintain database consistency and prevents partial updates.
