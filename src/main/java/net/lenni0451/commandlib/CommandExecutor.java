package net.lenni0451.commandlib;

import net.lenni0451.commandlib.exceptions.ChainExecutionException;
import net.lenni0451.commandlib.exceptions.CommandNotFoundException;
import net.lenni0451.commandlib.nodes.ArgumentNode;
import net.lenni0451.commandlib.nodes.StringArgumentNode;
import net.lenni0451.commandlib.utils.ArgumentComparator;
import net.lenni0451.commandlib.utils.StringReader;
import net.lenni0451.commandlib.utils.Util;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

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

    public void register(final ArgumentNode<E, ?> argumentNode) {
        if (!(argumentNode instanceof StringArgumentNode)) throw new IllegalArgumentException("Register argument node must be a StringArgumentNode");
        this.register((StringArgumentNode<E>) argumentNode);
    }

    public void register(final StringArgumentNode<E> stringArgumentNode) {
        this.chains.entrySet().removeIf(entry -> this.argumentComparator.compare(entry.getKey().name(), stringArgumentNode.name()));
        this.chains.put(stringArgumentNode, ArgumentChain.buildChains(stringArgumentNode));
    }

    public Set<String> completions(final E executor, final String command) {
        return this.completions(executor, new StringReader(command));
    }

    public Set<String> completions(final E executor, final StringReader reader) {
        Set<String> completions = new HashSet<>();
        if (!reader.canRead()) {
            completions.addAll(this.chains.keySet().stream().map(StringArgumentNode::name).collect(Collectors.toList()));
        } else {
            ExecutionContext<E> context = new ExecutionContext<>(this.argumentComparator, executor);
            Map<ArgumentChain<E>, ChainExecutionException> closeChains = new HashMap<>();
            Map<ArgumentChain<E>, List<ArgumentChain.MatchedArgument>> matchingChains = this.findMatchingChains(closeChains, true, context, reader);

//            for (List<ArgumentChain.MatchedArgument> value : matchingChains.values()) {
//                if (value.isEmpty()) continue;
//                completions.add(value.get(value.size() - 1).getMatch());
//            }
            for (Map.Entry<ArgumentChain<E>, ChainExecutionException> entry : closeChains.entrySet()) {
                ArgumentChain<E> chain = entry.getKey();
                ChainExecutionException exception = entry.getValue();
                int argOffset = 0;
                switch (exception.getReason()) {
                    case MISSING_SPACE:
                        argOffset = 1;
                    case ARGUMENT_PARSE_EXCEPTION:
                    case NO_ARGUMENTS_LEFT:
                        break;
                    default:
                        continue;
                }

                reader.setCursor(exception.getReaderCursor());
                ArgumentNode<E, ?> argument = chain.getArgument(exception.getExecutionIndex() - argOffset);
                String check = reader.peekRemaining();
                Set<String> argumentCompletions = argument.completions(context, reader);
                for (String completion : argumentCompletions) {
                    if (this.argumentComparator.startsWith(completion, check)) completions.add(completion);
                }
            }
        }
        return completions.stream().sorted().collect(Collectors.toCollection(LinkedHashSet::new));
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
        Map<ArgumentChain<E>, List<ArgumentChain.MatchedArgument>> matchingChains = this.findMatchingChains(closeChains, false, context, reader);
        try {
            return this.executeChain(matchingChains, context, reader);
        } catch (CommandNotFoundException e) {
            if (closeChains.isEmpty()) throw e;

            ArgumentChain<E> mostLikelyChain = this.findBestChain(closeChains.keySet());
            Map<ArgumentChain<E>, ChainExecutionException> similarChains = new HashMap<>();
            for (ArgumentChain<E> chain : closeChains.keySet()) {
                if (this.compareChains(mostLikelyChain, chain) == 0 && closeChains.get(mostLikelyChain).getReason().equals(closeChains.get(chain).getReason())) {
                    similarChains.put(chain, closeChains.get(chain));
                }
            }
            throw new CommandNotFoundException(e.getCommand(), Util.cast(similarChains));
        }
    }

    private Map<ArgumentChain<E>, List<ArgumentChain.MatchedArgument>> findMatchingChains(final Map<ArgumentChain<E>, ChainExecutionException> closeChains, final boolean closeMatchLiteral, final ExecutionContext<E> context, final StringReader reader) {
        Map<ArgumentChain<E>, List<ArgumentChain.MatchedArgument>> out = new HashMap<>();
        for (List<ArgumentChain<E>> chains : this.chains.values()) {
            for (ArgumentChain<E> chain : chains) {
                int cursor = reader.getCursor();
                try {
                    List<ArgumentChain.MatchedArgument> arguments = chain.execute(context, reader);
                    out.put(chain, arguments);
                } catch (ChainExecutionException e) {
                    if (e.getExecutionIndex() != 0 || closeMatchLiteral) closeChains.put(chain, e);
                }
                reader.setCursor(cursor);
            }
        }
        return out;
    }

    private <T> T executeChain(final Map<ArgumentChain<E>, List<ArgumentChain.MatchedArgument>> chains, final ExecutionContext<E> context, final StringReader reader) throws CommandNotFoundException {
        if (chains.isEmpty()) throw new CommandNotFoundException(reader.readWordOrString());
        if (chains.size() == 1) {
            ArgumentChain<E> chain = chains.keySet().iterator().next();
            List<ArgumentChain.MatchedArgument> arguments = chains.get(chain);
            chain.populateArguments(context, arguments);
            return (T) chain.getExecutor().apply(context);
        } else {
            Map<ArgumentChain<E>, List<ArgumentChain.MatchedArgument>> bestChainMap = new HashMap<>();
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
