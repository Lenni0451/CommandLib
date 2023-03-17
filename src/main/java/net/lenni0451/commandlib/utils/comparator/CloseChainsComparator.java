package net.lenni0451.commandlib.utils.comparator;

import net.lenni0451.commandlib.ParseResult;
import net.lenni0451.commandlib.exceptions.ArgumentParseException;
import net.lenni0451.commandlib.exceptions.ChainExecutionException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * Compare failed argument chains to provide the most likely wanted chain.
 */
public class CloseChainsComparator {

    /**
     * Sort and filter the given list of failed chains.<br>
     * The most likely wanted chain will be the first one in the list.<br>
     * <br>
     * The sorting is done by the following rules:<br>
     * 1. All chains are sorted by their reason weight.<br>
     * 2. All chains with the lowest reason weight get removed.<br>
     * 3. All chains are sorted by their execution index.<br>
     * 4. All chains with the same execution index are sorted by their length.<br>
     * 5. All chains with the same length are sorted by their argument weight.<br>
     *
     * @param in  The list to sort
     * @param <E> The type of the executor
     * @return The sorted list
     */
    public static <E> List<ParseResult.FailedChain<E>> sortAndFilter(final List<ParseResult.FailedChain<E>> in) {
        if (in.isEmpty()) return in;
        List<ParseResult.FailedChain<E>> out = new ArrayList<>(in);
        out.sort(CloseChainsComparator::sortReasonWeight);
        filterWeight(out);
        out.sort(((Comparator<ParseResult.FailedChain<E>>) CloseChainsComparator::sortExecutionProgress)
                .thenComparing(CloseChainsComparator::sortChainLength)
                .thenComparing(CloseChainsComparator::sortChainWeight));
        filterExecutionIndex(out);
        return out;
    }

    private static <E> void filterWeight(final List<ParseResult.FailedChain<E>> list) {
        int lastWeight = -1;
        Iterator<ParseResult.FailedChain<E>> it = list.iterator();
        while (it.hasNext()) {
            ParseResult.FailedChain<E> entry = it.next();
            int weight = getReasonWeight(entry.getExecutionException());
            if (lastWeight == -1) lastWeight = weight;
            else if (lastWeight != weight) it.remove();
        }
    }

    private static <E> void filterExecutionIndex(final List<ParseResult.FailedChain<E>> list) {
        int lastExecutionIndex = -1;
        Iterator<ParseResult.FailedChain<E>> it = list.iterator();
        while (it.hasNext()) {
            ParseResult.FailedChain<E> entry = it.next();
            int executionIndex = entry.getExecutionException().getExecutionIndex();
            if (lastExecutionIndex == -1) lastExecutionIndex = executionIndex;
            else if (lastExecutionIndex != executionIndex) it.remove();
        }
    }


    private static <E> int sortReasonWeight(final ParseResult.FailedChain<E> c1, final ParseResult.FailedChain<E> c2) {
        return Integer.compare(getReasonWeight(c2.getExecutionException()), getReasonWeight(c1.getExecutionException()));
    }

    private static <E> int sortChainLength(final ParseResult.FailedChain<E> c1, final ParseResult.FailedChain<E> c2) {
        return Integer.compare(c2.getArgumentChain().getLength(), c1.getArgumentChain().getLength());
    }

    private static <E> int sortChainWeight(final ParseResult.FailedChain<E> c1, final ParseResult.FailedChain<E> c2) {
        int[] weights1 = c1.getArgumentChain().getWeights();
        int[] weights2 = c2.getArgumentChain().getWeights();
        for (int i = 0; i < weights1.length; i++) {
            if (weights1[i] > weights2[i]) return -1;
            if (weights1[i] < weights2[i]) return 1;
        }
        return 0;
    }

    private static <E> int sortExecutionProgress(final ParseResult.FailedChain<E> c1, final ParseResult.FailedChain<E> c2) {
        return Integer.compare(c2.getExecutionException().getExecutionIndex(), c1.getExecutionException().getExecutionIndex());
    }

    private static int getReasonWeight(final ChainExecutionException exception) {
        switch (exception.getReason()) {
            case MISSING_SPACE:
            case NO_ARGUMENTS_LEFT:
                return 4;
            case TOO_MANY_ARGUMENTS:
                return 3;
            case ARGUMENT_PARSE_EXCEPTION:
                return 2;
            case RUNTIME_EXCEPTION:
                return 1;
            case REQUIREMENT_FAILED:
                return 0;
            case HANDLED_OTHERWISE:
                if (exception.getCause() instanceof ArgumentParseException) return 3;
                else if (exception.getCause() instanceof RuntimeException) return 2;
                else return 0;
            default:
                throw new IllegalStateException("Unexpected reason value '" + exception + "'");
        }
    }

}
