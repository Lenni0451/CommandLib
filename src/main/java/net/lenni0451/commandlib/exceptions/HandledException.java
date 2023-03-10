package net.lenni0451.commandlib.exceptions;

import net.lenni0451.commandlib.utils.interfaces.CommandExceptionHandler;

import javax.annotation.Nonnull;

/**
 * A wrapper exception which is used to mark the original exception as handled by a {@link CommandExceptionHandler}.
 */
public class HandledException extends RuntimeException {

    private static final StackTraceElement[] EMPTY_STACK_TRACE = new StackTraceElement[0];


    public HandledException(@Nonnull final Throwable cause) {
        super(cause);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }

    @Override
    public StackTraceElement[] getStackTrace() {
        return EMPTY_STACK_TRACE;
    }

}
