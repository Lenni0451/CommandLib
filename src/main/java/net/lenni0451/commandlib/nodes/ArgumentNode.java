package net.lenni0451.commandlib.nodes;

import net.lenni0451.commandlib.contexts.CompletionContext;
import net.lenni0451.commandlib.contexts.ExecutionContext;
import net.lenni0451.commandlib.exceptions.ArgumentParseException;
import net.lenni0451.commandlib.exceptions.HandledException;
import net.lenni0451.commandlib.utils.StringReader;
import net.lenni0451.commandlib.utils.interfaces.CommandExceptionHandler;
import net.lenni0451.commandlib.utils.interfaces.CompletionsProvider;
import net.lenni0451.commandlib.utils.interfaces.ValueValidator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class ArgumentNode<E, T> {

    private final String name;
    private final String description;
    private final List<ArgumentNode<E, ?>> children;
    protected int weight = 0;
    protected boolean providesArgument = true;
    private ValueValidator<T> validator;
    private CompletionsProvider<E> completionsProvider;
    private CommandExceptionHandler<E> exceptionHandler;
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

    public boolean providesArgument() {
        return this.providesArgument;
    }

    @Nullable
    public CompletionsProvider<E> completionsProvider() {
        return this.completionsProvider;
    }

    @Nullable
    public Function<ExecutionContext<E>, ?> executor() {
        return this.executor;
    }

    @Nonnull
    public T value(final ExecutionContext<E> context, final StringReader stringReader) throws ArgumentParseException, RuntimeException {
        T value;
        try {
            value = this.parseValue(context, stringReader);
        } catch (ArgumentParseException | RuntimeException e) {
            if (this.exceptionHandler != null && context.isExecution()) {
                this.exceptionHandler.handle(context, e);
                throw new HandledException(e);
            }
            throw e;
        }
        if (this.validator != null && !this.validator.validate(value)) throw ArgumentParseException.namedReason(this.name, "Invalid value");
        return value;
    }

    public Set<String> completions(final CompletionContext completionContext, final ExecutionContext<E> executionContext, final StringReader reader) {
        Set<String> completions = new HashSet<>();
        if (this.completionsProvider != null) this.completionsProvider.provide(completions, executionContext, reader);
        else this.parseCompletions(completions, completionContext, executionContext, reader);
        return completions;
    }

    @Nonnull
    protected abstract T parseValue(final ExecutionContext<E> context, final StringReader stringReader) throws ArgumentParseException, RuntimeException;

    protected abstract void parseCompletions(final Set<String> completions, final CompletionContext completionContext, final ExecutionContext<E> executionContext, final StringReader stringReader);

    public ArgumentNode<E, T> then(final ArgumentNode<E, ?> child) {
        this.children.add(child);
        return this;
    }

    public ArgumentNode<E, T> validator(@Nullable final ValueValidator<T> validator) {
        this.validator = validator;
        return this;
    }

    public ArgumentNode<E, T> suggestions(@Nullable final CompletionsProvider<E> completionsProvider) {
        this.completionsProvider = completionsProvider;
        return this;
    }

    public ArgumentNode<E, T> exceptionHandler(@Nullable final CommandExceptionHandler<E> exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
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
