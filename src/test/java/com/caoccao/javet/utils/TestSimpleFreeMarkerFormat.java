/*
 * Copyright (c) 2021-2024. caoccao.com Sam Cao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.caoccao.javet.utils;

import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestSimpleFreeMarkerFormat {
    @Test
    public void testInvalid() {
        assertEquals("abc",
                SimpleFreeMarkerFormat.format("abc", null),
                "Parameters being null should pass.");
        assertEquals("abc",
                SimpleFreeMarkerFormat.format("abc", new HashMap<>()),
                "Parameters being empty should pass.");
        assertEquals("abc${",
                SimpleFreeMarkerFormat.format("abc${", SimpleMap.of("d", "x")),
                "Open variable should pass.");
        assertEquals("abc${def",
                SimpleFreeMarkerFormat.format("abc${def", SimpleMap.of("d", "x")),
                "Open variable should pass.");
        assertEquals("abc$${d}",
                SimpleFreeMarkerFormat.format("abc$${d}", SimpleMap.of("d", "x")),
                "Double dollar should pass.");
        assertEquals("abc<null>def",
                SimpleFreeMarkerFormat.format("abc${e}def", SimpleMap.of("d", "x")),
                "Unknown variable should pass.");
        assertEquals("abc<null>def",
                SimpleFreeMarkerFormat.format("abc${}def", SimpleMap.of("d", "x")),
                "Empty variable should pass.");
        assertEquals("ab{def.$ghi}c",
                SimpleFreeMarkerFormat.format("ab{def.$ghi}c", SimpleMap.of("ghi", "x")),
                "Dollar should pass.");
    }

    @Test
    public void testValid() {
        assertEquals("abcx",
                SimpleFreeMarkerFormat.format("abc${d}", SimpleMap.of("d", "x")),
                "Variable at the end should pass.");
        assertEquals("xabc",
                SimpleFreeMarkerFormat.format("${d}abc", SimpleMap.of("d", "x")),
                "Variable at the beginning should pass.");
        assertEquals("abxc",
                SimpleFreeMarkerFormat.format("ab${d}c", SimpleMap.of("d", "x")),
                "Variable in the middle should pass.");
        assertEquals("abxc",
                SimpleFreeMarkerFormat.format("ab${def.${ghi}c", SimpleMap.of("def.${ghi", "x")),
                "Variable with dollar should pass.");
        assertEquals("abxc",
                SimpleFreeMarkerFormat.format("ab${{}c", SimpleMap.of("{", "x")),
                "Single open should pass.");
        assertEquals("ab12345678c",
                SimpleFreeMarkerFormat.format("ab${x}c", SimpleMap.of("x", 12345678)),
                "Integer should pass.");
        assertEquals("ab1234567890c",
                SimpleFreeMarkerFormat.format("ab${x}c", SimpleMap.of("x", 1234567890L)),
                "Long should pass.");
    }
}
