package net.lenni0451.commandlib.exceptions;

public class ArgumentParseException extends Exception {

    public ArgumentParseException(final String expected) {
        super("Failed to parse argument. Expected: " + expected);
    }

}
