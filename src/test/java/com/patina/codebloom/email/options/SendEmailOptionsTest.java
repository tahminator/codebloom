package com.patina.codebloom.email.options;

import com.patina.codebloom.common.email.options.SendEmailOptions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SendEmailOptionsTest {

    @Test
    void testSendEmailOptionsWithHtml() {
        String recipientEmail = "test@hunter.cuny.edu";
        String subject = "Test Subject";
        String body = "<h1>Test HTML Content</h1>";
        
        // Test the new builder pattern with HTML
        SendEmailOptions htmlOptions = SendEmailOptions.builder()
                .recipientEmail(recipientEmail)
                .subject(subject)
                .body(body)
                .isHtml(true)
                .build();
        
        assertEquals(recipientEmail, htmlOptions.getRecipientEmail());
        assertEquals(subject, htmlOptions.getSubject());
        assertEquals(body, htmlOptions.getBody());
        assertTrue(htmlOptions.getIsHtml());
    }

    @Test
    void testSendEmailOptionsBackwardsCompatibility() {
        String recipientEmail = "test@hunter.cuny.edu";
        String subject = "Test Subject";
        String body = "Plain text content";
        
        // Test backwards compatibility constructor
        SendEmailOptions textOptions = new SendEmailOptions(recipientEmail, subject, body);
        
        assertEquals(recipientEmail, textOptions.getRecipientEmail());
        assertEquals(subject, textOptions.getSubject());
        assertEquals(body, textOptions.getBody());
        assertFalse(textOptions.getIsHtml());
        
        System.out.println("✅ Backwards compatibility verified!");
    }

    @Test
    void testSendEmailOptionsDefaultBehavior() {
        // Test the builder without isHtml (should default to null, treated as false)
        SendEmailOptions defaultOptions = SendEmailOptions.builder()
                .recipientEmail("test@example.com")
                .subject("Test")
                .body("Content")
                .build();
        
        assertNull(defaultOptions.getIsHtml());
        System.out.println("✅ Default behavior (null isHtml) verified!");
    }
}