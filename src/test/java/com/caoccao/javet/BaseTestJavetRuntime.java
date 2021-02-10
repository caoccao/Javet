/*
 *    Copyright 2021. caoccao.com Sam Cao
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

package com.caoccao.javet;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Host;
import com.caoccao.javet.interop.V8Runtime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

public abstract class BaseTestJavetRuntime extends BaseTestJavet {
    protected V8Runtime v8Runtime;

    @BeforeEach
    public void beforeEach() throws JavetException {
        v8Runtime = V8Host.getInstance().createV8Runtime();
        assertFalse(v8Runtime.isPooled());
        v8Runtime.lock();
        assertEquals(0, v8Runtime.getReferenceCount(),
                "Reference count should be 0 before test case is started.");
    }

    @AfterEach
    public void afterEach() throws JavetException {
        assertEquals(0, v8Runtime.getReferenceCount(),
                "Reference count should be 0 after test case is ended.");
        v8Runtime.unlock();
        v8Runtime.close();
        assertEquals(0, V8Host.getInstance().getV8RuntimeCount());
    }
}
