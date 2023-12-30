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

package com.caoccao.javet.interop.executors;

import com.caoccao.javet.annotations.CheckReturnValue;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.V8ScriptOrigin;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.reference.V8Module;
import com.caoccao.javet.values.reference.V8Script;

import java.util.Objects;

/**
 * The type Base V8 executor.
 *
 * @since 0.7.0
 */
public abstract class BaseV8Executor implements IV8Executor {
    /**
     * The V8 runtime.
     *
     * @since 0.7.0
     */
    protected V8Runtime v8Runtime;
    /**
     * The V8 script origin.
     *
     * @since 0.7.0
     */
    protected V8ScriptOrigin v8ScriptOrigin;

    /**
     * Instantiates a new Base V8 executor.
     *
     * @param v8Runtime the V8 runtime
     * @since 0.7.0
     */
    public BaseV8Executor(V8Runtime v8Runtime) {
        this.v8Runtime = Objects.requireNonNull(v8Runtime);
        v8ScriptOrigin = new V8ScriptOrigin();
    }

    @Override
    public V8Runtime getV8Runtime() {
        return v8Runtime;
    }

    @Override
    public V8ScriptOrigin getV8ScriptOrigin() {
        return v8ScriptOrigin;
    }
}
