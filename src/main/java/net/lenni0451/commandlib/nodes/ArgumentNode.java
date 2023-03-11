package net.lenni0451.commandlib.nodes;

import net.lenni0451.commandlib.contexts.CompletionContext;
import net.lenni0451.commandlib.contexts.ExecutionContext;
import net.lenni0451.commandlib.exceptions.ArgumentParseException;
import net.lenni0451.commandlib.exceptions.HandledException;
import net.lenni0451.commandlib.utils.StringReader;
import net.lenni0451.commandlib.utils.interfaces.ArgumentRequirement;
import net.lenni0451.commandlib.utils.interfaces.CommandExceptionHandler;
import net.lenni0451.commandlib.utils.interfaces.CompletionsProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * The abstract argument node class containing all methods required to build a customizable argument tree.
 *
 * @param <E> The type of the executor
 * @param <T> The type of the argument
 */
public abstract class ArgumentNode<E, T> {

    private final String name;
    private final String description;
    private final List<ArgumentNode<E, ?>> children;
    protected int weight = 0;
    protected boolean providesArgument = true;
    private ArgumentRequirement<E> requirement = e -> true;
    private Predicate<T> validator;
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

    /**
     * @return The name of the argument
     */
    public String name() {
        return this.name;
    }

    /**
     * @return The description of the argument
     */
    @Nullable
    public String description() {
        return this.description;
    }

    /**
     * @return The children of this argument
     */
    public List<ArgumentNode<E, ?>> children() {
        return this.children;
    }

    /**
     * @return The weight of this argument
     */
    public int weight() {
        return this.weight;
    }

    /**
     * @return If this node provides an argument (See {@link StringNode})
     */
    public boolean providesArgument() {
        return this.providesArgument;
    }

    /**
     * @return The requirement of this argument
     */
    @Nonnull
    public ArgumentRequirement<E> requirement() {
        return this.requirement;
    }

    /**
     * @return The validator of this argument
     */
    @Nullable
    public Predicate<T> validator() {
        return this.validator;
    }

    /**
     * @return The completions provider of this argument
     */
    @Nullable
    public CompletionsProvider<E> completionsProvider() {
        return this.completionsProvider;
    }

    /**
     * @return The exception handler of this argument
     */
    @Nullable
    public CommandExceptionHandler<E> exceptionHandler() {
        return this.exceptionHandler;
    }

    /**
     * @return The executor of this argument
     */
    @Nullable
    public Function<ExecutionContext<E>, ?> executor() {
        return this.executor;
    }

    /**
     * Parse the value of this argument.
     *
     * @param executionContext The execution context
     * @param stringReader     The string reader
     * @return The parsed value
     * @throws ArgumentParseException If the value could not be parsed
     * @throws RuntimeException       If an unexpected error occurred
     */
    @Nonnull
    public T value(final ExecutionContext<E> executionContext, final StringReader stringReader) throws ArgumentParseException, RuntimeException {
        T value;
        try {
            value = this.parseValue(executionContext, stringReader);
        } catch (ArgumentParseException | RuntimeException e) {
            if (this.exceptionHandler != null && executionContext.isExecution()) {
                this.exceptionHandler.handle(executionContext, e);
                throw new HandledException(e);
            }
            throw e;
        }
        if (this.validator != null && !this.validator.test(value)) throw ArgumentParseException.namedReason(this.name, "Invalid value");
        return value;
    }

    /**
     * Provide the completions of this argument.
     *
     * @param completionContext The completion context
     * @param executionContext  The execution context
     * @param reader            The string reader
     * @return The completions
     */
    public Set<String> parseCompletions(final CompletionContext completionContext, final ExecutionContext<E> executionContext, final StringReader reader) {
        Set<String> completions = new HashSet<>();
        if (this.completionsProvider != null) this.completionsProvider.provide(completions, executionContext, reader);
        else this.parseCompletions(completions, completionContext, executionContext, reader);
        return completions;
    }

    /**
     * Parse the value of this argument.
     *
     * @param executionContext The execution context
     * @param stringReader     The string reader
     * @return The parsed value
     * @throws ArgumentParseException If the value could not be parsed
     * @throws RuntimeException       If an unexpected error occurred
     */
    @Nonnull
    protected abstract T parseValue(final ExecutionContext<E> executionContext, final StringReader stringReader) throws ArgumentParseException, RuntimeException;

    /**
     * Provide the completions of this argument.
     *
     * @param completions       The completions
     * @param completionContext The completion context
     * @param executionContext  The execution context
     * @param stringReader      The string reader
     */
    protected abstract void parseCompletions(final Set<String> completions, final CompletionContext completionContext, final ExecutionContext<E> executionContext, final StringReader stringReader);

    /**
     * Add a child to this argument node.
     *
     * @param child The child
     * @return This argument node
     */
    public ArgumentNode<E, T> then(final ArgumentNode<E, ?> child) {
        this.children.add(child);
        return this;
    }

    /**
     * Set the requirement of this argument node.
     *
     * @param requirement The requirement
     * @return This argument node
     */
    public ArgumentNode<E, T> requires(final ArgumentRequirement<E> requirement) {
        this.requirement = requirement;
        return this;
    }

    /**
     * Set the validator of this argument node.
     *
     * @param validator The validator
     * @return This argument node
     */
    public ArgumentNode<E, T> validator(@Nullable final Predicate<T> validator) {
        this.validator = validator;
        return this;
    }

    /**
     * Set the custom completions provider of this argument node.
     *
     * @param completionsProvider The completions provider
     * @return This argument node
     */
    public ArgumentNode<E, T> completions(@Nullable final CompletionsProvider<E> completionsProvider) {
        this.completionsProvider = completionsProvider;
        return this;
    }

    /**
     * Set the exception handler of this argument node.
     *
     * @param exceptionHandler The exception handler
     * @return This argument node
     */
    public ArgumentNode<E, T> exceptionHandler(@Nullable final CommandExceptionHandler<E> exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
        return this;
    }

    /**
     * Set the {@link Runnable} executor of this argument node.
     *
     * @param runnable The runnable
     * @return This argument node
     */
    public ArgumentNode<E, T> executes(final Runnable runnable) {
        this.executor = executionContext -> {
            runnable.run();
            return null;
        };
        return this;
    }

    /**
     * Set the {@link Consumer} executor of this argument node.
     *
     * @param consumer The consumer
     * @return This argument node
     */
    public ArgumentNode<E, T> executes(final Consumer<ExecutionContext<E>> consumer) {
        this.executor = executionContext -> {
            consumer.accept(executionContext);
            return null;
        };
        return this;
    }

    /**
     * Set the {@link Supplier} executor of this argument node.
     *
     * @param supplier The supplier
     * @return This argument node
     */
    public ArgumentNode<E, T> executes(final Supplier<?> supplier) {
        this.executor = executionContext -> supplier.get();
        return this;
    }

    /**
     * Set the {@link Function} executor of this argument node.
     *
     * @param function The function
     * @return This argument node
     */
    public ArgumentNode<E, T> executes(final Function<ExecutionContext<E>, ?> function) {
        this.executor = function;
        return this;
    }

}
