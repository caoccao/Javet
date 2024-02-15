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
import com.caoccao.javet.utils.StringUtils;
import com.caoccao.javet.values.IV8ValuePrimitiveValue;
import com.caoccao.javet.values.reference.V8ValueStringObject;

import java.util.Objects;

/**
 * The type V8 value string.
 *
 * @since 0.7.0
 */
@SuppressWarnings("unchecked")
public final class V8ValueString
        extends V8ValuePrimitive<String>
        implements IV8ValuePrimitiveValue<V8ValueStringObject> {
    /**
     * Instantiates a new V8 value string.
     *
     * @param v8Runtime the V8 runtime
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    public V8ValueString(V8Runtime v8Runtime) throws JavetException {
        this(v8Runtime, null);
    }

    /**
     * Instantiates a new V8 value string.
     *
     * @param v8Runtime the V8 runtime
     * @param value     the value
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    public V8ValueString(V8Runtime v8Runtime, String value) throws JavetException {
        super(v8Runtime, Objects.requireNonNull(value));
    }

    @Override
    public boolean asBoolean() {
        // The empty string "" turns into false; other strings turn into true.
        return StringUtils.isNotEmpty(value);
    }

    @Override
    public double asDouble() {
        String trimmedString = value.trim();
        try {
            return Double.parseDouble(trimmedString);
        } catch (Throwable ignored) {
        }
        return 0;
    }

    @Override
    public int asInt() {
        String trimmedString = value.trim();
        if (StringUtils.isDigital(trimmedString)) {
            try {
                return Integer.parseInt(trimmedString);
            } catch (Throwable ignored) {
            }
        }
        return 0;
    }

    @Override
    public long asLong() {
        String trimmedString = value.trim();
        if (StringUtils.isDigital(trimmedString)) {
            try {
                return Long.parseLong(trimmedString);
            } catch (Throwable ignored) {
            }
        }
        return 0;
    }

    @Override
    public V8ValueString toClone(boolean referenceCopy) throws JavetException {
        return this;
    }

    @Override
    public V8ValueStringObject toObject() throws JavetException {
        return checkV8Runtime().createV8ValueStringObject(value);
    }

    /**
     * To primitive string.
     *
     * @return the string
     * @since 0.7.0
     */
    public String toPrimitive() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
