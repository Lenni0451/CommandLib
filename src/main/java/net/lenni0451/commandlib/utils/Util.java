package net.lenni0451.commandlib.utils;

import net.lenni0451.commandlib.utils.interfaces.ArgumentRequirement;
import net.lenni0451.commandlib.utils.interfaces.ThrowingSupplier;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Some useful methods required by the command lib.
 */
public class Util {

    /**
     * A pattern to check if a string is an integer number.
     */
    public static final Pattern INT_PATTERN = Pattern.compile("^[+-]?\\d+$");
    /**
     * A pattern to check if a string is a decimal number.
     */
    public static final Pattern DECIMAL_PATTERN = Pattern.compile("^[+-]?(?:\\d+(\\.\\d*)?|\\d*\\.\\d+)$");

    /**
     * Get an optional from a throwing supplier.<br>
     * If the supplier throws an exception the optional will be empty.
     *
     * @param supplier The supplier
     * @param <T>      The type of the optional
     * @return The optional
     */
    public static <T> Optional<T> ofThrowing(final ThrowingSupplier<T> supplier) {
        try {
            return Optional.ofNullable(supplier.get());
        } catch (Throwable t) {
            return Optional.empty();
        }
    }

    /**
     * Cast anything to anything else.<br>
     * Be aware of class cast exceptions.
     *
     * @param o   The object to cast
     * @param <T> The type to cast to
     * @return The casted object
     * @throws ClassCastException If the cast is not possible
     */
    public static <T> T cast(final Object o) {
        return (T) o;
    }

    /**
     * Sort a map by a comparator.
     *
     * @param map        The map to sort
     * @param comparator The comparator to sort by
     * @param <K>        The key type
     * @param <V>        The value type
     * @return The sorted map
     */
    public static <K, V> LinkedHashMap<K, V> sortMap(final Map<K, V> map, final Comparator<Map.Entry<K, V>> comparator) {
        return map.entrySet().stream().sorted(comparator).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (k, v) -> v, LinkedHashMap::new));
    }

    /**
     * Prepend all strings in a set with a prefix.<br>
     * The set will be cleared and filled with the prepended strings.
     *
     * @param set    The set to prepend
     * @param prefix The prefix to prepend
     */
    public static void prepend(final Set<String> set, final String prefix) {
        Set<String> prepended = new HashSet<>();
        for (String s : set) prepended.add(prefix + s);
        set.clear();
        set.addAll(prepended);
    }

    /**
     * Beautify the name of an enum value.<br>
     * Example: "MY_ENUM_VALUE" -{@literal >} "My Enum Value"
     *
     * @param value     The enum value
     * @param addSpaces If spaces should be added
     * @return The beautified name
     */
    public static String beautify(final Enum<?> value, final boolean addSpaces) {
        String[] split = value.name().split("_");
        StringBuilder builder = new StringBuilder();
        for (String s : split) {
            builder.append(s.substring(0, 1).toUpperCase()).append(s.substring(1).toLowerCase());
            if (addSpaces) builder.append(" ");
        }
        return builder.toString().trim();
    }

    /**
     * And chain multiple argument requirements.<br>
     * If one of the requirements fails the remaining requirements will not be checked.
     *
     * @param predicates The predicates to check
     * @param <E>        The type of the predicate
     * @return The predicate
     */
    public static <E> ArgumentRequirement<E> and(final ArgumentRequirement<E>... predicates) {
        return executionContext -> {
            for (ArgumentRequirement<E> requirement : predicates) {
                if (!requirement.test(executionContext)) return false;
            }
            return true;
        };
    }

}
