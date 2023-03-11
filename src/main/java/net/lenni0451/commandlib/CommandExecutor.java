package net.lenni0451.commandlib;

import net.lenni0451.commandlib.contexts.CompletionContext;
import net.lenni0451.commandlib.contexts.ExecutionContext;
import net.lenni0451.commandlib.exceptions.ChainExecutionException;
import net.lenni0451.commandlib.exceptions.CommandExecutionException;
import net.lenni0451.commandlib.nodes.ArgumentNode;
import net.lenni0451.commandlib.nodes.RedirectNode;
import net.lenni0451.commandlib.nodes.StringNode;
import net.lenni0451.commandlib.utils.StringReader;
import net.lenni0451.commandlib.utils.Util;
import net.lenni0451.commandlib.utils.comparator.ArgumentComparator;
import net.lenni0451.commandlib.utils.comparator.CloseChainsComparator;
import net.lenni0451.commandlib.utils.comparator.CompletionsComparator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The handler for all registered commands.
 *
 * @param <E> The type of the executor
 */
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

    /**
     * Register an argument node.<br>
     * The argument node must be a {@link StringNode}.<br>
     * This method just exists for convenience. See {@link #register(StringNode)}.
     *
     * @param argumentNode The argument node
     */
    public void register(final ArgumentNode<E, ?> argumentNode) {
        if (!(argumentNode instanceof StringNode)) throw new IllegalArgumentException("Register argument node must be a StringArgumentNode");
        this.register((StringNode<E>) argumentNode);
    }

    /**
     * Register a string argument node.
     *
     * @param stringNode The string argument node
     */
    public void register(final StringNode<E> stringNode) {
        this.chains.entrySet().removeIf(entry -> this.argumentComparator.compare(entry.getKey().name(), stringNode.name()));
        this.chains.put(stringNode, ArgumentChain.buildChains(stringNode));
    }

    /**
     * Get completions for the given command input.
     *
     * @param executor The executor
     * @param command  The command input
     * @return The sorted completions
     */
    public Set<Completion> completions(@Nonnull final E executor, @Nonnull final String command) {
        return this.completions(executor, new StringReader(command));
    }

    /**
     * Get completions for the given command input.
     *
     * @param executor The executor
     * @param reader   The string reader
     * @return The sorted completions
     */
    public Set<Completion> completions(@Nonnull final E executor, @Nonnull final StringReader reader) {
        Set<Completion> completions = new HashSet<>();
        if (!reader.canRead()) {
            completions.addAll(this.chains.keySet().stream().map(StringNode::name).map(n -> new Completion(0, n)).collect(Collectors.toList()));
        } else {
            ExecutionContext<E> executionContext = new ExecutionContext<>(this.argumentComparator, executor, false);
            ParseResult<E> parseResult = this.parseChains(executionContext, reader);

            for (ParseResult.ParsedChain<E> parsedChain : parseResult.getParsedChains()) {
                if (parsedChain.getMatchedArguments().isEmpty()) continue;
                ArgumentChain<E> chain = parsedChain.getArgumentChain();
                List<ArgumentChain.MatchedArgument> matchedArguments = parsedChain.getMatchedArguments();

                CompletionContext completionContext = new CompletionContext();
                ArgumentChain.MatchedArgument match = matchedArguments.get(matchedArguments.size() - 1);
                ArgumentNode<E, ?> argument = chain.getArgument(matchedArguments.size() - 1);
                reader.setCursor(match.getCursor());
                String check = reader.peekRemaining();
                Set<String> argumentCompletions = argument.parseCompletions(completionContext, executionContext, reader);
                for (String completion : argumentCompletions) {
                    int trim = completionContext.getCompletionsTrim();
                    if (this.argumentComparator.startsWith(completion, check.substring(trim))) completions.add(new Completion(match.getCursor() + trim, completion));
                }
            }
            for (ParseResult.FailedChain<E> failedChain : parseResult.getFailedChains()) {
                ArgumentChain<E> chain = failedChain.getArgumentChain();
                ChainExecutionException exception = failedChain.getExecutionException();
                if (ChainExecutionException.Reason.REQUIREMENT_FAILED.equals(exception.getReason())) continue;

                CompletionContext completionContext = new CompletionContext();
                reader.setCursor(exception.getReaderCursor());
                ArgumentNode<E, ?> argument = chain.getArgument(exception.getExecutionIndex());
                while (argument instanceof RedirectNode) argument = ((RedirectNode<E>) argument).getTargetNode();
                String check = reader.peekRemaining();
                Set<String> argumentCompletions = argument.parseCompletions(completionContext, executionContext, reader);
                for (String completion : argumentCompletions) {
                    int trim = completionContext.getCompletionsTrim();
                    if (this.argumentComparator.startsWith(completion, check.substring(trim))) completions.add(new Completion(exception.getReaderCursor() + trim, completion));
                }
            }
        }
        return completions
                .stream()
                .sorted(new CompletionsComparator(this.argumentComparator))
                .map(s -> {
                    if (s.getCompletion().contains(" ")) return new Completion(s.getStart(), "\"" + s.getCompletion().replace("\\", "\\\\").replace("\"", "\\\"") + "\"");
                    else return s;
                })
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    /**
     * Execute the given command input.
     *
     * @param executor The executor
     * @param command  The command input
     * @param <T>      The return type of the executed command
     * @return The return value of the executed command
     * @throws CommandExecutionException If the command execution failed
     */
    @Nullable
    public <T> T execute(@Nonnull final E executor, @Nonnull final String command) throws CommandExecutionException {
        return this.execute(executor, new StringReader(command));
    }

    /**
     * Execute the given command input.
     *
     * @param executor The executor
     * @param reader   The string reader
     * @param <T>      The return type of the executed command
     * @return The return value of the executed command
     * @throws CommandExecutionException If the command execution failed
     */
    @Nullable
    public <T> T execute(@Nonnull final E executor, @Nonnull final StringReader reader) throws CommandExecutionException {
        if (!reader.canRead()) throw new CommandExecutionException("<none>");
        ExecutionContext<E> executionContext = new ExecutionContext<>(this.argumentComparator, executor, true);
        ParseResult<E> parseResult = this.parseChains(executionContext, reader);
        try {
            return this.executeChain(parseResult, executionContext, reader);
        } catch (CommandExecutionException e) {
            if (parseResult.getFailedChains().isEmpty()) throw e;

            List<ParseResult.FailedChain<E>> closeChains = CloseChainsComparator.getClosest(parseResult.getFailedChains());
            closeChains.sort((f1, f2) -> this.compareChains(f1.getArgumentChain(), f2.getArgumentChain()));
            throw new CommandExecutionException(e.getCommand(), Util.cast(closeChains));
        }
    }

    private ParseResult<E> parseChains(final ExecutionContext<E> executionContext, final StringReader reader) {
        List<ArgumentChain<E>> chains = new ArrayList<>();
        for (List<ArgumentChain<E>> nodeChains : this.chains.values()) chains.addAll(nodeChains);
        return this.parseChains(chains, executionContext, reader);
    }

    private ParseResult<E> parseChains(final List<ArgumentChain<E>> chains, final ExecutionContext<E> executionContext, final StringReader reader) {
        List<ParseResult.ParsedChain<E>> parsedChains = new ArrayList<>();
        List<ParseResult.FailedChain<E>> failedChains = new ArrayList<>();
        int cursor = reader.getCursor();
        for (ArgumentChain<E> chain : chains) {
            reader.setCursor(cursor);
            try {
                List<ArgumentChain.MatchedArgument> matchedArguments = chain.parse(executionContext, reader);
                if (chain.getArgument(chain.getLength() - 1) instanceof RedirectNode) {
                    RedirectNode<E> redirectNode = (RedirectNode<E>) chain.getArgument(chain.getLength() - 1);
                    ParseResult<E> redirectResult = this.parseChains(redirectNode.getTargetChains(), executionContext, reader);
                    for (ParseResult.ParsedChain<E> parsedChain : redirectResult.getParsedChains()) {
                        matchedArguments.addAll(parsedChain.getMatchedArguments());
                        parsedChains.add(new ParseResult.ParsedChain<>(ArgumentChain.merge(chain, parsedChain.getArgumentChain()), matchedArguments));
                    }
                    for (ParseResult.FailedChain<E> failedChain : redirectResult.getFailedChains()) {
                        ChainExecutionException mergedException = new ChainExecutionException(failedChain.getExecutionException(), chain.getLength());
                        failedChains.add(new ParseResult.FailedChain<>(ArgumentChain.merge(chain, failedChain.getArgumentChain()), mergedException));
                    }
                } else {
                    parsedChains.add(new ParseResult.ParsedChain<>(chain, matchedArguments));
                }
            } catch (ChainExecutionException e) {
                if (e.getExecutionIndex() == 0) {
                    reader.setCursor(e.getReaderCursor());
                    String word = reader.readWordOrString();
                    if (!this.argumentComparator.startsWith(chain.getArgument(0).name(), word)) continue;
                }
                failedChains.add(new ParseResult.FailedChain<>(chain, e));
            }
        }
        reader.setCursor(cursor);
        return new ParseResult<>(parsedChains, failedChains);
    }

    private <T> T executeChain(final ParseResult<E> parseResult, final ExecutionContext<E> executionContext, final StringReader reader) throws CommandExecutionException {
        if (parseResult.getParsedChains().isEmpty()) {
            throw new CommandExecutionException(reader.readWordOrString());
        } else if (parseResult.getParsedChains().size() == 1) {
            ParseResult.ParsedChain<E> chain = parseResult.getParsedChains().get(0);
            List<ArgumentChain.MatchedArgument> arguments = chain.getMatchedArguments();
            chain.getArgumentChain().populateArguments(executionContext, arguments);
            return (T) chain.getArgumentChain().getExecutor().apply(executionContext);
        } else {
            List<ParseResult.ParsedChain<E>> parsedChains = new ArrayList<>();
            ParseResult.ParsedChain<E> bestChain = this.findBestChain(parseResult.getParsedChains());
            parsedChains.add(bestChain);
            return this.executeChain(new ParseResult<>(parsedChains, parseResult.getFailedChains()), executionContext, reader);
        }
    }

    private ParseResult.ParsedChain<E> findBestChain(final List<ParseResult.ParsedChain<E>> chains) {
        return chains.stream().max((p1, p2) -> this.compareChains(p1.getArgumentChain(), p2.getArgumentChain())).orElseThrow(IllegalStateException::new);
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
