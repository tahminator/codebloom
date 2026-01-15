package org.patinanetwork.codebloom.common.components.duel;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

public class DuelExceptionTest {

    @Test
    void testConstructorWithThrowable() {
        var cause = new RuntimeException("Root cause");
        var exception = new DuelException(cause);

        assertEquals("Duel exception occurred.", exception.getMessage());
        assertEquals(cause, exception.getCause());
        assertTrue(exception.getHttpStatus().isEmpty());
    }

    @Test
    void testConstructorWithHttpStatusAndMessage() {
        var status = HttpStatus.BAD_REQUEST;
        var message = "Invalid duel configuration";

        var exception = new DuelException(status, message);

        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
        assertTrue(exception.getHttpStatus().isPresent());
        assertEquals(status, exception.getHttpStatus().get());
    }

    @Test
    void testConstructorWithNullHttpStatus() {
        var message = "Duel error without HTTP status";

        var exception = new DuelException(null, message);

        assertEquals(message, exception.getMessage());
        assertTrue(exception.getHttpStatus().isEmpty());
    }

    @Test
    void testPrintStackTraceWithHttpStatus() throws IOException {
        var status = HttpStatus.NOT_FOUND;
        var message = "Duel not found";
        var exception = new DuelException(status, message);

        var originalErr = System.err;
        var errContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errContent));

        try {
            exception.printStackTrace();

            var output = errContent.toString();
            assertTrue(output.contains("DuelException"));
            assertTrue(output.contains(message));
        } finally {
            System.setErr(originalErr);
        }
    }

    @Test
    void testPrintStackTraceWithoutHttpStatus() throws IOException {
        var cause = new IllegalStateException("Invalid state");
        var exception = new DuelException(cause);

        var originalErr = System.err;
        var errContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errContent));

        try {
            exception.printStackTrace();

            var output = errContent.toString();
            assertTrue(output.contains("DuelException"));
            assertTrue(output.contains("Invalid state"));
        } finally {
            System.setErr(originalErr);
        }
    }

    @Test
    void testGetHttpStatusReturnsOptional() {
        var exception1 = new DuelException(HttpStatus.INTERNAL_SERVER_ERROR, "Server error");
        var exception2 = new DuelException(new RuntimeException());

        assertTrue(exception1.getHttpStatus().isPresent());
        assertEquals(
                HttpStatus.INTERNAL_SERVER_ERROR, exception1.getHttpStatus().get());
        assertTrue(exception2.getHttpStatus().isEmpty());
    }

    @Test
    void testMessageOnlyConstructor() {
        // Arrange
        var message = "Custom error message";

        // Act
        var exception = new DuelException(null, message);

        // Assert
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
        assertTrue(exception.getHttpStatus().isEmpty());
    }
}
