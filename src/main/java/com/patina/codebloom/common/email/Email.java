package com.patina.codebloom.common.email;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.NoSuchProviderException;
import jakarta.mail.Session;
import jakarta.mail.Store;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@EnableConfigurationProperties(EmailProperties.class)
public class Email {
    private final EmailProperties emailProperties;
    private static Session session;

    public Email(final EmailProperties emailProperties) {
        this.emailProperties = emailProperties;
        final Properties properties = new Properties();
        properties.setProperty("mail.imap.host", emailProperties.getHost());
        properties.setProperty("mail.imap.port", emailProperties.getPort());
        properties.setProperty("mail.imap.starttls.enable", "true");

        session = Session.getDefaultInstance(properties);
    }

    public List<MessageLite> getPastMessages() throws NoSuchProviderException, MessagingException {
        final Store store = session.getStore("imaps");

        store.connect(emailProperties.getHost(), emailProperties.getUsername(), emailProperties.getPassword());

        final Folder emailFolder = store.getFolder("Inbox");
        emailFolder.open(Folder.READ_ONLY);

        List<MessageLite> messages = new ArrayList<>();
        int count = emailFolder.getMessageCount();
        for (Message m : emailFolder.getMessages(count - 5, count)) {
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
                messages.add(new MessageLite(m.getSubject(), content, m.getReceivedDate()));
            } catch (Exception e) {
                System.err.println(e);
            }
        }

        emailFolder.close();
        store.close();

        return messages;

    }

    public static Session getSession() {
        return session;
    }

    public static void setSession(final Session session) {
        Email.session = session;
    }

}
