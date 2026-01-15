package org.patinanetwork.codebloom.common.email.options;

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
}
