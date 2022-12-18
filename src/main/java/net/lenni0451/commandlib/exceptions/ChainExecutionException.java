package net.lenni0451.commandlib.exceptions;

public class ChainExecutionException extends Exception {

    private final Reason reason;

    public ChainExecutionException(final ArgumentParseException parseException) {
        super(parseException);
        this.reason = Reason.ARGUMENT_PARSE_EXCEPTION;
    }

    public ChainExecutionException(final Reason reason) {
        this.reason = reason;
    }

    public Reason getReason() {
        return this.reason;
    }


    public enum Reason {
        ARGUMENT_PARSE_EXCEPTION, NO_ARGUMENTS_LEFT
    }

}
