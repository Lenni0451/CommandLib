package net.lenni0451.commandlib.utils;

import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * A simple string reader used for parsing arguments.
 */
public class StringReader {

    private static final Pattern STRING_ESCAPE_REPLACEMENT = Pattern.compile("\\\\(.)");
    private static final Pattern INTEGER_NUMBER = Pattern.compile("-?\\d+");
    private static final Pattern DECIMAL_NUMBER = Pattern.compile("-?\\d+[,.]?\\d*");


    private final String string;
    private int cursor = 0;

    public StringReader(final String string) {
        this.string = string;
    }

    /**
     * @return The string that is read
     */
    public String getString() {
        return this.string;
    }

    /**
     * @return The current cursor position
     */
    public int getCursor() {
        return this.cursor;
    }

    /**
     * Set the cursor position.
     *
     * @param cursor The new cursor position
     * @return The string reader
     */
    public StringReader setCursor(final int cursor) {
        this.cursor = cursor;
        return this;
    }

    /**
     * @return The length of the string
     */
    public int length() {
        return this.string.length();
    }

    /**
     * @return The amount of remaining characters
     */
    public int remaining() {
        return this.string.length() - this.cursor;
    }

    /**
     * @return If the reader can read at least one character
     */
    public boolean canRead() {
        return this.canRead(1);
    }

    /**
     * Get if the reader can read the given amount of characters.
     *
     * @param length The amount of characters
     * @return If the reader can read the given amount of characters
     */
    public boolean canRead(final int length) {
        return this.cursor + length <= this.string.length();
    }

    /**
     * Ensure that the reader can read the given amount of characters.
     *
     * @param length The amount of characters
     * @return The string reader
     * @throws StringIndexOutOfBoundsException If the reader cannot read the given amount of characters
     */
    public StringReader ensureLength(final int length) {
        if (!this.canRead(length)) throw new StringIndexOutOfBoundsException("Cannot read " + length + " chars");
        return this;
    }

    /**
     * Require the next character to be in the given array.
     *
     * @param cs The array of characters
     * @return The string reader
     * @throws IllegalStateException If the nothing can be read or the next character is not in the given array
     */
    public StringReader require(final char... cs) {
        if (!this.canRead()) throw new IllegalStateException("Expected " + Arrays.toString(cs) + " but got <EOL>");
        if (cs.length == 0) return this;
        for (char c : cs) {
            if (this.peek() == c) return this;
        }
        throw new IllegalStateException("Expected " + Arrays.toString(cs) + " but got '" + this.peek() + "'");
    }

    /**
     * @return The next character without moving the cursor
     */
    public char peek() {
        return this.peek(0);
    }

    /**
     * Get the character with the given offset without moving the cursor.
     *
     * @param offset The offset
     * @return The character with the given offset
     */
    public char peek(final int offset) {
        this.ensureLength(1 + offset);
        return this.string.charAt(this.cursor + offset);
    }

    /**
     * @return The remaining string without moving the cursor
     */
    public String peekRemaining() {
        return this.string.substring(this.cursor);
    }

    /**
     * Skip all whitespace characters.<br>
     * If nothing can be read, nothing will happen.
     *
     * @return The string reader
     */
    public StringReader skipWhitespace() {
        while (this.canRead() && Character.isWhitespace(this.peek())) this.cursor++;
        return this;
    }

    /**
     * Skip all whitespace characters and ensure that at least one character was skipped.<br>
     * If nothing can be read, an exception will be thrown.
     *
     * @return The string reader
     * @throws IllegalStateException If nothing can be read or the next character is not a whitespace character
     */
    public StringReader skipRequiredWhitespace() {
        if (!this.canRead()) throw new IllegalStateException("Expected whitespace but got <EOL>");
        if (!Character.isWhitespace(this.peek())) throw new IllegalStateException("Expected whitespace but got '" + this.peek() + "'");
        this.skipWhitespace();
        return this;
    }

    /**
     * Skip all whitespace characters and ensure that at least one character was skipped.<br>
     * If nothing can be read, nothing will happen.
     *
     * @return The string reader
     * @throws IllegalStateException If the next character is not a whitespace character
     */
    public StringReader skipRequiredWhitespaceOrEOL() {
        if (!this.canRead()) return this;
        if (!Character.isWhitespace(this.peek())) throw new IllegalStateException("Expected whitespace or EOL but got '" + this.peek() + "'");
        this.skipWhitespace();
        return this;
    }

    /**
     * Move the cursor by one character.
     *
     * @return The string reader
     */
    public StringReader skip() {
        return this.skip(1);
    }

    /**
     * Move the cursor by the given amount of characters.
     *
     * @param length The amount of characters
     * @return The string reader
     * @throws StringIndexOutOfBoundsException If the given amount of characters is not available
     */
    public StringReader skip(final int length) {
        this.ensureLength(length);
        this.cursor += length;
        return this;
    }

    /**
     * @return The next character and move the cursor by one character
     */
    public char read() {
        return this.string.charAt(this.cursor++);
    }

    /**
     * Read the given amount of characters and move the cursor by the given amount of characters.
     *
     * @param length The amount of characters
     * @return The read characters
     * @throws StringIndexOutOfBoundsException If the given amount of characters is not available
     */
    public String read(final int length) {
        this.ensureLength(length);
        int start = this.cursor;
        this.cursor += length;
        return this.string.substring(start, this.cursor);
    }

    /**
     * Read until the given character is found or the end of the string is reached.<br>
     * The character will not be read.
     *
     * @param c The character
     * @return The read characters
     */
    public String readUntil(final char c) {
        return this.readUntil(c, false);
    }

    /**
     * Read until the given character is found or the end of the string is reached.<br>
     * The character will not be read.<br>
     * If the given character is escaped using a backslash, it will be ignored. The backslash will be removed.
     *
     * @param c           The character
     * @param allowEscape Whether to allow escaping
     * @return The read characters
     */
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

    /**
     * @return The remaining string and move the cursor to the end of the string
     */
    public String readRemaining() {
        return this.read(this.remaining());
    }

    /**
     * Read a word until the next whitespace character is found.
     *
     * @return The next word
     */
    public String readWord() {
        return this.readUntil(' ');
    }

    /**
     * Read a string starting with a single or double quote.<br>
     * The quotes will be removed.
     *
     * @return The read string
     * @throws IllegalStateException If the end of the string is reached without finding a closing quote
     */
    public String readString() {
        char start = this.require('"', '\'').read();
        String s = this.readUntil(start, true);
        this.require(start).skip();
        return s;
    }

    /**
     * Read a word or a string if the next character is a single or double quote.
     *
     * @return The read word or string
     * @throws IllegalStateException If the end of the string is reached without finding a closing quote (if a string is read)
     */
    public String readWordOrString() {
        if (this.peek() == '"' || this.peek() == '\'') return this.readString();
        return this.readWord();
    }

    /**
     * Read a word and ensure that it is a valid integer number.
     *
     * @return The unparsed integer number
     */
    public String readIntegerNumber() {
        String s = this.readWord();
        if (!INTEGER_NUMBER.matcher(s).matches()) throw new IllegalStateException("Expected integer but got '" + s + "'");
        return s;
    }

    /**
     * Read a word and ensure that it is a valid decimal number.
     *
     * @return The unparsed decimal number
     */
    public String readDecimalNumber() {
        String s = this.readWord();
        if (!DECIMAL_NUMBER.matcher(s).matches()) throw new IllegalStateException("Expected decimal number but got '" + s + "'");
        return s.replace(',', '.');
    }

}
