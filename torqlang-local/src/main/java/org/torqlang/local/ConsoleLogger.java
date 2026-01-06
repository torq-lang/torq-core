/*
 * Copyright (c) 2024-2026 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torq Lang License v1.0 along with this program.
 * If not, see <http://torq-lang.github.io/licensing/torq-lang-license-v1_0>.
 */

package org.torqlang.local;

import org.torqlang.util.Logger;
import org.torqlang.util.LoggerFormatter;

import static org.torqlang.util.LoggerLevel.DEBUG;
import static org.torqlang.util.LoggerLevel.ERROR;
import static org.torqlang.util.LoggerLevel.INFO;
import static org.torqlang.util.LoggerLevel.TRACE;
import static org.torqlang.util.LoggerLevel.WARN;

public final class ConsoleLogger implements Logger {

    public final static ConsoleLogger SINGLETON = new ConsoleLogger();

    private ConsoleLogger() {
    }

    @Override
    public void debug(String message) {
        if (GlobalLoggerLevel.isLoggable(DEBUG)) {
            log(LoggerFormatter.console().apply(DEBUG.name(), null, message));
        }
    }

    @Override
    public void debug(String caller, String message) {
        if (GlobalLoggerLevel.isLoggable(DEBUG)) {
            log(LoggerFormatter.console().apply(DEBUG.name(), caller, message));
        }
    }

    @Override
    public void error(String message) {
        if (GlobalLoggerLevel.isLoggable(ERROR)) {
            log(LoggerFormatter.console().apply(ERROR.name(), null, message));
        }
    }

    @Override
    public void error(String caller, String message) {
        if (GlobalLoggerLevel.isLoggable(ERROR)) {
            log(LoggerFormatter.console().apply(ERROR.name(), caller, message));
        }
    }

    @Override
    public void info(String message) {
        if (GlobalLoggerLevel.isLoggable(INFO)) {
            log(LoggerFormatter.console().apply(INFO.name(), null, message));
        }
    }

    @Override
    public void info(String caller, String message) {
        if (GlobalLoggerLevel.isLoggable(INFO)) {
            log(LoggerFormatter.console().apply(INFO.name(), caller, message));
        }
    }

    private void log(String formattedMessage) {
        System.out.println(formattedMessage);
    }

    @Override
    public void trace(String message) {
        if (GlobalLoggerLevel.isLoggable(TRACE)) {
            log(LoggerFormatter.console().apply(TRACE.name(), null, message));
        }
    }

    @Override
    public void trace(String caller, String message) {
        if (GlobalLoggerLevel.isLoggable(TRACE)) {
            log(LoggerFormatter.console().apply(TRACE.name(), caller, message));
        }
    }

    @Override
    public void warn(String message) {
        if (GlobalLoggerLevel.isLoggable(WARN)) {
            log(LoggerFormatter.console().apply(WARN.name(), null, message));
        }
    }

    @Override
    public void warn(String caller, String message) {
        if (GlobalLoggerLevel.isLoggable(WARN)) {
            log(LoggerFormatter.console().apply(WARN.name(), caller, message));
        }
    }

}
