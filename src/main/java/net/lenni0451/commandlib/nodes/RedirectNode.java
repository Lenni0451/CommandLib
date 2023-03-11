package net.lenni0451.commandlib.nodes;

import net.lenni0451.commandlib.ArgumentChain;
import net.lenni0451.commandlib.contexts.CompletionContext;
import net.lenni0451.commandlib.contexts.ExecutionContext;
import net.lenni0451.commandlib.exceptions.ArgumentParseException;
import net.lenni0451.commandlib.utils.StringReader;
import net.lenni0451.commandlib.utils.interfaces.CommandExceptionHandler;
import net.lenni0451.commandlib.utils.interfaces.CompletionsProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * A node that redirects the execution to another node.
 *
 * @param <E> The type of the executor
 */
public class RedirectNode<E> extends ArgumentNode<E, Void> {

    private final ArgumentNode<E, ?> targetNode;
    private List<ArgumentChain<E>> targetChains;

    public RedirectNode(final String name, final ArgumentNode<E, ?> targetNode) {
        super(name);
        this.targetNode = targetNode;
    }

    public RedirectNode(final String name, @Nullable final String description, final ArgumentNode<E, ?> targetNode) {
        super(name, description);
        this.targetNode = targetNode;
    }

    /**
     * @return The target argument node
     */
    public ArgumentNode<E, ?> getTargetNode() {
        return this.targetNode;
    }

    /**
     * @return The dynamically generated list of target chains
     */
    public List<ArgumentChain<E>> getTargetChains() {
        if (this.targetChains == null) this.targetChains = ArgumentChain.buildChains(this.targetNode);
        return this.targetChains;
    }

    @Nonnull
    @Override
    protected Void parseValue(ExecutionContext<E> executionContext, StringReader stringReader) throws ArgumentParseException, RuntimeException {
        throw ArgumentParseException.namedReason(this.name(), "Redirects can't be parsed");
    }

    @Override
    protected void parseCompletions(Set<String> completions, CompletionContext completionContext, ExecutionContext<E> executionContext, StringReader stringReader) {
        throw new UnsupportedOperationException("Redirects can't be parsed");
    }

    @Override
    public List<ArgumentNode<E, ?>> children() {
        return Collections.emptyList();
    }

    @Override
    public boolean providesArgument() {
        return false;
    }

    @Nullable
    @Override
    public Predicate<Void> validator() {
        return null;
    }

    @Override
    public ArgumentNode<E, Void> validator(@Nullable Predicate<Void> validator) {
        throw new UnsupportedOperationException("Redirects can't be parsed");
    }

    @Nullable
    @Override
    public CompletionsProvider<E> completionsProvider() {
        return null;
    }

    @Override
    public ArgumentNode<E, Void> completions(@Nullable CompletionsProvider<E> completionsProvider) {
        throw new UnsupportedOperationException("Redirects can't be parsed");
    }

    @Nullable
    public CommandExceptionHandler<E> exceptionHandler() {
        return null;
    }

    @Override
    public ArgumentNode<E, Void> exceptionHandler(@Nullable CommandExceptionHandler<E> exceptionHandler) {
        throw new UnsupportedOperationException("Redirects can't be parsed");
    }

    @Nullable
    public Function<ExecutionContext<E>, ?> executor() {
        return null;
    }

    @Override
    public ArgumentNode<E, Void> executes(Runnable runnable) {
        throw new UnsupportedOperationException("Redirects can't be parsed");
    }

    @Override
    public ArgumentNode<E, Void> executes(Consumer<ExecutionContext<E>> consumer) {
        throw new UnsupportedOperationException("Redirects can't be parsed");
    }

    @Override
    public ArgumentNode<E, Void> executes(Supplier<?> supplier) {
        throw new UnsupportedOperationException("Redirects can't be parsed");
    }

    @Override
    public ArgumentNode<E, Void> executes(Function<ExecutionContext<E>, ?> function) {
        throw new UnsupportedOperationException("Redirects can't be parsed");
    }

}
