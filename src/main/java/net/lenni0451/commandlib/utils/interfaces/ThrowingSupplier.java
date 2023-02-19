package net.lenni0451.commandlib.utils.interfaces;

import java.util.function.Supplier;

/**
 * A functional interface equivalent to {@link Supplier} but with the ability to throw an exception.
 *
 * @param <T> The type of the value to supply
 */
@FunctionalInterface
public interface ThrowingSupplier<T> {

    /**
     * Get the value.
     *
     * @return The value
     * @throws Throwable If an exception occurs
     */
    T get() throws Throwable;

}
