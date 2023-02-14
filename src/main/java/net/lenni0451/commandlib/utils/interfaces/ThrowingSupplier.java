package net.lenni0451.commandlib.utils.interfaces;

@FunctionalInterface
public interface ThrowingSupplier<T> {

    T get() throws Throwable;

}
