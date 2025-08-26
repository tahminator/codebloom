# Repository Practices

This document should represent the best practices for writing/creating repositories inside of the backend that interface with the database layer.

**NOTE - All examples will assume that the database table name is `Agent`**

1. Location of files should be in the `common/db` folder.
    - Repositories should be placed into `common/db/repos/*`
    - Models should be placed into `common/db/models/*`
2. Use interface pattern.
    - The interface file should be named `AgentRepository`
    - The implementation should be named `AgentSqlRepository`.
3. Stick to using Lombok for the Java database models.

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

        - `@Jacksonized` - This is required so a class can be de-serialized if it's ever converted from a JSON string into a Java object. It is best to always add this annotation,

    - **NOTE - Do not use @Data. The annotation has too much scope, and it's better to just use the annotations you were going to apply.**
    - Example file of a database object in Java:

        ```java
        @Getter
        @Setter
        @Jacksonized
        @Builder
        @EqualsAndHashCode
        @ToString
        public class Agent {
            private String id;
            private String name;
        }
        ```

4. Follow these rules when it comes to repositories:

    1. **_Create_**

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
             * <li>name</li>
             * <li>type</li>
             * </ul>
             */
            void createAgent(Agent agent);

            // ...
        }
        ```

    2. **Read/Find**

        - Read functions should indicate the required values in order to find the row to read (e.g. `byId` or `byLeaderboardName`).
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
                                .build();
                }

                @Override
                public Agent getAgentById(final String id) {
                    // ...

                    if (rs.next()) {
                      return parseResultSetToAgent(rs);
                    }

                    // ...
                }

            }
            ```

    3. **Update**

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
             * @param agent - overridden fields:
             * <ul>
             * <li>name</li>
             * <li>type</li>
             * </ul>
             */
            boolean updateAgentById(Agent agent);

            // ...
        }
        ```

    4. **Delete**

        - For delete operations, you should return a boolean depending on whether or not the operation was successful or not. The entire Object is not required to be passed in, just what the function requires to find the row to delete.

        ```java
        // AgentRepository.java

        public interface AgentRepository {
            // ...

            boolean deleteAgentById(String id);

            // ...
        }
        ```

    5. **Exceptions**

        - There is a chance you may need to implement a method that doesn't fall neatly into the standards, such as searching for an external object ID (e.g. finding UserTags by using an userId). These are completely reasonable, and it's well within means to attempt a solution, which can get revised during pull requests.

5. Examples
    - [Auth table](https://github.com/tahminator/codebloom/blob/main/src/main/java/com/patina/codebloom/common/db/repos/auth/AuthRepository.java)
    - [UserTag table](https://github.com/tahminator/codebloom/tree/main/src/main/java/com/patina/codebloom/common/db/repos/usertag)
