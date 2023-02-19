package net.lenni0451.commandlib.utils.interfaces;

/**
 * A functional interface to allow the validation of command parameters.
 *
 * @param <T> The type of the value to validate
 */
@FunctionalInterface
public interface ValueValidator<T> {

    /**
     * Check if the given value is valid.
     *
     * @param value The value to validate
     * @return If the value is valid
     */
    boolean validate(final T value);

}
