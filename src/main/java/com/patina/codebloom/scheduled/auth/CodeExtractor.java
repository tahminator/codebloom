package com.patina.codebloom.scheduled.auth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CodeExtractor {

    public static String extractCode(final String content) {
        Pattern pattern = Pattern.compile("Verification code:\\s*(\\d{6})");
        Matcher matcher = pattern.matcher(content);

        if (matcher.find()) {
            return matcher.group(1);
        }

        return null;
    }
}
