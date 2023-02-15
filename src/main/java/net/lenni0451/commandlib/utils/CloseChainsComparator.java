package net.lenni0451.commandlib.utils;

import net.lenni0451.commandlib.ArgumentChain;
import net.lenni0451.commandlib.exceptions.ChainExecutionException;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;

public class CloseChainsComparator<E> implements Comparator<Map.Entry<ArgumentChain<E>, ChainExecutionException>> {

    public static <E> Map<ArgumentChain<E>, ChainExecutionException> getClosest(final Map<ArgumentChain<E>, ChainExecutionException> in) {
        if (in.isEmpty()) return in;
        Map<ArgumentChain<E>, ChainExecutionException> closest = Util.sortMap(in, new CloseChainsComparator<>());

        int lastWeight = -1;
        Iterator<Map.Entry<ArgumentChain<E>, ChainExecutionException>> it = closest.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<ArgumentChain<E>, ChainExecutionException> entry = it.next();
            int weight = getReasonWeight(entry.getValue().getReason());
            if (lastWeight == -1) lastWeight = weight;
            else if (lastWeight != weight) it.remove();
        }
        return closest;
    }

    private static int getReasonWeight(final ChainExecutionException.Reason reason) {
        switch (reason) {
            case MISSING_SPACE:
            case NO_ARGUMENTS_LEFT:
                return 4;
            case ARGUMENT_PARSE_EXCEPTION:
                return 3;
            case RUNTIME_EXCEPTION:
                return 2;
            case TOO_MANY_ARGUMENTS:
                return 1;
            default:
                throw new IllegalStateException("Unexpected reason value: " + reason);
        }
    }


    @Override
    public int compare(Map.Entry<ArgumentChain<E>, ChainExecutionException> o1, Map.Entry<ArgumentChain<E>, ChainExecutionException> o2) {
        int weight1 = getReasonWeight(o1.getValue().getReason());
        int weight2 = getReasonWeight(o2.getValue().getReason());
        return Integer.compare(weight2, weight1);
    }

}
