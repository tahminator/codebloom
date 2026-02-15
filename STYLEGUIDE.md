

<!-- toc -->

- [Backend](#backend)
  * [Naming conventions](#naming-conventions)
  * [Abstraction / Code Structure](#abstraction--code-structure)
  * [Documentation](#documentation)
  * [SQL](#sql)
  * [Database Repository Best Practices](#database-repository-best-practices)
    + [Location of files should be in the `common/db` folder](#location-of-files-should-be-in-the-commondb-folder)
    + [Use interface pattern](#use-interface-pattern)
    + [Stick to using Lombok for the Java database models](#stick-to-using-lombok-for-the-java-database-models)
    + [Repository Rules](#repository-rules)
      - [**_Create_**](#_create_)
      - [**Read/Find**](#readfind)
      - [**Update**](#update)
      - [**Delete**](#delete)
      - [**Exceptions**](#exceptions)
    + [Examples](#examples)
  * [Error Handling](#error-handling)
  * [Testing](#testing)
    + [Location](#location)
    + [Mocking](#mocking)
- [Frontend](#frontend)
  * [Routing](#routing)
  * [Naming Conventions](#naming-conventions)
    + [TypeScript Files](#typescript-files)
    + [Function Naming](#function-naming)
  * [Folder Naming Conventions](#folder-naming-conventions)
  * [Separation of Concerns](#separation-of-concerns)
  * [React Query](#react-query)
  * [Comments](#comments)
  * [Styling](#styling)
  * [Typesafety](#typesafety)
    + [`schema.ts`](#schemats)
    + [ApiURL](#apiurl)
      - [Intent and Usage](#intent-and-usage)
      - [POST request example](#post-request-example)
      - [Dynamic path example](#dynamic-path-example)

<!-- tocstop -->

<!-- Run `markdown-toc -i STYLEGUIDE.md to re-generate` -->

# Backend

## Naming conventions

1. All Java files must match the name of their class/interface/enum. So `src/main/java/org/patinanetwork/codebloom/CodebloomApplication.java` will expose the class CodebloomApplication.
1. All Java classes/interfaces/enums must be in PascalCase, while actual functions, methods, and variables can be written in camelCase.

## Abstraction / Code Structure

It's a little difficult to draw the line between what should be grouped together, so here are my thoughts organized as best as I can. Keep in mind that this is not strictly enforced, and that these thoughts may change.

1. All controllers should be grouped by service, such as the `AuthController`, `LeaderboardController`, `SubmissionController`. Considering the gravity of Codebloom, it may be inevitable that there will be some bleed between the jurisdiction of each controller. If that is the case, it isn't an issue; just make sure it's documented so that the rest of the team can understand what you did.

1. You can also group Request bodies and Response bodies within the same folder of the controller as long as you group it by a `body` folder. For example, check this [example](./src/main/java/org/patinanetwork/codebloom/api/submission).

1. Classes/Objects that can be shared across many different boundaries, such as database items and repositories or the LeetCode API client should be stored in the `common` folder. This isn't too strict, and it's never a bad idea to ask me what should and should not go in there.

1. Scheduled tasks should go in the `/scheduled` folder, so it's not polluting the rest of the codebase. Check it out [here](./src/main/java/org/patinanetwork/codebloom/scheduled).

1. The `utilities` folder is used to setup things that the core application may use that the actual user code doesn't **really** touch, like loading the frontend files or a global exception handler.

1. The `jda` folder is used to hold the Discord bot implementation. This is also considered a client, where it exports some functions to actually use the Discord bot within the actual web logic, but it should not be directly imported and worked with. (This is not currently true right now due to our initial goal of just getting it to work, but once this is abstracted out it should stay that way.)

## Documentation

**TL:DR - If you want to access the Swagger documentation locally, run the backend development server and go to**

`localhost:8080/swagger-ui.html`

> [!NOTE]
> As of recently, Codebloom has turned on Swagger documentation in production and staging.
>
> - [codebloom.patinanetwork.org/swagger-ui.html](https://codebloom.patinanetwork.org/swagger-ui.html)
> - [stg.codebloom.patinanetwork.org/swagger-ui.html](https://stg.codebloom.patinanetwork.org/swagger-ui.html)

- _Only for controllers_ - These documentations are automatically generated within your code, so just take a look at [this](./src/main/java/org/patinanetwork/codebloom/api/user/UserController.java) on what a good example of auto-documentation should look like. However, this is one of those things that just run really deep so if you have never used `Springdoc` before, I think it would be better to have a quick one on one so we can see eye to eye.

- For non-controllers, you can always use Javadoc comments on a class/function to help clarify details that may be important. Click [here](./src/main/java/org/patinanetwork/codebloom/common/simpleredis/SimpleRedis.java) and [here](./src/main/java/org/patinanetwork/codebloom/common/db/repos/leaderboard/LeaderboardRepository.java) to see a very good example of clear documentation for a class or a function that may need it. This is not strictly enforced, but a good rule of thumb is if someone else can't just easily read it and understand it, you should probably comment it.

## SQL

- I think it's probably best to come through the code and see what each repository can do (in terms of the interface file/what the outside world can use). If you have to actually edit any implementation of the repository, it may be a good idea to view a diagram of the database with `Dbeaver` or any other DB viewer tool. This will help you realize how the data is connected.

- Furthermore, you can always copy the SQL statement from any repository and run it via `Dbeaver` or any other DB tool (you just have to replace the `?` with actual values) to see what it should return. This way, you can also modify the query and figure out what each part of the query is doing.

- We want to **avoid** SQL complexity, even if it means our data fetching is not as efficient. It is very hard to understand 100+ line SQL queries. So, our solution is to use JOINS to get the bare minimum information we did to setup the flow of our data (like for example, getting the `totalScore` from the `Metadata` table and joining it on `Metadata.userId = User.id`), but as for actually fetching the user data, you should instead inject the repository needed to fetch the data through our backend instead. This seems complicated, so maybe an [example](./src/main/java/org/patinanetwork/codebloom/common/db/repos/leaderboard/LeaderboardSqlRepository.java#L96-L155) may be easier to understand.

- If you don't know what JOINs are, here's a good [link](https://www.w3schools.com/Sql/sql_join.asp) to get you exposed to the idea of how it should work. The most important joins are (INNER) JOIN & LEFT JOIN, but a little knowledge of FULL OUTER JOIN is good too. You should also get in touch with me so I can give you a crash course on how they actually work.

## Database Repository Best Practices

This document should represent the best practices for writing/creating repositories inside of the backend that interface with the database layer.

**NOTE - All examples will assume that the database table name is `Agent`**

1. ### Location of files should be in the `common/db` folder

    - Repositories should be placed into `common/db/repos/*`
    - Models should be placed into `common/db/models/*`

2. ### Use interface pattern

    - The interface file should be named `AgentRepository`
    - The implementation should be named `AgentSqlRepository`.

3. ### Stick to using Lombok for the Java database models

    - Required annotations:
        - `@Getter` - Create getter methods for all variables: `getId()`
        - `@Setter` - Create setter methods for all variables: `setId("4819e35f-003b-4ad5-930f-6cd6a6102623")`. If the class is immutable, `@Setter` is not required.
        - `@ToString` - Generates a `toString()` method that will, by default, include all variable names. If you need to override this behavior, please check Lombok documentation on how to do so.
        - `@EqualsAndHashCode` - Generates an `equals()` method, _which requires `hashcode()` method, which is why it's included_, which allows you to compare object equalities. This is very important when writing tests.
        - `@Builder` - Generates a `ClassName.builder()` which lets you use a builder pattern to create the object instead:
            - You may need `@SuperBuilder` instead if the class is extending from another base class which has `@Builder`.

            ```java
            Agent agent = Agent.builder().id("79e1d624-ab4f-4a28-9178-08f5a8bc4641").name("James Bond").build()
            ```

        - `@Jacksonized` - This is required so a class can be de-serialized if it's ever converted from a JSON string into a Java object. It is best to always add this annotation.

    - **NOTE - Do not use @Data. The annotation has too much scope, and it's better to just use the annotations you were going to apply.**
    - Example file of a database object in Java:

        ```java
        @Getter
        @Setter
        @Builder
        @EqualsAndHashCode
        @ToString
        public class Agent {
            private String id;
            private String name;
            // Optional indicates that it can be null.
            private Optional<String> email;
        }
        ```

4. ### Repository Rules

    Follow these rules when it comes to repositories:

   1. #### **_Create_**

        - The function for creating a new database object should always return void. Instead, the function should accept an input of the database object, and in the implementation, use the setters on the object to update any new values from the database. **NOTE - You should make this specific behavior clear in the interface file for the specific creation method**
        - If you accept the object itself as an input (which you almost always should), make sure to leave a note about what fields are required to be
          set inside of the object.
        - Below is an example of what it should look like:

        ```java
        // AgentRepository.java

        public interface AgentRepository {
            // ...

            /**
             * @note - The provided object's methods will be overridden with any returned
             * data from the database.
             *
             * @param agent - required fields:
             * <ul>
             * <li>id</li>
             * <li>type</li>
             * </ul>
             * optional fields:
             * <ul>
             * <li>email</li>
             * </ul>
             */
            void createAgent(Agent agent);

            // ...
        }
        ```

   2. #### **Read/Find**

        - Read functions should indicate the required values in order to find the row to read (e.g. `byId` or `byLeaderboardName`).
        - Read functions that can be `null` if it doesn't exist, then it should return `Optional<T>` instead of just `T`.
        - Read methods should return the database model object, but you should use a private method to standardize the way that the object is read from the ResultSet. Here is an example:

            ```java
            // in AgentSqlRepository.java

            @Component
            public class AgentSqlRepository implements AgentRepository {
                // ...

                private parseResultSetToAgent(final ResultSet rs) {
                    return Agent.builder()
                                .id(rs.getString("id"))
                                .name(rs.getString("name"))
                                .email(Optional.ofNullable(rs.getString("email"))
                                .build();
                }

                @Override
                public Optional<Agent> getAgentById(final String id) {
                    // ...

                    if (rs.next()) {
                      return parseResultSetToAgent(rs);
                    }

                    // ...
                }

            }
            ```

   3. #### **Update**

        - For update functions, you should return a boolean depending on whether or not the operation was successful or not, but you should also refer to the **Create** operation and replace any values on the object if required. The function name should include what the function will use to search for the row to update (e.g. `byId` or `byUserId`), but you should still pass in the entire Object if it may need to be updated.
        - Use Javadoc to indicate what fields will be updated, like in the example below:

        ```java
        // AgentRepository.java

        public interface AgentRepository {
            // ...

            /**
             * @note - The provided object's methods will be overridden with any returned
             * data from the database.
             *
             * @param agent - overridable fields:
             * <ul>
             * <li>name</li>
             * <li>email</li>
             * </ul>
             */
            boolean updateAgentById(Agent agent);

            // ...
        }
        ```

   4. #### **Delete**

        - For delete operations, you should return a boolean depending on whether or not the operation was successful or not. The entire Object is not required to be passed in, just what the function requires to find the row to delete.

        ```java
        // AgentRepository.java

        public interface AgentRepository {
            // ...

            boolean deleteAgentById(String id);

            // ...
        }
        ```

   5. #### **Exceptions**

        - There is a chance you may need to implement a method that doesn't fall neatly into the standards, such as searching for an external object ID (e.g. finding UserTags by using an userId). These are completely reasonable, and it's well within means to attempt a solution, which can get revised during pull requests.

5. ### Examples

    - [Lobby table](./src/main/java/org/patinanetwork/codebloom/common/db/repos/lobby/LobbyRepository.java)
    - [LobbyQuestion table](./src/main/java/org/patinanetwork/codebloom/common/db/repos/lobby/LobbyQuestionRepository.java)

## Error Handling

We use a structured logging framework in production, so it is important that you do not use `e.printStackTrace()` like so:

```java
try {
    // ...
} catch (Exception e) {
    e.printStackTrace();
}
```

Instead, you should import `@Slf4j` and apply it to the given class, then simply do:

```java
try {
    // ...
} catch (Exception e) {
    log.error("Exception thrown in ABClass.xyZFunction", e);
}
```

## Testing

### Location

All tests should match the same package as the class it is testing + the same class name + some variation of the word `Test`.

For example, if we are testing

```java
package org.patinanetwork.codebloom.common.simpleredis

public class SimpleRedis {
    // ...
}
```

then our test file can be

```java
package org.patinanetwork.codebloom.common.simpleredis

public class SimpleRedisTest { // if you have multiple test files, you can add word(s) between the class name and the word test. e.g SimpleRedisIntegrationTest, SimpleRedisAcceptanceTest, SimpleRedisRegressionTest, etc.
    // ...
}
```

### Mocking

When mocking dependencies for a test file you should follow this pattern:

```java
import static org.mockito.Mockito.*;

public class XYZTest {
    private final Dep1 dep1 = mock(Dep1.class);
    private final Dep2 dep2 = mock(Dep2.class);

    private final XYZ xyz;

    public XYZTest() {
        xyz = new XYZ(dep1, dep2);
    }
}
```

By default, Mockito will re-run the mocks for each `@Test` method, so we stick with this pattern to explicitly force the understanding of a class's behavior(s).

# Frontend

## Routing

All routes must be within the app directory.

The entry point for the route should always end in `.page.tsx` (ex. `Root.page.tsx`)

The folder names should represent the route. (ex. If you want to create a `/blog`, you should create a folder called `blog` inside of the `app` directory, then make a `CallMeAnything.page.tsx` file. Import that function into `/lib/router.tsx` to actually attach the routes.)

Dynamic routes, where a specific part of the route can be changed, can be created by covering the file name with a bracket.
(ex. If you want to create `/blog/:blogId`, you should create the folder like so `/app/blog/[blogId]`, then make a `*.Page.tsx` file. Make sure to import that function to `lib/router.tsx` to actually attach the routes. In order to use the route, you can use this function from the page-level entry point:

```tsx
const { blogId } = useParams();
```

If you need to utilize more complex routing behaviors, visit the docs [here](https://reactrouter.com/6.29.0/route/route).

To make it clear that a folder and ALL it's children is not part of the route, append a `_` at the start of the file route. (ex. `/app/dashboard/_components`)

Examples:

- `/app/Root.page.tsx` → `/`
- `/app/dashboard/Dashboard.page.tsx` → `/dashboard`
- `/app/submission/s/[submissionId]/SubmissionDetails.page.tsx` → `/submission/s/[submissionId]`

```tsx
export const router = createBrowserRouter([
    {
        path: "/",
        element: <RootPage />,
    },
    {
        path: "/dashboard",
        element: <DashboardPage />,
    },
    {
        path: "/submission/s/:submissionId",
        element: <SubmissionPage />,
    },
]);
```

## Naming Conventions

### TypeScript Files

- Regular ts files can be named in camelCase (ex. `customTypes.ts`)
- React files (.tsx) must be named in PascalCase (ex. `DashboardPage.tsx`)
- If a file doesn't need to be .tsx, then it should be a .ts file

### Function Naming

- React functions should be in PascalCase

    ```tsx
    export default function Dashboard() {}
    ```

- React hooks and any other function/constant should be in camelCase

    ```tsx
    const useAuthQuery = () => {};
    ```

## Folder Naming Conventions

You should try to limit folders to one word, but if you must require multiple words, you may use kebab-case (ex. `/lib/custom-types`)

## Separation of Concerns

You may use inline styles as long as it isn't deemed to be too long or complicated (at which you should be using `.module.css` files to separate off into.). Please read the section [below](#styling) on styling for more details.

You should put any custom hooks inside of a `hook.ts` file, and any custom types inside of a `types.ts` file. If you don't see any reason why the type may be re-used, you may put the file inside of the `/app` folder in the same route that it's used in. However, if you believe that the type may be re-used or would be easier to track down if in a central location (such as a database model type), put them in `/lib/types` or `/lib/hooks`.

## React Query

React Query should NEVER be created inline to a component. Instead, you should abstract the query into a custom hook so that if you need to call this query in another component, you can do so trivially.

```tsx
export const useFetchPotdQuery = () => {
    return useQuery({
        queryKey: ["potd", new Date().getDay()],
        queryFn: fetchPotd,
    });
};

async function fetchPotd() {
    const res = await fetch("/api/leetcode/potd");

    const json = (await res.json()) as ApiResponse<POTD>;

    return json;
}
```

All React Query functions should be placed inside of the `js/src/lib/queries` folder. You should try to match it a specific service so similar hooks can be found together, but this isn't strictly enforced.

[React Query docs](https://tanstack.com/query/latest/docs/framework/react/overview)

## Comments

Do not leave comments within the JSX, unless you ABSOLUTELY have to. If you are in the situation where you feel like you have to, you should rethink your composition to reduce complexity.

Complicated hooks should have JSDoc comments at the top of the function like so:

```tsx
/**
 * A custom React hook that will attach the state to the URL params.
 * @param name The name of the key in the URL
 * Returns a stateful value and a function to update it.
 */
```

as well as comments inside of the function wherever necessary. A good example can be found [here](./js/src/lib/hooks/useUrlState.ts).

## Styling

You should only use built-in components inside of Mantine, such as `Flex`, `Container`, `Box`, `Stack`. They are customizable so reach for the docs or reach out to Tahmid if you are confused about what the better choice may be between styling.

If you must, you may use inline styles via the style prop like so:

```tsx
<Text style={{ display: "inline" }} />
```

but you **MUST NOT** use `style` for a specific property if the component already has an equivalent prop.

If the styling is very complicated, you may reach for CSS files, but only if you use `*.module.css` so that the styles don't bleed into the global scope. Module CSS files restrict the styling by renaming styles automatically at build time so that they do not bleed into the global namespace.

Tailwind is inside this project due to the ease of prototyping during development, but you shouldn't have to use it in production. Thereby, it is HIGHLY discouraged but not banned.

[Mantine docs](https://mantine.dev/core/package/)

## Typesafety

The Codebloom frontend has a very close relationship with the Codebloom backend.

### `schema.ts`

We use `openapi-typescript` to introspect the backend's OpenAPI schema endpoint, which will then convert everything into TypeScript and save into a `schema.ts` file. This file is saved at `js/src/lib/api/types/autogen/schema.ts`.

We have two main use-cases for the schema file:

1. We have a helper method called `ApiURL` (read about [here](#apiurl)) which helps us maintain full type-safety when passing data between the frontend & backend and vice versa.
2. All enums sent from the backend are converted into `TypeScript` enums, which allow us to programmatically define behaviors based on the enums.

These enums are extra special because we can fully generate UI code based on these enums **at compile time**.

For example, our leaderboard filters are generated by the [`useFilters`](./src/lib/hooks/useFilters.ts), which takes all the enum values and generates a hook object.

<div align="center">
    <img src="/screenshots/stg-filters.png" alt="Filters" />
    <p>
        <i>
            Every single one of these filters are automatically generated from
            <a href="../src/main/java/org/patinanetwork/codebloom/common/db/models/usertag/Tag.java">
                <code>Tag.java</code>
            </a>
        </i>
    </p>
    <p>
        <i>
            Last updated: 02/15/2026
        </i>
    </p>
</div>
<br />

We actually decided to try something very experimental: generating more complex types based off these enums via the Codebloom backend. This experiment is currently inside of [`ComplexJSTypesGenerator.java`](../src/main/java/org/patinanetwork/codebloom/utilities/generator/complex/ComplexJSTypesGenerator.java) which currently generates the current file:

```ts
/**
 * This file was generated by the Codebloom backend.
 * DO NOT EDIT THIS FILE MANUALLY!!!
 */
import { Tag } from "@/lib/api/types/schema";

export const PARENT_TAGS_TO_CHILD_TAGS: Record<Tag, Tag[]> = {
    [Tag.MHCPlusPlus]: [],
    [Tag.Rpi]: [],
    [Tag.Baruch]: [],
    [Tag.Columbia]: [],
    [Tag.Patina]: [],
    [Tag.Sbu]: [],
    [Tag.Bmcc]: [],
    [Tag.Cornell]: [],
    [Tag.Hunter]: [Tag.Gwc, Tag.MHCPlusPlus],
    [Tag.Gwc]: [],
    [Tag.Nyu]: [],
    [Tag.Ccny]: [],
} as const;
```

<p align="center">
    <i>
        Last updated: 02/15/2026
    </i>
</p>
<br />

which allows us to define a complex relationship of parent tags to child tags. This `Record` is now currently being used in production to generate our Club Filters.

<div align="center">
    <img src="/screenshots/stg-club-filters.png" alt="Filters" />
    <p>
        <i>
            Last updated: 02/15/2026
        </i>
    </p>
</div>

### ApiURL

You can view the implementation of `ApiURL` [here](./js/src/lib/api/common/apiURL.ts).

`ApiURL` is a custom utility class designed to enforce type-safe requests when the frontend is sending/receiving data to/from the backend. It integrates directly with the generated `schema.ts` (read more about `schema.ts` [here](#schemats)) file to ensure every `fetch` request — its method, parameters, body, and response — is validated at compile time.

#### Intent and Usage

`ApiURL` serves as the single entry point for building strongly-typed requests.  
It provides the following core methods and behaviors:

- **`ApiURL.create(path, options)`** — Static factory method to create `ApiURL`.that validates the provided path, method, and optionally path/query parameters.
  - `path` - Must be a valid endpoint path. > **Note**: URLs with dynamic paths are still supported.

- **`.url`** — Accessor that returns the Web API `URL` object after substituting path and query parameters.
  This can be passed directly into `fetch()`.

- **`.method`** — Accessor returns the validated HTTP method (e.g., `"GET"`, `"POST"`) to use in `fetch`.  
  Only allows valid methods defined in the backend OpenAPI schema.

- **`.req(body)`** — Function that serializes and validates a request body (at compile-time) according to the backend’s expected type definition.  
  Under the hood, `.req` calls `JSON.stringify` for you. As such, it returns a `string` for use as `fetch`'s `body`.

- **`.res(response)`** — Function that validates a JSON response against the expected type.  
  Adds 0 runtime overhead - it’s purely a compile-time safety check.
    > [!NOTE]
    > T `.res` will always target an `OK` response. This is because Codebloom uses a custom `ApiResponder` type that will always return some fields back to the client. Read more about how it's implemented in the backend [here](Backend-Technical-Docs#apiresponder).

#### POST request example

```ts
const { url, method, req, res } = ApiURL.create(
    "/api/admin/user/admin/toggle", // full autocomplete
    {
        method: "POST",
    },
);

const response = await fetch(url, {
    method,
    headers: { "Content-Type": "application/json" },
    body: req({ id: userId, toggleTo }), // full autocomplete
});

const json = res(await response.json());

return json; // `json` is fully typed
```

#### Dynamic path example

```ts
const { url, method, res } = ApiURL.create(
    "/api/leetcode/submission/{submissionId}", // full autocomplete
    {
        method: "GET",
        params: {
            // params has full autocomplete
            submissionId,
        },
    },
);
const response = await fetch(url, {
    method,
});

const json = res(await response.json());

return json; // `json` is fully typed
```
