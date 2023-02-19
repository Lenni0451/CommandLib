package net.lenni0451.commandlib.utils.interfaces;

import net.lenni0451.commandlib.contexts.ExecutionContext;
import net.lenni0451.commandlib.utils.StringReader;

import java.util.Set;

/**
 * A functional interface to allow supplying custom completions.
 *
 * @param <E> The type of the executor
 */
@FunctionalInterface
public interface CompletionsProvider<E> {

    /**
     * Provide custom completions.
     *
     * @param completions      The set of completions
     * @param executionContext The execution context
     * @param stringReader     The string reader
     */
    void provide(final Set<String> completions, final ExecutionContext<E> executionContext, final StringReader stringReader);

}
