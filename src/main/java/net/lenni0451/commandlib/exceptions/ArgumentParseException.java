package net.lenni0451.commandlib.exceptions;

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
        return of("Failed to parse argument " + name);
    }

    /**
     * Create a new exception with the given expected value.
     *
     * @param expected The expected value
     * @return The created exception
     */
    public static ArgumentParseException expected(final String expected) {
        return of("Failed to parse argument. Expected: " + expected);
    }

    /**
     * Create a new exception with the given reason.
     *
     * @param reason The reason
     * @return The created exception
     */
    public static ArgumentParseException reason(final String reason) {
        return of("Failed to parse argument. Reason: " + reason);
    }

    /**
     * Create a new exception with the given name of the argument and the expected value.
     *
     * @param name     The name of the argument
     * @param expected The expected value
     * @return The created exception
     */
    public static ArgumentParseException namedExpected(final String name, final String expected) {
        return of("Failed to parse argument " + name + ". Expected: " + expected);
    }

    /**
     * Create a new exception with the given name of the argument and the reason.
     *
     * @param name   The name of the argument
     * @param reason The reason
     * @return The created exception
     */
    public static ArgumentParseException namedReason(final String name, final String reason) {
        return of("Failed to parse argument " + name + ". Reason: " + reason);
    }

    /**
     * Create a new exception with the given message.
     *
     * @param message The message
     * @return The created exception
     */
    public static ArgumentParseException of(final String message) {
        return new ArgumentParseException(message);
    }


    private ArgumentParseException(final String reason) {
        super(reason);
    }

}
