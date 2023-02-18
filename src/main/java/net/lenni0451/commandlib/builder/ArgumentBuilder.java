package net.lenni0451.commandlib.builder;

import net.lenni0451.commandlib.nodes.ListArgumentNode;
import net.lenni0451.commandlib.nodes.StringArgumentNode;
import net.lenni0451.commandlib.nodes.StringArrayNode;
import net.lenni0451.commandlib.nodes.TypedArgumentNode;
import net.lenni0451.commandlib.types.ArgumentType;
import net.lenni0451.commandlib.types.DynamicType;
import net.lenni0451.commandlib.utils.Util;
import net.lenni0451.commandlib.utils.interfaces.CompletionsProvider;

import javax.annotation.Nullable;

public interface ArgumentBuilder<E> {

    default StringArgumentNode<E> string(final String s) {
        return new StringArgumentNode<>(s);
    }

    default StringArgumentNode<E> string(final String s, @Nullable final String description) {
        return new StringArgumentNode<>(s, description);
    }

    default <T> TypedArgumentNode<E, T> typed(final String name, final ArgumentType<E, T> type) {
        return new TypedArgumentNode<>(name, type);
    }

    default <T> TypedArgumentNode<E, T> typed(final String name, @Nullable final String description, final ArgumentType<E, T> type) {
        return new TypedArgumentNode<>(name, description, type);
    }

    default <T> ListArgumentNode<E, T> list(final String name, final ArgumentType<E, T> type) {
        return new ListArgumentNode<>(name, type);
    }

    default <T> ListArgumentNode<E, T> list(final String name, @Nullable final String description, final ArgumentType<E, T> type) {
        return new ListArgumentNode<>(name, description, type);
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
