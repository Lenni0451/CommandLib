package net.lenni0451.commandlib.nodes;

import net.lenni0451.commandlib.contexts.CompletionContext;
import net.lenni0451.commandlib.contexts.ExecutionContext;
import net.lenni0451.commandlib.exceptions.ArgumentParseException;
import net.lenni0451.commandlib.types.ArgumentType;
import net.lenni0451.commandlib.utils.StringReader;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * The list node can be used to parse the same argument multiple times.<br>
 * This has to be the last node in the tree as it will consume all remaining input.<br>
 * The parsed arguments have to be separated by a space.
 *
 * @param <E> The type of the executor
 * @param <T> The type of the argument
 */
public class ListNode<E, T> extends ArgumentNode<E, List<T>> {

    private final ArgumentType<E, T> type;

    public ListNode(final String name, final ArgumentType<E, T> type) {
        super(name);
        this.type = type;
        this.weight = type.getWeight();
    }

    public ListNode(final String name, @Nullable final String description, final ArgumentType<E, T> type) {
        super(name, description);
        this.type = type;
        this.weight = type.getWeight();
    }

    @Nonnull
    @Override
    protected List<T> parseValue(ExecutionContext<E> executionContext, StringReader stringReader) throws ArgumentParseException, RuntimeException {
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
    protected void parseCompletions(Set<String> completions, CompletionContext completionContext, ExecutionContext<E> executionContext, StringReader stringReader) {
        if (!stringReader.canRead()) {
            this.type.parseCompletions(completions, executionContext, stringReader);
            return;
        }

        int start = stringReader.getCursor();
        String prefix = stringReader.peekRemaining();
        boolean endsWithSpace = false;
        int lastCursor = stringReader.getCursor();
        while (stringReader.canRead()) {
            try {
                lastCursor = stringReader.getCursor();
                this.type.parseValue(executionContext, stringReader);
                if (stringReader.canRead() && stringReader.read() == ' ' && !stringReader.canRead()) endsWithSpace = true;
            } catch (Throwable t) {
                break;
            }
        }
        if (!endsWithSpace) prefix = stringReader.getString().substring(start, lastCursor);
        stringReader.setCursor(lastCursor);
        this.type.parseCompletions(completions, executionContext, new StringReader(prefix));
        completionContext.setCompletionsTrim(prefix.length());
    }

}
