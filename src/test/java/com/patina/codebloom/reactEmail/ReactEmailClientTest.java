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
    void jsoupRenderTest() throws IOException {
        ReactEmailClient client = new ReactEmailClientImpl();

        String recipientName = "Example";
        String verifyUrl = "https://example.com";
        String supportEmail = "codebloom@patinanetwork.org";

        String renderedHtml = client.createExampleTemplate(recipientName, verifyUrl, supportEmail);
        System.out.println(renderedHtml);

        Document doc = Jsoup.parse(renderedHtml);

        Element recipientText = doc.getElementById("input-recipientName-innerText");
        assertNotNull(recipientText, "Missing element: input-recipientName-innerText");
        assertEquals("Example", recipientText.text(), "recipientName text not set");

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
}