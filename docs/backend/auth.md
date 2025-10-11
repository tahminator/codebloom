# Authentication Routes and Flow

## Base Endpoint

All authentication routes are prefixed with: `/api/auth/*`

## Custom Routes

- **GET: `/api/auth/validate`** - Verifies whether the user is authenticated based on cookies stored in the browser.
- **GET: `/api/auth/logout`** - Logs out the user by invalidating the session and removing cookies from the browser. It automatically returns back to the frontend with either:
  - `/login?success=false&message=This is the failure message`.
  - `/login?success=true&message=This is the success message!`
  - These should be handled on the frontend route.

## Spring OAuth Routes

- **OAuth Initiation:** `GET: /api/auth/flow/{provider}` - Begins the OAuth authentication process for a specific provider. (Example: `/api/auth/flow/discord` starts the Discord OAuth flow.)
- **OAuth Callback:**`GET: /api/auth/flow/callback/{provider}` - Handles the callback process after the OAuth provider returns data to authenticate the user. (Example: `/api/auth/flow/callback/discord` processes the callback from Discord OAuth.)
  - If successful, the user is automatically redirected to `/dashboard`.
  - If failed, the user is automatically redirected to `/login?success=false&message=This is the failure message`.
    - This should be handled on the frontend route.

## Security Details

- **CSRF Protection** - Automatically managed by Spring Security, so no additional configuration is required.
- **Auth Validator / Session Token Cookie Setter** - Managed by the `CustomAuthenticationSuccessHandler`.
  - Cookie Settings:
    - Name: `session_token`
    - Max Age: **30 days** (configurable via a private variable).

## Environment Variables

Include the following in your `.env` file for OAuth configuration:

- DISCORD_CLIENT_ID=your-client-id
- DISCORD_CLIENT_SECRET=your-client-secret

## Backend Objects

- [Protector.java](https://github.com/tahminator/codebloom/tree/main/src/main/java/com/patina/codebloom/common/security/Protector.java) is used to validate whether the user is logged in or not. It automatically handles unauthorized requests via GlobalExceptionHandler.java

- [Protected.java](https://github.com/tahminator/codebloom/tree/main/src/main/java/com/patina/codebloom/common/security/annotation/Protected.java) can be applied to a controller method as an annotation. You can find an example inside of the file's Javadoc.

  - [AuthController.java](https://github.com/tahminator/codebloom/tree/main/src/main/java/com/patina/codebloom/api/auth/AuthController.java) contains examples of using Protector.java to protect endpoints.

- [GlobalExceptionHandler.java](https://github.com/tahminator/codebloom/blob/main/src/main/java/com/patina/codebloom/utilities/GlobalExceptionHandler.java) manages exception handling for unauthorized requests

- [SecurityConfig.java](https://github.com/tahminator/codebloom/blob/main/src/main/java/com/patina/codebloom/api/auth/security/SecurityConfig.java) holds the OAuth provider

  - [CustomAuthenticationSuccessHandler.java](https://github.com/tahminator/codebloom/tree/main/src/main/java/com/patina/codebloom/api/auth/security/CustomAuthenticationSuccessHandler.java) actually handles the process of authenticating the user once they have been successfully redirected from the OAuth provider back to our server
