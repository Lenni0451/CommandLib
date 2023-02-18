package net.lenni0451.commandlib.types;

import net.lenni0451.commandlib.contexts.ExecutionContext;
import net.lenni0451.commandlib.exceptions.ArgumentParseException;
import net.lenni0451.commandlib.utils.StringReader;
import net.lenni0451.commandlib.utils.Util;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.Set;

public class IntegerArgumentType<E> implements ArgumentType<E, Integer> {

    public static <E> IntegerArgumentType<E> integer() {
        return new IntegerArgumentType<>(null, null);
    }

    public static <E> IntegerArgumentType<E> minInteger(final int min) {
        return new IntegerArgumentType<>(min, null);
    }

    public static <E> IntegerArgumentType<E> maxInteger(final int max) {
        return new IntegerArgumentType<>(null, max);
    }

    public static <E> IntegerArgumentType<E> integer(final int min, final int max) {
        return new IntegerArgumentType<>(min, max);
    }


    private final Integer min;
    private final Integer max;

    private IntegerArgumentType(final Integer min, final Integer max) {
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
            if (diff > 10) {
                for (int i = 0; i < 10; i++) completions.add(String.valueOf(this.min + (diff / 10 * i)));
            } else {
                for (int i = this.min; i <= this.max; i++) completions.add(String.valueOf(i));
            }
        } else if (this.min != null) {
            for (int i = this.min; i <= this.min + 10; i++) completions.add(String.valueOf(i));
        } else if (this.max != null) {
            for (int i = this.max - 10; i <= this.max; i++) completions.add(String.valueOf(i));
        } else {
            for (int i = 0; i <= 10; i++) completions.add(String.valueOf(i));
        }
    }

}
