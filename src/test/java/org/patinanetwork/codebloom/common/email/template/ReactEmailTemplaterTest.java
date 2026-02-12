package org.patinanetwork.codebloom.common.email.template;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.Test;

<<<<<<<< Updated upstream:src/test/java/org/patinanetwork/codebloom/common/email/template/ReactEmailTemplaterTest.java
<<<<<<<< Updated upstream:src/test/java/org/patinanetwork/codebloom/common/email/template/ReactEmailTemplaterTest.java
class ReactEmailTemplaterTest {

    @Test
    void exampleTemplateTest() throws IOException {
        ReactEmailTemplater templater = new ReactEmailTemplaterImpl();
========
class ReactEmailTest {

    @Test
    void exampleTemplateTest() throws IOException {
        ReactEmail client = new ReactEmailImpl();
>>>>>>>> Stashed changes:src/test/java/org/patinanetwork/codebloom/common/email/template/ReactEmailTest.java
========
class ReactEmailTest {

    @Test
    void exampleTemplateTest() throws IOException {
        ReactEmail client = new ReactEmailImpl();
>>>>>>>> Stashed changes:src/test/java/org/patinanetwork/codebloom/common/email/template/ReactEmailTest.java

        String recipientName = "Example";
        String verifyUrl = "https://example.com";
        String supportEmail = "codebloom@patinanetwork.org";

        String renderedHtml = templater.createExampleTemplate(recipientName, verifyUrl, supportEmail);

        Document doc = Jsoup.parse(renderedHtml);

        Element recipientText = doc.getElementById("input-recipientName-innerText");
        assertNotNull(recipientText, "Missing element: input-recipientName-innerText");
        assertEquals("Example", recipientText.text(), "recipientName text not set");

        Element verifyText = doc.getElementById("input-verifyUrl-innerText");
        assertNotNull(verifyText, "Missing element: input-verifyUrl-innerText");
        assertEquals("https://example.com", verifyText.text(), "verifyUrl text not set");

        Element verifyHref = doc.getElementById("input-verifyUrl-href");
        assertNotNull(verifyHref, "Missing element: input-verifyUrl-href");
        assertEquals("https://example.com", verifyHref.attr("href"), "verifyUrl href not set");

        Element supportText = doc.getElementById("input-supportEmail-innerText");
        assertNotNull(supportText, "Missing element: input-supportEmail-innerText");
        assertEquals("codebloom@patinanetwork.org", supportText.text(), "supportEmail text not set");

        Element supportHref = doc.getElementById("input-supportEmail-href");
        assertNotNull(supportHref, "Missing element: input-supportEmail-href");
        assertEquals("codebloom@patinanetwork.org", supportHref.attr("href"), "supportEmail href not set");
    }

    @Test
    void emailTest() throws IOException {
<<<<<<<< Updated upstream:src/test/java/org/patinanetwork/codebloom/common/email/template/ReactEmailTemplaterTest.java
<<<<<<<< Updated upstream:src/test/java/org/patinanetwork/codebloom/common/email/template/ReactEmailTemplaterTest.java
        ReactEmailTemplater templater = new ReactEmailTemplaterImpl();
========
        ReactEmail client = new ReactEmailImpl();
>>>>>>>> Stashed changes:src/test/java/org/patinanetwork/codebloom/common/email/template/ReactEmailTest.java
========
        ReactEmail client = new ReactEmailImpl();
>>>>>>>> Stashed changes:src/test/java/org/patinanetwork/codebloom/common/email/template/ReactEmailTest.java
        String verifyUrl = "https://example.com/example/href";

        String renderedHtml = templater.schoolEmailTemplate(verifyUrl);

        Document doc = Jsoup.parse(renderedHtml);

        Element verifyText = doc.getElementById("input-verifyUrl-innerText");
        assertNotNull(verifyText, "Missing element: input-verifyUrl-innerText");
        assertEquals(verifyUrl, verifyText.text(), "verifyUrl text not set");

        Element verifyHref = doc.getElementById("input-verifyUrl-href");
        assertNotNull(verifyHref, "Missing element: input-verifyUrl-href");
        assertEquals(verifyUrl, verifyHref.attr("href"), "verifyUrl href not set");
    }
}
