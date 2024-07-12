package net.lenni0451.commandlib.types;

import net.lenni0451.commandlib.contexts.CompletionContext;
import net.lenni0451.commandlib.contexts.ExecutionContext;
import net.lenni0451.commandlib.exceptions.ArgumentParseException;
import net.lenni0451.commandlib.utils.StringReader;

import javax.annotation.Nonnull;
import java.util.Set;

/**
 * The string type parses a string from the string reader.<br>
 * There are three different types of strings:<br>
 * - {@link Type#WORD} - A single word<br>
 * - {@link Type#STRING} - A single word or a string surrounded by single or double quotes<br>
 * - {@link Type#GREEDY_STRING} - A string that contains all remaining characters
 *
 * @param <E> The type of the executor
 */
public class StringType<E> implements ArgumentType<E, String> {

    /**
     * Create a new string type that parses a single word.
     *
     * @param <E> The type of the executor
     * @return The new string type
     */
    public static <E> StringType<E> word() {
        return new StringType<>(Type.WORD);
    }

    /**
     * Create a new string type that parses a single word or a string surrounded by single or double quotes.
     *
     * @param <E> The type of the executor
     * @return The new string type
     */
    public static <E> StringType<E> string() {
        return new StringType<>(Type.STRING);
    }

    /**
     * Create a new string type that parses a string that contains all remaining characters.
     *
     * @param <E> The type of the executor
     * @return The new string type
     */
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
    public void parseCompletions(Set<String> completions, CompletionContext completionContext, ExecutionContext<E> executionContext, StringReader stringReader) {
    }


    private enum Type {
        WORD, STRING, GREEDY_STRING
    }

}
