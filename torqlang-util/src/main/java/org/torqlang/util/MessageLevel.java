/*
 * Copyright (c) 2024-2026 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torq Lang License v1.0 along with this program.
 * If not, see <http://torq-lang.github.io/licensing/torq-lang-license-v1_0>.
 */

package org.torqlang.util;

/*
 * Message severity levels modeled after Logback's level scheme.
 *
 * Levels are ordered by integer value where higher values indicate more severe messages.
 *
 * Level values use Logback's spacing (multiples of 5,000/10,000) to allow for potential
 * intermediate levels while maintaining compatibility with SLF4J and Logback.
 */
public final class MessageLevel {

    public static final int ERROR_INT = 40_000;
    public static final int WARN_INT = 30_000;
    public static final int INFO_INT = 20_000;
    public static final int DEBUG_INT = 10_000;
    public static final int TRACE_INT = 5_000;

    public static final MessageLevel ERROR = new MessageLevel("ERROR", ERROR_INT);
    public static final MessageLevel WARN = new MessageLevel("WARN", WARN_INT);
    public static final MessageLevel INFO = new MessageLevel("INFO", INFO_INT);
    public static final MessageLevel DEBUG = new MessageLevel("DEBUG", DEBUG_INT);
    public static final MessageLevel TRACE = new MessageLevel("TRACE", TRACE_INT);

    private final String name;
    private final int severity;

    private MessageLevel(String name, int severity) {
        this.name = name;
        this.severity = severity;
    }

    /*
     * Returns the MessageLevel matching the given name (case-insensitive).
     *
     * Throw an IllegalArgumentException if no matching level is found.
     */
    public static MessageLevel parse(String name) {
        String upper = name.toUpperCase();
        return switch (upper) {
            case "ERROR" -> ERROR;
            case "WARN", "WARNING" -> WARN;
            case "INFO" -> INFO;
            case "DEBUG" -> DEBUG;
            case "TRACE" -> TRACE;
            default -> throw new IllegalArgumentException("Unknown level: " + name);
        };
    }

    public static LoggerLevel toLoggerLevel(MessageLevel level) {
        if (level.severity >= ERROR_INT) return LoggerLevel.ERROR;
        if (level.severity >= WARN_INT) return LoggerLevel.WARN;
        if (level.severity >= INFO_INT) return LoggerLevel.INFO;
        if (level.severity >= DEBUG_INT) return LoggerLevel.DEBUG;
        return LoggerLevel.TRACE;
    }

    public String name() {
        return name;
    }

    public int severity() {
        return severity;
    }

    @Override
    public String toString() {
        return name;
    }

}
