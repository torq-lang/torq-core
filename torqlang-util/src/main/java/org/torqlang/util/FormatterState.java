/*
 * Copyright (c) 2024-2026 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torq Lang License v1.0 along with this program.
 * If not, see <http://torq-lang.github.io/licensing/torq-lang-license-v1_0>.
 */

package org.torqlang.util;

import java.io.IOException;
import java.io.Writer;

public class FormatterState {

    public static final int INLINE_VALUE = -1;
    public static final char NEWLINE = '\n';
    public static final char SPACE = ' ';

    private static final int INDENT_SIZE = 4;

    private final int level;
    private final Writer writer;

    public FormatterState(Writer writer) {
        this(writer, 0);
    }

    private FormatterState(Writer writer, int level) {
        this.writer = writer;
        this.level = level;
    }

    public final void flush() {
        try {
            writer.flush();
        } catch (IOException exc) {
            throw new FormatterError("Cannot flush writer", exc);
        }
    }

    public final FormatterState inline() {
        return level == INLINE_VALUE ? this :
            new FormatterState(writer, INLINE_VALUE);
    }

    public final int level() {
        return level;
    }

    public final FormatterState nextLevel() {
        return level == INLINE_VALUE ? this :
            new FormatterState(writer, level + 1);
    }

    public final void write(char c) {
        try {
            writer.write(c);
        } catch (IOException exc) {
            throw new FormatterError("Cannot write character", exc);
        }
    }

    public final void write(String s) {
        try {
            writer.write(s);
        } catch (IOException exc) {
            throw new FormatterError("Cannot write string", exc);
        }
    }

    public final void writeAfterNewLineAndIdent(String s) {
        writeNewLine();
        writeIndent();
        write(s);
    }

    public final void writeIndent() {
        if (level == 0 || level == INLINE_VALUE) {
            return;
        }
        int totalIndent = level * INDENT_SIZE;
        for (int i = 0; i < totalIndent; i++) {
            write(SPACE);
        }
    }

    public final void writeNewLine() {
        if (level == INLINE_VALUE) {
            write(SPACE);
        } else {
            write(NEWLINE);
        }
    }

    public final void writeNewLineAndIndent() {
        writeNewLine();
        writeIndent();
    }

}
