package net.lenni0451.commandlib.exceptions;

import net.lenni0451.commandlib.ArgumentChain;

import javax.annotation.Nullable;
import java.util.Map;

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

    public String getCommand() {
        return this.command;
    }

    @Nullable
    public Map<ArgumentChain<?>, ChainExecutionException> getMostLikelyChains() {
        return this.mostLikelyChains;
    }

}
