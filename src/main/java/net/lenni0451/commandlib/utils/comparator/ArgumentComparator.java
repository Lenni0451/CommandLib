package net.lenni0451.commandlib.utils.comparator;

import java.util.List;

public abstract class ArgumentComparator {

    public static final ArgumentComparator CASE_SENSITIVE = new ArgumentComparator() {
        @Override
        public boolean compare(String a, String b) {
            return a.equals(b);
        }

        @Override
        public boolean compare(String s, List<String> list) {
            return list.contains(s);
        }

        @Override
        public boolean startsWith(String s, String b) {
            return s.startsWith(b);
        }
    };
    public static final ArgumentComparator CASE_INSENSITIVE = new ArgumentComparator() {
        @Override
        public boolean compare(String a, String b) {
            return a.equalsIgnoreCase(b);
        }

        @Override
        public boolean compare(String s, List<String> list) {
            for (String l : list) {
                if (s.equalsIgnoreCase(l)) return true;
            }
            return false;
        }

        @Override
        public boolean startsWith(String s, String b) {
            return s.toLowerCase().startsWith(b.toLowerCase());
        }
    };


    public abstract boolean compare(final String a, final String b);

    public abstract boolean compare(final String s, final List<String> list);

    public abstract boolean startsWith(final String s, final String b);

}
