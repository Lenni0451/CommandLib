package net.lenni0451.commandlib.utils.interfaces;

import net.lenni0451.commandlib.contexts.ExecutionContext;

/**
 * A functional interface to allow checking if a command can be executed.
 *
 * @param <E> The type of the executor
 */
@FunctionalInterface
public interface ArgumentRequirement<E> {

    /**
     * Test if the command can be executed.<br>
     * If this method returns false the command will not be executed.
     *
     * @param executionContext The execution context
     * @return If the command can be executed
     */
    boolean test(final ExecutionContext<E> executionContext);

    /**
     * Called if the command execution fails.
     *
     * @param executionContext The execution context
     */
    default void onExecuteFail(final ExecutionContext<E> executionContext) {
    }

}
