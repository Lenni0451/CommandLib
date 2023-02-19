package net.lenni0451.commandlib.types;

import net.lenni0451.commandlib.contexts.ExecutionContext;
import net.lenni0451.commandlib.exceptions.ArgumentParseException;
import net.lenni0451.commandlib.utils.StringReader;

import javax.annotation.Nonnull;
import java.util.Set;

/**
 * The argument type is a simple wrapper interface to argument nodes.<br>
 * It is used to parse the value of an argument and to provide completions for it.
 *
 * @param <E> The type of the executor
 * @param <T> The type of the argument
 */
public interface ArgumentType<E, T> {

    /**
     * @return The weight of this type. The higher the weight the more important it is.
     */
    default int getWeight() {
        return 0;
    }

    /**
     * Parses the value of the argument.
     *
     * @param executionContext The execution context
     * @param stringReader     The string reader
     * @return The parsed value
     * @throws ArgumentParseException If the value could not be parsed
     * @throws RuntimeException       If an unexpected error occurred
     */
    @Nonnull
    T parseValue(final ExecutionContext<E> executionContext, final StringReader stringReader) throws ArgumentParseException, RuntimeException;

    /**
     * Provide the completions of this argument.
     *
     * @param completions      The set of completions
     * @param executionContext The execution context
     * @param stringReader     The string reader
     */
    default void parseCompletions(final Set<String> completions, final ExecutionContext<E> executionContext, final StringReader stringReader) {
    }

}
