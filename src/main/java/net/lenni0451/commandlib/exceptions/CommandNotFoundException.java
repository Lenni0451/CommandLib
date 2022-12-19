package net.lenni0451.commandlib.exceptions;

import net.lenni0451.commandlib.ArgumentChain;

import javax.annotation.Nullable;

public class CommandNotFoundException extends Exception {

    private final String command;
    private final ArgumentChain<?> mostLikelyChain;
    private final ChainExecutionException chainExecutionException;

    public CommandNotFoundException(final String command) {
        super("The command '" + command + "' does not exist");
        this.command = command;
        this.mostLikelyChain = null;
        this.chainExecutionException = null;
    }

    public CommandNotFoundException(final String command, final ArgumentChain<?> mostLikelyChain, final ChainExecutionException chainExecutionException) {
        super("The command '" + command + "' does not exist");
        this.command = command;
        this.mostLikelyChain = mostLikelyChain;
        this.chainExecutionException = chainExecutionException;
    }

    public String getCommand() {
        return this.command;
    }

    @Nullable
    public ArgumentChain<?> getMostLikelyChain() {
        return this.mostLikelyChain;
    }

    @Nullable
    public ChainExecutionException getChainExecutionException() {
        return this.chainExecutionException;
    }

}
