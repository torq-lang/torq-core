package org.torqlang.util;

public interface LoggerFormatter {
    String apply(String level, String caller, String message);
}
