package net.lenni0451.commandlib.exceptions;

import net.lenni0451.commandlib.ArgumentChain;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * An exception which is thrown when an error occurs during the execution of a command.<br>
 * It contains a map of all chains which were tried to execute and the exception which was thrown while executing them.<br>
 * The chains are sorted by the most likely to be the correct one.
 */
public class CommandExecutionException extends Exception {

    private final String command;
    private final Map<ArgumentChain<?>, ChainExecutionException> mostLikelyChains;

    public CommandExecutionException(final String command) {
        this(command, null);
    }

    public CommandExecutionException(final String command, @Nullable final Map<ArgumentChain<?>, ChainExecutionException> mostLikelyChains) {
        super("An error occurred whilst executing command: " + command + "");
        this.command = command;
        this.mostLikelyChains = mostLikelyChains;
    }

    /**
     * @return The command which was executed
     */
    public String getCommand() {
        return this.command;
    }

    /**
     * @return A map of all chains which were tried to execute
     */
    @Nullable
    public Map<ArgumentChain<?>, ChainExecutionException> getMostLikelyChains() {
        return this.mostLikelyChains;
    }

}
