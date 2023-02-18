package net.lenni0451.commandlib.nodes;

import net.lenni0451.commandlib.contexts.CompletionContext;
import net.lenni0451.commandlib.contexts.ExecutionContext;
import net.lenni0451.commandlib.exceptions.ArgumentParseException;
import net.lenni0451.commandlib.types.ArgumentType;
import net.lenni0451.commandlib.utils.StringReader;
import net.lenni0451.commandlib.utils.Util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
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
    public List<T> parseValue(ExecutionContext<E> executionContext, StringReader stringReader) throws ArgumentParseException, RuntimeException {
        List<T> result = new ArrayList<>();
        while (stringReader.canRead()) {
            result.add(this.type.parseValue(executionContext, stringReader));
            if (stringReader.canRead()) {
                if (stringReader.read() != ' ') throw ArgumentParseException.namedReason(this.name(), "Expected space");
                if (!stringReader.canRead()) throw ArgumentParseException.namedReason(this.name(), "Expected value");
            }
        }
        return result;
    }

    @Override
    public void parseCompletions(Set<String> completions, CompletionContext completionContext, ExecutionContext<E> executionContext, StringReader stringReader) {
        int start = stringReader.getCursor();
        if (!stringReader.canRead()) {
            this.type.parseCompletions(completions, executionContext, stringReader);
            return;
        }
        while (stringReader.canRead()) {
            int cursor = stringReader.getCursor();
            try {
                this.type.parseValue(executionContext, stringReader);
                if (!stringReader.canRead()) return;
                if (stringReader.read() != ' ') return;
                if (!stringReader.canRead()) {
                    stringReader.setCursor(start);
                    String prefix = stringReader.peekRemaining();
                    this.type.parseCompletions(completions, executionContext, stringReader);
                    Util.prepend(completions, prefix);
                    completionContext.setCompletionsTrim(prefix.length());
                    return;
                }
            } catch (Throwable t) {
                String prefix = stringReader.getString().substring(0, cursor);
                stringReader.setCursor(start);
                this.type.parseCompletions(completions, executionContext, new StringReader(prefix));
                Util.prepend(completions, prefix);
                completionContext.setCompletionsTrim(prefix.length());
                return;
            }
        }
    }

}
