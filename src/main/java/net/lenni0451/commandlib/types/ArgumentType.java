package net.lenni0451.commandlib.types;

import net.lenni0451.commandlib.ExecutionContext;
import net.lenni0451.commandlib.exceptions.ArgumentParseException;
import net.lenni0451.commandlib.utils.StringReader;

import javax.annotation.Nonnull;
import java.util.List;

public interface ArgumentType<E, T> {

    default int getWeight() {
        return 0;
    }

    @Nonnull
    T parseValue(final ExecutionContext<E> context, final StringReader stringReader) throws ArgumentParseException, RuntimeException;

    void parseCompletions(final List<String> completions, final ExecutionContext<E> context, final StringReader stringReader);

}
