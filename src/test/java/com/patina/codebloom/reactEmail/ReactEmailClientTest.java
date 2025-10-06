package com.patina.codebloom.reactEmail;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.patina.codebloom.common.email.client.ReactEmailClient;
import com.patina.codebloom.common.email.client.ReactEmailClientImpl;

class ReactEmailClientTest {

    @Test
    void exampleTemplateTest() throws IOException {
        ReactEmailClient client = new ReactEmailClientImpl();

        String recipientName = "Example";
        String verifyUrl = "https://example.com";
        String supportEmail = "codebloom@patinanetwork.org";

        String renderedHtml = client.createExampleTemplate(recipientName, verifyUrl, supportEmail);

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
        ReactEmailClient client = new ReactEmailClientImpl();
        String verifyUrl = "https://example.com";

        String renderedHtml = client.schoolEmailTemplate(verifyUrl);

        Document doc = Jsoup.parse(renderedHtml);

        Element verifyText = doc.getElementById("input-verifyUrl-innerText");
        assertNotNull(verifyText, "Missing element: input-verifyUrl-innerText");
        assertEquals(verifyUrl, verifyText.text(), "verifyUrl text not set");

        Element verifyHref = doc.getElementById("input-verifyUrl-href");
        assertNotNull(verifyHref, "Missing element: input-verifyUrl-href");
        assertEquals(verifyUrl, verifyHref.attr("href"), "verifyUrl href not set");

    }
}