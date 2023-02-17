package net.lenni0451.commandlib.types;

import net.lenni0451.commandlib.contexts.ExecutionContext;
import net.lenni0451.commandlib.exceptions.ArgumentParseException;
import net.lenni0451.commandlib.utils.StringReader;

import javax.annotation.Nonnull;
import java.util.Set;

public interface ArgumentType<E, T> {

    default int getWeight() {
        return 0;
    }

    @Nonnull
    T parseValue(final ExecutionContext<E> context, final StringReader stringReader) throws ArgumentParseException, RuntimeException;

    default void parseCompletions(final Set<String> completions, final ExecutionContext<E> context, final StringReader stringReader) {
    }

}
