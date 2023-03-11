package net.lenni0451.commandlib.exceptions;

import net.lenni0451.commandlib.ParseResult;

import javax.annotation.Nullable;
import java.util.List;

/**
 * An exception which is thrown when an error occurs during the execution of a command.<br>
 * It contains a map of all chains which were tried to execute and the exception which was thrown while executing them.<br>
 * The chains are sorted by the most likely to be the correct one.
 */
public class CommandExecutionException extends Exception {

    private final String command;
    private final List<ParseResult.FailedChain<?>> mostLikelyChains;

    public CommandExecutionException(final String command) {
        this(command, null);
    }

    public CommandExecutionException(final String command, @Nullable final List<ParseResult.FailedChain<?>> mostLikelyChains) {
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
    public List<ParseResult.FailedChain<?>> getMostLikelyChains() {
        return this.mostLikelyChains;
    }

}
