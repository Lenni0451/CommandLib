package net.lenni0451.commandlib.types;

import net.lenni0451.commandlib.contexts.ExecutionContext;
import net.lenni0451.commandlib.exceptions.ArgumentParseException;
import net.lenni0451.commandlib.utils.StringReader;

import javax.annotation.Nonnull;
import java.util.Set;

public class StringType<E> implements ArgumentType<E, String> {

    public static <E> StringType<E> word() {
        return new StringType<>(Type.WORD);
    }

    public static <E> StringType<E> string() {
        return new StringType<>(Type.STRING);
    }

    public static <E> StringType<E> greedyString() {
        return new StringType<>(Type.GREEDY_STRING);
    }


    private final Type type;

    private StringType(final Type type) {
        this.type = type;
    }

    @Override
    public int getWeight() {
        return 10;
    }

    @Nonnull
    @Override
    public String parseValue(ExecutionContext<E> executionContext, StringReader stringReader) throws ArgumentParseException, RuntimeException {
        switch (this.type) {
            case WORD:
                return stringReader.readWord();
            case STRING:
                return stringReader.readWordOrString();
            case GREEDY_STRING:
                return stringReader.readRemaining();
            default:
                throw ArgumentParseException.reason("Unknown type '" + this.type + "'");
        }
    }

    @Override
    public void parseCompletions(Set<String> completions, ExecutionContext<E> executionContext, StringReader stringReader) {
    }


    private enum Type {
        WORD, STRING, GREEDY_STRING
    }

}
