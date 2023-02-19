package net.lenni0451.commandlib.utils.comparator;

/**
 * Used to compare strings in a case-sensitive or case-insensitive way.
 */
public abstract class ArgumentComparator {

    /**
     * A case-sensitive comparator.
     */
    public static final ArgumentComparator CASE_SENSITIVE = new ArgumentComparator() {
        @Override
        public boolean compare(String a, String b) {
            return a.equals(b);
        }

        @Override
        public boolean startsWith(String s, String b) {
            return s.startsWith(b);
        }

        @Override
        public int compareTo(String a, String b) {
            return a.compareTo(b);
        }
    };
    /**
     * A case-insensitive comparator.
     */
    public static final ArgumentComparator CASE_INSENSITIVE = new ArgumentComparator() {
        @Override
        public boolean compare(String a, String b) {
            return a.equalsIgnoreCase(b);
        }

        @Override
        public boolean startsWith(String s, String b) {
            return s.toLowerCase().startsWith(b.toLowerCase());
        }

        @Override
        public int compareTo(String a, String b) {
            return a.compareToIgnoreCase(b);
        }
    };


    /**
     * Compare if two strings are equal.
     *
     * @param a The first string
     * @param b The second string
     * @return If the strings are equal
     */
    public abstract boolean compare(final String a, final String b);

    /**
     * Check if string a starts with string b.
     *
     * @param s The string to check
     * @param b The string to check for
     * @return If string a starts with string b
     */
    public abstract boolean startsWith(final String s, final String b);

    /**
     * Compare two strings.
     *
     * @param a The first string
     * @param b The second string
     * @return The result of {@link String#compareTo(String)}
     */
    public abstract int compareTo(final String a, final String b);

}
