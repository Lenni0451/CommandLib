package net.lenni0451.commandlib.nodes;


import net.lenni0451.commandlib.contexts.CompletionContext;
import net.lenni0451.commandlib.contexts.ExecutionContext;
import net.lenni0451.commandlib.exceptions.ArgumentParseException;
import net.lenni0451.commandlib.utils.StringReader;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

public class StringNode<E> extends ArgumentNode<E, String> {

    public StringNode(final String name) {
        super(name);
        this.weight = 100;
        this.providesArgument = false;
    }

    public StringNode(final String name, @Nullable final String description) {
        super(name, description);
        this.weight = 100;
        this.providesArgument = false;
    }

    @Nonnull
    @Override
    public String parseValue(ExecutionContext<E> executionContext, StringReader reader) throws ArgumentParseException, RuntimeException {
        String result = reader.readWordOrString();
        if (executionContext.getArgumentComparator().compare(result, this.name())) return this.name();
        throw ArgumentParseException.namedReason(this.name(), "Expected '" + this.name() + "'");
    }

    @Override
    public void parseCompletions(Set<String> completions, CompletionContext completionContext, ExecutionContext<E> executionContext, StringReader stringReader) {
        completions.add(this.name());
    }

}
