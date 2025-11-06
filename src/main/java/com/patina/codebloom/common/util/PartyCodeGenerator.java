package com.patina.codebloom.common.util;

import java.security.SecureRandom;


public class PartyCodeGenerator {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGTH = 6;
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * Generates a unique party code by checking against existing codes.
     * 
     * @param existingCodeChecker Function to check if a code already exists
     * @return A unique party code (e.g., "ABC123")
     */
    public static String generateUniqueCode(final java.util.function.Predicate<String> existingCodeChecker) {
        final int maxAttempts = 10;

        for (int attempts = 0; attempts < maxAttempts; attempts++) {
            StringBuilder codeBuilder = new StringBuilder(CODE_LENGTH);
            for (int i = 0; i < CODE_LENGTH; i++) {
                codeBuilder.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
            }
            String code = codeBuilder.toString();

            if (!existingCodeChecker.test(code)) {
                return code;
            }
        }

        throw new RuntimeException("Failed to generate unique party code after " + maxAttempts + " attempts");
    }
}