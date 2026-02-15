package org.patinanetwork.codebloom.common.email.client.github;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import jakarta.mail.Folder;
import jakarta.mail.Session;
import jakarta.mail.Store;
import java.io.ByteArrayInputStream;
import java.util.Date;
import org.junit.jupiter.api.Test;
import org.patinanetwork.codebloom.common.email.error.EmailException;
import org.patinanetwork.codebloom.common.email.options.SendEmailOptions;

class GithubOAuthEmailClientTest {

    @Test
    void testSendMessageThrowsUnsupportedOperationException() {
        GithubOAuthEmailClientProperties properties = new GithubOAuthEmailClientProperties();
        properties.setHost("imap.gmail.com");
        properties.setPort("993");

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
        properties.setPort("993");

        GithubOAuthEmailClient client = new GithubOAuthEmailClient(properties);

        EmailException exception = assertThrows(EmailException.class, client::getPastMessages);
        assertEquals("Something went wrong when receiving past messages", exception.getMessage());
    }

    @Test
    void testConnectionThrowsEmailException() {
        GithubOAuthEmailClientProperties properties = new GithubOAuthEmailClientProperties();
        properties.setHost("invalid.host.example");
        properties.setPort("993");

        GithubOAuthEmailClient client = new GithubOAuthEmailClient(properties);

        EmailException exception = assertThrows(EmailException.class, client::testConnection);
        assertEquals("Something went wrong when testing connection", exception.getMessage());
    }

    @Test
    void testGetPastMessagesReadsTextPlainContent() throws Exception {
        GithubOAuthEmailClientProperties properties = new GithubOAuthEmailClientProperties();
        properties.setHost("imap.gmail.com");
        properties.setPort("993");

        Store mockStore = mock(Store.class);
        Folder mockFolder = mock(Folder.class);
        jakarta.mail.Message mockMessage = mock(jakarta.mail.Message.class);
        Session mockSession = mock(Session.class);

        when(mockMessage.isMimeType("text/plain")).thenReturn(true);
        when(mockMessage.getInputStream()).thenReturn(new ByteArrayInputStream("test".getBytes()));
        when(mockMessage.getSubject()).thenReturn("Subject");
        when(mockMessage.getReceivedDate()).thenReturn(new Date());
        when(mockFolder.getMessages(anyInt(), anyInt())).thenReturn(new jakarta.mail.Message[] {mockMessage});
        when(mockFolder.getMessageCount()).thenReturn(1);
        when(mockStore.getFolder("Inbox")).thenReturn(mockFolder);
        when(mockSession.getStore("imap")).thenReturn(mockStore);

        GithubOAuthEmailClient client = new GithubOAuthEmailClient(properties);
        java.lang.reflect.Field sessionField = GithubOAuthEmailClient.class.getDeclaredField("session");
        sessionField.setAccessible(true);
        sessionField.set(client, mockSession);

        assertEquals(1, client.getPastMessages().size());
    }

    @Test
    void testGetPastMessagesCatchesException() throws Exception {
        GithubOAuthEmailClientProperties properties = new GithubOAuthEmailClientProperties();
        properties.setHost("imap.gmail.com");
        properties.setPort("993");

        Store mockStore = mock(Store.class);
        Folder mockFolder = mock(Folder.class);
        jakarta.mail.Message mockMessage = mock(jakarta.mail.Message.class);
        Session mockSession = mock(Session.class);

        when(mockMessage.isMimeType("text/plain")).thenThrow(new RuntimeException());
        when(mockFolder.getMessages(anyInt(), anyInt())).thenReturn(new jakarta.mail.Message[] {mockMessage});
        when(mockFolder.getMessageCount()).thenReturn(1);
        when(mockStore.getFolder("Inbox")).thenReturn(mockFolder);
        when(mockSession.getStore("imap")).thenReturn(mockStore);

        GithubOAuthEmailClient client = new GithubOAuthEmailClient(properties);
        java.lang.reflect.Field sessionField = GithubOAuthEmailClient.class.getDeclaredField("session");
        sessionField.setAccessible(true);
        sessionField.set(client, mockSession);

        assertEquals(0, client.getPastMessages().size());
    }
}
