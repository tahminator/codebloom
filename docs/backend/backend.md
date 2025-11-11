# Backend

## Development Server

```bash
mvn spring-boot:run
or 
```
just backend-dev
```

## Naming conventions

-   All Java files must match the name of their class/interface/enum. So `src/main/java/com/patina/codebloom/CodebloomApplication.java` will expose the class CodebloomApplication.
-   All Java classes/interfaces/enums must be in PascalCase, while actual functions, methods, and variables can be written in camelCase.

### Abstraction / Code Structure

It's a little difficult to draw the line between what should be grouped together, so here are my thoughts organized as best as I can. Keep in mind that this is not strictly enforced, and that these thoughts may change.

-   All controllers should be grouped by service, such as the `AuthController`, `LeaderboardController`, `SubmissionController`. Considering the gravity of Codebloom, it may be inevitable that there will be some bleed between the jurisdiction of each controller. If that is the case, it isn't an issue; just make sure it's documented so that the rest of the team can understand what you did.

-   You can also group Request bodies and Response bodies within the same folder of the controller as long as you group it by a `body` folder. For example, check this [example](https://github.com/tahminator/codebloom/tree/main/src/main/java/com/patina/codebloom/api/submission).

-   Classes/Objects that can be shared across many different boundaries, such as database items and repositories or the LeetCode API client should be stored in the `common` folder. This isn't too strict, and it's never a bad idea to ask me what should and should not go in there.

-   Scheduled tasks should go in the `/scheduled` folder, so it's not polluting the rest of the codebase. Check it out [here](https://github.com/tahminator/codebloom/tree/main/src/main/java/com/patina/codebloom/scheduled).

-   The `utilities` folder is used to setup things that the core application may use that the actual user code doesn't **really** touch, like loading the frontend files or a global exception handler.

-   The `jda` folder is used to hold the Discord bot implementation. This is also considered a client, where it exports some functions to actually use the Discord bot within the actual web logic, but it should not be directly imported and worked with. (This is not currently true right now due to our initial goal of just getting it to work, but once this is abstracted out it should stay that way.)

### Documentation

**TL:DR - If you want to access the documentation (not available in production), run the backend development server and go to**

`localhost:8080/swagger-ui.html`

**in your browser.**

-   _Only for controllers_ - These documentations are automatically generated within your code, so just take a look at [this](https://github.com/tahminator/codebloom/tree/main/src/main/java/com/patina/codebloom/api/user/UserController.java) on what a good example of auto-documentation should look like. However, this is one of those things that just run really deep so if you have never used `Springdoc` before, I think it would be better to have a quick one on one so we can see eye to eye.

-   For non-controllers, you can always use Javadoc comments on a class/function to help clarify details that may be important. Click [here](https://github.com/tahminator/codebloom/tree/main/src/main/java/com/patina/codebloom/common/simpleredis/SimpleRedis.java) and [here](https://github.com/tahminator/codebloom/tree/main/src/main/java/com/patina/codebloom/common/db/repos/leaderboard/LeaderboardRepository.java) to see a very good example of clear documentation for a class or a function that may need it. This is not strictly enforced, but a good rule of thumb is if someone else can't just easily read it and understand it, you should probably comment it.

### SQL

-   I think it's probably best to come through the code and see what each repository can do (in terms of the interface file/what the outside world can use). If you have to actually edit any implementation of the repository, it may be a good idea to view a diagram of the database with `Dbeaver` or any other DB viewer tool. This will help you realize how the data is connected.

-   Furthermore, you can always copy the SQL statement from any repository and run it via `Dbeaver` or any other DB tool (you just have to replace the `?` with actual values) to see what it should return. This way, you can also modify the query and figure out what each part of the query is doing.

-   We want to **avoid** SQL complexity, even if it means our data fetching is not as efficient. It is very hard to understand 100+ line SQL queries. So, our solution is to use JOINS to get the bare minimum information we did to setup the flow of our data (like for example, getting the `totalScore` from the `Metadata` table and joining it on `Metadata.userId = User.id`), but as for actually fetching the user data, you should instead inject the repository needed to fetch the data through our backend instead. This seems complicated, so maybe an [example](https://github.com/tahminator/codebloom/blob/896ac57fa26b4060752cf3ae1f15a47393f53015/src/main/java/com/patina/codebloom/common/db/repos/leaderboard/LeaderboardSqlRepository.java#L96-L155) may be easier to understand.

-   If you don't know what JOINs are, here's a good [link](https://www.w3schools.com/Sql/sql_join.asp) to get you exposed to the idea of how it should work. The most important joins are (INNER) JOIN & LEFT JOIN, but a little knowledge of FULL OUTER JOIN is good too. You should also get in touch with me so I can give you a crash course on how they actually work.

### Testing

-   To run the backend tests, run

    ```bash
    just backend-test
    ```
