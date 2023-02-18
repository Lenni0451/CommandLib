package net.lenni0451.commandlib.nodes;

import net.lenni0451.commandlib.contexts.CompletionContext;
import net.lenni0451.commandlib.contexts.ExecutionContext;
import net.lenni0451.commandlib.exceptions.ArgumentParseException;
import net.lenni0451.commandlib.utils.StringReader;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StringArrayNode<E> extends ArgumentNode<E, String[]> {

    private final Completor<E> completor;

    public StringArrayNode(final String name, @Nullable final Completor<E> completor) {
        super(name);
        this.completor = completor;
    }

    public StringArrayNode(final String name, @Nullable final String description, @Nullable final Completor<E> completor) {
        super(name, description);
        this.completor = completor;
    }

    @Nonnull
    @Override
    protected String[] parseValue(ExecutionContext<E> executionContext, StringReader stringReader) throws ArgumentParseException, RuntimeException {
        List<String> args = new ArrayList<>();
        while (stringReader.canRead()) {
            args.add(stringReader.readWordOrString());
            if (stringReader.canRead()) {
                if (stringReader.read() != ' ') throw ArgumentParseException.namedReason(this.name(), "Expected space");
                if (!stringReader.canRead()) throw ArgumentParseException.namedReason(this.name(), "Expected value");
            }
        }
        return args.toArray(new String[0]);
    }

    @Override
    protected void parseCompletions(Set<String> completions, CompletionContext completionContext, ExecutionContext<E> executionContext, StringReader stringReader) {
        if (this.completor == null) return;
        if (!stringReader.canRead()) {
            this.completor.complete(completions, new String[0], executionContext);
            return;
        }

        int start = stringReader.getCursor();
        String prefix = stringReader.peekRemaining();
        boolean endsWithSpace = false;
        List<String> args = new ArrayList<>();
        int lastCursor = stringReader.getCursor();
        while (stringReader.canRead()) {
            try {
                lastCursor = stringReader.getCursor();
                args.add(stringReader.readWordOrString());
                if (stringReader.canRead() && stringReader.read() == ' ' && !stringReader.canRead()) endsWithSpace = true;
            } catch (Throwable t) {
                break;
            }
        }
        if (args.isEmpty()) {
            this.completor.complete(completions, new String[0], executionContext);
            return;
        }
        if (!endsWithSpace) {
            args.remove(args.size() - 1);
            prefix = stringReader.getString().substring(start, lastCursor);
        }
        this.completor.complete(completions, args.toArray(new String[0]), executionContext);
        Set<String> prepended = new HashSet<>();
        for (String completion : completions) prepended.add(prefix + completion);
        completions.clear();
        completions.addAll(prepended);
        completionContext.setCompletionsTrim(prefix.length());
    }


    @FunctionalInterface
    public interface Executor<E> {
        void execute(final E executor, final String[] args, final ExecutionContext<E> executionContext);
    }

    public interface Completor<E> {
        void complete(final Set<String> completions, final String[] currentArgs, final ExecutionContext<E> executionContext);
    }

}
