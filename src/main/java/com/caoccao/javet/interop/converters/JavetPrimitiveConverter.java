/*
 *   Copyright (c) 2021. caoccao.com Sam Cao
 *   All rights reserved.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.caoccao.javet.interop.converters;

import com.caoccao.javet.annotations.CheckReturnValue;
import com.caoccao.javet.exceptions.JavetConverterException;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.primitive.V8ValuePrimitive;

import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * The type Javet primitive converter.
 *
 * @since 0.7.1
 */
@SuppressWarnings("unchecked")
public class JavetPrimitiveConverter implements IJavetConverter {
    /**
     * The Config.
     *
     * @since 0.9.4
     */
    protected JavetConverterConfig config;

    /**
     * Instantiates a new Javet primitive converter.
     *
     * @since 0.7.1
     */
    public JavetPrimitiveConverter() {
        config = new JavetConverterConfig();
    }

    @Override
    public JavetConverterConfig getConfig() {
        return config;
    }

    /**
     * Sets config.
     *
     * @param config the config
     * @since 0.9.4
     */
    public void setConfig(JavetConverterConfig config) {
        this.config = Objects.requireNonNull(config);
    }

    @Override
    public final Object toObject(V8Value v8Value) throws JavetException {
        return toObject(v8Value, 0);
    }

    /**
     * To object object.
     *
     * @param v8Value the v 8 value
     * @param depth   the depth
     * @return the object
     * @throws JavetException the javet exception
     * @since 0.9.3
     */
    protected Object toObject(V8Value v8Value, final int depth) throws JavetException {
        validateDepth(depth);
        if (v8Value == null || v8Value.isNull() || v8Value.isUndefined()) {
            return null;
        } else if (v8Value instanceof V8ValuePrimitive) {
            return ((V8ValuePrimitive<?>) v8Value).getValue();
        }
        return v8Value;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    @CheckReturnValue
    public final <T extends V8Value> T toV8Value(V8Runtime v8Runtime, Object object) throws JavetException {
        return toV8Value(v8Runtime, object, 0);
    }

    /**
     * To V8 value.
     *
     * @param <T>       the type parameter
     * @param v8Runtime the V8 runtime
     * @param object    the object
     * @param depth     the depth
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 0.9.3
     */
    @SuppressWarnings("ConstantConditions")
    @CheckReturnValue
    protected <T extends V8Value> T toV8Value(
            V8Runtime v8Runtime, Object object, final int depth) throws JavetException {
        validateDepth(depth);
        /*
         * The following test is based on statistical analysis
         * so that the performance can be maximized.
         */
        V8Value v8Value;
        if (object == null) {
            v8Value = v8Runtime.createV8ValueNull();
        } else if (object.getClass().isPrimitive()) {
            Class<?> objectClass = object.getClass();
            if (objectClass == int.class) {
                v8Value = v8Runtime.createV8ValueInteger((int) object);
            } else if (objectClass == boolean.class) {
                v8Value = v8Runtime.createV8ValueBoolean((boolean) object);
            } else if (objectClass == double.class) {
                v8Value = v8Runtime.createV8ValueDouble((double) object);
            } else if (objectClass == float.class) {
                v8Value = v8Runtime.createV8ValueDouble((double) object);
            } else if (objectClass == long.class) {
                v8Value = v8Runtime.createV8ValueLong((long) object);
            } else if (objectClass == short.class) {
                v8Value = v8Runtime.createV8ValueInteger((int) object);
            } else if (objectClass == byte.class) {
                v8Value = v8Runtime.createV8ValueInteger((int) object);
            } else if (objectClass == char.class) {
                v8Value = v8Runtime.createV8ValueString(Character.toString((char) object));
            } else {
                v8Value = v8Runtime.createV8ValueUndefined();
            }
        } else if (object instanceof V8Value) {
            v8Value = (V8Value) object;
        } else if (object instanceof Integer) {
            v8Value = v8Runtime.createV8ValueInteger((Integer) object);
        } else if (object instanceof Boolean) {
            v8Value = v8Runtime.createV8ValueBoolean((Boolean) object);
        } else if (object instanceof String) {
            v8Value = v8Runtime.createV8ValueString((String) object);
        } else if (object instanceof Double) {
            v8Value = v8Runtime.createV8ValueDouble((Double) object);
        } else if (object instanceof Float) {
            v8Value = v8Runtime.createV8ValueDouble((Float) object);
        } else if (object instanceof Long) {
            v8Value = v8Runtime.createV8ValueLong((Long) object);
        } else if (object instanceof Short) {
            v8Value = v8Runtime.createV8ValueInteger((Short) object);
        } else if (object instanceof ZonedDateTime) {
            v8Value = v8Runtime.createV8ValueZonedDateTime((ZonedDateTime) object);
        } else if (object instanceof Byte) {
            v8Value = v8Runtime.createV8ValueInteger((Byte) object);
        } else if (object instanceof Character) {
            v8Value = v8Runtime.createV8ValueString(((Character) object).toString());
        } else {
            v8Value = v8Runtime.createV8ValueUndefined();
        }
        return (T) v8Runtime.decorateV8Value(v8Value);
    }

    /**
     * Validate depth.
     *
     * @param depth the depth
     * @throws JavetException the javet exception
     * @since 0.9.3
     */
    protected void validateDepth(final int depth) throws JavetException {
        if (depth >= config.getMaxDepth()) {
            throw JavetConverterException.circularStructure(config.getMaxDepth());
        }
    }

}
