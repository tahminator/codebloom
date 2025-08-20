package com.patina.codebloom.common.email.template;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Service for rendering email templates using React Email.
 * This service calls the Node.js script to render React email templates to HTML.
 */
@Service
public class EmailTemplateService {
    private static final Logger logger = LoggerFactory.getLogger(EmailTemplateService.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String scriptPath;

    public EmailTemplateService() {
        // Determine the path to the render-email script
        String rootPath = System.getProperty("user.dir");
        this.scriptPath = Paths.get(rootPath, "js", "scripts", "render-email.ts").toString();
    }

    /**
     * Renders the school verification email template
     */
    public String renderSchoolVerificationEmail(String recipientEmail, String verificationLink) throws EmailTemplateException {
        Map<String, Object> templateData = Map.of(
            "recipientEmail", recipientEmail,
            "verificationLink", verificationLink
        );
        
        return renderTemplate("school-verification", templateData);
    }

    /**
     * Generic method to render any email template
     */
    private String renderTemplate(String templateName, Map<String, Object> data) throws EmailTemplateException {
        try {
            // Create the request JSON
            Map<String, Object> request = Map.of(
                "template", templateName,
                "data", data
            );
            
            String requestJson = objectMapper.writeValueAsString(request);
            
            // Check if script exists
            Path scriptPathObj = Paths.get(scriptPath);
            if (!Files.exists(scriptPathObj)) {
                throw new EmailTemplateException("Email template script not found at: " + scriptPath);
            }
            
            // Run the Node.js script
            ProcessBuilder processBuilder = new ProcessBuilder("npx", "tsx", scriptPath, requestJson);
            processBuilder.directory(Paths.get(scriptPath).getParent().getParent().toFile());
            
            Process process = processBuilder.start();
            
            // Wait for the process to complete with timeout
            boolean finished = process.waitFor(30, TimeUnit.SECONDS);
            if (!finished) {
                process.destroyForcibly();
                throw new EmailTemplateException("Email template rendering timed out");
            }
            
            int exitCode = process.exitValue();
            if (exitCode != 0) {
                String errorOutput = new String(process.getErrorStream().readAllBytes());
                throw new EmailTemplateException("Failed to render email template. Exit code: " + exitCode + ". Error: " + errorOutput);
            }
            
            // Read the rendered HTML from stdout
            String renderedHtml = new String(process.getInputStream().readAllBytes());
            
            if (renderedHtml.trim().isEmpty()) {
                throw new EmailTemplateException("Email template rendering produced no output");
            }
            
            return renderedHtml;
            
        } catch (IOException | InterruptedException e) {
            throw new EmailTemplateException("Failed to render email template: " + e.getMessage(), e);
        }
    }
}