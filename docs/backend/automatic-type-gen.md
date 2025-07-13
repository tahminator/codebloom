# Automatic type generation

We use `openapi-typescript` to read our OpenAPI schema from the backend and convert into a schema file for the frontend to use and consume.

This command only runs locally, and the generated schema file is checked into version control. The command is run once per start (this includes reloading the local server from file changes) via [JSTypesGenerator.java](https://github.com/tahminator/codebloom/blob/fix-type-gen-infinite-loop/src/main/java/com/patina/codebloom/utilities/JSTypesGenerator.java).

WIP

For more details about the actual command or how the schema is used on the frontend, check [the frontend document](https://github.com/tahminator/codebloom/tree/fix-type-gen-infinite-loop)
