package net.lenni0451.commandlib;

import net.lenni0451.commandlib.exceptions.ChainExecutionException;

import java.util.Collections;
import java.util.List;

/**
 * Storage for all parsed argument chains.
 *
 * @param <E> The type of the executor
 */
public class ParseResult<E> {

    private final List<ParsedChain<E>> parsedChains;
    private final List<FailedChain<E>> failedChains;

    public ParseResult(final List<ParsedChain<E>> parsedChains, final List<FailedChain<E>> failedChains) {
        this.parsedChains = parsedChains;
        this.failedChains = failedChains;
    }

    public List<ParsedChain<E>> getParsedChains() {
        return Collections.unmodifiableList(this.parsedChains);
    }

    public List<FailedChain<E>> getFailedChains() {
        return Collections.unmodifiableList(this.failedChains);
    }


    public static class ParsedChain<E> {
        private final ArgumentChain<E> argumentChain;
        private final List<ArgumentChain.MatchedArgument> matchedArguments;

        public ParsedChain(final ArgumentChain<E> argumentChain, final List<ArgumentChain.MatchedArgument> matchedArguments) {
            this.argumentChain = argumentChain;
            this.matchedArguments = matchedArguments;
        }

        public ArgumentChain<E> getArgumentChain() {
            return this.argumentChain;
        }

        public List<ArgumentChain.MatchedArgument> getMatchedArguments() {
            return this.matchedArguments;
        }
    }

    public static class FailedChain<E> {
        private final ArgumentChain<E> argumentChain;
        private final ChainExecutionException executionException;

        public FailedChain(final ArgumentChain<E> argumentChain, final ChainExecutionException executionException) {
            this.argumentChain = argumentChain;
            this.executionException = executionException;
        }

        public ArgumentChain<E> getArgumentChain() {
            return this.argumentChain;
        }

        public ChainExecutionException getExecutionException() {
            return this.executionException;
        }
    }

}
