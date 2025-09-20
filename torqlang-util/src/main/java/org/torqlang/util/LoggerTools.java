/*
 * Copyright (c) 2025 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torq Lang License v1.0 along with this program.
 * If not, see <http://torq-lang.github.io/licensing/torq-lang-license-v1_0>.
 */

package org.torqlang.util;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;

public final class LoggerTools {
    private static final Clock TICK_MILLIS = Clock.tickMillis(ZoneId.systemDefault());

    private static LoggerFormatter formatter;
    private static MessageLevel loggingLevel = MessageLevel.INFO;

    public static LoggerFormatter formatter() {
        if (formatter == null) {
            formatter = (String level, String caller, String message) -> {
                LocalDateTime time = LocalDateTime.now(TICK_MILLIS);
                String levelField = String.format("%-5s", level);
                String prefix =
                    "[" + levelField + "]" +
                        "[" + time + "]" +
                        "[" + Thread.currentThread().getName() + "]" +
                        (caller != null ? "[" + caller + "]" : "");
                return prefix + " " + message;
            };
        }
        return formatter;
    }

    public static boolean isLogging(MessageLevel level) {
        return isLogging(level.ordinal());
    }

    public static boolean isLogging(int ordinal) {
        return ordinal <= LoggerTools.loggingLevel.ordinal();
    }

    public static MessageLevel loggingLevel() {
        return LoggerTools.loggingLevel;
    }

    public static void setFormatter(LoggerFormatter formatter) {
        if (LoggerTools.formatter != null) {
            throw new IllegalStateException("Formatter is already set");
        }
        LoggerTools.formatter = formatter;
    }

    public static void setLoggingLevel(MessageLevel loggingLevel) {
        Objects.requireNonNull(loggingLevel);
        LoggerTools.loggingLevel = loggingLevel;
    }

}
