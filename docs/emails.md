# Email Templates with React Email

This project now uses [React Email](https://react.email/) to create professional, responsive email templates that are rendered server-side and sent as HTML emails.

## Overview

The system consists of:

1. **Frontend Email Templates** (`js/src/emails/`): React components using React Email
2. **Email Template Rendering** (`js/scripts/render-email.ts`): Node.js script to render templates to HTML
3. **Backend Integration** (`src/main/java/.../email/template/`): Java service to call the renderer
4. **Enhanced Email Options**: Support for both HTML and plain text emails

## Email Templates

### School Verification Email

Professional template for school email verification with:
- Clean, modern design with Codebloom branding
- Responsive layout that works on all devices
- Clear call-to-action button
- Fallback plain text link
- Professional styling and typography
- Proper accessibility features

## Architecture

```
Java Backend (AuthController)
    ↓ calls
EmailTemplateService
    ↓ executes
Node.js Script (render-email.ts)
    ↓ renders
React Email Component
    ↓ returns
HTML Email Content
    ↓ sent via
Jakarta Mail (OfficialCodebloomEmail)
```

## Usage in Java

```java
@Autowired
private EmailTemplateService emailTemplateService;

// Render and send HTML email
try {
    String htmlContent = emailTemplateService.renderSchoolVerificationEmail(
        email, verificationLink
    );
    
    SendEmailOptions options = SendEmailOptions.builder()
        .recipientEmail(email)
        .subject("Verify your school email")
        .body(htmlContent)
        .isHtml(true)
        .build();
        
    emailClient.sendMessage(options);
} catch (EmailTemplateException e) {
    // Graceful fallback to plain text
    // ... fallback implementation
}
```

## Adding New Templates

1. Create a new React component in `js/src/emails/`
2. Add a rendering method to `EmailRenderer.ts`
3. Add a case in `render-email.ts`
4. Add a public method to `EmailTemplateService.java`

## Development

### Frontend Development
```bash
cd js
npm install
npm run build
```

### Testing Email Rendering
```bash
cd js
echo '{"template":"school-verification","data":{"recipientEmail":"test@example.com","verificationLink":"http://localhost:8080/verify?token=abc"}}' | npx tsx scripts/render-email.ts
```

### Backend Testing
```bash
./mvnw test -Dtest="EmailTemplateServiceManualTest"
```

## Benefits

- **Professional Appearance**: Beautiful, branded emails instead of plain text
- **Responsive Design**: Works perfectly on desktop, tablet, and mobile
- **Maintainable**: Templates are written in React with proper component structure
- **Type-Safe**: Full TypeScript support for template data
- **Backwards Compatible**: Existing plain text emails continue to work
- **Graceful Degradation**: Automatic fallback to plain text if rendering fails
- **Developer Friendly**: Easy to create new templates and preview them