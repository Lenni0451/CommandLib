package net.lenni0451.commandlib.utils.comparator;

import net.lenni0451.commandlib.ParseResult;
import net.lenni0451.commandlib.exceptions.ArgumentParseException;
import net.lenni0451.commandlib.exceptions.ChainExecutionException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * Compare failed argument chains by their reason.
 *
 * @param <E> The type of the executor
 */
public class CloseChainsComparator<E> implements Comparator<ParseResult.FailedChain<E>> {

    /**
     * Sort the given list of failed chains by their reason.<br>
     * The most likely wanted chain will be the first one in the list.
     *
     * @param in  The list to sort
     * @param <E> The type of the executor
     * @return The sorted list
     */
    public static <E> List<ParseResult.FailedChain<E>> getClosest(final List<ParseResult.FailedChain<E>> in) {
        if (in.isEmpty()) return in;
        List<ParseResult.FailedChain<E>> out = new ArrayList<>(in);
        out.sort(new CloseChainsComparator<>());

        int lastWeight = -1;
        Iterator<ParseResult.FailedChain<E>> it = out.iterator();
        while (it.hasNext()) {
            ParseResult.FailedChain<E> entry = it.next();
            int weight = getReasonWeight(entry.getExecutionException());
            if (lastWeight == -1) lastWeight = weight;
            else if (lastWeight != weight) it.remove();
        }
        return out;
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


    @Override
    public int compare(ParseResult.FailedChain<E> o1, ParseResult.FailedChain<E> o2) {
        int weight1 = getReasonWeight(o1.getExecutionException());
        int weight2 = getReasonWeight(o2.getExecutionException());
        return Integer.compare(weight2, weight1);
    }

}
