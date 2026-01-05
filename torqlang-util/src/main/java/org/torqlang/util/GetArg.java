/*
 * Copyright (c) 2024-2026 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torq Lang License v1.0 along with this program.
 * If not, see <http://torq-lang.github.io/licensing/torq-lang-license-v1_0>.
 */

package org.torqlang.util;

import java.util.ArrayList;
import java.util.List;

public final class GetArg {

    /*
     * Return null if the requested option is not found. Otherwise, return its arguments.
     *
     * Throw an IllegalArgumentException if a duplicate option is found.
     */
    public static List<String> get(String option, List<String> args) {
        validateOption(option);
        List<String> argsAtOpt = null;
        int i = 0;
        while (i < args.size()) {
            if (args.get(i).equals(option)) {
                argsAtOpt = new ArrayList<>();
                while (++i < args.size()) {
                    String a = args.get(i);
                    if (a.startsWith("--")) {
                        break;
                    }
                    argsAtOpt.add(a);
                }
                while (i < args.size()) {
                    if (args.get(i).equals(option)) {
                        throw new IllegalArgumentException("Duplicate option error: " + option);
                    }
                    i++;
                }
            } else {
                i++;
            }
        }
        return argsAtOpt != null ? List.copyOf(argsAtOpt) : null;
    }

    public static String getSingle(String option, List<String> args) {
        List<String> argsAtOpt = GetArg.get(option, args);
        if (argsAtOpt == null || argsAtOpt.isEmpty()) {
            return null;
        }
        if (argsAtOpt.size() > 1) {
            throw new IllegalArgumentException("More than one argument found for option: " + option);
        }
        return argsAtOpt.get(0);
    }

    private static void validateOption(String option) {
        if (!option.startsWith("--")) {
            throw new IllegalArgumentException("Expected option to start with '--'");
        }
        String optionWord = option.substring(2);
        char[] optionChars = optionWord.toCharArray();
        for (int i = 0; i < optionChars.length; i++) {
            char c = optionChars[i];
            if (c >= 'a' && c <= 'z') {
                continue;
            }
            if (c >= 'A' && c <= 'Z') {
                continue;
            }
            if (c >= '0' && c <= '9') {
                continue;
            }
            if (c == '-') {
                if (i == optionChars.length - 1) {
                    continue;
                }
                if (optionChars[i + 1] == '-') {
                    throw new IllegalArgumentException("Option cannot contain a '--'");
                } else {
                    continue;
                }
            }
            throw new IllegalArgumentException("Expected option to contain letters, numbers, and single hyphens");
        }
    }

}
