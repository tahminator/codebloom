package com.patina.codebloom.common.email.client;

import java.io.IOException;

public interface ReactEmailClient {

    /**
     * Load the generated HTML from ClassPathResources as a String then injects
     * variables using Jsoup and renders HTML as a string.
     * 
     * @param recipientName
     * @param verifyUrl
     * @param supportEmail
     * @return the rendered HTML as a string
     * @throws IOException
     */
    String createExampleTemplate(String recipientName, String verifyUrl, String supportEmail) throws IOException;

    /**
     * Load the verifyUrl into the school email template.
     * 
     * @param verifyUrl
     * @return rendered HTML as a string
     * @throws IOException
     */
    String schoolEmailImplementation(String verifyUrl) throws IOException;
}
