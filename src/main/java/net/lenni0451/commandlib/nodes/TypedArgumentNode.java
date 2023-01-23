package net.lenni0451.commandlib.nodes;

import net.lenni0451.commandlib.ExecutionContext;
import net.lenni0451.commandlib.exceptions.ArgumentParseException;
import net.lenni0451.commandlib.types.ArgumentType;
import net.lenni0451.commandlib.utils.StringReader;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

public class TypedArgumentNode<E, T> extends ArgumentNode<E, T> {

    private final ArgumentType<E, T> type;

    public TypedArgumentNode(final String name, final ArgumentType<E, T> type) {
        super(name);
        this.type = type;
        this.weight = type.getWeight();
    }

    public TypedArgumentNode(final String name, @Nullable final String description, final ArgumentType<E, T> type) {
        super(name, description);
        this.type = type;
        this.weight = type.getWeight();
    }

    @Nonnull
    @Override
    public T parseValue(ExecutionContext<E> context, StringReader stringReader) throws ArgumentParseException, RuntimeException {
        return this.type.parseValue(context, stringReader);
    }

    @Override
    public void parseCompletions(Set<String> completions, ExecutionContext<E> context, StringReader stringReader) {
        this.type.parseCompletions(completions, context, stringReader);
    }

}
