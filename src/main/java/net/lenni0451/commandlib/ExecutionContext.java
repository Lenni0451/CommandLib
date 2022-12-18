package net.lenni0451.commandlib;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class ExecutionContext<E> {

    private final E executor;
    private final Map<String, Object> arguments;

    public ExecutionContext(final E executor) {
        this.executor = executor;
        this.arguments = new HashMap<>();
    }

    public E getExecutor() {
        return this.executor;
    }

    public Map<String, Object> getArguments() {
        return this.arguments;
    }

    @Nonnull
    public <T> T getArgument(final String name) {
        if (!this.arguments.containsKey(name)) throw new IllegalArgumentException("The argument '" + name + "' does not exist");
        return (T) this.arguments.get(name);
    }

    public void addArgument(final String name, final Object value) {
        this.arguments.put(name, value);
    }

}
