package org.patinanetwork.codebloom.common.email.template;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

@Component
<<<<<<< Updated upstream
<<<<<<<< Updated upstream:src/main/java/org/patinanetwork/codebloom/common/email/template/ReactEmailTemplaterImpl.java
public class ReactEmailTemplaterImpl implements ReactEmailTemplater {
========
public class ReactEmailImpl implements ReactEmail {
>>>>>>>> Stashed changes:src/main/java/org/patinanetwork/codebloom/common/email/template/ReactEmailImpl.java
=======
public class ReactEmailImpl implements ReactEmail {
>>>>>>> Stashed changes

    private String getHtmlAsString(final String path) throws IOException {
        ClassPathResource resource = new ClassPathResource(path);
        return StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
    }

    @Override
    public String createExampleTemplate(final String recipientName, final String verifyUrl, final String supportEmail)
            throws IOException {
        final String html = getHtmlAsString("static/email/example.html");
        final Document doc = Jsoup.parse(html);

        doc.getElementById("input-recipientName-innerText").text(recipientName);
        doc.getElementById("input-verifyUrl-innerText").text(verifyUrl);
        doc.getElementById("input-verifyUrl-href").attr("href", verifyUrl);
        doc.getElementById("input-supportEmail-innerText").text(supportEmail);
        doc.getElementById("input-supportEmail-href").attr("href", supportEmail);

        return doc.outerHtml();
    }

    @Override
    public String schoolEmailTemplate(final String verifyUrl) throws IOException {
        final String html = getHtmlAsString("static/email/school-email.html");
        final Document doc = Jsoup.parse(html);

        doc.getElementById("input-verifyUrl-innerText").text(verifyUrl);
        doc.getElementById("input-verifyUrl-href").attr("href", verifyUrl);

        return doc.outerHtml();
    }
}
