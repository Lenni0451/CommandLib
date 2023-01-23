package net.lenni0451.commandlib.exceptions;

import net.lenni0451.commandlib.ArgumentChain;

import javax.annotation.Nullable;
import java.util.Map;

public class CommandNotFoundException extends Exception {

    private final String command;
    private final Map<ArgumentChain<?>, ChainExecutionException> mostLikelyChains;

    public CommandNotFoundException(final String command) {
        super("The command '" + command + "' does not exist");
        this.command = command;
        this.mostLikelyChains = null;
    }

    public CommandNotFoundException(final String command, final Map<ArgumentChain<?>, ChainExecutionException> mostLikelyChains) {
        super("The command '" + command + "' does not exist");
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
