package org.patinanetwork.codebloom.common.email.template;

import java.io.IOException;

<<<<<<< Updated upstream
<<<<<<<< Updated upstream:src/main/java/org/patinanetwork/codebloom/common/email/template/ReactEmailTemplater.java
public interface ReactEmailTemplater {
========
public interface ReactEmail {
>>>>>>>> Stashed changes:src/main/java/org/patinanetwork/codebloom/common/email/template/ReactEmail.java
=======
public interface ReactEmail {
>>>>>>> Stashed changes
    /**
     * Load the generated HTML from ClassPathResources as a String then injects variables using Jsoup and renders HTML
     * as a string.
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
    String schoolEmailTemplate(String verifyUrl) throws IOException;
}
