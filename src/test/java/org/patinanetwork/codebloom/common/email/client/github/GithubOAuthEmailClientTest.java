package org.patinanetwork.codebloom.common.email.client.github;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import jakarta.mail.*;
import java.io.ByteArrayInputStream;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.patinanetwork.codebloom.common.email.error.EmailException;

class GithubOAuthEmailClientTest {

    private final Session mockSession = mock(Session.class, Answers.RETURNS_DEEP_STUBS);
    private final GithubOAuthEmailClientProperties properties = mock(GithubOAuthEmailClientProperties.class);

    private GithubOAuthEmailClient buildClient() {
        when(properties.getHost()).thenReturn("imap.example.com");
        when(properties.getPort()).thenReturn("993");
        try (var sessionStatic = mockStatic(Session.class)) {
            sessionStatic.when(() -> Session.getDefaultInstance(any())).thenReturn(mockSession);
            return new GithubOAuthEmailClient(properties);
        }
    }

    @Test
    void getPastMessagesReturnsMessages() throws Exception {
        Message textMessage = mock(Message.class);
        when(textMessage.isMimeType("text/plain")).thenReturn(true);
        when(textMessage.getInputStream()).thenReturn(new ByteArrayInputStream("line1\nline2".getBytes()));
        when(textMessage.getSubject()).thenReturn("subject");
        when(textMessage.getReceivedDate()).thenReturn(new Date());

        Message otherMessage = mock(Message.class);
        when(otherMessage.isMimeType("text/plain")).thenReturn(false);

        Message badMessage = mock(Message.class);
        when(badMessage.isMimeType(anyString())).thenThrow(new RuntimeException());

        Folder folder = mockSession.getStore("imap").getFolder("Inbox");
        when(folder.getMessageCount()).thenReturn(3);
        when(folder.getMessages(anyInt(), anyInt())).thenReturn(new Message[] {textMessage, otherMessage, badMessage});

        List<org.patinanetwork.codebloom.common.email.Message> result =
                buildClient().getPastMessages();

        assertEquals(2, result.size());
        verify(folder).close();
    }

    @Test
    void getPastMessagesThrowsEmailException() throws Exception {
        when(mockSession.getStore(anyString())).thenThrow(new NoSuchProviderException());
        assertThrows(EmailException.class, () -> buildClient().getPastMessages());
    }

    @Test
    void testConnectionSucceeds() throws Exception {
        buildClient().testConnection();
        verify(mockSession.getStore("imap")).close();
    }

    @Test
    void testConnectionThrowsEmailException() throws Exception {
        when(mockSession.getStore("imap")).thenThrow(new NoSuchProviderException());
        assertThrows(EmailException.class, () -> buildClient().testConnection());
    }

    @Test
    void sendMessageThrowsUnsupported() {
        assertThrows(UnsupportedOperationException.class, () -> buildClient().sendMessage(null));
    }
}
