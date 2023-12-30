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

package com.caoccao.javet.exceptions;

import com.caoccao.javet.BaseTestJavetRuntime;
import com.caoccao.javet.enums.JavetErrorType;
import com.caoccao.javet.utils.SimpleMap;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestJavetException extends BaseTestJavetRuntime {

    @Test
    public void testSystem() {
        JavetException[] javetExceptions = new JavetException[]{
                new JavetException(JavetError.OSNotSupported, SimpleMap.of(JavetError.PARAMETER_OS, "abc")),
                new JavetException(JavetError.LibraryNotFound, SimpleMap.of(JavetError.PARAMETER_PATH, "abc")),
                new JavetException(JavetError.LibraryNotLoaded, SimpleMap.of(JavetError.PARAMETER_REASON, "abc")),
                new JavetException(JavetError.NotSupported, SimpleMap.of(JavetError.PARAMETER_FEATURE, "abc")),
                new JavetException(JavetError.FailedToReadPath, SimpleMap.of(JavetError.PARAMETER_PATH, "abc")),
        };
        Arrays.stream(javetExceptions).forEach(e -> assertEquals(JavetErrorType.System, e.getError().getType()));
        assertEquals("OS abc is not supported", javetExceptions[0].getMessage());
        assertEquals("Javet library abc is not found", javetExceptions[1].getMessage());
        assertEquals("Javet library is not loaded because abc", javetExceptions[2].getMessage());
        assertEquals("abc is not supported", javetExceptions[3].getMessage());
        assertEquals("Failed to read abc", javetExceptions[4].getMessage());
    }
}
