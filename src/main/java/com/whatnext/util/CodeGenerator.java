package com.whatnext.util;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class CodeGenerator {

    private static final Random RANDOM = new SecureRandom();

    /**
     * Generate a 5-letter code from username
     * Takes random letters from username (uppercase)
     * If username has fewer letters, repeats or adds random letters
     */
    public static String generateTaskCode(String username) {
        if (username == null || username.isEmpty()) {
            return generateRandomCode();
        }

        // Extract only letters from username
        String lettersOnly = username.replaceAll("[^a-zA-Z]", "").toUpperCase();

        if (lettersOnly.length() == 0) {
            return generateRandomCode();
        }

        if (lettersOnly.length() >= 5) {
            // Pick 5 random letters from username
            List<Character> chars = new ArrayList<>();
            for (char c : lettersOnly.toCharArray()) {
                chars.add(c);
            }
            Collections.shuffle(chars, RANDOM);

            StringBuilder code = new StringBuilder();
            for (int i = 0; i < 5; i++) {
                code.append(chars.get(i));
            }
            return code.toString();
        } else {
            // Use all letters and fill remaining with random
            StringBuilder code = new StringBuilder(lettersOnly);
            while (code.length() < 5) {
                code.append((char) ('A' + RANDOM.nextInt(26)));
            }
            return code.toString();
        }
    }

    /**
     * Generate a random 5-letter code
     */
    private static String generateRandomCode() {
        StringBuilder code = new StringBuilder(5);
        for (int i = 0; i < 5; i++) {
            code.append((char) ('A' + RANDOM.nextInt(26)));
        }
        return code.toString();
    }

    /**
     * Generate a 6-digit OTP
     */
    public static String generateOTP() {
        return String.format("%06d", RANDOM.nextInt(1000000));
    }
}
