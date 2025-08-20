package com.patina.codebloom.email.template;

import com.patina.codebloom.common.email.template.EmailTemplateService;
import com.patina.codebloom.common.email.template.EmailTemplateException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestInstance;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class EmailTemplateServiceTest {
    
    @Autowired
    private EmailTemplateService emailTemplateService;

    @Test
    void testRenderSchoolVerificationEmail() throws EmailTemplateException {
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
    }
}