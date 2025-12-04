package com.patina.codebloom.common.email.client.codebloom;

import com.patina.codebloom.common.email.Email;
import com.patina.codebloom.common.email.Message;
import com.patina.codebloom.common.email.error.EmailException;
import com.patina.codebloom.common.email.options.SendEmailOptions;
import jakarta.mail.Authenticator;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.List;
import java.util.Properties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * This is the official Codebloom email client which we use to interface with our actual users.
 *
 * <p>For example, we use this client to send emails to users who are trying to verify their school status.
 */
@Component
@EnableConfigurationProperties(OfficialCodebloomEmailProperties.class)
public class OfficialCodebloomEmail extends Email {

    private final OfficialCodebloomEmailProperties emailProperties;
    private Session session;

    public OfficialCodebloomEmail(final OfficialCodebloomEmailProperties emailProperties) {
        this.emailProperties = emailProperties;
        final Properties properties = new Properties();
        properties.setProperty("mail.smtp.host", emailProperties.getHost());
        properties.setProperty("mail.smtp.port", emailProperties.getPort());
        properties.setProperty("mail.smtp.starttls.enable", "true");
        properties.setProperty("mail.smtp.starttls.required", "true");
        properties.setProperty("mail.smtp.auth", "true");

        session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(emailProperties.getUsername(), emailProperties.getPassword());
            }
        });
    }

    /** @deprecated - This is not supported. */
    @Override
    @Deprecated
    public List<Message> getPastMessages() throws EmailException {
        throw new EmailException("Reading messages is not supported");
    }

    @Override
    public void sendMessage(final SendEmailOptions sendEmailOptions) throws EmailException {
        try {
            jakarta.mail.Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(emailProperties.getUsername()));
            message.setRecipient(
                    jakarta.mail.Message.RecipientType.TO, new InternetAddress(sendEmailOptions.getRecipientEmail()));
            message.setSubject(sendEmailOptions.getSubject());
            message.setContent(sendEmailOptions.getBody(), "text/html; charset=UTF-8");

            Transport.send(message);
        } catch (Exception e) {
            throw new EmailException("Something went wrong when sending message", e);
        }
    }

    @Override
    public void testConnection() throws EmailException {
        try {
            // Should trigger connection already by this point, but just to be safe, calling
            // something with session.
            new MimeMessage(session);
            // TODO - May need to actually test sending an email out, which can be added
            // pretty trivially.
        } catch (Exception e) {
            throw new EmailException("Something went wrong when testing connection", e);
        }
    }
}
