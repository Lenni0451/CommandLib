package net.lenni0451.commandlib.utils;

import net.lenni0451.commandlib.utils.interfaces.ThrowingSupplier;

import java.util.*;
import java.util.stream.Collectors;

public class Util {

    public static <T> Optional<T> ofThrowing(final ThrowingSupplier<T> supplier) {
        try {
            return Optional.ofNullable(supplier.get());
        } catch (Throwable t) {
            return Optional.empty();
        }
    }

    public static <T> T cast(final Object o) {
        return (T) o;
    }

    public static <K, V> LinkedHashMap<K, V> sortMap(final Map<K, V> map, final Comparator<Map.Entry<K, V>> comparator) {
        return map.entrySet().stream().sorted(comparator).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (k, v) -> v, LinkedHashMap::new));
    }

    public static void prepend(final Set<String> set, final String prefix) {
        Set<String> prepended = new HashSet<>();
        for (String s : set) prepended.add(prefix + s);
        set.clear();
        set.addAll(prepended);
    }

}
