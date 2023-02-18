package net.lenni0451.commandlib;

import net.lenni0451.commandlib.contexts.CompletionContext;
import net.lenni0451.commandlib.contexts.ExecutionContext;
import net.lenni0451.commandlib.exceptions.ChainExecutionException;
import net.lenni0451.commandlib.exceptions.CommandNotFoundException;
import net.lenni0451.commandlib.nodes.ArgumentNode;
import net.lenni0451.commandlib.nodes.StringNode;
import net.lenni0451.commandlib.utils.ArgumentComparator;
import net.lenni0451.commandlib.utils.CloseChainsComparator;
import net.lenni0451.commandlib.utils.StringReader;
import net.lenni0451.commandlib.utils.Util;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class CommandExecutor<E> {

    private final ArgumentComparator argumentComparator;
    private final Map<StringNode<E>, List<ArgumentChain<E>>> chains;

    public CommandExecutor() {
        this(ArgumentComparator.CASE_INSENSITIVE);
    }

    public CommandExecutor(final ArgumentComparator argumentComparator) {
        this.argumentComparator = argumentComparator;
        this.chains = new HashMap<>();
    }

    public void register(final ArgumentNode<E, ?> argumentNode) {
        if (!(argumentNode instanceof StringNode)) throw new IllegalArgumentException("Register argument node must be a StringArgumentNode");
        this.register((StringNode<E>) argumentNode);
    }

    public void register(final StringNode<E> stringNode) {
        this.chains.entrySet().removeIf(entry -> this.argumentComparator.compare(entry.getKey().name(), stringNode.name()));
        this.chains.put(stringNode, ArgumentChain.buildChains(stringNode));
    }

    public Set<String> completions(final E executor, final String command) {
        return this.completions(executor, new StringReader(command));
    }

    public Set<String> completions(final E executor, final StringReader reader) {
        Set<String> completions = new HashSet<>();
        if (!reader.canRead()) {
            completions.addAll(this.chains.keySet().stream().map(StringNode::name).collect(Collectors.toList()));
        } else {
            ExecutionContext<E> executionContext = new ExecutionContext<>(this.argumentComparator, executor, false);
            Map<ArgumentChain<E>, ChainExecutionException> closeChains = new HashMap<>();
            Map<ArgumentChain<E>, List<ArgumentChain.MatchedArgument>> matchingChains = this.findMatchingChains(closeChains, true, executionContext, reader);

            for (Map.Entry<ArgumentChain<E>, List<ArgumentChain.MatchedArgument>> entry : matchingChains.entrySet()) {
                if (entry.getValue().isEmpty()) continue;
                ArgumentChain<E> chain = entry.getKey();
                List<ArgumentChain.MatchedArgument> matchedArguments = entry.getValue();

                CompletionContext completionContext = new CompletionContext();
                ArgumentChain.MatchedArgument match = matchedArguments.get(matchedArguments.size() - 1);
                ArgumentNode<E, ?> argument = chain.getArgument(matchedArguments.size() - 1);
                reader.setCursor(match.getCursor());
                String check = reader.peekRemaining();
                Set<String> argumentCompletions = argument.completions(completionContext, executionContext, reader);
                for (String completion : argumentCompletions) {
                    if (this.argumentComparator.startsWith(completion, check)) completions.add(completion.substring(completionContext.getCompletionsTrim()));
                }
            }
            for (Map.Entry<ArgumentChain<E>, ChainExecutionException> entry : closeChains.entrySet()) {
                ArgumentChain<E> chain = entry.getKey();
                ChainExecutionException exception = entry.getValue();
                int argOffset = 0;
                if (ChainExecutionException.Reason.MISSING_SPACE.equals(exception.getReason())) argOffset = 1;

                CompletionContext completionContext = new CompletionContext();
                reader.setCursor(exception.getReaderCursor());
                ArgumentNode<E, ?> argument = chain.getArgument(exception.getExecutionIndex() - argOffset);
                String check = reader.peekRemaining();
                Set<String> argumentCompletions = argument.completions(completionContext, executionContext, reader);
                for (String completion : argumentCompletions) {
                    if (this.argumentComparator.startsWith(completion, check)) completions.add(completion.substring(completionContext.getCompletionsTrim()));
                }
            }
        }
        return completions
                .stream()
                .sorted()
                .map(s -> {
                    if (s.contains(" ")) return "\"" + s.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
                    else return s;
                })
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Nullable
    public <T> T execute(final E executor, final String command) throws CommandNotFoundException {
        return this.execute(executor, new StringReader(command));
    }

    @Nullable
    public <T> T execute(final E executor, final StringReader reader) throws CommandNotFoundException {
        if (!reader.canRead()) throw new CommandNotFoundException("<none>");
        ExecutionContext<E> executionContext = new ExecutionContext<>(this.argumentComparator, executor, true);
        Map<ArgumentChain<E>, ChainExecutionException> closeChains = new HashMap<>();
        Map<ArgumentChain<E>, List<ArgumentChain.MatchedArgument>> matchingChains = this.findMatchingChains(closeChains, false, executionContext, reader);
        try {
            return this.executeChain(matchingChains, executionContext, reader);
        } catch (CommandNotFoundException e) {
            if (closeChains.isEmpty()) throw e;

            closeChains = CloseChainsComparator.getClosest(closeChains);
            closeChains = Util.sortMap(closeChains, (o1, o2) -> this.compareChains(o1.getKey(), o2.getKey()));
            throw new CommandNotFoundException(e.getCommand(), Util.cast(closeChains));
        }
    }

    private Map<ArgumentChain<E>, List<ArgumentChain.MatchedArgument>> findMatchingChains(final Map<ArgumentChain<E>, ChainExecutionException> closeChains, final boolean closeMatchLiteral, final ExecutionContext<E> executionContext, final StringReader reader) {
        Map<ArgumentChain<E>, List<ArgumentChain.MatchedArgument>> out = new HashMap<>();
        for (List<ArgumentChain<E>> chains : this.chains.values()) {
            for (ArgumentChain<E> chain : chains) {
                int cursor = reader.getCursor();
                try {
                    List<ArgumentChain.MatchedArgument> arguments = chain.parse(executionContext, reader);
                    out.put(chain, arguments);
                } catch (ChainExecutionException e) {
                    if (e.getExecutionIndex() != 0 || closeMatchLiteral) closeChains.put(chain, e);
                }
                reader.setCursor(cursor);
            }
        }
        return out;
    }

    private <T> T executeChain(final Map<ArgumentChain<E>, List<ArgumentChain.MatchedArgument>> chains, final ExecutionContext<E> executionContext, final StringReader reader) throws CommandNotFoundException {
        if (chains.isEmpty()) {
            throw new CommandNotFoundException(reader.readWordOrString());
        } else if (chains.size() == 1) {
            ArgumentChain<E> chain = chains.keySet().iterator().next();
            List<ArgumentChain.MatchedArgument> arguments = chains.get(chain);
            chain.populateArguments(executionContext, arguments);
            return (T) chain.getExecutor().apply(executionContext);
        } else {
            Map<ArgumentChain<E>, List<ArgumentChain.MatchedArgument>> bestChainMap = new HashMap<>();
            ArgumentChain<E> bestChain = this.findBestChain(chains.keySet());
            bestChainMap.put(bestChain, chains.get(bestChain));
            return this.executeChain(bestChainMap, executionContext, reader);
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
