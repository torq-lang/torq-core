/*
 * Copyright (c) 2025 Torqware LLC. All rights reserved.
 *
 * You should have received a copy of the Torq Lang License v1.0 along with this program.
 * If not, see <http://torq-lang.github.io/licensing/torq-lang-license-v1_0>.
 */

package org.torqlang.util;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestGetArg {

    @Test
    public void test01() {
        String testOption = "--notfound";
        List<String> mainArgs = List.of();
        List<String> optionArgs = GetArg.get(testOption, mainArgs);
        assertNull(optionArgs);
    }

    @Test
    public void test02() {
        // An option that does not start with a double hyphen is an error
        String testOption = "-invalidOption";
        List<String> mainArgs = List.of(testOption);
        IllegalArgumentException exc = assertThrows(IllegalArgumentException.class,
            () -> GetArg.get(testOption, mainArgs));
        assertEquals("Expected option to start with '--'", exc.getMessage());
    }

    @Test
    public void test03() {
        // An option containing a double hyphen is an error
        String testOption = "--invalid--Option";
        List<String> mainArgs = List.of(testOption);
        IllegalArgumentException exc = assertThrows(IllegalArgumentException.class,
            () -> GetArg.get(testOption, mainArgs));
        assertEquals("Option cannot contain a '--'", exc.getMessage());
    }

    @Test
    public void test04() {
        // An option containing a double hyphen is an error
        String testOption = "----invalidOption";
        List<String> mainArgs = List.of(testOption);
        IllegalArgumentException exc = assertThrows(IllegalArgumentException.class,
            () -> GetArg.get(testOption, mainArgs));
        assertEquals("Option cannot contain a '--'", exc.getMessage());
    }

    @Test
    public void test05() {
        String testOption = "--no-args";
        List<String> mainArgs = List.of(testOption);
        List<String> optionArgs = GetArg.get(testOption, mainArgs);
        assertNotNull(optionArgs);
        assertTrue(optionArgs.isEmpty());
    }

    @Test
    public void test06() {
        String testOption = "---no-args";
        List<String> mainArgs = List.of(testOption);
        List<String> optionArgs = GetArg.get(testOption, mainArgs);
        assertNotNull(optionArgs);
        assertTrue(optionArgs.isEmpty());
    }

    @Test
    public void test07() {
        String testOption = "--option1";
        List<String> mainArgs = List.of(testOption, "a");
        List<String> optionArgs = GetArg.get(testOption, mainArgs);
        assertNotNull(optionArgs);
        assertEquals(List.of("a"), optionArgs);
    }

    @Test
    public void test08() {
        String testOption = "--option1";
        List<String> mainArgs = List.of(testOption, "a", "--option2", "b");
        List<String> optionArgs = GetArg.get(testOption, mainArgs);
        assertNotNull(optionArgs);
        assertEquals(List.of("a"), optionArgs);
    }

    @Test
    public void test09() {
        // Second duplicate option is an error
        String testOption = "--option1";
        List<String> mainArgs = List.of(testOption, "a", "--option1", "b");
        IllegalArgumentException exc = assertThrows(IllegalArgumentException.class,
            () -> GetArg.get(testOption, mainArgs));
        assertEquals("Duplicate option error: --option1", exc.getMessage());
    }

    @Test
    public void test10() {
        String testOption = "--option2";
        List<String> mainArgs = List.of("--option1", "a", testOption, "b");
        List<String> optionArgs = GetArg.get(testOption, mainArgs);
        assertNotNull(optionArgs);
        assertEquals(List.of("b"), optionArgs);
    }

    @Test
    public void test11() {
        String testOption = "--option2";
        List<String> mainArgs = List.of("--option1", "a", testOption, "b", "c");
        List<String> optionArgs = GetArg.get(testOption, mainArgs);
        assertNotNull(optionArgs);
        assertEquals(List.of("b", "c"), optionArgs);
    }

    @Test
    public void test12() {
        // Test numeric arguments
        String testOption = "--option2";
        List<String> mainArgs = List.of("--option1", "a", testOption, "b", "c", "-1", "0");
        List<String> optionArgs = GetArg.get(testOption, mainArgs);
        assertNotNull(optionArgs);
        assertEquals(List.of("b", "c", "-1", "0"), optionArgs);
    }

    @Test
    public void test13() {
        String testOption = "--option1";
        List<String> mainArgs = List.of(testOption);
        String optionArg = GetArg.getSingle(testOption, mainArgs);
        assertNull(optionArg);
    }

    @Test
    public void test14() {
        String testOption = "--option1";
        List<String> mainArgs = List.of(testOption, "a");
        String optionArg = GetArg.getSingle(testOption, mainArgs);
        assertEquals("a", optionArg);
    }

    @Test
    public void test15() {
        String testOption = "--option1";
        List<String> mainArgs = List.of(testOption, "a", "b");
        IllegalArgumentException exc = assertThrows(IllegalArgumentException.class,
            () -> GetArg.getSingle(testOption, mainArgs));
        assertEquals("More than one argument found for option: --option1", exc.getMessage());
    }

}
