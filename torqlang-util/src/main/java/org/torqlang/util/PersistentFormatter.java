/*
 * Copyright (c) 2024-2026 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torq Lang License v1.0 along with this program.
 * If not, see <http://torq-lang.github.io/licensing/torq-lang-license-v1_0>.
 */

package org.torqlang.util;

/*
 * Format log messages with a parsable bracketed prefix and an escaped message.
 *
 * ESCAPE
 * As each character arrives in the stream, the system checks if it is a "special"
 * character (like a newline, tab, or backslash). If so, it outputs the multi-character
 * escape sequence (e.g., \n, \t, or \\).
 *
 * UNESCAPE
 * This implements a two-state machine:
 * 1. Normal State: Output incoming characters. If a backslash (\) is encountered,
 *    switch to Escape State.
 * 2. Escape State: Determine the replacement for the next character. If unknown,
 *    the sequence is preserved (backslash + character) to prevent data loss.
 *
 * The "correctness" of unescaping unknown sequences is defined by whether
 * unescape(escape(message)).equals(message) remains true. This class achieves this:
 *
 * Escape Phase: Only \, \n, \r, and \t are transformed. Any other character (e.g., z)
 * is passed through as-is.
 *
 * Unescape Phase: If the code encounters a sequence it didn't create (like \z), it recognizes
 * that this isn't a "special" sequence it knows how to decode. By outputting \z literally, it
 * ensures that if the original input actually contained the literal characters \ and z, they
 * are not lost or corrupted.
 */
public final class PersistentFormatter implements LoggerFormatter {

    private static final LoggerFormatter SINGLETON = new PersistentFormatter();

    private PersistentFormatter() {
    }

    public static LoggerFormatter get() {
        return SINGLETON;
    }

    /**
     * Escapes message to ensure it remains on a single line.
     * Optimized for 2026 JVMs by using intrinsic checks where possible.
     */
    public static String escape(String message) {
        if (message == null || message.isEmpty()) {
            return message;
        }

        // Fast-path: Check for most common triggers using intrinsic-optimized indexOf
        int first = message.indexOf('\n');
        if (first == -1) {
            first = findFirstEscapable(message);
        }

        if (first == -1) {
            return message;
        }

        int len = message.length();
        // Pre-allocate with slack to avoid resizing on messages with few escapes
        StringBuilder sb = new StringBuilder(len + 16);
        sb.append(message, 0, first);

        for (int i = first; i < len; i++) {
            char c = message.charAt(i);
            switch (c) {
                case '\\' -> sb.append("\\\\");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                default -> sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * Manual scan for characters not covered by the initial indexOf optimization.
     */
    private static int findFirstEscapable(String s) {
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '\\' || c == '\r' || c == '\t') {
                return i;
            }
        }
        return -1;
    }

    /**
     * Reverses escaping. Handles tab consistency bug from previous versions.
     */
    public static String unescape(String message) {
        if (message == null || message.indexOf('\\') == -1) {
            return message;
        }

        int len = message.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = message.charAt(i);
            if (c == '\\' && i + 1 < len) {
                char next = message.charAt(i + 1);
                switch (next) {
                    case '\\' -> sb.append('\\');
                    case 'n' -> sb.append('\n');
                    case 'r' -> sb.append('\r');
                    case 't' -> sb.append('\t');
                    // Fallback to prevent data loss on invalid escape sequences
                    default -> sb.append('\\').append(next);
                }
                i++; // Consume the escaped character
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    @Override
    public final String apply(String level, String caller, String message) {
        // Return a strictly single-line string for persistent logging.
        // The calling handler should append System.lineSeparator() to this result.
        return LoggerFormatter.prefix(level, caller) + " " + escape(message);
    }

}
