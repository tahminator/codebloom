package com.patina.codebloom.common.email.client;

import java.io.IOException;
import java.util.Map;

public interface ReactEmailClient {
    /**
     * Helper function to get HTML as a String.
     */
    String getHtmlAsString(String path) throws IOException;

    /**
     * Load the generated HTML from ClassPathResources as a String then injects
     * variables using Jsoup and renders HTML as a string.
     * 
     * Supports "-innerText" and "-href"
     * 
     * @param classpathHtmlPath the location of the generated HTML
     * @param vars a map where each key is the ID of an HTML element, and each value
     * is the text or attribute to insert for that element
     * @return the rendered HTML with variables filled
     * @throws IOException
     */
    String renderWithJsoup(String classpathHtmlPath, Map<String, String> vars) throws IOException;
}
