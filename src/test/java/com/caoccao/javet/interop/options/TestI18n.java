/*
 * Copyright (c) 2024. caoccao.com Sam Cao
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

package com.caoccao.javet.interop.options;

import com.caoccao.javet.BaseTestJavetRuntime;
import com.caoccao.javet.exceptions.JavetException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestI18n extends BaseTestJavetRuntime {
    @Test
    public void testToLocalString() throws JavetException {
        if (isI18nEnabled()) {
            assertEquals(
                    "123,456,789",
                    v8Runtime.getExecutor("let a = 123456789; a.toLocaleString('en-US');").executeString());
            assertEquals(
                    "2/2/2000, 3:04:05 AM",
                    v8Runtime.getExecutor("a = new Date(Date.UTC(2000, 1, 2, 3, 4, 5)); a.toLocaleString('en-US', { timeZone: 'UTC' });").executeString());
        } else {
            assertEquals(
                    "123456789",
                    v8Runtime.getExecutor("let a = 123456789; a.toLocaleString('en-US');").executeString());
        }
    }
}
