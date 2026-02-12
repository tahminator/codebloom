package org.patinanetwork.codebloom.common.email.client.github;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.patinanetwork.codebloom.common.email.error.EmailException;
import org.patinanetwork.codebloom.common.email.options.SendEmailOptions;

class GithubOAuthEmailClientTest {

    @Test
    void testSendMessageThrowsUnsupportedOperationException() {
        GithubOAuthEmailClientProperties properties = new GithubOAuthEmailClientProperties();
        properties.setHost("imap.gmail.com");
        properties.setPort("8081");
        properties.setUsername("test@gmail.com");
        properties.setPassword("password");

        GithubOAuthEmailClient client = new GithubOAuthEmailClient(properties);

        SendEmailOptions options = SendEmailOptions.builder()
                .recipientEmail("recipient@example.com")
                .subject("Test")
                .body("Test content")
                .build();

        assertThrows(UnsupportedOperationException.class, () -> client.sendMessage(options));
    }

    @Test
    void testGetPastMessagesThrowsEmailException() {
        GithubOAuthEmailClientProperties properties = new GithubOAuthEmailClientProperties();
        properties.setHost("invalid.host.example");
        properties.setPort("8081");
        properties.setUsername("invalid@example.com");
        properties.setPassword("wrongpassword");

        GithubOAuthEmailClient client = new GithubOAuthEmailClient(properties);

        EmailException exception = assertThrows(EmailException.class, () -> client.getPastMessages());

        assertNotNull(exception);
        assertEquals("Something went wrong when receiving past messages", exception.getMessage());
    }

    @Test
    void testConnectionThrowsEmailException() {
        GithubOAuthEmailClientProperties properties = new GithubOAuthEmailClientProperties();
        properties.setHost("invalid.host.example");
        properties.setPort("8081");
        properties.setUsername("invalid@example.com");
        properties.setPassword("wrongpassword");

        GithubOAuthEmailClient client = new GithubOAuthEmailClient(properties);

        EmailException exception = assertThrows(EmailException.class, () -> client.testConnection());

        assertNotNull(exception);
        assertEquals("Something went wrong when testing connection", exception.getMessage());
    }
}
