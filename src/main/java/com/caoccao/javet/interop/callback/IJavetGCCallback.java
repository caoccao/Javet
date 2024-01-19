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

import java.util.EnumSet;

/**
 * The interface Javet GC callback.
 *
 * @since 1.0.3
 */
public interface IJavetGCCallback {
    /**
     * Callback.
     *
     * @param v8GCTypeEnumSet          the V8 GC type enum set
     * @param v8GCCallbackFlagsEnumSet the V8 GC callback flags enum set
     * @since 1.0.3
     */
    void callback(
            EnumSet<V8GCType> v8GCTypeEnumSet,
            EnumSet<V8GCCallbackFlags> v8GCCallbackFlagsEnumSet);
}
