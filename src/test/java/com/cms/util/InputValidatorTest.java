package com.cms.util;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

public class InputValidatorTest {

    @Test
    void testReadString() {
        Scanner scanner = new Scanner("  Hello World  \n");
        String result = InputValidator.readString(scanner, "prompt: ", false);
        assertEquals("Hello World", result);
    }

    @Test
    void testReadInt_PositiveOnly() {
        Scanner scanner = new Scanner("-5\n0\n42\n");
        int result = InputValidator.readInt(scanner, "prompt: ", true);
        assertEquals(42, result);
    }

    @Test
    void testReadLocalDate_ValidFormat() {
        Scanner scanner = new Scanner("2003-05-15\n");
        LocalDate date = InputValidator.readLocalDate(scanner, "prompt: ", false);
        assertEquals(LocalDate.of(2003, 5, 15), date);
    }

    @Test
    void testReadEmail_ValidEmail() {
        Scanner scanner = new Scanner("invalid-email\nstudent@example.com\n");
        String email = InputValidator.readEmail(scanner, "prompt: ", false);
        assertEquals("student@example.com", email);
    }

    @Test
    void testReadPhone_ValidPhone() {
        Scanner scanner = new Scanner("abc\n0912345678\n");
        String phone = InputValidator.readPhone(scanner, "prompt: ", false);
        assertEquals("0912345678", phone);
    }

    @Test
    void testReadSex_MaleAndFemale() {
        Scanner scanner1 = new Scanner("1\n");
        assertTrue(InputValidator.readSex(scanner1, "prompt: "));

        Scanner scanner2 = new Scanner("0\n");
        assertFalse(InputValidator.readSex(scanner2, "prompt: "));
    }

    @Test
    void testConfirm_YesAndNo() {
        Scanner scanner1 = new Scanner("Y\n");
        assertTrue(InputValidator.confirm(scanner1, "prompt: "));

        Scanner scanner2 = new Scanner("n\n");
        assertFalse(InputValidator.confirm(scanner2, "prompt: "));
    }
}

