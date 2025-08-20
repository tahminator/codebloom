package com.patina.codebloom.common.email.options;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class SendEmailOptions {
    private final String recipientEmail;
    private final String subject;
    private final String body;
    private final Boolean isHtml; // Optional field to indicate if body is HTML
    
    // Constructor for backwards compatibility
    public SendEmailOptions(String recipientEmail, String subject, String body) {
        this.recipientEmail = recipientEmail;
        this.subject = subject;
        this.body = body;
        this.isHtml = false;
    }
}
