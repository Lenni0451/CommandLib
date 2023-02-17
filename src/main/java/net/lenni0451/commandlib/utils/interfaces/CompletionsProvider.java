package net.lenni0451.commandlib.utils.interfaces;

import net.lenni0451.commandlib.contexts.ExecutionContext;
import net.lenni0451.commandlib.utils.StringReader;

import java.util.Set;

@FunctionalInterface
public interface CompletionsProvider<E> {

    void provide(final Set<String> completions, final ExecutionContext<E> context, final StringReader stringReader);

}
