package com.patina.codebloom.common.email.client.github;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import jakarta.mail.Folder;
import jakarta.mail.Session;
import jakarta.mail.Store;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import com.patina.codebloom.common.email.Email;
import com.patina.codebloom.common.email.Message;
import com.patina.codebloom.common.email.error.EmailException;
import com.patina.codebloom.common.email.options.SendEmailOptions;

/**
 * Provides read-only access to the Github email account in order to access the
 * OAuth code.
 */
@Component
@EnableConfigurationProperties(GithubOAuthEmailProperties.class)
public class GithubOAuthEmail extends Email {
    private final GithubOAuthEmailProperties emailProperties;
    private static Session session;

    public GithubOAuthEmail(final GithubOAuthEmailProperties emailProperties) {
        this.emailProperties = emailProperties;
        final Properties properties = new Properties();
        properties.setProperty("mail.imap.host", emailProperties.getHost());
        properties.setProperty("mail.imap.port", emailProperties.getPort());

        session = Session.getDefaultInstance(properties);
    }

    public List<Message> getPastMessages() throws EmailException {
        try {

            final Store store = session.getStore("imap");

            store.connect(emailProperties.getHost(), emailProperties.getUsername(), emailProperties.getPassword());

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

    /**
     * @deprecated - This is not supported.
     */
    @Override
    public void sendMessage(final SendEmailOptions sendEmailOptions) throws EmailException {
        throw new UnsupportedOperationException("GithubOAuthEmail does not support sending messages.");
    }

    @Override
    public void testConnection() throws EmailException {
        try {
            final Store store = session.getStore("imap");

            store.connect(emailProperties.getHost(), emailProperties.getUsername(), emailProperties.getPassword());

            final Folder emailFolder = store.getFolder("Inbox");
            emailFolder.open(Folder.READ_ONLY);

            emailFolder.close();
            store.close();
        } catch (Exception e) {
            throw new EmailException("Something went wrong when testing connection", e);
        }
    }

}
