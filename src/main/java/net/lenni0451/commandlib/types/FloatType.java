package net.lenni0451.commandlib.types;

import net.lenni0451.commandlib.contexts.ExecutionContext;
import net.lenni0451.commandlib.exceptions.ArgumentParseException;
import net.lenni0451.commandlib.utils.StringReader;
import net.lenni0451.commandlib.utils.Util;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.Set;

/**
 * The float type parses a float number from the string reader.<br>
 * Min and max values can be defined (inclusive).<br>
 * Completions are calculated based on the min and max values.
 *
 * @param <E> The type of the executor
 */
public class FloatType<E> implements ArgumentType<E, Float> {

    /**
     * Create a new float type with no min and max value.<br>
     * Values between -{@link Float#MAX_VALUE} and {@link Float#MAX_VALUE} are allowed.
     *
     * @param <E> The type of the executor
     * @return The new float type
     */
    public static <E> FloatType<E> floatt() {
        return new FloatType<>(null, null);
    }

    /**
     * Create a new float type with a min value.<br>
     * Values between {@code min} and {@link Float#MAX_VALUE} are allowed.
     *
     * @param min The min value
     * @param <E> The type of the executor
     * @return The new float type
     */
    public static <E> FloatType<E> minFloat(final float min) {
        return new FloatType<>(min, null);
    }

    /**
     * Create a new float type with a max value.<br>
     * Values between -{@link Float#MAX_VALUE} and {@code max} are allowed.
     *
     * @param max The max value
     * @param <E> The type of the executor
     * @return The new float type
     */
    public static <E> FloatType<E> maxFloat(final float max) {
        return new FloatType<>(null, max);
    }

    /**
     * Create a new float type with a min and max value.<br>
     * Values between {@code min} and {@code max} are allowed.
     *
     * @param min The min value
     * @param max The max value
     * @param <E> The type of the executor
     * @return The new float type
     */
    public static <E> FloatType<E> rangeFloat(final float min, final float max) {
        return new FloatType<>(min, max);
    }


    private final Float min;
    private final Float max;

    private FloatType(final Float min, final Float max) {
        if (min != null && max != null && min > max) throw new IllegalArgumentException("Min value must be smaller than max value");
        this.min = min;
        this.max = max;
    }

    @Override
    public int getWeight() {
        return 50;
    }

    @Nonnull
    @Override
    public Float parseValue(ExecutionContext<E> executionContext, StringReader stringReader) throws ArgumentParseException, RuntimeException {
        Optional<Float> f = Util.ofThrowing(() -> Float.parseFloat(stringReader.readDecimalNumber()));
        if (!f.isPresent()) {
            if (this.min == null && this.max == null) throw ArgumentParseException.expected("float");
            else if (this.min == null) throw ArgumentParseException.expected("float <= " + this.max);
            else if (this.max == null) throw ArgumentParseException.expected("float >= " + this.min);
            else throw ArgumentParseException.expected("float >= " + this.min + " & <= " + this.max);
        }
        if (this.min != null && f.get() < this.min) throw ArgumentParseException.reason("Number is too small (min: " + this.min + ")");
        if (this.max != null && f.get() > this.max) throw ArgumentParseException.reason("Number is too big (max: " + this.max + ")");
        return f.get();
    }

    @Override
    public void parseCompletions(Set<String> completions, ExecutionContext<E> executionContext, StringReader stringReader) {
        if (this.min != null && this.max != null) {
            float diff = this.max - this.min;
            float step = Math.max(1, diff / 10);
            for (float f = this.min; f <= this.max; f += step) completions.add(String.valueOf(f));
            completions.add(String.valueOf(this.max));
        } else if (this.min != null) {
            for (float f = this.min; f <= this.min + 10; f++) completions.add(String.valueOf(f));
        } else if (this.max != null) {
            for (float f = this.max - 10; f <= this.max; f++) completions.add(String.valueOf(f));
        } else {
            for (float f = -5; f <= 10; f++) completions.add(String.valueOf(f));
        }
    }

}
