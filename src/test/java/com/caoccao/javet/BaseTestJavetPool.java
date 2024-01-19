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

package com.caoccao.javet;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Host;
import com.caoccao.javet.interop.engine.IJavetEnginePool;
import com.caoccao.javet.interop.engine.JavetEnginePool;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class BaseTestJavetPool extends BaseTestJavet {
    protected IJavetEnginePool<?> javetEnginePool;

    @AfterEach
    public void afterEach() throws JavetException {
        javetEnginePool.close();
        v8Host.clearInternalStatistic();
        assertEquals(0, V8Host.getInstance(javetEnginePool.getConfig().getJSRuntimeType()).getV8RuntimeCount());
    }

    @BeforeEach
    public void beforeEach() {
        javetEnginePool = new JavetEnginePool<>();
        javetEnginePool.getConfig().setEngineGuardCheckIntervalMillis(1);
        javetEnginePool.getConfig().setJSRuntimeType(v8Host.getJSRuntimeType());
    }

}
