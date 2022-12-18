package net.lenni0451.commandlib.exceptions;

public class CommandNotFoundException extends Exception {

    public CommandNotFoundException(final String command) {
        super("The command '" + command + "' does not exist");
    }

}
