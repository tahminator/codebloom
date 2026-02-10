package org.patinanetwork.codebloom.common.email.client.github;

import io.micrometer.core.annotation.Timed;
import jakarta.mail.Folder;
import jakarta.mail.Session;
import jakarta.mail.Store;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.patinanetwork.codebloom.common.email.EmailClient;
import org.patinanetwork.codebloom.common.email.Message;
import org.patinanetwork.codebloom.common.email.error.EmailException;
import org.patinanetwork.codebloom.common.email.options.SendEmailOptions;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

/** Provides read-only access to the Github email account in order to access the OAuth code. */
@Component
@EnableConfigurationProperties(GithubOAuthEmailClientProperties.class)
@Timed(value = "email.client.execution")
public class GithubOAuthEmailClient extends EmailClient {

    private final GithubOAuthEmailClientProperties emailClientProperties;
    private static Session session;

    public GithubOAuthEmailClient(final GithubOAuthEmailClientProperties emailClientProperties) {
        this.emailClientProperties = emailClientProperties;
        final Properties properties = new Properties();
        properties.setProperty("mail.imap.host", emailClientProperties.getHost());
        properties.setProperty("mail.imap.port", emailClientProperties.getPort());
        properties.setProperty("mail.imap.ssl.enable", "true");
        properties.setProperty("mail.imap.auth", "true");
        properties.setProperty("mail.store.protocol", "imap");

        session = Session.getDefaultInstance(properties);
    }

    public List<Message> getPastMessages() throws EmailException {
        try {
            final Store store = session.getStore("imap");

            store.connect(
                    emailClientProperties.getHost(),
                    emailClientProperties.getUsername(),
                    emailClientProperties.getPassword());

            final Folder emailFolder = store.getFolder("Inbox");
            emailFolder.open(Folder.READ_ONLY);

            List<Message> messages = new ArrayList<>();
            int count = emailFolder.getMessageCount();
            for (var m : emailFolder.getMessages(count - 5, count)) {
                try {
                    String content = null;
                    if (m.isMimeType("text/plain")) {
                        content = "";
                        InputStream is = m.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                        StringBuilder sb = new StringBuilder();
                        String thisLine;
                        while ((thisLine = reader.readLine()) != null) {
                            sb.append(thisLine).append("\n");
                        }
                        content = sb.toString();
                    }
                    messages.add(new Message(m.getSubject(), content, m.getReceivedDate()));
                } catch (Exception e) {
                    System.err.println(e);
                }
            }
            emailFolder.close();
            store.close();

            return messages;
        } catch (Exception e) {
            throw new EmailException("Something went wrong when receiving past messages", e);
        }
    }

    /** @deprecated - This is not supported. */
    @Override
    @Deprecated
    public void sendMessage(final SendEmailOptions sendEmailOptions) throws EmailException {
        throw new UnsupportedOperationException("GithubOAuthEmail does not support sending messages.");
    }

    @Override
    public void testConnection() throws EmailException {
        try {
            final Store store = session.getStore("imap");

            store.connect(
                    emailClientProperties.getHost(),
                    emailClientProperties.getUsername(),
                    emailClientProperties.getPassword());

            final Folder emailFolder = store.getFolder("Inbox");
            emailFolder.open(Folder.READ_ONLY);

            emailFolder.close();
            store.close();
        } catch (Exception e) {
            throw new EmailException("Something went wrong when testing connection", e);
        }
    }
}
