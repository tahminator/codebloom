package org.patinanetwork.codebloom.common.email;

import java.util.List;
import org.patinanetwork.codebloom.common.email.error.EmailException;
import org.patinanetwork.codebloom.common.email.options.SendEmailOptions;

/**
 * The base email interface.
 *
 * <p>NOTE: Inherited classes may NOT have every method implemented. You should check the implementation of each Email
 * type and ensure that it has the features you require.
 */
public abstract class EmailClient {

    public abstract List<Message> getPastMessages() throws EmailException;

    public abstract void sendMessage(SendEmailOptions sendEmailOptions) throws EmailException;

    /** Validate that the connection and all the properties work, without actually doing any action. */
    public abstract void testConnection() throws EmailException;
}
