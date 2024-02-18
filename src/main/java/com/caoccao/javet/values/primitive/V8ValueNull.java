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

package com.caoccao.javet.values.primitive;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.values.IV8ValueNonProxyable;
import com.caoccao.javet.values.V8Value;

/**
 * The type V8 value null.
 *
 * @since 0.7.0
 */
@SuppressWarnings("unchecked")
public final class V8ValueNull extends V8Value implements IV8ValueNonProxyable {

    /**
     * The constant NULL.
     *
     * @since 0.7.0
     */
    public static final String NULL = "null";

    /**
     * Instantiates a new V8 value null.
     *
     * @param v8Runtime the V8 runtime
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    public V8ValueNull(V8Runtime v8Runtime) throws JavetException {
        super(v8Runtime);
    }

    @Override
    public boolean asBoolean() {
        return false;
    }

    @Override
    public void close() throws JavetException {
    }

    @Override
    public boolean equals(V8Value v8Value) {
        return v8Value instanceof V8ValueNull;
    }

    @Override
    public boolean sameValue(V8Value v8Value) {
        return equals(v8Value);
    }

    @Override
    public boolean strictEquals(V8Value v8Value) {
        return equals(v8Value);
    }

    @Override
    public V8ValueNull toClone(boolean referenceCopy) {
        return this;
    }

    @Override
    public String toString() {
        return NULL;
    }
}
