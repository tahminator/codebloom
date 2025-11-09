package com.patina.codebloom.common.utils.duel;

import java.security.SecureRandom;

public class PartyCodeGenerator {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGTH = 6;
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * Generates a random party code
     * 
     * @return A random party code (e.g., "ABC123")
     */
    public static String generateCode() {
        StringBuilder codeBuilder = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            codeBuilder.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return codeBuilder.toString();
    }
}