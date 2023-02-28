package net.lenni0451.commandlib;

import java.util.Objects;

/**
 * A completion for the user input suggesting what the user might want to type.
 */
public class Completion {

    private final int start;
    private final String completion;

    public Completion(final int start, final String completion) {
        this.start = start;
        this.completion = completion;
    }

    /**
     * The start of a suggestion is the index in the input where the suggestion starts.<br>
     * To fill the suggestion into the input you could do something like this:
     * <br><br><code>
     *     // The input the user types<br>
     *     String input = "test inp";<br>
     *     // Get the completion from somewhere<br>
     *     Completion completion = new Completion(5, "input");<br>
     *     input = input.substring(0, completion.getStart()) + completion.getCompletion();
     * </code><br><br>
     * In this example the input would be "test input" after the completion was applied.
     *
     * @return The start of the suggestion
     */
    public int getStart() {
        return this.start;
    }

    /**
     * @return The completion itself
     */
    public String getCompletion() {
        return this.completion;
    }

    @Override
    public String toString() {
        return "Suggestion{" +
                "start=" + start +
                ", completion='" + completion + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Completion that = (Completion) o;
        return start == that.start && Objects.equals(completion, that.completion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, completion);
    }

}
