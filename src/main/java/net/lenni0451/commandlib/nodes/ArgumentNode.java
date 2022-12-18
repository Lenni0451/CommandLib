package net.lenni0451.commandlib.nodes;

import net.lenni0451.commandlib.ExecutionContext;
import net.lenni0451.commandlib.exceptions.ArgumentParseException;
import net.lenni0451.commandlib.utils.CompletionsProvider;
import net.lenni0451.commandlib.utils.StringReader;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class ArgumentNode<E, T> {

    private final String name;
    private final String description;
    private final List<ArgumentNode<E, ?>> children;
    protected int weight = 0;
    private CompletionsProvider<E> optionalCompletionsProvider;
    private CompletionsProvider<E> enforcedCompletionsProvider;
    private Function<ExecutionContext<E>, ?> executor;

    public ArgumentNode(final String name) {
        this(name, null);
    }

    public ArgumentNode(final String name, @Nullable final String description) {
        this.name = name;
        this.description = description;
        this.children = new ArrayList<>();
    }

    public String name() {
        return this.name;
    }

    @Nullable
    public String description() {
        return this.description;
    }

    public List<ArgumentNode<E, ?>> children() {
        return this.children;
    }

    public int weight() {
        return this.weight;
    }

    @Nullable
    public CompletionsProvider<E> optionalCompletionsProvider() {
        return this.optionalCompletionsProvider;
    }

    @Nullable
    public CompletionsProvider<E> enforcedCompletionsProvider() {
        return this.enforcedCompletionsProvider;
    }

    @Nullable
    public Function<ExecutionContext<E>, ?> executor() {
        return this.executor;
    }

    public List<String> completions(final ExecutionContext<E> context, final StringReader reader) {
        List<String> completions = new ArrayList<>();
        if (this.enforcedCompletionsProvider != null) this.enforcedCompletionsProvider.provide(completions, context, reader);
        else if (this.optionalCompletionsProvider != null) this.optionalCompletionsProvider.provide(completions, context, reader);
        else this.parseCompletions(completions, context, reader);
        return completions;
    }

    @Nonnull
    public T parse(final ExecutionContext<E> context, final StringReader reader) throws ArgumentParseException {
        int cursor = reader.getCursor();
        T parsed = this.parseValue(context, reader);
        String read = reader.getString().substring(cursor, reader.getCursor()).trim();
        if (this.enforcedCompletionsProvider != null) {
            List<String> completions = new ArrayList<>();
            this.enforcedCompletionsProvider.provide(completions, context, reader);
            if (!context.getArgumentComparator().compare(read, completions)) throw new ArgumentParseException("Invalid value");
        } else if (this.optionalCompletionsProvider == null) {
            List<String> completions = new ArrayList<>();
            this.parseCompletions(completions, context, reader);
            if (!context.getArgumentComparator().compare(read, completions)) throw new ArgumentParseException("Invalid value");
        }
        return parsed;
    }

    @Nonnull
    protected abstract T parseValue(final ExecutionContext<E> context, final StringReader stringReader) throws ArgumentParseException;

    public abstract void parseCompletions(final List<String> completions, final ExecutionContext<E> context, final StringReader stringReader);

    public ArgumentNode<E, T> then(final ArgumentNode<E, ?> child) {
        this.children.add(child);
        return this;
    }

    public ArgumentNode<E, T> optional(final CompletionsProvider<E> completionsProvider) {
        this.optionalCompletionsProvider = completionsProvider;
        return this;
    }

    public ArgumentNode<E, T> enforced(final CompletionsProvider<E> completionsProvider) {
        this.enforcedCompletionsProvider = completionsProvider;
        return this;
    }

    public ArgumentNode<E, T> executes(final Runnable runnable) {
        this.executor = context -> {
            runnable.run();
            return null;
        };
        return this;
    }

    public ArgumentNode<E, T> executes(final Consumer<ExecutionContext<E>> consumer) {
        this.executor = context -> {
            consumer.accept(context);
            return null;
        };
        return this;
    }

    public ArgumentNode<E, T> executes(final Supplier<?> supplier) {
        this.executor = context -> supplier.get();
        return this;
    }

    public ArgumentNode<E, T> executes(final Function<ExecutionContext<E>, ?> function) {
        this.executor = function;
        return this;
    }

}
