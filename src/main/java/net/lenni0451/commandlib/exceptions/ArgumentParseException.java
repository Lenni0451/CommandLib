package net.lenni0451.commandlib.exceptions;

import javax.annotation.Nullable;

/**
 * An exception which is thrown when an argument could not be parsed.
 */
public class ArgumentParseException extends Exception {

    /**
     * Create a new exception with the given name of the argument.
     *
     * @param name The name of the argument
     * @return The created exception
     */
    public static ArgumentParseException named(final String name) {
        return new ArgumentParseException("Failed to parse argument " + name, name, null, null);
    }

    /**
     * Create a new exception with the given expected value.
     *
     * @param expected The expected value
     * @return The created exception
     */
    public static ArgumentParseException expected(final String expected) {
        return new ArgumentParseException("Failed to parse argument. Expected: " + expected, null, null, expected);
    }

    /**
     * Create a new exception with the given reason.
     *
     * @param reason The reason
     * @return The created exception
     */
    public static ArgumentParseException reason(final String reason) {
        return new ArgumentParseException("Failed to parse argument. Reason: " + reason, null, reason, null);
    }

    /**
     * Create a new exception with the given name of the argument and the expected value.
     *
     * @param name     The name of the argument
     * @param expected The expected value
     * @return The created exception
     */
    public static ArgumentParseException namedExpected(final String name, final String expected) {
        return new ArgumentParseException("Failed to parse argument " + name + ". Expected: " + expected, name, null, expected);
    }

    /**
     * Create a new exception with the given name of the argument and the reason.
     *
     * @param name   The name of the argument
     * @param reason The reason
     * @return The created exception
     */
    public static ArgumentParseException namedReason(final String name, final String reason) {
        return new ArgumentParseException("Failed to parse argument " + name + ". Reason: " + reason, name, reason, null);
    }

    /**
     * Create a new exception with the given message.
     *
     * @param message The message
     * @return The created exception
     */
    public static ArgumentParseException of(final String message) {
        return new ArgumentParseException(message, null, null, null);
    }

    /**
     * Create a new exception with the given name of the argument, the reason for failure and the expected value.
     *
     * @param name     The name of the argument
     * @param reason   The reason
     * @param expected The expected value
     * @return The created exception
     */
    public static ArgumentParseException of(final String name, final String reason, final String expected) {
        return new ArgumentParseException("Failed to parse argument " + name + ". Reason: " + reason, name, reason, expected);
    }


    private final String name;
    private final String reason;
    private final String expected;

    private ArgumentParseException(final String message, @Nullable final String name, @Nullable final String reason, @Nullable final String expected) {
        super(message);
        this.name = name;
        this.reason = reason;
        this.expected = expected;
    }

    /**
     * @return The name of the argument
     */
    @Nullable
    public String getName() {
        return this.name;
    }

    /**
     * @return The reason for the failure
     */
    @Nullable
    public String getReason() {
        return this.reason;
    }

    /**
     * @return The expected value
     */
    @Nullable
    public String getExpected() {
        return this.expected;
    }

}
