package net.lenni0451.commandlib;

import net.lenni0451.commandlib.exceptions.ArgumentParseException;
import net.lenni0451.commandlib.exceptions.ChainExecutionException;
import net.lenni0451.commandlib.nodes.ArgumentNode;
import net.lenni0451.commandlib.utils.StringReader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class ArgumentChain<E> {

    public static <E> List<ArgumentChain<E>> buildChains(final ArgumentNode<E, ?> argument) {
        List<ArgumentChain<E>> chains = new ArrayList<>();

        Map<ArgumentNode<E, ?>, ArgumentChain<E>> branches = new HashMap<>();
        branches.put(argument, new ArgumentChain<>(argument));
        while (!branches.isEmpty()) {
            Map<ArgumentNode<E, ?>, ArgumentChain<E>> newBranches = new HashMap<>();
            for (Map.Entry<ArgumentNode<E, ?>, ArgumentChain<E>> entry : branches.entrySet()) {
                ArgumentNode<E, ?> node = entry.getKey();
                ArgumentChain<E> chain = entry.getValue();

                if (node.executor() != null) {
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

    public int[] getWeights() {
        int[] weights = new int[this.arguments.size()];
        for (int i = 0; i < this.arguments.size(); i++) weights[i] = this.arguments.get(i).weight();
        return weights;
    }

    public ArgumentNode<E, ?> getArgument(final int index) {
        return this.arguments.get(index);
    }

    public List<MatchedArgument> execute(final ExecutionContext<E> context, final StringReader reader) throws ChainExecutionException {
        List<MatchedArgument> out = new ArrayList<>();
        for (int i = 0; i < this.arguments.size(); i++) {
            int cursor = reader.getCursor();
            ArgumentNode<E, ?> argument = this.arguments.get(i);
            boolean isLast = i == this.arguments.size() - 1;
            try {
                Object parsedArgument = argument.parseValue(context, reader);
                out.add(new MatchedArgument(reader.getString().substring(cursor, reader.getCursor()), parsedArgument));
                if (!isLast && !reader.canRead()) {
                    String missingArguments = new ArgumentChain<>(this.arguments.subList(i + 1, this.arguments.size())).toString();
                    throw new ChainExecutionException(ChainExecutionException.Reason.NO_ARGUMENTS_LEFT, i + 1, reader.getCursor(), null, missingArguments);
                } else if (isLast && reader.canRead()) {
                    throw new ChainExecutionException(ChainExecutionException.Reason.TOO_MANY_ARGUMENTS, i, reader.getCursor(), null, reader.readRemaining());
                }
            } catch (ArgumentParseException e) {
                throw new ChainExecutionException(e, i, cursor, argument.name(), reader.getString().substring(cursor, reader.getCursor()));
            } catch (RuntimeException e) {
                throw new ChainExecutionException(e, i, cursor, argument.name(), reader.getString().substring(cursor, reader.getCursor()));
            }
        }
        return out;
    }

    public void populateArguments(final ExecutionContext<E> context, final List<ArgumentChain.MatchedArgument> arguments) {
        for (int i = 0; i < this.arguments.size(); i++) {
            ArgumentNode<E, ?> argumentNode = this.arguments.get(i);
            if (!argumentNode.providesArgument()) continue;
            context.addArgument(argumentNode.name(), arguments.get(i).getValue());
        }
    }

    public Function<ExecutionContext<E>, ?> getExecutor() {
        return this.arguments.get(this.arguments.size() - 1).executor();
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        for (ArgumentNode<E, ?> argument : this.arguments) {
            if (argument.providesArgument()) out.append('<').append(argument.name()).append('>');
            else out.append(argument.name());
            out.append(' ');
        }
        return out.toString().trim();
    }


    public static class MatchedArgument {
        private final String match;
        private final Object value;

        private MatchedArgument(final String match, final Object value) {
            this.match = match;
            this.value = value;
        }

        public String getMatch() {
            return this.match;
        }

        public Object getValue() {
            return this.value;
        }
    }

}
