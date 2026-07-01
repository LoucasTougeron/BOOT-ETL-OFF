package com.etloff.parser;

/**
 * Exception thrown when a CSV line does not contain the expected number of columns.
 */
public class MalformedCsvLineException extends RuntimeException {

    /**
     * Constructs a new exception with a descriptive message.
     *
     * @param lineNumber the line number (for context)
     * @param actual     the actual number of columns found
     * @param expected   the expected number of columns
     */
    public MalformedCsvLineException(int lineNumber, int actual, int expected) {
        super("Malformed CSV line %d: expected %d columns but got %d".formatted(lineNumber, expected, actual));
    }

    /**
     * Constructs a new exception with just a message.
     *
     * @param message the detail message
     */
    public MalformedCsvLineException(String message) {
        super(message);
    }
}