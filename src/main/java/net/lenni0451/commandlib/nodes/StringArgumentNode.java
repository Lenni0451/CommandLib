package net.lenni0451.commandlib.nodes;


import net.lenni0451.commandlib.ExecutionContext;
import net.lenni0451.commandlib.exceptions.ArgumentParseException;
import net.lenni0451.commandlib.utils.StringReader;

import javax.annotation.Nullable;
import java.util.List;

public class StringArgumentNode<E> extends ArgumentNode<E, String> {

    public StringArgumentNode(final String name) {
        super(name);
        this.weight = 100;
    }

    public StringArgumentNode(final String name, @Nullable final String description) {
        super(name, description);
        this.weight = 100;
    }

    @Override
    public String parse(ExecutionContext<E> context, StringReader reader) throws ArgumentParseException {
        String result = reader.readWordOrString();
        if (result.equalsIgnoreCase(this.name())) return this.name();
        throw new ArgumentParseException(this.name());
    }

    @Override
    public void parseCompletions(List<String> completions, StringReader stringReader, ExecutionContext<E> context) {
        completions.add(this.name());
    }

}
