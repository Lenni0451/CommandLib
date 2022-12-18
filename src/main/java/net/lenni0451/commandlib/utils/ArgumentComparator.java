package net.lenni0451.commandlib.utils;

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
    };


    public abstract boolean compare(final String a, final String b);

    public abstract boolean compare(final String s, final List<String> list);

}
