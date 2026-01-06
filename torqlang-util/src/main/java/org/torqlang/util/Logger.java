/*
 * Copyright (c) 2024-2026 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torq Lang License v1.0 along with this program.
 * If not, see <http://torq-lang.github.io/licensing/torq-lang-license-v1_0>.
 */

package org.torqlang.util;

/*
 * The Logger provides a structured way to format and record application events.
 * Use methods named after severity levels to ensure messages are categorized correctly.
 *
 * Logging levels (from highest to lowest severity):
 *     ERROR - Runtime failures that allow the application to continue
 *     WARN  - Unexpected behavior or deprecated API usage
 *     INFO  - Routine events highlighting application progress
 *     DEBUG - Detailed diagnostic data for troubleshooting during development
 *     TRACE - Fine-grained execution flow for deep-dive debugging
 *
 * Each log level provides two methods: one for a simple message and one that accepts
 * an explicit caller identifier (e.g., a class or method name).
 *
 * Log entries are written using a structured prefix:
 *     [level][time-in-millis][thread-name][optional-caller] Message text
 *
 * The 4th field (optional-caller) only appears if a caller string was provided to the
 * logging method.
 */
public interface Logger {

    static void log(Message message, Logger logger) {
        if (message.type().equals(MessageLevel.ERROR.name())) {
            logger.error(message.message());
        } else if (message.type().equals(MessageLevel.WARN.name())) {
            logger.warn(message.message());
        } else if (message.type().equals(MessageLevel.INFO.name())) {
            logger.info(message.message());
        } else if (message.type().equals(MessageLevel.DEBUG.name())) {
            logger.debug(message.message());
        } else {
            logger.trace(message.message());
        }
    }

    void debug(String message);

    void debug(String caller, String message);

    void error(String message);

    void error(String caller, String message);

    void info(String message);

    void info(String caller, String message);

    void trace(String message);

    void trace(String caller, String message);

    void warn(String message);

    void warn(String caller, String message);

}
