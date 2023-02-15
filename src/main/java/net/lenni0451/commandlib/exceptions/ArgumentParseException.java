package net.lenni0451.commandlib.exceptions;

public class ArgumentParseException extends Exception {

    public static ArgumentParseException named(final String name) {
        return of("Failed to parse argument " + name);
    }

    public static ArgumentParseException expected(final String expected) {
        return of("Failed to parse argument. Expected: " + expected);
    }

    public static ArgumentParseException reason(final String reason) {
        return of("Failed to parse argument. Reason: " + reason);
    }

    public static ArgumentParseException namedExpected(final String name, final String expected) {
        return of("Failed to parse argument " + name + ". Expected: " + expected);
    }

    public static ArgumentParseException namedReason(final String name, final String reason) {
        return of("Failed to parse argument " + name + ". Reason: " + reason);
    }

    public static ArgumentParseException of(final String reason) {
        return new ArgumentParseException(reason);
    }


    private ArgumentParseException(final String reason) {
        super(reason);
    }

}
