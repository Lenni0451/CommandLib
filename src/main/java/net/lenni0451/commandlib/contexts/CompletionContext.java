package net.lenni0451.commandlib.contexts;

import net.lenni0451.commandlib.utils.comparator.ArgumentComparator;

/**
 * A context which is used to store information about the completion process.
 */
public class CompletionContext {

    private int completionsTrim = 0;
    private CompletionMatcher completionMatcher = CompletionMatcher.STARTS_WITH;

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

    /**
     * @return The completion matcher for selecting shown completions
     */
    public CompletionMatcher getCompletionMatcher() {
        return this.completionMatcher;
    }

    /**
     * Set the completion matcher for selecting shown completions.
     *
     * @param completionMatcher The completion matcher
     */
    public void setCompletionMatcher(final CompletionMatcher completionMatcher) {
        this.completionMatcher = completionMatcher;
    }


    public enum CompletionMatcher {
        STARTS_WITH(ArgumentComparator::startsWith),
        CONTAINS(ArgumentComparator::contains);

        private final CompletionMatcherFunction matcherFunction;

        CompletionMatcher(final CompletionMatcherFunction matcherFunction) {
            this.matcherFunction = matcherFunction;
        }

        public boolean match(final ArgumentComparator comparator, final String s, final String b) {
            return this.matcherFunction.match(comparator, s, b);
        }


        private interface CompletionMatcherFunction {
            boolean match(final ArgumentComparator comparator, final String s, final String b);
        }
    }

}
