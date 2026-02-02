package ui;

import org.junit.jupiter.api.Test;

import java.util.Scanner;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InputValidatorTest {
    @Test
    void readInt_validInput() {
        InputValidator validator = new InputValidator();
        Scanner scanner = new Scanner("42\n");

        int result = validator.readInt(scanner);

        assertEquals(42, result);
    }

    @Test
    void readInt_invalidThenValid() {
        InputValidator validator = new InputValidator();
        Scanner scanner = new Scanner("abc\n15\n");

        int result = validator.readInt(scanner);

        assertEquals(15, result);
    }


    @Test
    void readIndex_valid() {
        InputValidator validator = new InputValidator();
        Scanner scanner = new Scanner("3\n");

        int result = validator.readIndex(scanner, 5);

        assertEquals(2, result);
    }

    @Test
    void readIndex_invalidThenValid() {
        InputValidator validator = new InputValidator();
        Scanner scanner = new Scanner("10\n2\n");

        int result = validator.readIndex(scanner, 5);

        assertEquals(1, result);
    }

    @Test
    void readUUID_valid() {
        InputValidator validator = new InputValidator();
        UUID expected = UUID.randomUUID();
        Scanner scanner = new Scanner(expected + "\n");

        UUID result = validator.readUUID(scanner);

        assertEquals(expected, result);
    }

    @Test
    void readUUID_invalidThenValid() {
        InputValidator validator = new InputValidator();
        UUID expected = UUID.randomUUID();
        Scanner scanner = new Scanner("not-a-uuid\n" + expected + "\n");

        UUID result = validator.readUUID(scanner);

        assertEquals(expected, result);
    }

    @Test
    void readEmail_valid() {
        InputValidator validator = new InputValidator();
        Scanner scanner = new Scanner("test@example.com\n");

        String result = validator.readEmail(scanner);

        assertEquals("test@example.com", result);
    }

    @Test
    void readEmail_invalidThenValid() {
        InputValidator validator = new InputValidator();
        Scanner scanner = new Scanner("wrong-email\nvalid@mail.com\n");

        String result = validator.readEmail(scanner);

        assertEquals("valid@mail.com", result);
    }
}