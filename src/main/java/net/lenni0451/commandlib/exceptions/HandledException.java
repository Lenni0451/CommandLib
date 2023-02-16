package net.lenni0451.commandlib.exceptions;

import javax.annotation.Nonnull;

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
