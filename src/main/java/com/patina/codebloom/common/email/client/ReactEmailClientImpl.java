package com.patina.codebloom.common.email.client;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;

public class ReactEmailClientImpl implements ReactEmailClient {

    @Override
    public String getHtmlAsString(final String path) throws IOException {
        ClassPathResource resource = new ClassPathResource(path);
        return StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);

    }

    @Override
    public String renderWithJsoup(final String classpathHtmlPath, final Map<String, String> vars) throws IOException {
        final String html = getHtmlAsString(classpathHtmlPath);
        final Document doc = Jsoup.parse(html);

        for (Map.Entry<String, String> e : vars.entrySet()) {
            final String key = e.getKey();
            final String value = e.getValue();

            Element textEl = doc.getElementById("input-" + key + "-innerText");
            if (textEl != null) {
                textEl.text(value);
            }
            Element hrefEl = doc.getElementById("input-" + key + "-href");
            if (hrefEl != null) {
                hrefEl.attr("href", value);
            }
        }

        return doc.outerHtml();
    }

    public static void main(final String[] args) throws IOException {
        ReactEmailClient client = new ReactEmailClientImpl();
        String path = "static/email/example.html";
        String html = client.getHtmlAsString(path);
        System.out.println("Loaded HTML: " + html);
        System.out.println("\n \n");
        System.out.println("Here is the rendered Jsoup \n");

        Map<String, String> vars = Map.of(

                        "recipientName", "Alfardil",
                        "verifyUrl", "https://example.com",
                        "supportEmail", "codebloom@patinanetwork.org"

        );

        String rendered = client.renderWithJsoup(path, vars);
        System.out.println(rendered);
    }

}
