package net.lenni0451.commandlib.utils;

public interface ThrowingSupplier<T> {

    T get() throws Throwable;

}
