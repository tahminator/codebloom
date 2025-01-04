# Authentication Routes and Flow

## Base Endpoint  
All authentication routes are prefixed with: `/api/auth/*`  

## Custom Routes  
- **`/api/auth/validate`** - Verifies whether the user is authenticated based on cookies stored in the browser.  
- **`/api/auth/logout`** - Logs out the user by invalidating the session and removing cookies from the browser.  

## Spring OAuth Routes  
- **OAuth Initiation:** `/api/auth/flow/{provider}` - Begins the OAuth authentication process for a specific provider. (Example: `/api/auth/flow/discord` starts the Discord OAuth flow.)  
- **OAuth Callback:** `/api/auth/flow/callback/{provider}` - Handles the callback process after the OAuth provider returns data to authenticate the user. (Example: `/api/auth/flow/callback/discord` processes the callback from Discord OAuth.)  

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

