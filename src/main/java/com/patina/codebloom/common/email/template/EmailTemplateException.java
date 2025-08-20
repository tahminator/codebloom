package com.patina.codebloom.common.email.template;

/**
 * Exception thrown when email template rendering fails
 */
public class EmailTemplateException extends Exception {
    public EmailTemplateException(String message) {
        super(message);
    }
    
    public EmailTemplateException(String message, Throwable cause) {
        super(message, cause);
    }
}