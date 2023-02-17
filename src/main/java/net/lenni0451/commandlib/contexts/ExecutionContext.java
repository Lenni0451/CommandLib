package net.lenni0451.commandlib.contexts;

import net.lenni0451.commandlib.utils.ArgumentComparator;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class ExecutionContext<E> {

    private final ArgumentComparator argumentComparator;
    private final E executor;
    private final Map<String, Object> arguments;
    private final boolean isExecution;

    public ExecutionContext(final ArgumentComparator argumentComparator, final E executor, final boolean isExecution) {
        this.argumentComparator = argumentComparator;
        this.executor = executor;
        this.arguments = new HashMap<>();
        this.isExecution = isExecution;
    }

    public ArgumentComparator getArgumentComparator() {
        return this.argumentComparator;
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

    public boolean isExecution() {
        return this.isExecution;
    }

}
