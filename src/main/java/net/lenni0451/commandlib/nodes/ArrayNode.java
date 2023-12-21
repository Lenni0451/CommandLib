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
 * The array node can be used to parse the same argument multiple times.<br>
 * The parsed arguments have to be separated by a comma.
 *
 * @param <E> The type of the executor
 * @param <T> The type of the argument
 */
public class ArrayNode<E, T> extends ArgumentNode<E, List<T>> {

    private final ArgumentType<E, T> type;

    public ArrayNode(final String name, final ArgumentType<E, T> type) {
        super(name);
        this.type = type;
        this.weight = type.getWeight();
    }

    public ArrayNode(final String name, @Nullable final String description, final ArgumentType<E, T> type) {
        super(name, description);
        this.type = type;
        this.weight = type.getWeight();
    }

    @Nonnull
    @Override
    protected List<T> parseValue(ExecutionContext<E> executionContext, StringReader stringReader) throws ArgumentParseException, RuntimeException {
        List<T> result = new ArrayList<>();
        while (stringReader.canRead()) {
            String part;
            if (stringReader.peek() == '"' || stringReader.peek() == '\'') part = stringReader.readString();
            else part = stringReader.readUntil(false, ',', ' ');
            StringReader partReader = new StringReader(part);
            result.add(this.type.parseValue(executionContext, partReader));
            if (partReader.canRead()) throw ArgumentParseException.namedReason(this.name(), "Argument didn't end");
            if (stringReader.canRead()) {
                char next = stringReader.peek();
                if (next == ',') stringReader.skip();
                else if (next == ' ') break;
                else throw ArgumentParseException.namedReason(this.name(), "Expected comma or space");
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
        int lastCursor = stringReader.getCursor();
        while (stringReader.canRead()) {
            try {
                lastCursor = stringReader.getCursor();
                String part;
                if (stringReader.peek() == '"' || stringReader.peek() == '\'') part = stringReader.readString();
                else part = stringReader.readUntil(false, ',', ' ');
                StringReader partReader = new StringReader(part);
                this.type.parseValue(executionContext, partReader);
                if (partReader.canRead()) throw ArgumentParseException.namedReason(this.name(), "Argument didn't end");
                if (stringReader.canRead()) {
                    char next = stringReader.peek();
                    if (next == ',') {
                        stringReader.skip();
                        if (!stringReader.canRead()) lastCursor = stringReader.getCursor();
                    } else if (next == ' ') {
                        break;
                    } else {
                        throw ArgumentParseException.namedReason(this.name(), "Expected comma or space");
                    }
                }
            } catch (Throwable t) {
                break;
            }
        }
        this.type.parseCompletions(completions, executionContext, new StringReader(prefix.substring(lastCursor - start)));
        stringReader.setCursor(lastCursor);
        completionContext.setCompletionsTrim(lastCursor - start);
    }

}
