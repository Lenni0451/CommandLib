package net.lenni0451.commandlib.types;

import net.lenni0451.commandlib.contexts.ExecutionContext;
import net.lenni0451.commandlib.exceptions.ArgumentParseException;
import net.lenni0451.commandlib.utils.StringReader;
import net.lenni0451.commandlib.utils.Util;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * The enum type parses an enum value from the string reader.
 *
 * @param <E> The type of the executor
 * @param <T> The enum type
 */
public class EnumType<E, T extends Enum<T>> implements ArgumentType<E, T> {

    /**
     * Create a new enum type.
     *
     * @param enumClass The enum class
     * @param <E>       The type of the executor
     * @param <T>       The enum type
     * @return The new enum type
     */
    public static <E, T extends Enum<T>> EnumType<E, T> of(final Class<T> enumClass) {
        return new EnumType<>(enumClass);
    }


    private final Class<T> enumClass;
    private final Map<String, T> nameToValue = new HashMap<>();

    private EnumType(final Class<T> enumClass) {
        this.enumClass = enumClass;
        for (T value : enumClass.getEnumConstants()) {
            this.nameToValue.put(Util.beautify(value, false).toLowerCase(), value);
        }
    }

    @Nonnull
    @Override
    public T parseValue(ExecutionContext<E> executionContext, StringReader stringReader) throws ArgumentParseException, RuntimeException {
        String name = stringReader.readWord();
        T value = this.nameToValue.get(name.toLowerCase());
        if (value == null) throw ArgumentParseException.reason("Unknown enum value '" + name + "'");
        return value;
    }

    @Override
    public void parseCompletions(Set<String> completions, ExecutionContext<E> executionContext, StringReader stringReader) {
        for (T value : this.enumClass.getEnumConstants()) completions.add(Util.beautify(value, false));
    }

}
