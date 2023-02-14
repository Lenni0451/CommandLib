package net.lenni0451.commandlib.utils.interfaces;

@FunctionalInterface
public interface ValueValidator<T> {

    boolean validate(final T value);

}
