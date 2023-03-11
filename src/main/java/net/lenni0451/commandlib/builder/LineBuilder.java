package net.lenni0451.commandlib.builder;

import net.lenni0451.commandlib.contexts.ExecutionContext;
import net.lenni0451.commandlib.nodes.ArgumentNode;
import net.lenni0451.commandlib.nodes.TypedNode;
import net.lenni0451.commandlib.types.ArgumentType;
import net.lenni0451.commandlib.utils.Util;
import net.lenni0451.commandlib.utils.interfaces.CommandExceptionHandler;
import net.lenni0451.commandlib.utils.interfaces.CompletionsProvider;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.*;

/**
 * Easily build a line of arguments into an {@link ArgumentNode}.
 *
 * @param <E> The type of the executor
 */
public class LineBuilder<E> {

    /**
     * Create a new line builder.
     *
     * @param <E> The type of the executor
     * @return The created line builder
     */
    public static <E> LineBuilder<E> create() {
        return new LineBuilder<>();
    }


    private final List<LineNode<E, ?>> nodes = new ArrayList<>();

    private LineBuilder() {
    }

    /**
     * Add an existing node to the line.
     *
     * @param node The node
     * @return The line builder
     */
    public LineBuilder<E> node(final ArgumentNode<E, ?> node) {
        this.nodes.add(new LineNode<>(node));
        return this;
    }

    /**
     * Start building an argument and add it to the line.
     *
     * @param name The name of the argument
     * @param type The argument type
     * @param <T>  The type of the argument
     * @return The line builder
     */
    public <T> LineBuilder<E> arg(final String name, final ArgumentType<E, T> type) {
        this.nodes.add(new LineNode<>(name, null, type));
        return this;
    }

    /**
     * Start building an argument and add it to the line.
     *
     * @param name        The name of the argument
     * @param description The description of the argument
     * @param type        The argument type
     * @param <T>         The type of the argument
     * @return The line builder
     */
    public <T> LineBuilder<E> arg(final String name, @Nullable final String description, final ArgumentType<E, T> type) {
        this.nodes.add(new LineNode<>(name, description, type));
        return this;
    }

    /**
     * Add a validator to the last added argument.
     *
     * @param validator The validator
     * @param <T>       The type of the argument
     * @return The line builder
     * @throws IllegalStateException If no argument was added before
     */
    public <T> LineBuilder<E> validator(@Nullable final Predicate<T> validator) {
        if (this.nodes.isEmpty()) throw new IllegalStateException("No argument was added before");
        this.nodes.get(this.nodes.size() - 1).validator = Util.cast(validator);
        return this;
    }

    /**
     * Add a completions provider to the last added argument.
     *
     * @param completionsProvider The completions provider
     * @return The line builder
     * @throws IllegalStateException If no argument was added before
     */
    public LineBuilder<E> completions(@Nullable final CompletionsProvider<E> completionsProvider) {
        if (this.nodes.isEmpty()) throw new IllegalStateException("No argument was added before");
        this.nodes.get(this.nodes.size() - 1).completionsProvider = completionsProvider;
        return this;
    }

    /**
     * Add an exception handler to the last added argument.
     *
     * @param exceptionHandler The exception handler
     * @return The line builder
     * @throws IllegalStateException If no argument was added before
     */
    public LineBuilder<E> exceptionHandler(@Nullable final CommandExceptionHandler<E> exceptionHandler) {
        if (this.nodes.isEmpty()) throw new IllegalStateException("No argument was added before");
        this.nodes.get(this.nodes.size() - 1).exceptionHandler = exceptionHandler;
        return this;
    }

    /**
     * Set the default value of the last added argument.
     *
     * @param defaultValue The default value
     * @param <T>          The type of the argument
     * @return The line builder
     * @throws IllegalStateException If no argument was added before
     */
    public <T> LineBuilder<E> defaultValue(@Nullable final T defaultValue) {
        if (this.nodes.isEmpty()) throw new IllegalStateException("No argument was added before");
        this.nodes.get(this.nodes.size() - 1).defaultValue = Util.cast(defaultValue);
        return this;
    }

    /**
     * Set the {@link Runnable} executor of the line and build the line into an {@link ArgumentNode}.
     *
     * @param runnable The executor
     * @param <R>      The return type of the executor
     * @return The created {@link ArgumentNode}
     */
    public <R> ArgumentNode<E, R> executes(final Runnable runnable) {
        return this.build((def, node) -> node.executes(runnable));
    }

    /**
     * Set the {@link Consumer} executor of the line and build the line into an {@link ArgumentNode}.<br>
     * The consumer will receive the {@link ExecutionContext} as argument.
     *
     * @param consumer The executor
     * @param <R>      The return type of the executor
     * @return The created {@link ArgumentNode}
     */
    public <R> ArgumentNode<E, R> executes(final Consumer<ExecutionContext<E>> consumer) {
        return this.build((def, node) -> node.executes(ctx -> {
            for (LineNode<E, R> lineNode : def) ctx.getArguments().put(lineNode.name, lineNode.defaultValue);
            consumer.accept(ctx);
        }));
    }

    /**
     * Set the {@link Supplier} executor of the line and build the line into an {@link ArgumentNode}.
     *
     * @param supplier The executor
     * @param <R>      The return type of the executor
     * @return The created {@link ArgumentNode}
     */
    public <R> ArgumentNode<E, R> executes(final Supplier<R> supplier) {
        return this.build((def, node) -> node.executes(supplier));
    }

    /**
     * Set the {@link Function} executor of the line and build the line into an {@link ArgumentNode}.<br>
     * The function will receive the {@link ExecutionContext} as argument.
     *
     * @param function The executor
     * @param <R>      The return type of the executor
     * @return The created {@link ArgumentNode}
     */
    public <R> ArgumentNode<E, R> executes(final Function<ExecutionContext<E>, R> function) {
        return this.build((def, node) -> node.executes((Function<ExecutionContext<E>, R>) ctx -> {
            for (LineNode<E, R> lineNode : def) ctx.getArguments().put(lineNode.name, lineNode.defaultValue);
            return function.apply(ctx);
        }));
    }

    private <R> ArgumentNode<E, R> build(final BiConsumer<List<LineNode<E, R>>, ArgumentNode<E, R>> executorAppender) {
        if (this.nodes.isEmpty()) throw new IllegalStateException("No arguments defined");

        ArgumentNode<E, R> root = null;
        ArgumentNode<E, R> current = null;
        for (int i = 0; i < this.nodes.size(); i++) {
            LineNode<E, R> node = (LineNode<E, R>) this.nodes.get(i);
            LineNode<E, R> next = i + 1 < this.nodes.size() ? (LineNode<E, R>) this.nodes.get(i + 1) : null;

            ArgumentNode<E, R> newNode = node.toArgumentNode();
            if (next == null || next.defaultValue != null) {
                List<LineNode<E, R>> defaults = Util.cast(this.nodes.subList(i + 1, this.nodes.size()));
                executorAppender.accept(defaults, newNode);
            }
            if (root == null) root = newNode;
            if (current != null) current.then(newNode);
            current = newNode;
        }
        return root;
    }


    private static class LineNode<E, T> {
        private final ArgumentNode<E, T> node;
        private final String name;
        private final String description;
        private final ArgumentType<E, T> type;
        private Predicate<T> validator;
        private CompletionsProvider<E> completionsProvider;
        private CommandExceptionHandler<E> exceptionHandler;
        private T defaultValue;

        private LineNode(final ArgumentNode<E, T> node) {
            this.node = node;
            this.name = null;
            this.description = null;
            this.type = null;
        }

        private LineNode(final String name, @Nullable final String description, final ArgumentType<E, T> argumentType) {
            this.node = null;
            this.name = name;
            this.description = description;
            this.type = argumentType;
        }

        private ArgumentNode<E, T> toArgumentNode() {
            ArgumentNode<E, T> node;
            if (this.node == null) node = new TypedNode<>(this.name, this.description, this.type);
            else node = this.node;
            node
                    .validator(this.validator)
                    .completions(this.completionsProvider)
                    .exceptionHandler(this.exceptionHandler);
            return node;
        }
    }

}
