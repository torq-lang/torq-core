/*
 * Copyright (c) 2024 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torq Lang License v1.0 along with this program.
 * If not, see <http://torq-lang.github.io/licensing/torq-lang-license-v1_0>.
 */

package org.torqlang.util;

import java.util.List;

/*
 * The primary responsibility of a Logger is to log a line of text.
 *
 *     void log(String text);
 *
 * There are several convenience methods centered on the standard logging levels, which allow you to log a line of
 * text in a standard format that includes the log level. The log levels are shown here by increasing level of detail.
 * Note that the level is a "level of detail" and not a severity level. A log configured to show tracing will contain
 * more messages than a log configured to show just errors. Therefore, the TRACE level is a larger ordinal value than
 * the ERROR level.
 *
 *     ERROR - Error events that might still allow the application to continue
 *     WARN  - Potentially harmful situations or deprecated API usage
 *     INFO  - Informational messages highlighting application progress
 *     DEBUG - Fine-grained informational events useful for debugging
 *     TRACE - Most detailed information, typically only for diagnosing issues
 *
 * There exists two logging methods for each log level, one with a caller and one without. For example:
 *
 *     void error(String message);
 *     void error(String caller, String message);
 *
 * When a convenience method is used, it writes a two-part message. The first part consists of 3 or 4 fields
 * enclosed in square brackets. The 4th field only appears if a caller was passed to the logger method.
 *
 *     [level][time-in-millis][thread-name][optional-caller] Message text
 *
 * LoggerTools contains settings for the formatter and logging level.
 */
public interface Logger {

    void debug(String message);

    void debug(String caller, String message);

    void error(String message);

    void error(String caller, String message);

    void info(String message);

    void info(String caller, String message);

    void log(String text);

    void logAll(List<String> text);

    void trace(String message);

    void trace(String caller, String message);

    void warn(String message);

    void warn(String caller, String message);

    interface Formatter {
        String apply(String level, String caller, String message);
    }
}
