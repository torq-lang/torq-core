/*
 * Copyright (c) 2024-2026 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torq Lang License v1.0 along with this program.
 * If not, see <http://torq-lang.github.io/licensing/torq-lang-license-v1_0>.
 */

package org.torqlang.util;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;

/*
 * Format log messages with a parsable bracketed prefix.
 *
 * The format uses square brackets around each metadata field to enable deterministic parsing:
 *     [level][time][thread][caller] message
 * Example:
 *     [INFO ][2024-01-15T14:32:45.123][main][MyService] Starting application
 *
 * The bracketed prefix differs from the common space-delimited convention (e.g., time [thread] level logger - message)
 * but provides reliable field extraction via simple pattern matching.
 *
 * A formatter can add custom metadata fields, but values cannot contain brackets.
 *
 * See ConsoleFormatter
 * See PersistentFormatter
 */
public interface LoggerFormatter {

    Clock TICK_MILLIS = Clock.tickMillis(ZoneId.systemDefault());

    static LoggerFormatter console() {
        return ConsoleFormatter.get();
    }

    static LoggerFormatter persistent() {
        return PersistentFormatter.get();
    }

    static String prefix(String level, String caller) {
        LocalDateTime time = LocalDateTime.now(TICK_MILLIS);
        String levelField = String.format("%-5s", level);
        return "[" + levelField + "]" +
            "[" + time + "]" +
            "[" + Thread.currentThread().getName() + "]" +
            (caller != null ? "[" + caller + "]" : "");
    }

    String apply(String level, String caller, String message);

}
