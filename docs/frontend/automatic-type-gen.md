# Automatic type generation

We use `openapi-typescript` to read our OpenAPI schema from the backend and convert into a schema file for the frontend to use and consume.

There are two scripts inside of the `package.json`:

- `generate` - Runs the generation script one time

- `generate:dev` - Runs the generation every time any `*.java` file changes (NOT RECOMMENDED)

The command requires the server to be running, but in most cases you won't have to worry about that because the backend already runs this command anytime the dev server restarts.

WIP

For more details about how the backend runs the script or how it generates the schema, check [the frontend document](https://github.com/tahminator/codebloom/tree/fix-type-gen-infinite-loop)
