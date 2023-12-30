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

package com.caoccao.javet.interop.callback;

import com.caoccao.javet.enums.V8GCCallbackFlags;
import com.caoccao.javet.enums.V8GCType;
import com.caoccao.javet.interfaces.IEnumBitset;
import com.caoccao.javet.interfaces.IJavetLogger;

import java.util.EnumSet;

/**
 * The type Javet GC callback.
 *
 * @since 1.0.3
 */
public class JavetGCCallback implements IJavetGCCallback {
    /**
     * The Logger.
     *
     * @since 1.0.3
     */
    protected IJavetLogger logger;

    /**
     * Instantiates a new Javet GC callback.
     *
     * @param logger the logger
     * @since 1.0.3
     */
    public JavetGCCallback(IJavetLogger logger) {
        this.logger = logger;
    }

    @Override
    public void callback(EnumSet<V8GCType> v8GCTypeEnumSet, EnumSet<V8GCCallbackFlags> v8GCCallbackFlagsEnumSet) {
        logger.logWarn(
                "Received GC callback with GC type {0} and GC callback flags {1}.",
                Integer.toString(IEnumBitset.getValue(v8GCTypeEnumSet)),
                Integer.toString(IEnumBitset.getValue(v8GCCallbackFlagsEnumSet)));
    }
}
