package org.patinanetwork.codebloom.common.email.client.codebloom;

import io.micrometer.core.annotation.Timed;
import jakarta.mail.Authenticator;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.List;
import java.util.Properties;
import org.patinanetwork.codebloom.common.email.EmailClient;
import org.patinanetwork.codebloom.common.email.Message;
import org.patinanetwork.codebloom.common.email.error.EmailException;
import org.patinanetwork.codebloom.common.email.options.SendEmailOptions;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * This is the official Codebloom email client which we use to interface with our actual users.
 *
 * <p>For example, we use this client to send emails to users who are trying to verify their school status.
 */
@Component
@EnableConfigurationProperties(OfficialCodebloomEmailClientProperties.class)
@Timed(value = "email.client.execution")
public class OfficialCodebloomEmailClient extends EmailClient {

    private final OfficialCodebloomEmailClientProperties emailClientProperties;
    private Session session;

    public OfficialCodebloomEmailClient(final OfficialCodebloomEmailClientProperties emailClientProperties) {
        this.emailClientProperties = emailClientProperties;
        final Properties properties = new Properties();
        properties.setProperty("mail.smtp.host", emailClientProperties.getHost());
        properties.setProperty("mail.smtp.port", emailClientProperties.getPort());
        properties.setProperty("mail.smtp.starttls.enable", "true");
        properties.setProperty("mail.smtp.starttls.required", "true");
        properties.setProperty("mail.smtp.auth", "true");

        session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(
                        emailClientProperties.getUsername(), emailClientProperties.getPassword());
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
            message.setFrom(new InternetAddress(emailClientProperties.getUsername()));
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
