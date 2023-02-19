package net.lenni0451.commandlib.utils.comparator;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * A comparator to sort command completions.<br>
 * Numbers are sorted first, then the rest is sorted by {@link Comparator#naturalOrder()}.
 */
public class CompletionsComparator implements Comparator<String> {

    private static final Pattern INT_PATTERN = Pattern.compile("^[+-]?\\d+$");
    private static final Pattern DECIMAL_PATTERN = Pattern.compile("^[+-]?(?:\\d+(\\.\\d*)?|\\d*\\.\\d+)$");
    private static final Comparator<BigInteger> BIG_INTEGER_COMPARATOR = BigInteger::compareTo;
    private static final Comparator<BigDecimal> BIG_DECIMAL_COMPARATOR = BigDecimal::compareTo;


    private final Map<String, BigInteger> bigIntegerCache = new HashMap<>();
    private final Map<String, BigDecimal> bigDecimalCache = new HashMap<>();
    private final ArgumentComparator argumentComparator;

    public CompletionsComparator(final ArgumentComparator argumentComparator) {
        this.argumentComparator = argumentComparator;
    }

    @Override
    public int compare(String s1, String s2) {
        Integer result = this.compareNumber(s1, s2);
        if (result != null) return result;
        return this.argumentComparator.compareTo(s1, s2);
    }

    private Integer compareNumber(final String s1, final String s2) {
        NumberType t1 = this.getNumberType(s1);
        NumberType t2 = this.getNumberType(s2);
        if (t1 == null || t2 == null) return null;

        if (NumberType.INT.equals(t1) && NumberType.INT.equals(t2)) {
            return BIG_INTEGER_COMPARATOR.compare(this.bigIntegerCache.computeIfAbsent(s1, BigInteger::new), this.bigIntegerCache.computeIfAbsent(s2, BigInteger::new));
        } else {
            return BIG_DECIMAL_COMPARATOR.compare(this.bigDecimalCache.computeIfAbsent(s1, BigDecimal::new), this.bigDecimalCache.computeIfAbsent(s2, BigDecimal::new));
        }
    }

    private NumberType getNumberType(final String number) {
        if (INT_PATTERN.matcher(number).matches()) return NumberType.INT;
        if (DECIMAL_PATTERN.matcher(number).matches()) return NumberType.DECIMAL;
        return null;
    }


    private enum NumberType {
        INT,
        DECIMAL
    }

}
