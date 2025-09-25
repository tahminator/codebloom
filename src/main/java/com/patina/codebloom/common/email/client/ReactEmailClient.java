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
     * @return the rendered HTML with variables filled
     * @throws IOException
     */
    String createExampleTemplate(String recipientName, String verifyUrl, String supportEmail) throws IOException;
}
