package net.lenni0451.commandlib.types;

import net.lenni0451.commandlib.contexts.ExecutionContext;
import net.lenni0451.commandlib.exceptions.ArgumentParseException;
import net.lenni0451.commandlib.utils.StringReader;
import net.lenni0451.commandlib.utils.Util;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.Set;

/**
 * The integer type parses an integer number from the string reader.<br>
 * Min and max values can be defined (inclusive).<br>
 * Completions are calculated based on the min and max values.
 *
 * @param <E> The type of the executor
 */
public class IntegerType<E> implements ArgumentType<E, Integer> {

    /**
     * Create a new integer type with no min and max value.<br>
     * Values between {@link Integer#MIN_VALUE} and {@link Integer#MAX_VALUE} are allowed.
     *
     * @param <E> The type of the executor
     * @return The new integer type
     */
    public static <E> IntegerType<E> integer() {
        return new IntegerType<>(null, null);
    }

    /**
     * Create a new integer type with a min value.<br>
     * Values between {@code min} and {@link Integer#MAX_VALUE} are allowed.
     *
     * @param min The min value
     * @param <E> The type of the executor
     * @return The new integer type
     */
    public static <E> IntegerType<E> minInteger(final int min) {
        return new IntegerType<>(min, null);
    }

    /**
     * Create a new integer type with a max value.<br>
     * Values between {@link Integer#MIN_VALUE} and {@code max} are allowed.
     *
     * @param max The max value
     * @param <E> The type of the executor
     * @return The new integer type
     */
    public static <E> IntegerType<E> maxInteger(final int max) {
        return new IntegerType<>(null, max);
    }

    /**
     * Create a new integer type with a min and max value.<br>
     * Values between {@code min} and {@code max} are allowed.
     *
     * @param min The min value
     * @param max The max value
     * @param <E> The type of the executor
     * @return The new integer type
     */
    public static <E> IntegerType<E> integer(final int min, final int max) {
        return new IntegerType<>(min, max);
    }


    private final Integer min;
    private final Integer max;

    private IntegerType(final Integer min, final Integer max) {
        if (min != null && max != null && min > max) throw new IllegalArgumentException("min must be smaller than max");
        this.min = min;
        this.max = max;
    }

    @Override
    public int getWeight() {
        return 50;
    }

    @Nonnull
    @Override
    public Integer parseValue(ExecutionContext<E> executionContext, StringReader stringReader) throws ArgumentParseException, RuntimeException {
        Optional<Integer> i = Util.ofThrowing(() -> Integer.parseInt(stringReader.readIntegerNumber()));
        if (!i.isPresent()) {
            if (this.min == null && this.max == null) throw ArgumentParseException.expected("int");
            else if (this.min == null) throw ArgumentParseException.expected("int <= " + this.max);
            else if (this.max == null) throw ArgumentParseException.expected("int >= " + this.min);
            else throw ArgumentParseException.expected("int >= " + this.min + " & <= " + this.max);
        }
        if (this.min != null && i.get() < this.min) throw ArgumentParseException.reason("Number is too small (min: " + this.min + ")");
        if (this.max != null && i.get() > this.max) throw ArgumentParseException.reason("Number is too big (max: " + this.max + ")");
        return i.get();
    }

    @Override
    public void parseCompletions(Set<String> completions, ExecutionContext<E> executionContext, StringReader stringReader) {
        if (this.min != null && this.max != null) {
            int diff = this.max - this.min;
            int step = Math.max(1, diff / 10);
            for (int i = this.min; i <= this.max; i += step) completions.add(String.valueOf(i));
            completions.add(String.valueOf(this.max));
        } else if (this.min != null) {
            for (int i = this.min; i <= this.min + 10; i++) completions.add(String.valueOf(i));
        } else if (this.max != null) {
            for (int i = this.max - 10; i <= this.max; i++) completions.add(String.valueOf(i));
        } else {
            for (int i = -5; i <= 10; i++) completions.add(String.valueOf(i));
        }
    }

}
