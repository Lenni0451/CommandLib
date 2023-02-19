package net.lenni0451.commandlib.nodes;

import net.lenni0451.commandlib.contexts.CompletionContext;
import net.lenni0451.commandlib.contexts.ExecutionContext;
import net.lenni0451.commandlib.exceptions.ArgumentParseException;
import net.lenni0451.commandlib.types.ArgumentType;
import net.lenni0451.commandlib.utils.StringReader;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

/**
 * The typed argument node matches the given {@link ArgumentType} which can be treated like a wrapper for argument nodes.
 *
 * @param <E> The type of the executor
 * @param <T> The type of the argument
 */
public class TypedNode<E, T> extends ArgumentNode<E, T> {

    private final ArgumentType<E, T> type;

    public TypedNode(final String name, final ArgumentType<E, T> type) {
        super(name);
        this.type = type;
        this.weight = type.getWeight();
    }

    public TypedNode(final String name, @Nullable final String description, final ArgumentType<E, T> type) {
        super(name, description);
        this.type = type;
        this.weight = type.getWeight();
    }

    @Nonnull
    @Override
    protected T parseValue(ExecutionContext<E> executionContext, StringReader stringReader) throws ArgumentParseException, RuntimeException {
        return this.type.parseValue(executionContext, stringReader);
    }

    @Override
    protected void parseCompletions(Set<String> completions, CompletionContext completionContext, ExecutionContext<E> executionContext, StringReader stringReader) {
        this.type.parseCompletions(completions, executionContext, stringReader);
    }

}
