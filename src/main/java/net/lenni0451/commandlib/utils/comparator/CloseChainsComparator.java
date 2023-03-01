package net.lenni0451.commandlib.utils.comparator;

import net.lenni0451.commandlib.ArgumentChain;
import net.lenni0451.commandlib.exceptions.ArgumentParseException;
import net.lenni0451.commandlib.exceptions.ChainExecutionException;
import net.lenni0451.commandlib.utils.Util;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;

/**
 * Compare failed argument chains by their reason.
 *
 * @param <E> The type of the executor
 */
public class CloseChainsComparator<E> implements Comparator<Map.Entry<ArgumentChain<E>, ChainExecutionException>> {

    /**
     * Sort the given map by the fail reason.<br>
     * The most likely wanted chain will be the first one in the map.
     *
     * @param in  The map to sort
     * @param <E> The type of the executor
     * @return The sorted map
     */
    public static <E> Map<ArgumentChain<E>, ChainExecutionException> getClosest(final Map<ArgumentChain<E>, ChainExecutionException> in) {
        if (in.isEmpty()) return in;
        Map<ArgumentChain<E>, ChainExecutionException> closest = Util.sortMap(in, new CloseChainsComparator<>());

        int lastWeight = -1;
        Iterator<Map.Entry<ArgumentChain<E>, ChainExecutionException>> it = closest.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<ArgumentChain<E>, ChainExecutionException> entry = it.next();
            int weight = getReasonWeight(entry.getValue());
            if (lastWeight == -1) lastWeight = weight;
            else if (lastWeight != weight) it.remove();
        }
        return closest;
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
    public int compare(Map.Entry<ArgumentChain<E>, ChainExecutionException> o1, Map.Entry<ArgumentChain<E>, ChainExecutionException> o2) {
        int weight1 = getReasonWeight(o1.getValue());
        int weight2 = getReasonWeight(o2.getValue());
        return Integer.compare(weight2, weight1);
    }

}
