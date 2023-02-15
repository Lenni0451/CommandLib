package net.lenni0451.commandlib.types;

import net.lenni0451.commandlib.ExecutionContext;
import net.lenni0451.commandlib.exceptions.ArgumentParseException;
import net.lenni0451.commandlib.utils.StringReader;

import javax.annotation.Nonnull;
import java.util.Set;

public class StringArgumentType<E> implements ArgumentType<E, String> {

    public static <E> StringArgumentType<E> word() {
        return new StringArgumentType<>(Type.WORD);
    }

    public static <E> StringArgumentType<E> string() {
        return new StringArgumentType<>(Type.STRING);
    }

    public static <E> StringArgumentType<E> greedyString() {
        return new StringArgumentType<>(Type.GREEDY_STRING);
    }


    private final Type type;

    private StringArgumentType(final Type type) {
        this.type = type;
    }

    @Override
    public int getWeight() {
        return 10;
    }

    @Nonnull
    @Override
    public String parseValue(ExecutionContext<E> context, StringReader stringReader) throws ArgumentParseException, RuntimeException {
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
    public void parseCompletions(Set<String> completions, ExecutionContext<E> context, StringReader stringReader) {
    }


    private enum Type {
        WORD, STRING, GREEDY_STRING
    }

}
