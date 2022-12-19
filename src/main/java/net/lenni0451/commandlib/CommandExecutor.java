package net.lenni0451.commandlib;

import net.lenni0451.commandlib.exceptions.ChainExecutionException;
import net.lenni0451.commandlib.exceptions.CommandNotFoundException;
import net.lenni0451.commandlib.nodes.StringArgumentNode;
import net.lenni0451.commandlib.utils.ArgumentComparator;
import net.lenni0451.commandlib.utils.StringReader;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CommandExecutor<E> {

    private final ArgumentComparator argumentComparator;
    private final Map<StringArgumentNode<E>, List<ArgumentChain<E>>> chains;

    public CommandExecutor() {
        this(ArgumentComparator.CASE_INSENSITIVE);
    }

    public CommandExecutor(final ArgumentComparator argumentComparator) {
        this.argumentComparator = argumentComparator;
        this.chains = new HashMap<>();
    }

    public void register(final StringArgumentNode<E> stringArgumentNode) {
        this.chains.entrySet().removeIf(entry -> this.argumentComparator.compare(entry.getKey().name(), stringArgumentNode.name()));
        this.chains.put(stringArgumentNode, ArgumentChain.buildChains(stringArgumentNode));
    }

    public List<String> completions(final E executor, final String command) {
        //TODO: Implement completions
        return null;
    }

    @Nullable
    public <T> T execute(final E executor, final String command) throws CommandNotFoundException {
        return this.execute(executor, new StringReader(command));
    }

    @Nullable
    public <T> T execute(final E executor, final StringReader reader) throws CommandNotFoundException {
        if (!reader.canRead()) throw new CommandNotFoundException("<none>");
        ExecutionContext<E> context = new ExecutionContext<>(this.argumentComparator, executor);
        Map<ArgumentChain<E>, ChainExecutionException> closeChains = new HashMap<>();
        Map<ArgumentChain<E>, List<Object>> matchingChains = this.findMatchingChains(closeChains, context, reader);
        try {
            return this.executeChain(matchingChains, context, reader);
        } catch (CommandNotFoundException e) {
            if (closeChains.isEmpty()) throw e;

            ArgumentChain<E> mostLikelyChain = this.findBestChain(closeChains.keySet());
            throw new CommandNotFoundException(e.getCommand(), mostLikelyChain, closeChains.get(mostLikelyChain));
        }
    }

    private Map<ArgumentChain<E>, List<Object>> findMatchingChains(final Map<ArgumentChain<E>, ChainExecutionException> closeChains, final ExecutionContext<E> context, final StringReader reader) {
        Map<ArgumentChain<E>, List<Object>> out = new HashMap<>();
        for (List<ArgumentChain<E>> chains : this.chains.values()) {
            for (ArgumentChain<E> chain : chains) {
                int cursor = reader.getCursor();
                try {
                    List<Object> arguments = chain.execute(context, reader);
                    out.put(chain, arguments);
                } catch (ChainExecutionException e) {
                    if (e.getExecutionIndex() != 0) closeChains.put(chain, e);
                }
                reader.setCursor(cursor);
            }
        }
        return out;
    }

    private <T> T executeChain(final Map<ArgumentChain<E>, List<Object>> chains, final ExecutionContext<E> context, final StringReader reader) throws CommandNotFoundException {
        if (chains.isEmpty()) throw new CommandNotFoundException(reader.readWordOrString());
        if (chains.size() == 1) {
            ArgumentChain<E> chain = chains.keySet().iterator().next();
            List<Object> arguments = chains.get(chain);
            chain.populateArguments(context, arguments);
            return (T) chain.getExecutor().apply(context);
        } else {
            Map<ArgumentChain<E>, List<Object>> bestChainMap = new HashMap<>();
            ArgumentChain<E> bestChain = this.findBestChain(chains.keySet());
            bestChainMap.put(bestChain, chains.get(bestChain));
            return this.executeChain(bestChainMap, context, reader);
        }
    }

    private ArgumentChain<E> findBestChain(final Set<ArgumentChain<E>> chains) {
        return chains.stream().max(this::compareChains).orElseThrow(IllegalStateException::new);
    }

    private int compareChains(final ArgumentChain<E> chain1, final ArgumentChain<E> chain2) {
        if (chain1.getLength() > chain2.getLength()) return 1;
        if (chain1.getLength() < chain2.getLength()) return -1;

        int[] weights1 = chain1.getWeights();
        int[] weights2 = chain2.getWeights();
        for (int i = 0; i < weights1.length; i++) {
            if (weights1[i] > weights2[i]) return 1;
            if (weights1[i] < weights2[i]) return -1;
        }
        return 0;
    }

}
