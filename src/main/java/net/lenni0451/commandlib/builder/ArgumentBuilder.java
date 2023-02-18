package net.lenni0451.commandlib.builder;

import net.lenni0451.commandlib.nodes.ListNode;
import net.lenni0451.commandlib.nodes.StringArrayNode;
import net.lenni0451.commandlib.nodes.StringNode;
import net.lenni0451.commandlib.nodes.TypedNode;
import net.lenni0451.commandlib.types.ArgumentType;
import net.lenni0451.commandlib.types.DynamicType;
import net.lenni0451.commandlib.utils.Util;
import net.lenni0451.commandlib.utils.interfaces.CompletionsProvider;

import javax.annotation.Nullable;

public interface ArgumentBuilder<E> {

    default StringNode<E> string(final String s) {
        return new StringNode<>(s);
    }

    default StringNode<E> string(final String s, @Nullable final String description) {
        return new StringNode<>(s, description);
    }

    default <T> TypedNode<E, T> typed(final String name, final ArgumentType<E, T> type) {
        return new TypedNode<>(name, type);
    }

    default <T> TypedNode<E, T> typed(final String name, @Nullable final String description, final ArgumentType<E, T> type) {
        return new TypedNode<>(name, description, type);
    }

    default <T> ListNode<E, T> list(final String name, final ArgumentType<E, T> type) {
        return new ListNode<>(name, type);
    }

    default <T> ListNode<E, T> list(final String name, @Nullable final String description, final ArgumentType<E, T> type) {
        return new ListNode<>(name, description, type);
    }

    default StringArrayNode<E> stringArray(final String name, final StringArrayNode.Executor<E> executor) {
        StringArrayNode<E> node = new StringArrayNode<>(name, null);
        node.executes(executionContext -> {
            executor.execute(Util.cast(executionContext.getExecutor()), executionContext.getArgument(name), Util.cast(executionContext));
        });
        return node;
    }

    default StringArrayNode<E> stringArray(final String name, final StringArrayNode.Executor<E> executor, final StringArrayNode.Completor<E> completor) {
        StringArrayNode<E> node = new StringArrayNode<>(name, completor);
        node.executes(executionContext -> {
            executor.execute(Util.cast(executionContext.getExecutor()), executionContext.getArgument(name), Util.cast(executionContext));
        });
        return node;
    }

    default StringArrayNode<E> stringArray(final String name, @Nullable final String description, final StringArrayNode.Executor<E> executor) {
        StringArrayNode<E> node = new StringArrayNode<>(name, description, null);
        node.executes(executionContext -> {
            executor.execute(Util.cast(executionContext.getExecutor()), executionContext.getArgument(name), Util.cast(executionContext));
        });
        return node;
    }

    default StringArrayNode<E> stringArray(final String name, @Nullable final String description, final StringArrayNode.Executor<E> executor, final StringArrayNode.Completor<E> completor) {
        StringArrayNode<E> node = new StringArrayNode<>(name, description, completor);
        node.executes(executionContext -> {
            executor.execute(Util.cast(executionContext.getExecutor()), executionContext.getArgument(name), Util.cast(executionContext));
        });
        return node;
    }

    default LineBuilder<E> line() {
        return LineBuilder.create();
    }

    default <T> ArgumentType<E, T> dynamicType(final DynamicType.SingleParser<T> parser) {
        return new DynamicType<>(parser);
    }

    default <T> ArgumentType<E, T> dynamicType(final DynamicType.SingleParser<T> parser, final CompletionsProvider<E> completionsProvider) {
        return new DynamicType<>(parser, completionsProvider);
    }

    default <T> ArgumentType<E, T> dynamicType(final DynamicType.BiParser<E, T> parser) {
        return new DynamicType<>(parser);
    }

    default <T> ArgumentType<E, T> dynamicType(final DynamicType.BiParser<E, T> parser, final CompletionsProvider<E> completionsProvider) {
        return new DynamicType<>(parser, completionsProvider);
    }

}
