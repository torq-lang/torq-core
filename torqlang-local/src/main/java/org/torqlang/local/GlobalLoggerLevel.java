/*
 * Copyright (c) 2024-2026 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torq Lang License v1.0 along with this program.
 * If not, see <http://torq-lang.github.io/licensing/torq-lang-license-v1_0>.
 */

package org.torqlang.local;

import org.torqlang.util.LoggerLevel;

import java.util.Objects;

public final class GlobalLoggerLevel {

    private static LoggerLevel loggerLevel = LoggerLevel.INFO;

    public static LoggerLevel get() {
        return GlobalLoggerLevel.loggerLevel;
    }

    public static boolean isLoggable(int severity) {
        return LoggerLevel.isLoggableAt(severity, loggerLevel.severity());
    }

    public static boolean isLoggable(LoggerLevel level) {
        return isLoggable(level.severity());
    }

    public static void set(LoggerLevel loggerLevel) {
        Objects.requireNonNull(loggerLevel);
        GlobalLoggerLevel.loggerLevel = loggerLevel;
    }

}
