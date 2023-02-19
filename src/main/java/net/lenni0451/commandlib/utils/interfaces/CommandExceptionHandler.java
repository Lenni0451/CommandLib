package net.lenni0451.commandlib.utils.interfaces;

import net.lenni0451.commandlib.contexts.ExecutionContext;
import net.lenni0451.commandlib.exceptions.ArgumentParseException;

/**
 * A functional interface to allow handling exceptions thrown when parsing arguments.
 *
 * @param <E> The type of the executor
 */
@FunctionalInterface
public interface CommandExceptionHandler<E> {

    /**
     * Handle the thrown exception.
     *
     * @param executor The executor
     * @param t        The thrown exception
     * @throws ArgumentParseException If the exception should be handled as an invalid argument
     * @throws RuntimeException       If the exception should be handled as an internal error
     */
    void handle(final ExecutionContext<E> executor, final Exception t) throws ArgumentParseException, RuntimeException;

}
