/*
 * Copyright (c) 2024-2026 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torq Lang License v1.0 along with this program.
 * If not, see <http://torq-lang.github.io/licensing/torq-lang-license-v1_0>.
 */

package org.torqlang.util;

/*
 * Logger levels modeled after Logback's level scheme.
 *
 * Levels are ordered by integer value where higher values indicate more severe messages.
 * A message is loggable if its level value is greater than or equal to the configured threshold.
 *
 * Level values use Logback's spacing (multiples of 5,000/10,000) to allow for potential
 * intermediate levels while maintaining compatibility with SLF4J and Logback.
 */
public final class LoggerLevel {

    public static final int OFF_INT = Integer.MAX_VALUE;
    public static final int ERROR_INT = 40_000;
    public static final int WARN_INT = 30_000;
    public static final int INFO_INT = 20_000;
    public static final int DEBUG_INT = 10_000;
    public static final int TRACE_INT = 5_000;
    public static final int ALL_INT = Integer.MIN_VALUE;

    public static final LoggerLevel OFF = new LoggerLevel("OFF", OFF_INT);
    public static final LoggerLevel ERROR = new LoggerLevel("ERROR", ERROR_INT);
    public static final LoggerLevel WARN = new LoggerLevel("WARN", WARN_INT);
    public static final LoggerLevel INFO = new LoggerLevel("INFO", INFO_INT);
    public static final LoggerLevel DEBUG = new LoggerLevel("DEBUG", DEBUG_INT);
    public static final LoggerLevel TRACE = new LoggerLevel("TRACE", TRACE_INT);
    public static final LoggerLevel ALL = new LoggerLevel("ALL", ALL_INT);

    private final String name;
    private final int severity;

    private LoggerLevel(String name, int severity) {
        this.name = name;
        this.severity = severity;
    }

    public static boolean isLoggableAt(int severity, int threshold) {
        return severity >= threshold;
    }

    public static boolean isLoggableAt(int severity, LoggerLevel threshold) {
        return severity >= threshold.severity;
    }

    public static boolean isLoggableAt(LoggerLevel level, int threshold) {
        return level.severity >= threshold;
    }

    public static boolean isLoggableAt(LoggerLevel level, LoggerLevel threshold) {
        return level.severity >= threshold.severity;
    }

    /*
     * Returns the LoggerLevel matching the given name (case-insensitive).
     *
     * Throw an IllegalArgumentException if no matching level is found
     */
    public static LoggerLevel parse(String name) {
        String upper = name.toUpperCase();
        return switch (upper) {
            case "OFF" -> OFF;
            case "ERROR" -> ERROR;
            case "WARN", "WARNING" -> WARN;
            case "INFO" -> INFO;
            case "DEBUG" -> DEBUG;
            case "TRACE" -> TRACE;
            case "ALL" -> ALL;
            default -> throw new IllegalArgumentException("Unknown level: " + name);
        };
    }

    public final boolean isLoggableAt(LoggerLevel threshold) {
        return isLoggableAt(this.severity, threshold.severity);
    }

    public final boolean isLoggableAt(int threshold) {
        return isLoggableAt(this.severity, threshold);
    }

    public final String name() {
        return name;
    }

    public final int severity() {
        return severity;
    }

    @Override
    public String toString() {
        return name;
    }

}
