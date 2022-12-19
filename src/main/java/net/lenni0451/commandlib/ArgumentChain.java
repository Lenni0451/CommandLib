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

                if (node.executor() != null) chains.add(chain);
                else if (node.children().isEmpty()) throw new IllegalStateException("Chain ended but has no executor: " + chain);
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

    public List<Object> execute(final ExecutionContext<E> context, final StringReader reader) throws ChainExecutionException {
        List<Object> out = new ArrayList<>();
        for (int i = 0; i < this.arguments.size(); i++) {
            ArgumentNode<E, ?> argument = this.arguments.get(i);
            boolean isLast = i == this.arguments.size() - 1;
            try {
                out.add(argument.parseValue(context, reader));
                if (!isLast && !reader.canRead()) throw new ChainExecutionException(ChainExecutionException.Reason.NO_ARGUMENTS_LEFT);
            } catch (ArgumentParseException e) {
                throw new ChainExecutionException(e);
            }
        }
        return out;
    }

    public void populateArguments(final ExecutionContext<E> context, final List<Object> arguments) {
        for (int i = 0; i < this.arguments.size(); i++) context.addArgument(this.arguments.get(i).name(), arguments.get(i));
    }

    public Function<ExecutionContext<E>, ?> getExecutor() {
        return this.arguments.get(this.arguments.size() - 1).executor();
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        for (ArgumentNode<E, ?> argument : this.arguments) out.append(argument.name()).append(" ");
        return out.toString().trim();
    }

}
