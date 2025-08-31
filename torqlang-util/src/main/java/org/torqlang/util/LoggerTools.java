package org.torqlang.util;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;

public final class LoggerTools {
    private static final Clock TICK_MILLIS = Clock.tickMillis(ZoneId.systemDefault());

    private static Logger.Formatter formatter;
    private static MessageLevel loggingLevel = MessageLevel.INFO;

    public static Logger.Formatter formatter() {
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

    public static void setFormatter(Logger.Formatter formatter) {
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
