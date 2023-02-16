package net.lenni0451.commandlib.exceptions;

import javax.annotation.Nullable;

public class ChainExecutionException extends Exception {

    private final Reason reason;
    private final int executionIndex;
    private final int readerCursor;
    private final String argumentName;
    private final String extraData;

    public ChainExecutionException(final ArgumentParseException parseException, final int executionIndex, final int readerCursor, @Nullable final String argumentName, @Nullable final String extraData) {
        super(parseException);
        this.reason = Reason.ARGUMENT_PARSE_EXCEPTION;
        this.executionIndex = executionIndex;
        this.readerCursor = readerCursor;
        this.argumentName = argumentName;
        this.extraData = extraData;
    }

    public ChainExecutionException(final RuntimeException e, final int executionIndex, final int readerCursor, @Nullable final String argumentName, @Nullable final String extraData) {
        super(e);
        this.reason = Reason.RUNTIME_EXCEPTION;
        this.executionIndex = executionIndex;
        this.readerCursor = readerCursor;
        this.argumentName = argumentName;
        this.extraData = extraData;
    }

    public ChainExecutionException(final HandledException handled, final int executionIndex, final int readerCursor, @Nullable final String argumentName, @Nullable final String extraData) {
        super(handled.getCause());
        this.reason = Reason.HANDLED_OTHERWISE;
        this.executionIndex = executionIndex;
        this.readerCursor = readerCursor;
        this.argumentName = argumentName;
        this.extraData = extraData;
    }

    public ChainExecutionException(final Reason reason, final int executionIndex, final int readerCursor, @Nullable final String argumentName, @Nullable final String extraData) {
        this.reason = reason;
        this.executionIndex = executionIndex;
        this.readerCursor = readerCursor;
        this.argumentName = argumentName;
        this.extraData = extraData;
    }

    public Reason getReason() {
        return this.reason;
    }

    public int getExecutionIndex() {
        return this.executionIndex;
    }

    public int getReaderCursor() {
        return this.readerCursor;
    }

    @Nullable
    public String getArgumentName() {
        return this.argumentName;
    }

    @Nullable
    public String getExtraData() {
        return this.extraData;
    }


    public enum Reason {
        HANDLED_OTHERWISE, ARGUMENT_PARSE_EXCEPTION, RUNTIME_EXCEPTION, MISSING_SPACE, NO_ARGUMENTS_LEFT, TOO_MANY_ARGUMENTS
    }

}
