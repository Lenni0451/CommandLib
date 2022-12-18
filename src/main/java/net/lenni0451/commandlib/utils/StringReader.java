package net.lenni0451.commandlib.utils;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.regex.Pattern;

public class StringReader {

    private static final Pattern STRING_ESCAPE_REPLACEMENT = Pattern.compile("\\\\(.)");
    private static final Pattern INTEGER_NUMBER = Pattern.compile("-?\\d+");
    private static final Pattern DECIMAL_NUMBER = Pattern.compile("-?\\d+[,.]?\\d*");

    private final String string;
    private int cursor = 0;

    public StringReader(@Nonnull final String string) {
        this.string = string;
    }

    public String getString() {
        return this.string;
    }

    public int getCursor() {
        return this.cursor;
    }

    public StringReader setCursor(final int cursor) {
        this.cursor = cursor;
        return this;
    }

    public int length() {
        return this.string.length();
    }

    public int left() {
        return this.string.length() - this.cursor;
    }

    public boolean canRead() {
        return this.canRead(1);
    }

    public boolean canRead(final int length) {
        return this.cursor + length <= this.string.length();
    }

    public StringReader ensureLength(final int length) {
        if (!this.canRead(length)) throw new StringIndexOutOfBoundsException("Cannot read " + length + " chars");
        return this;
    }

    public StringReader require(final char... cs) {
        if (!this.canRead()) throw new IllegalArgumentException("Expected " + Arrays.toString(cs) + " but got <EOL>");
        if (cs.length == 0) return this;
        for (char c : cs) {
            if (this.peek() == c) return this;
        }
        throw new IllegalArgumentException("Expected " + Arrays.toString(cs) + " but got '" + this.peek() + "'");
    }

    public char peek() {
        return this.peek(0);
    }

    public char peek(final int offset) {
        this.ensureLength(1 + offset);
        return this.string.charAt(this.cursor + offset);
    }

    public StringReader skipWhitespace() {
        while (this.canRead() && Character.isWhitespace(this.peek())) this.cursor++;
        return this;
    }

    public StringReader skipRequiredWhitespace() {
        if (!this.canRead()) throw new IllegalArgumentException("Expected whitespace but got <EOL>");
        if (!Character.isWhitespace(this.peek())) throw new IllegalArgumentException("Expected whitespace but got '" + this.peek() + "'");
        this.skipWhitespace();
        return this;
    }

    public StringReader skipRequiredWhitespaceOrEOL() {
        if (!this.canRead()) return this;
        if (!Character.isWhitespace(this.peek())) throw new IllegalArgumentException("Expected whitespace or EOL but got '" + this.peek() + "'");
        this.skipWhitespace();
        return this;
    }

    public StringReader skip() {
        return this.skip(1);
    }

    public StringReader skip(final int length) {
        this.ensureLength(length);
        this.cursor += length;
        return this;
    }

    public char read() {
        return this.string.charAt(this.cursor++);
    }

    public String read(final int length) {
        this.ensureLength(length);
        int start = this.cursor;
        this.cursor += length;
        return this.string.substring(start, this.cursor);
    }

    public String readUntil(final char c) {
        return this.readUntil(c, false);
    }

    public String readUntil(final char c, final boolean allowEscape) {
        int start = this.cursor;
        while (this.canRead()) {
            if (this.peek() == c) break;
            if (allowEscape && this.peek() == '\\') this.cursor++;
            this.cursor++;
        }
        String s = this.string.substring(start, this.cursor);
        if (allowEscape) s = STRING_ESCAPE_REPLACEMENT.matcher(s).replaceAll("$1");
        return s;
    }

    public String readRemaining() {
        return this.read(this.left());
    }

    public String readWord() {
        String word = this.readUntil(' ');
        this.skipWhitespace();
        return word;
    }

    public String readString() {
        char start = this.require('"', '\'').read();
        String s = this.readUntil(start, true);
        this.require(start).skip();
        this.skipWhitespace();
        return s;
    }

    public String readWordOrString() {
        if (this.peek() == '"' || this.peek() == '\'') return this.readString();
        return this.readWord();
    }

    public String readIntegerNumber() {
        String s = this.readWord();
        if (!INTEGER_NUMBER.matcher(s).matches()) throw new IllegalArgumentException("Expected integer but got '" + s + "'");
        return s;
    }

    public String readDecimalNumber() {
        String s = this.readWord();
        if (!DECIMAL_NUMBER.matcher(s).matches()) throw new IllegalArgumentException("Expected decimal number but got '" + s + "'");
        return s.replace(',', '.');
    }

}
