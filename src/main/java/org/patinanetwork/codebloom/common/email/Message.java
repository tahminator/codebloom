package org.patinanetwork.codebloom.common.email;

import java.util.Date;

/**
 * This class exists due to the fact that when the email connection is closed, all Messages are subsequently closed as
 * well.
 */
public class Message {

    private final String subject;
    private final String message;
    private final Date sentAt;

    public Message(final String subject, final String message, final Date sentAt) {
        this.subject = subject;
        this.message = message;
        this.sentAt = sentAt;
    }

    public String getSubject() {
        return subject;
    }

    public String getMessage() {
        return message;
    }

    public Date getSentAt() {
        return sentAt;
    }
}
