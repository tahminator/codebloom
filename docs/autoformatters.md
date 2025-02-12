# Autoformatters

## Frontend

In the frontend, we are using:

- ESLint, which catches JavaScript/TypeScript code issues.
- Stylelint, which catches CSS and styling issues.
- Prettier, which formats code for consistency.

These will help us maintain good code quality and ensure readability and consistency.

To run the entire suite of tests, just run:

```bash
pnpm run test
```

And if there are any errors, be sure to fix it.

#### Autofixes

If you have VSCode, make sure you have the following installed:

- Prettier - Code formatter
- Pretter ESLint
- ESLint
- Stylelint
- PostCSS Language Support
- PostCSS Intellisense and Highlighting

There are also some automatic settings applied within the workspace for VS Code users that will automatically format and fix your code for you.

Please make sure you run these tests before you commit, as the CI/CD pipeline will fail the deployment if it doesn't pass the full suite of tests.

## Backend

In the backend, we are using:

- Language Support for Java(TM) by Red Hat, which will format Java code using the java-formatter.xml file.
- Checkstyle, which enforces Java code issues using the checkstyle.xml file.

To run the full suite of tests, just run:

```bash
mvn test checkstyle:check
```

And if there are any errors, be sure to fix it.

#### Autofixes

If you have VSCode, make sure you have the following installed:

- Language Support for Java(TM) by Red Hat
- Checkstyle for Java

Unfortunately, there is no autofix on save, but if the error is easily fixable, you can use Quick Fix to resolve instead.

Please make sure you run these tests before you commit, as the CI/CD pipeline will fail the deployment if it doesn't pass the full suite of tests.
