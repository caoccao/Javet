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

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.values.V8Value;

/**
 * The type Javet virtual object.
 *
 * @since 0.9.10
 */
public class JavetVirtualObject {
    /**
     * The Converted.
     *
     * @since 0.9.10
     */
    protected boolean converted;
    /**
     * The Object.
     *
     * @since 0.9.10
     */
    protected Object object;
    /**
     * The V8 value.
     *
     * @since 0.9.10
     */
    protected V8Value v8Value;

    /**
     * Instantiates a new Javet virtual object.
     *
     * @param v8Value the V8 value
     * @since 0.9.10
     */
    public JavetVirtualObject(V8Value v8Value) {
        converted = false;
        object = null;
        this.v8Value = v8Value;
    }

    /**
     * Gets object.
     *
     * @return the object
     * @throws JavetException the javet exception
     * @since 0.9.10
     */
    public Object getObject() throws JavetException {
        if (!converted) {
            object = v8Value.getV8Runtime().toObject(v8Value);
            converted = true;
        }
        return object;
    }

    /**
     * Gets V8 value.
     *
     * @return the V8 value
     * @since 0.9.10
     */
    public V8Value getV8Value() {
        return v8Value;
    }

    /**
     * Is converted.
     *
     * @return the boolean
     * @since 0.9.10
     */
    public boolean isConverted() {
        return converted;
    }
}
