package net.lenni0451.commandlib;

import net.lenni0451.commandlib.contexts.ExecutionContext;
import net.lenni0451.commandlib.exceptions.ArgumentParseException;
import net.lenni0451.commandlib.exceptions.ChainExecutionException;
import net.lenni0451.commandlib.exceptions.HandledException;
import net.lenni0451.commandlib.nodes.ArgumentNode;
import net.lenni0451.commandlib.nodes.RedirectNode;
import net.lenni0451.commandlib.utils.StringReader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * A chain of arguments required for correct execution.
 *
 * @param <E> The type of the executor
 */
public class ArgumentChain<E> {

    /**
     * Build a list of all chains for the given argument node.
     *
     * @param argument The argument node
     * @param <E>      The type of the executor
     * @return The list of chains
     * @throws IllegalArgumentException If a duplicate argument name is found or the chain end has no executor
     */
    public static <E> List<ArgumentChain<E>> buildChains(final ArgumentNode<E, ?> argument) {
        List<ArgumentChain<E>> chains = new ArrayList<>();

        Map<ArgumentNode<E, ?>, ArgumentChain<E>> branches = new HashMap<>();
        branches.put(argument, new ArgumentChain<>(argument));
        while (!branches.isEmpty()) {
            Map<ArgumentNode<E, ?>, ArgumentChain<E>> newBranches = new HashMap<>();
            for (Map.Entry<ArgumentNode<E, ?>, ArgumentChain<E>> entry : branches.entrySet()) {
                ArgumentNode<E, ?> node = entry.getKey();
                ArgumentChain<E> chain = entry.getValue();

                if (node.executor() != null || node instanceof RedirectNode) {
                    List<String> names = new ArrayList<>();
                    for (ArgumentNode<E, ?> argumentNode : chain.arguments) {
                        if (!argumentNode.providesArgument()) continue;
                        if (names.contains(argumentNode.name())) throw new IllegalArgumentException("Duplicate argument name '" + argumentNode.name() + "' in chain: " + chain);
                        names.add(argumentNode.name());
                    }
                    chains.add(chain);
                } else if (node.children().isEmpty()) {
                    throw new IllegalStateException("Chain ended but has no executor: " + chain);
                }
                for (ArgumentNode<E, ?> child : node.children()) {
                    ArgumentChain<E> newChain = new ArgumentChain<>(chain);
                    newChain.addArgument(child);
                    newBranches.put(child, newChain);
                }
            }
            branches = newBranches;
        }

        return chains;
    }

    public static <E> ArgumentChain<E> merge(final ArgumentChain<E> chain1, final ArgumentChain<E> chain2) {
        if (chain1.getExecutor() != null) throw new IllegalArgumentException("Can not merge chains if the first chain does not end with a redirect node");
        if (chain2.getExecutor() == null) throw new IllegalArgumentException("Can not merge chains if the second chain does not have an executor");
        return new ArgumentChain<E>() {
            @Override
            public int getLength() {
                return chain1.getLength() + chain2.getLength();
            }

            @Override
            public int[] getWeights() {
                int[] weights = new int[getLength()];
                System.arraycopy(chain1.getWeights(), 0, weights, 0, chain1.getLength());
                System.arraycopy(chain2.getWeights(), 0, weights, chain1.getLength(), chain2.getLength());
                return weights;
            }

            @Override
            public ArgumentNode<E, ?> getArgument(int index) {
                if (index < chain1.getLength()) return chain1.getArgument(index);
                return chain2.getArgument(index - chain1.getLength());
            }

            @Override
            public List<MatchedArgument> parse(ExecutionContext<E> executionContext, StringReader reader) {
                throw new UnsupportedOperationException("Can not parse a merged chain");
            }

            @Override
            public void populateArguments(ExecutionContext<E> executionContext, List<MatchedArgument> arguments) {
                chain1.populateArguments(executionContext, arguments.subList(0, chain1.getLength()));
                chain2.populateArguments(executionContext, arguments.subList(chain1.getLength(), arguments.size()));
            }

            @Override
            public Function<ExecutionContext<E>, ?> getExecutor() {
                return chain2.getExecutor();
            }
        };
    }


    private final List<ArgumentNode<E, ?>> arguments;

    private ArgumentChain() {
        this.arguments = new ArrayList<>();
    }

    private ArgumentChain(final List<ArgumentNode<E, ?>> arguments) {
        this.arguments = arguments;
    }

    private ArgumentChain(final ArgumentNode<E, ?> argument) {
        this.arguments = new ArrayList<>();
        this.arguments.add(argument);
    }

    private ArgumentChain(final ArgumentChain<E> chain) {
        this.arguments = new ArrayList<>(chain.arguments);
    }

    private void addArgument(final ArgumentNode<E, ?> argument) {
        this.arguments.add(argument);
    }

    public int getLength() {
        return this.arguments.size();
    }

    /**
     * @return The weights of all arguments in this chain
     */
    public int[] getWeights() {
        int[] weights = new int[this.arguments.size()];
        for (int i = 0; i < this.arguments.size(); i++) weights[i] = this.arguments.get(i).weight();
        return weights;
    }

    /**
     * Get an argument node by its index.
     *
     * @param index The index
     * @return The argument node
     * @throws IndexOutOfBoundsException If the index is out of bounds
     */
    public ArgumentNode<E, ?> getArgument(final int index) {
        return this.arguments.get(index);
    }

    /**
     * Parse the given input and return the parsed arguments.
     *
     * @param executionContext The execution context
     * @param reader           The input reader
     * @return The parsed arguments
     * @throws ChainExecutionException If the input can not be parsed
     */
    public List<MatchedArgument> parse(final ExecutionContext<E> executionContext, final StringReader reader) throws ChainExecutionException {
        List<MatchedArgument> out = new ArrayList<>();
        for (int i = 0; i < this.arguments.size(); i++) {
            int cursor = reader.getCursor();
            ArgumentNode<E, ?> argument = this.arguments.get(i);
            boolean isLast = i == this.arguments.size() - 1;
            try {
                if (!argument.requirement().test(executionContext)) {
                    throw new ChainExecutionException(ChainExecutionException.Reason.REQUIREMENT_FAILED, i, cursor, argument.name(), reader.readRemaining());
                }
                if (argument instanceof RedirectNode<?>) {
                    out.add(new MatchedArgument(cursor, "", argument.name()));
                    return out;
                }
                Object parsedArgument = argument.value(executionContext, reader);
                out.add(new MatchedArgument(cursor, reader.getString().substring(cursor, reader.getCursor()), parsedArgument));
                if (!isLast && (!reader.canRead() || reader.read() != ' ')) {
                    throw new ChainExecutionException(ChainExecutionException.Reason.MISSING_SPACE, i, cursor, null, reader.readRemaining());
                }
                if (!isLast && !reader.canRead()) {
                    ArgumentNode<E, ?> nextArgument = this.arguments.get(i + 1);
                    if (!nextArgument.requirement().test(executionContext)) {
                        throw new ChainExecutionException(ChainExecutionException.Reason.REQUIREMENT_FAILED, i + 1, cursor, nextArgument.name(), reader.readRemaining());
                    }
                    String missingArguments = new ArgumentChain<>(this.arguments.subList(i + 1, this.arguments.size())).toString();
                    throw new ChainExecutionException(ChainExecutionException.Reason.NO_ARGUMENTS_LEFT, i + 1, reader.getCursor(), null, missingArguments);
                } else if (isLast && reader.canRead()) {
                    throw new ChainExecutionException(ChainExecutionException.Reason.TOO_MANY_ARGUMENTS, i, reader.getCursor(), null, reader.readRemaining());
                }
            } catch (HandledException e) {
                throw new ChainExecutionException(e, i, cursor, argument.name(), reader.getString().substring(cursor, reader.getCursor()));
            } catch (ArgumentParseException e) {
                throw new ChainExecutionException(e, i, cursor, argument.name(), reader.getString().substring(cursor, reader.getCursor()));
            } catch (RuntimeException e) {
                throw new ChainExecutionException(e, i, cursor, argument.name(), reader.getString().substring(cursor, reader.getCursor()));
            }
        }
        return out;
    }

    /**
     * Populate the given execution context with the given arguments.
     *
     * @param executionContext The execution context
     * @param arguments        The arguments
     */
    public void populateArguments(final ExecutionContext<E> executionContext, final List<ArgumentChain.MatchedArgument> arguments) {
        for (int i = 0; i < this.arguments.size(); i++) {
            ArgumentNode<E, ?> argumentNode = this.arguments.get(i);
            if (!argumentNode.providesArgument()) continue;
            executionContext.addArgument(argumentNode.name(), arguments.get(i).getValue());
        }
    }

    /**
     * Get the executor of this chain.
     *
     * @return The executor
     */
    public Function<ExecutionContext<E>, ?> getExecutor() {
        return this.arguments.get(this.arguments.size() - 1).executor();
    }

    @Override
    public String toString() {
        return this.toString(this.getExecutor() != null);
    }

    public String toString(final boolean skipRedirects) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < this.getLength(); i++) {
            ArgumentNode<E, ?> argument = this.getArgument(i);
            if (argument instanceof RedirectNode) {
                if (skipRedirects) continue;
                else out.append("(").append(argument.name()).append(")").append("->");
            } else if (argument.providesArgument()) {
                out.append('<').append(argument.name()).append('>');
            } else {
                out.append(argument.name());
            }
            out.append(' ');
        }
        return out.toString().trim();
    }


    /**
     * A wrapper for an argument that has been matched.
     */
    public static class MatchedArgument {
        private final int cursor;
        private final String match;
        private final Object value;

        private MatchedArgument(final int cursor, final String match, final Object value) {
            this.cursor = cursor;
            this.match = match;
            this.value = value;
        }

        /**
         * @return The cursor position of the start of the match
         */
        public int getCursor() {
            return this.cursor;
        }

        /**
         * @return The matched string
         */
        public String getMatch() {
            return this.match;
        }

        /**
         * @return The parsed value
         */
        public Object getValue() {
            return this.value;
        }
    }

}
