/*
 * Copyright (c) 2024-2026 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torq Lang License v1.0 along with this program.
 * If not, see <http://torq-lang.github.io/licensing/torq-lang-license-v1_0>.
 */

package org.torqlang.local;

import org.torqlang.util.LoggerFormatter;
import org.torqlang.util.LoggerLevel;

import static org.torqlang.util.LoggerLevel.DEBUG;
import static org.torqlang.util.LoggerLevel.ERROR;
import static org.torqlang.util.LoggerLevel.INFO;
import static org.torqlang.util.LoggerLevel.TRACE;
import static org.torqlang.util.LoggerLevel.WARN;

public final class ConsoleLogger implements LocalLogger {

    private final static ConsoleLogger GLOBAL = new ConsoleLogger(LoggerLevel.INFO);

    private LoggerLevel threshold;

    private ConsoleLogger(LoggerLevel threshold) {
        this.threshold = threshold;
    }

    public static ConsoleLogger global() {
        return ConsoleLogger.GLOBAL;
    }

    @Override
    public final void debug(String message) {
        if (LoggerLevel.DEBUG.isLoggableAt(threshold)) {
            log(LoggerFormatter.console().apply(DEBUG.name(), null, message));
        }
    }

    @Override
    public final void debug(String caller, String message) {
        if (LoggerLevel.DEBUG.isLoggableAt(threshold)) {
            log(LoggerFormatter.console().apply(DEBUG.name(), caller, message));
        }
    }

    @Override
    public final void error(String message) {
        if (LoggerLevel.ERROR.isLoggableAt(threshold)) {
            log(LoggerFormatter.console().apply(ERROR.name(), null, message));
        }
    }

    @Override
    public final void error(String caller, String message) {
        if (LoggerLevel.ERROR.isLoggableAt(threshold)) {
            log(LoggerFormatter.console().apply(ERROR.name(), caller, message));
        }
    }

    @Override
    public final void info(String message) {
        if (LoggerLevel.INFO.isLoggableAt(threshold)) {
            log(LoggerFormatter.console().apply(INFO.name(), null, message));
        }
    }

    @Override
    public final void info(String caller, String message) {
        if (LoggerLevel.INFO.isLoggableAt(threshold)) {
            log(LoggerFormatter.console().apply(INFO.name(), caller, message));
        }
    }

    private void log(String formattedMessage) {
        System.out.println(formattedMessage);
    }

    @Override
    public final void setThreshold(LoggerLevel threshold) {
        this.threshold = threshold;
    }

    @Override
    public final LoggerLevel threshold() {
        return threshold;
    }

    @Override
    public final void trace(String message) {
        if (LoggerLevel.TRACE.isLoggableAt(threshold)) {
            log(LoggerFormatter.console().apply(TRACE.name(), null, message));
        }
    }

    @Override
    public final void trace(String caller, String message) {
        if (LoggerLevel.TRACE.isLoggableAt(threshold)) {
            log(LoggerFormatter.console().apply(TRACE.name(), caller, message));
        }
    }

    @Override
    public final void warn(String message) {
        if (LoggerLevel.WARN.isLoggableAt(threshold)) {
            log(LoggerFormatter.console().apply(WARN.name(), null, message));
        }
    }

    @Override
    public final void warn(String caller, String message) {
        if (LoggerLevel.WARN.isLoggableAt(threshold)) {
            log(LoggerFormatter.console().apply(WARN.name(), caller, message));
        }
    }

}
