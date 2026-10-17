package com.cms.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class SecurityUtil {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private SecurityUtil() {
        // Private constructor
    }

    public static String sha256(String input) {
        if (input == null) {
            return null;
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    public static String hashPassword(String input) {
        if (input == null) {
            return null;
        }
        byte[] salt = new byte[16];
        SECURE_RANDOM.nextBytes(salt);
        StringBuilder saltHex = new StringBuilder();
        for (byte b : salt) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                saltHex.append('0');
            }
            saltHex.append(hex);
        }
        String saltStr = saltHex.toString();
        String hashHex = sha256(saltStr + input);
        return saltStr + "$" + hashHex;
    }

    public static boolean verifyPassword(String rawInput, String storedHash) {
        if (rawInput == null || storedHash == null) {
            return false;
        }
        int sep = storedHash.indexOf('$');
        if (sep > 0 && sep < storedHash.length() - 1) {
            String saltPart = storedHash.substring(0, sep);
            String candidate = saltPart + "$" + sha256(saltPart + rawInput);
            return MessageDigest.isEqual(
                    storedHash.getBytes(StandardCharsets.UTF_8),
                    candidate.getBytes(StandardCharsets.UTF_8)
            );
        } else if (sep == -1) {
            String candidate = sha256(rawInput);
            if (candidate == null) {
                return false;
            }
            return MessageDigest.isEqual(
                    storedHash.getBytes(StandardCharsets.UTF_8),
                    candidate.getBytes(StandardCharsets.UTF_8)
            );
        }
        return false;
    }
}
