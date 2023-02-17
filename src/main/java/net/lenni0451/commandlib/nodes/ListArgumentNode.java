package net.lenni0451.commandlib.nodes;

import net.lenni0451.commandlib.contexts.CompletionContext;
import net.lenni0451.commandlib.contexts.ExecutionContext;
import net.lenni0451.commandlib.exceptions.ArgumentParseException;
import net.lenni0451.commandlib.types.ArgumentType;
import net.lenni0451.commandlib.utils.StringReader;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ListArgumentNode<E, T> extends ArgumentNode<E, List<T>> {

    private final ArgumentType<E, T> type;

    public ListArgumentNode(final String name, final ArgumentType<E, T> type) {
        super(name);
        this.type = type;
        this.weight = type.getWeight();
    }

    public ListArgumentNode(final String name, @Nullable final String description, final ArgumentType<E, T> type) {
        super(name, description);
        this.type = type;
        this.weight = type.getWeight();
    }

    @Nonnull
    @Override
    public List<T> parseValue(ExecutionContext<E> context, StringReader stringReader) throws ArgumentParseException, RuntimeException {
        List<T> result = new ArrayList<>();
        while (stringReader.canRead()) {
            result.add(this.type.parseValue(context, stringReader));
            if (stringReader.canRead()) {
                if (stringReader.read() != ' ') throw ArgumentParseException.namedReason(this.name(), "Expected space");
                if (!stringReader.canRead()) throw ArgumentParseException.namedReason(this.name(), "Expected value");
            }
        }
        return result;
    }

    @Override
    public void parseCompletions(Set<String> completions, CompletionContext completionContext, ExecutionContext<E> executionContext, StringReader stringReader) {
        String remaining = stringReader.peekRemaining();
        if (remaining.isEmpty() || remaining.endsWith(" ")) this.type.parseCompletions(completions, executionContext, stringReader);
        if (remaining.endsWith(" ")) {
            Set<String> prependedCompletions = new HashSet<>();
            for (String completion : completions) prependedCompletions.add(remaining + completion);
            completions.clear();
            completions.addAll(prependedCompletions);
            completionContext.setCompletionsTrim(remaining.length());
        }
    }

}
