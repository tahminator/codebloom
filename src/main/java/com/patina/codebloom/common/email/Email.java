package com.patina.codebloom.common.email;

import java.util.List;

import com.patina.codebloom.common.email.error.EmailException;
import com.patina.codebloom.common.email.options.SendEmailOptions;

/**
 * The base email interface.
 *
 * NOTE: Inherited classes may NOT have every method implemented. You should
 * check the implementation of each Email type and ensure that it has the
 * features you require.
 */
public abstract class Email {
    public abstract List<Message> getPastMessages() throws EmailException;

    public abstract void sendMessage(final SendEmailOptions sendEmailOptions) throws EmailException;

    /**
     * Validate that the connection and all the properties work, without actually
     * doing any action.
     */
    public abstract void testConnection() throws EmailException;
}
