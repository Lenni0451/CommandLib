package net.lenni0451.commandlib.exceptions;

import net.lenni0451.commandlib.utils.interfaces.CommandExceptionHandler;

import javax.annotation.Nullable;

/**
 * An exception which is thrown when the execution of a command chain fails.<br>
 * Reasons for failure are:<br>
 * - A {@link ArgumentParseException} was thrown while parsing an argument<br>
 * - A unhandled {@link RuntimeException} was thrown while parsing an argument<br>
 * - The input ended before all arguments were parsed<br>
 * - The input contained more arguments than the command chain can handle
 */
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

    public ChainExecutionException(final ChainExecutionException exception, final int executionIndexOffset) {
        this.reason = exception.reason;
        this.executionIndex = exception.executionIndex + executionIndexOffset;
        this.readerCursor = exception.readerCursor;
        this.argumentName = exception.argumentName;
        this.extraData = exception.extraData;
    }

    /**
     * @return The reason why the execution failed
     */
    public Reason getReason() {
        return this.reason;
    }

    /**
     * @return The index of the argument which caused the failure
     */
    public int getExecutionIndex() {
        return this.executionIndex;
    }

    /**
     * @return The cursor position of the reader when the failure occurred
     */
    public int getReaderCursor() {
        return this.readerCursor;
    }

    /**
     * @return The name of the argument which caused the failure
     */
    @Nullable
    public String getArgumentName() {
        return this.argumentName;
    }

    /**
     * @return Extra data which was provided by the command chain
     */
    @Nullable
    public String getExtraData() {
        return this.extraData;
    }


    /**
     * The reason why the execution failed.
     */
    public enum Reason {
        /**
         * This is not really a reason but is required if the occurred exception was already handled by the argument node {@link CommandExceptionHandler}.<br>
         * It should be treated like {@link #ARGUMENT_PARSE_EXCEPTION} or {@link #RUNTIME_EXCEPTION}.
         */
        HANDLED_OTHERWISE,
        ARGUMENT_PARSE_EXCEPTION,
        RUNTIME_EXCEPTION,
        MISSING_SPACE,
        NO_ARGUMENTS_LEFT,
        TOO_MANY_ARGUMENTS,
        REQUIREMENT_FAILED
    }

}
