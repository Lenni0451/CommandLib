package net.lenni0451.commandlib.utils.comparator;

public abstract class ArgumentComparator {

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


    public abstract boolean compare(final String a, final String b);


    public abstract boolean startsWith(final String s, final String b);

    public abstract int compareTo(final String a, final String b);

}
