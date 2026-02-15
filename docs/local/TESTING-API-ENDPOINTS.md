# Prerequisites

1. Postman

# How-to test any new API endpoints with Postman

1. Ensure that you deployed your changes to staging within your PR ([read more here](https://github.com/tahminator/codebloom/wiki/GitHub-Actions#how-to-trigger-deployment)).

1. Inside of Postman, hit the Import button near the top left:

 <img width="527" height="160" alt="image" src="https://github.com/user-attachments/assets/dea8a686-ec6f-4cb8-b898-0ed5495d27bd" />

1. Inside the input box, enter the following link:

    ```
    https://stg.codebloom.patinanetwork.org/v3/api-docs

    # or if you're testing things locally
    http://localhost:8080/v3/api-docs

    # or prod
    https://codebloom.patinanetwork.org/v3/api-docs
    ```

    <img width="944" height="585" alt="image" src="https://github.com/user-attachments/assets/34dc58e5-8a8a-43a8-8439-98f1f923541f" />

1. Once it finishes importing the OpenAPI spec, hit `View Import Settings` and set `Folder organization` to `Tags`:

 <img width="473" height="116" alt="image" src="https://github.com/user-attachments/assets/a7df2b37-ad9e-49db-8ad7-995afe7090d9" />
 <img width="661" height="67" alt="image" src="https://github.com/user-attachments/assets/1563b99d-774d-4020-82b3-dcc0aa48f46e" />

1. You can then hit `Import` and rename the new collection to indicate the environment (e.g. `Codebloom dev` or `Codebloom stg`)

 <img width="293" height="481" alt="image" src="https://github.com/user-attachments/assets/0d387d34-6134-43f4-bc23-226519a70c6e" />

# How to authenticate requests in Postman

> [!NOTE]
> These instructions require Google Chrome (or any Chromium based browser)

## Prerequisites

1. Install Postman Interceptor [here](https://chromewebstore.google.com/detail/postman-interceptor/aicmkgpgakddgnaphhhpliifpcfhicfo?hl=en)

## Instructions

1. Log into Postman Interceptor with the same account that you used to authenticate on your local Postman instance.

1. Swap to the `Sync Cookie` tab

 <img width="548" height="100" alt="image" src="https://github.com/user-attachments/assets/37cb4c3f-09fe-4927-974c-1889e6a4bb15" />

1. Enter in `stg.codebloom.patinanetwork.org` (or local/prod) into the domain list

 <img width="600" height="174" alt="image" src="https://github.com/user-attachments/assets/8c847252-75ac-444d-83c3-0a1e1ffec422" />

1. Hit `Sync Cookies`

 <img width="565" height="144" alt="image" src="https://github.com/user-attachments/assets/37737619-866b-4fc9-8ffc-d480e2fb8c64" />

1. Navigate to [stg.codebloom.patinanetwork.org](https://stg.codebloom.patinanetwork.org) (or local/prod) and log in as you normally would (if you are logged in, log out then log back in).

1. Once authenticated, Postman Interceptor will automatically copy your authentication cookie to your local Postman client, and you can now hit endpoints under your user!

 <img width="990" height="729" alt="image" src="https://github.com/user-attachments/assets/5881512a-5037-4f29-9ab2-be4e9a9a5c2d" />
