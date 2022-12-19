package net.lenni0451.commandlib.utils;

import net.lenni0451.commandlib.nodes.StringArgumentNode;
import net.lenni0451.commandlib.nodes.TypedArgumentNode;
import net.lenni0451.commandlib.types.ArgumentType;

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

}
