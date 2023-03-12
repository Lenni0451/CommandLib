package net.lenni0451.commandlib.builder;

import net.lenni0451.commandlib.nodes.*;
import net.lenni0451.commandlib.types.ArgumentType;
import net.lenni0451.commandlib.types.DynamicType;
import net.lenni0451.commandlib.types.DynamicType.BiParser;
import net.lenni0451.commandlib.types.DynamicType.SingleParser;
import net.lenni0451.commandlib.utils.Util;
import net.lenni0451.commandlib.utils.comparator.ArgumentComparator;

import javax.annotation.Nullable;

/**
 * A convenience interface to allow for the easy creation of arguments.
 *
 * @param <E> The type of the executor
 */
public interface ArgumentBuilder<E> {

    /**
     * Create a new string argument node.<br>
     * The argument will be matched as a single word. The case-sensitivity will be determined by the {@link ArgumentComparator} in the command executor.
     *
     * @param s The name of the argument
     * @return The created node
     */
    default StringNode<E> string(final String s) {
        return new StringNode<>(s);
    }

    /**
     * Create a new string argument node.<br>
     * The argument will be matched as a single word. The case-sensitivity will be determined by the {@link ArgumentComparator} in the command executor.
     *
     * @param s           The name of the argument
     * @param description The description of the argument
     * @return The created node
     */
    default StringNode<E> string(final String s, @Nullable final String description) {
        return new StringNode<>(s, description);
    }

    /**
     * Create a new typed argument node.<br>
     * The argument will be parsed by the given {@link ArgumentType}.
     *
     * @param name The name of the argument
     * @param type The argument type
     * @param <T>  The type of the argument
     * @return The created node
     */
    default <T> TypedNode<E, T> typed(final String name, final ArgumentType<E, T> type) {
        return new TypedNode<>(name, type);
    }

    /**
     * Create a new typed argument node.<br>
     * The argument will be parsed by the given {@link ArgumentType}.
     *
     * @param name        The name of the argument
     * @param description The description of the argument
     * @param type        The argument type
     * @param <T>         The type of the argument
     * @return The created node
     */
    default <T> TypedNode<E, T> typed(final String name, @Nullable final String description, final ArgumentType<E, T> type) {
        return new TypedNode<>(name, description, type);
    }

    /**
     * Create a redirect node.<br>
     * The redirect node will redirect the execution to the given target node.
     *
     * @param targetNode The target node
     * @return The created node
     */
    default RedirectNode<E> redirect(final ArgumentNode<E, ?> targetNode) {
        return new RedirectNode<>(targetNode);
    }

    /**
     * Create a new list argument node.<br>
     * The argument will be parsed by the given {@link ArgumentType}.<br>
     * The argument can be repeated multiple times.
     *
     * @param name The name of the argument
     * @param type The argument type
     * @param <T>  The type of the argument
     * @return The created node
     */
    default <T> ListNode<E, T> list(final String name, final ArgumentType<E, T> type) {
        return new ListNode<>(name, type);
    }

    /**
     * Create a new list argument node.<br>
     * The argument will be parsed by the given {@link ArgumentType}.<br>
     * The argument can be repeated multiple times.
     *
     * @param name        The name of the argument
     * @param description The description of the argument
     * @param type        The argument type
     * @param <T>         The type of the argument
     * @return The created node
     */
    default <T> ListNode<E, T> list(final String name, @Nullable final String description, final ArgumentType<E, T> type) {
        return new ListNode<>(name, description, type);
    }

    /**
     * Create a new string array argument node.<br>
     * The given executor receives the whole string array as argument.<br>
     * This allows you to handle commands the traditional way.
     *
     * @param name     The name of the argument
     * @param executor The executor
     * @return The created node
     */
    default StringArrayNode<E> stringArray(final String name, final StringArrayNode.Executor<E> executor) {
        StringArrayNode<E> node = new StringArrayNode<>(name, null);
        node.executes(executionContext -> {
            executor.execute(Util.cast(executionContext.getExecutor()), executionContext.getArgument(name), Util.cast(executionContext));
        });
        return node;
    }

    /**
     * Create a new string array argument node.<br>
     * The given executor receives the whole string array as argument.<br>
     * The given completor receives all arguments to this point as argument.<br>
     * This allows you to handle commands the traditional way.
     *
     * @param name      The name of the argument
     * @param executor  The executor
     * @param completor The completor
     * @return The created node
     */
    default StringArrayNode<E> stringArray(final String name, final StringArrayNode.Executor<E> executor, final StringArrayNode.Completor<E> completor) {
        StringArrayNode<E> node = new StringArrayNode<>(name, completor);
        node.executes(executionContext -> {
            executor.execute(Util.cast(executionContext.getExecutor()), executionContext.getArgument(name), Util.cast(executionContext));
        });
        return node;
    }

    /**
     * Create a new string array argument node.<br>
     * The given executor receives the whole string array as argument.<br>
     * This allows you to handle commands the traditional way.
     *
     * @param name        The name of the argument
     * @param description The description of the argument
     * @param executor    The executor
     * @return The created node
     */
    default StringArrayNode<E> stringArray(final String name, @Nullable final String description, final StringArrayNode.Executor<E> executor) {
        StringArrayNode<E> node = new StringArrayNode<>(name, description, null);
        node.executes(executionContext -> {
            executor.execute(Util.cast(executionContext.getExecutor()), executionContext.getArgument(name), Util.cast(executionContext));
        });
        return node;
    }

    /**
     * Create a new string array argument node.<br>
     * The given executor receives the whole string array as argument.<br>
     * The given completor receives all arguments to this point as argument.<br>
     * This allows you to handle commands the traditional way.
     *
     * @param name        The name of the argument
     * @param description The description of the argument
     * @param executor    The executor
     * @param completor   The completor
     * @return The created node
     */
    default StringArrayNode<E> stringArray(final String name, @Nullable final String description, final StringArrayNode.Executor<E> executor, final StringArrayNode.Completor<E> completor) {
        StringArrayNode<E> node = new StringArrayNode<>(name, description, completor);
        node.executes(executionContext -> {
            executor.execute(Util.cast(executionContext.getExecutor()), executionContext.getArgument(name), Util.cast(executionContext));
        });
        return node;
    }

    /**
     * Create a dynamic parsing argument type.<br>
     * The {@link SingleParser} only receives the string reader to parse the argument.
     *
     * @param parser The parser
     * @param <T>    The type of the parsed argument
     * @return The created argument type
     */
    default <T> ArgumentType<E, T> dynamicType(final SingleParser<T> parser) {
        return new DynamicType<>(parser);
    }

    /**
     * Create a dynamic parsing argument type.<br>
     * The {@link BiParser} receives the execution context and the string reader to parse the argument.
     *
     * @param parser The parser
     * @param <T>    The type of the parsed argument
     * @return The created argument type
     */
    default <T> ArgumentType<E, T> dynamicType(final BiParser<E, T> parser) {
        return new DynamicType<>(parser);
    }

    /**
     * @return A new line builder
     */
    default LineBuilder<E> line() {
        return LineBuilder.create();
    }

}
