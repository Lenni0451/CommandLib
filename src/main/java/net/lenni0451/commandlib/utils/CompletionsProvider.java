package net.lenni0451.commandlib.utils;

import net.lenni0451.commandlib.ExecutionContext;

import java.util.List;

public interface CompletionsProvider<E> {

    void provide(final List<String> completions, final StringReader stringReader, final ExecutionContext<E> context);

}
