package net.lenni0451.commandlib.builder;

import net.lenni0451.commandlib.ExecutionContext;
import net.lenni0451.commandlib.nodes.ArgumentNode;
import net.lenni0451.commandlib.nodes.TypedArgumentNode;
import net.lenni0451.commandlib.types.ArgumentType;
import net.lenni0451.commandlib.utils.Util;
import net.lenni0451.commandlib.utils.interfaces.CommandExceptionHandler;
import net.lenni0451.commandlib.utils.interfaces.CompletionsProvider;
import net.lenni0451.commandlib.utils.interfaces.ValueValidator;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class LineBuilder<E> {

    public static <E> LineBuilder<E> create() {
        return new LineBuilder<>();
    }


    private final List<LineNode<E, ?>> nodes = new ArrayList<>();

    private LineBuilder() {
    }

    public <T> LineBuilder<E> arg(final String name, final ArgumentType<E, T> type) {
        this.nodes.add(new LineNode<>(name, null, type));
        return this;
    }

    public <T> LineBuilder<E> arg(final String name, final String description, final ArgumentType<E, T> type) {
        this.nodes.add(new LineNode<>(name, description, type));
        return this;
    }

    public <T> LineBuilder<E> validator(@Nullable final ValueValidator<T> validator) {
        this.nodes.get(this.nodes.size() - 1).validator = Util.cast(validator);
        return this;
    }

    public LineBuilder<E> suggestions(@Nullable final CompletionsProvider<E> completionsProvider) {
        this.nodes.get(this.nodes.size() - 1).completionsProvider = completionsProvider;
        return this;
    }

    public LineBuilder<E> exceptionHandler(@Nullable final CommandExceptionHandler<E> exceptionHandler) {
        this.nodes.get(this.nodes.size() - 1).exceptionHandler = exceptionHandler;
        return this;
    }

    public <T> LineBuilder<E> defaultValue(@Nullable final T defaultValue) {
        this.nodes.get(this.nodes.size() - 1).defaultValue = Util.cast(defaultValue);
        return this;
    }

    public <R> ArgumentNode<E, R> executes(final Runnable runnable) {
        return this.build((def, node) -> node.executes(runnable));
    }

    public <R> ArgumentNode<E, R> executes(final Consumer<ExecutionContext<E>> consumer) {
        return this.build((def, node) -> node.executes(ctx -> {
            for (LineNode<E, R> lineNode : def) ctx.getArguments().put(lineNode.name, lineNode.defaultValue);
            consumer.accept(ctx);
        }));
    }

    public <R> ArgumentNode<E, R> executes(final Supplier<R> supplier) {
        return this.build((def, node) -> node.executes(supplier));
    }

    public <R> ArgumentNode<E, R> executes(final Function<ExecutionContext<E>, R> function) {
        return this.build((def, node) -> node.executes((Function<ExecutionContext<E>, R>) ctx -> {
            for (LineNode<E, R> lineNode : def) ctx.getArguments().put(lineNode.name, lineNode.defaultValue);
            return function.apply(ctx);
        }));
    }

    private <R> TypedArgumentNode<E, R> build(final BiConsumer<List<LineNode<E, R>>, TypedArgumentNode<E, R>> executorAppender) {
        if (this.nodes.isEmpty()) throw new IllegalStateException("No arguments defined");

        TypedArgumentNode<E, R> root = null;
        TypedArgumentNode<E, R> current = null;
        for (int i = 0; i < this.nodes.size(); i++) {
            LineNode<E, R> node = (LineNode<E, R>) this.nodes.get(i);
            LineNode<E, R> next = i + 1 < this.nodes.size() ? (LineNode<E, R>) this.nodes.get(i + 1) : null;

            TypedArgumentNode<E, R> newNode = node.toArgumentNode();
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
        private final String name;
        private final String description;
        private final ArgumentType<E, T> type;
        private ValueValidator<T> validator;
        private CompletionsProvider<E> completionsProvider;
        private CommandExceptionHandler<E> exceptionHandler;
        private T defaultValue;

        private LineNode(final String name, @Nullable final String description, final ArgumentType<E, T> argumentType) {
            this.name = name;
            this.description = description;
            this.type = argumentType;
        }

        private TypedArgumentNode<E, T> toArgumentNode() {
            TypedArgumentNode<E, T> node = new TypedArgumentNode<>(this.name, this.description, this.type);
            node
                    .validator(this.validator)
                    .suggestions(this.completionsProvider)
                    .exceptionHandler(this.exceptionHandler);
            return node;
        }
    }

}
