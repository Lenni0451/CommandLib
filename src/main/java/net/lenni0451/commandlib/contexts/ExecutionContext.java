package net.lenni0451.commandlib.contexts;

import net.lenni0451.commandlib.utils.comparator.ArgumentComparator;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

/**
 * A context which is used to store information about the execution process.
 *
 * @param <E> The type of the executor
 */
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

    /**
     * @return The used argument comparator
     */
    public ArgumentComparator getArgumentComparator() {
        return this.argumentComparator;
    }

    /**
     * @return The executor executing the command
     */
    public E getExecutor() {
        return this.executor;
    }

    /**
     * @return All parsed arguments
     */
    public Map<String, Object> getArguments() {
        return this.arguments;
    }

    /**
     * Get a parsed argument by its name.
     *
     * @param name The name of the argument
     * @param <T>  The type of the argument
     * @return The parsed argument
     * @throws IllegalArgumentException If the argument does not exist
     */
    @Nonnull
    public <T> T getArgument(final String name) {
        if (!this.arguments.containsKey(name)) throw new IllegalArgumentException("The argument '" + name + "' does not exist");
        return (T) this.arguments.get(name);
    }

    /**
     * Add a parsed argument.
     *
     * @param name  The name of the argument
     * @param value The value of the argument
     */
    public void addArgument(final String name, final Object value) {
        this.arguments.put(name, value);
    }

    /**
     * @return If the context is used for execution
     */
    public boolean isExecution() {
        return this.isExecution;
    }

}
