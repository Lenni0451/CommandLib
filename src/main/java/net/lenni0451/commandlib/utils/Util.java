package net.lenni0451.commandlib.utils;

import java.util.Optional;

public class Util {

    public static <T> Optional<T> ofThrowing(final ThrowingSupplier<T> supplier) {
        try {
            return Optional.ofNullable(supplier.get());
        } catch (Throwable t) {
            return Optional.empty();
        }
    }

}
