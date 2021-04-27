package com.caoccao.javet.utils;

import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestSimpleFreeMarkerFormat {
    @Test
    public void testInvalid() {
        assertEquals( "abc",
                SimpleFreeMarkerFormat.format("abc", null),
                "Parameters being null should pass.");
        assertEquals( "abc",
                SimpleFreeMarkerFormat.format("abc", new HashMap<>()),
                "Parameters being empty should pass.");
        assertEquals( "abc${",
                SimpleFreeMarkerFormat.format("abc${", SimpleMap.of("d", "x")),
                "Open variable should pass.");
        assertEquals( "abc${def",
                SimpleFreeMarkerFormat.format("abc${def", SimpleMap.of("d", "x")),
                "Open variable should pass.");
        assertEquals( "abc$${d}",
                SimpleFreeMarkerFormat.format("abc$${d}", SimpleMap.of("d", "x")),
                "Double dollar should pass.");
        assertEquals( "abc<null>def",
                SimpleFreeMarkerFormat.format("abc${e}def", SimpleMap.of("d", "x")),
                "Unknown variable should pass.");
        assertEquals( "abc<null>def",
                SimpleFreeMarkerFormat.format("abc${}def", SimpleMap.of("d", "x")),
                "Empty variable should pass.");
        assertEquals( "ab{def.$ghi}c",
                SimpleFreeMarkerFormat.format("ab{def.$ghi}c", SimpleMap.of("ghi", "x")),
                "Dollar should pass.");
    }

    @Test
    public void testValid() {
        assertEquals( "abcx",
                SimpleFreeMarkerFormat.format("abc${d}", SimpleMap.of("d", "x")),
                "Variable at the end should pass.");
        assertEquals( "xabc",
                SimpleFreeMarkerFormat.format("${d}abc", SimpleMap.of("d", "x")),
                "Variable at the beginning should pass.");
        assertEquals( "abxc",
                SimpleFreeMarkerFormat.format("ab${d}c", SimpleMap.of("d", "x")),
                "Variable in the middle should pass.");
        assertEquals( "abxc",
                SimpleFreeMarkerFormat.format("ab${def.${ghi}c", SimpleMap.of("def.${ghi", "x")),
                "Variable with dollar should pass.");
        assertEquals( "abxc",
                SimpleFreeMarkerFormat.format("ab${{}c", SimpleMap.of("{", "x")),
                "Single open should pass.");
    }
}
