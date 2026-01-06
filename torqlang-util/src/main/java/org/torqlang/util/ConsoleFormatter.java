/*
 * Copyright (c) 2024-2026 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torq Lang License v1.0 along with this program.
 * If not, see <http://torq-lang.github.io/licensing/torq-lang-license-v1_0>.
 */

package org.torqlang.util;

/*
 * Format log messages with a parsable bracketed prefix.
 */
public final class ConsoleFormatter implements LoggerFormatter {

    private static final LoggerFormatter SINGLETON = new ConsoleFormatter();

    private ConsoleFormatter() {
    }

    public static LoggerFormatter get() {
        return SINGLETON;
    }

    @Override
    public final String apply(String level, String caller, String message) {
        return LoggerFormatter.prefix(level, caller) + " " + message;
    }

}
