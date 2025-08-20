package com.patina.codebloom.email.template;

import com.patina.codebloom.common.email.template.EmailTemplateService;
import com.patina.codebloom.common.email.template.EmailTemplateException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EmailTemplateServiceManualTest {

    @Test
    void testRenderSchoolVerificationEmail() throws EmailTemplateException {
        EmailTemplateService emailTemplateService = new EmailTemplateService();
        
        String recipientEmail = "test@hunter.cuny.edu";
        String verificationLink = "http://localhost:8080/api/auth/school/verify?state=test-token";
        
        String renderedHtml = emailTemplateService.renderSchoolVerificationEmail(recipientEmail, verificationLink);
        
        assertNotNull(renderedHtml);
        assertFalse(renderedHtml.trim().isEmpty());
        assertTrue(renderedHtml.contains("<!DOCTYPE html"));
        assertTrue(renderedHtml.contains(recipientEmail));
        assertTrue(renderedHtml.contains(verificationLink));
        assertTrue(renderedHtml.contains("Welcome to Codebloom"));
        assertTrue(renderedHtml.contains("Verify School Email"));
        
        System.out.println("✅ Email template rendered successfully!");
        System.out.println("📧 Email size: " + renderedHtml.length() + " characters");
        
        // Test with a longer link to verify it's properly handled
        String longLink = "http://localhost:8080/api/auth/school/verify?state=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
        String renderedHtmlLong = emailTemplateService.renderSchoolVerificationEmail(recipientEmail, longLink);
        
        assertTrue(renderedHtmlLong.contains(longLink));
        System.out.println("✅ Long link handling verified!");
    }
}