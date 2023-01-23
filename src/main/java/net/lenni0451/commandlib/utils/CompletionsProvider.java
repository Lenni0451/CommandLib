package net.lenni0451.commandlib.utils;

import net.lenni0451.commandlib.ExecutionContext;

import java.util.Set;

public interface CompletionsProvider<E> {

    void provide(final Set<String> completions, final ExecutionContext<E> context, final StringReader stringReader);

}
