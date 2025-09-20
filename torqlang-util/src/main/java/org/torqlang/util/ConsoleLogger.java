/*
 * Copyright (c) 2025 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torq Lang License v1.0 along with this program.
 * If not, see <http://torq-lang.github.io/licensing/torq-lang-license-v1_0>.
 */

package org.torqlang.util;

import java.util.List;
import java.util.Objects;

import static org.torqlang.util.LoggerTools.formatter;
import static org.torqlang.util.MessageLevel.DEBUG;
import static org.torqlang.util.MessageLevel.ERROR;
import static org.torqlang.util.MessageLevel.INFO;
import static org.torqlang.util.MessageLevel.TRACE;
import static org.torqlang.util.MessageLevel.WARN;

public final class ConsoleLogger implements Logger {

    public final static ConsoleLogger SINGLETON = new ConsoleLogger();

    private ConsoleLogger() {
    }

    @Override
    public void debug(String message) {
        if (LoggerTools.isLogging(DEBUG)) {
            log(formatter().apply(DEBUG.name(), null, message));
        }
    }

    @Override
    public void debug(String caller, String message) {
        if (LoggerTools.isLogging(DEBUG)) {
            log(formatter().apply(DEBUG.name(), caller, message));
        }
    }

    @Override
    public void error(String message) {
        if (LoggerTools.isLogging(ERROR)) {
            log(formatter().apply(ERROR.name(), null, message));
        }
    }

    @Override
    public void error(String caller, String message) {
        if (LoggerTools.isLogging(ERROR)) {
            log(formatter().apply(ERROR.name(), caller, message));
        }
    }

    @Override
    public void info(String message) {
        if (LoggerTools.isLogging(INFO)) {
            log(formatter().apply(INFO.name(), null, message));
        }
    }

    @Override
    public void info(String caller, String message) {
        if (LoggerTools.isLogging(INFO)) {
            log(formatter().apply(INFO.name(), caller, message));
        }
    }

    @Override
    public void log(String formattedMessage) {
        System.out.println(formattedMessage);
    }

    @Override
    public void logAll(List<String> formattedMessages) {
        Objects.requireNonNull(formattedMessages);
        for (String message : formattedMessages) {
            log(message);
        }
    }

    @Override
    public void trace(String message) {
        if (LoggerTools.isLogging(TRACE)) {
            log(formatter().apply(TRACE.name(), null, message));
        }
    }

    @Override
    public void trace(String caller, String message) {
        if (LoggerTools.isLogging(TRACE)) {
            log(formatter().apply(TRACE.name(), caller, message));
        }
    }

    @Override
    public void warn(String message) {
        if (LoggerTools.isLogging(WARN)) {
            log(formatter().apply(WARN.name(), null, message));
        }
    }

    @Override
    public void warn(String caller, String message) {
        if (LoggerTools.isLogging(WARN)) {
            log(formatter().apply(WARN.name(), caller, message));
        }
    }

}
