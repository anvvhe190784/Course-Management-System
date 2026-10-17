package com.cms.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SecurityUtilTest {

    @Test
    void testSha256_ValidHash() {
        String input = "admin123";
        // Known SHA-256 for admin123
        String expected = "240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9";

        String actual = SecurityUtil.sha256(input);

        assertEquals(expected, actual);
    }

    @Test
    void testSha256_NullInputReturnsNull() {
        assertNull(SecurityUtil.sha256(null));
    }

    @Test
    void testSha256_EmptyInput() {
        // Known SHA-256 for empty string
        String emptyHash = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855";
        assertEquals(emptyHash, SecurityUtil.sha256(""));
    }

    @Test
    void testHashPassword_GeneratesUniqueSaltedHashes() {
        String password = "secret123";
        String hash1 = SecurityUtil.hashPassword(password);
        String hash2 = SecurityUtil.hashPassword(password);

        assertNotNull(hash1);
        assertNotNull(hash2);
        assertNotEquals(hash1, hash2);
        assertTrue(hash1.contains("$"));
        assertTrue(hash2.contains("$"));
        assertTrue(SecurityUtil.verifyPassword(password, hash1));
        assertTrue(SecurityUtil.verifyPassword(password, hash2));
    }

    @Test
    void testVerifyPassword_SupportsLegacyUnsaltedSha256() {
        String password = "secret123";
        String legacyHash = SecurityUtil.sha256(password);

        assertTrue(SecurityUtil.verifyPassword(password, legacyHash));
        assertFalse(SecurityUtil.verifyPassword("wrongSecret", legacyHash));
        assertFalse(SecurityUtil.verifyPassword(null, legacyHash));
        assertFalse(SecurityUtil.verifyPassword(password, null));
    }
}
