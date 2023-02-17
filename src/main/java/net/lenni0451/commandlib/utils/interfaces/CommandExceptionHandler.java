package net.lenni0451.commandlib.utils.interfaces;

import net.lenni0451.commandlib.contexts.ExecutionContext;
import net.lenni0451.commandlib.exceptions.ArgumentParseException;

@FunctionalInterface
public interface CommandExceptionHandler<E> {

    void handle(final ExecutionContext<E> executor, final Exception t) throws ArgumentParseException, RuntimeException;

}
