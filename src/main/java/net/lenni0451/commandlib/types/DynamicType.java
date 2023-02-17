package net.lenni0451.commandlib.types;

import net.lenni0451.commandlib.contexts.ExecutionContext;
import net.lenni0451.commandlib.exceptions.ArgumentParseException;
import net.lenni0451.commandlib.utils.StringReader;
import net.lenni0451.commandlib.utils.interfaces.CompletionsProvider;

import javax.annotation.Nonnull;
import java.util.Set;

public class DynamicType<E, T> implements ArgumentType<E, T> {

    private final SingleParser<T> singleParser;
    private final BiParser<E, T> biParser;
    private final CompletionsProvider<E> completionsProvider;

    public DynamicType(final SingleParser<T> singleParser) {
        this.singleParser = singleParser;
        this.biParser = null;
        this.completionsProvider = null;
    }

    public DynamicType(final SingleParser<T> singleParser, final CompletionsProvider<E> completionsProvider) {
        this.singleParser = singleParser;
        this.biParser = null;
        this.completionsProvider = completionsProvider;
    }

    public DynamicType(final BiParser<E, T> biParser) {
        this.singleParser = null;
        this.biParser = biParser;
        this.completionsProvider = null;
    }

    public DynamicType(final BiParser<E, T> biParser, final CompletionsProvider<E> completionsProvider) {
        this.singleParser = null;
        this.biParser = biParser;
        this.completionsProvider = completionsProvider;
    }

    @Nonnull
    @Override
    public T parseValue(ExecutionContext<E> executionContext, StringReader stringReader) throws ArgumentParseException, RuntimeException {
        if (this.singleParser != null) return this.singleParser.parse(stringReader);
        if (this.biParser != null) return this.biParser.parse(executionContext, stringReader);
        throw ArgumentParseException.reason("No parser was set");
    }

    @Override
    public void parseCompletions(Set<String> completions, ExecutionContext<E> executionContext, StringReader stringReader) {
        if (this.completionsProvider != null) this.completionsProvider.provide(completions, executionContext, stringReader);
    }


    @FunctionalInterface
    public interface SingleParser<T> {
        T parse(final StringReader stringReader) throws ArgumentParseException, RuntimeException;
    }

    @FunctionalInterface
    public interface BiParser<E, T> {
        T parse(final ExecutionContext<E> executionContext, final StringReader stringReader) throws ArgumentParseException, RuntimeException;
    }

}
