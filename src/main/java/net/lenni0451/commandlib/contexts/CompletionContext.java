package net.lenni0451.commandlib.contexts;

/**
 * A context which is used to store information about the completion process.
 */
public class CompletionContext {

    private int completionsTrim = 0;

    /**
     * @return The amount of characters that should be trimmed from the completions
     */
    public int getCompletionsTrim() {
        return this.completionsTrim;
    }

    /**
     * Set the amount of characters that should be trimmed from the completions.<br>
     * The characters will be trimmed from the start of the completion.
     *
     * @param completionsTrim The amount of characters
     */
    public void setCompletionsTrim(final int completionsTrim) {
        this.completionsTrim = completionsTrim;
    }

}
