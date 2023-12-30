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

package com.caoccao.javet.interop.converters;

import com.caoccao.javet.annotations.CheckReturnValue;
import com.caoccao.javet.exceptions.JavetConverterException;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.values.V8Value;

import java.util.Objects;

/**
 * The type Base javet converter.
 *
 * @since 1.0.4
 */
public abstract class BaseJavetConverter implements IJavetConverter {
    /**
     * The Config.
     *
     * @since 1.0.4
     */
    protected JavetConverterConfig<?> config;

    /**
     * Instantiates a new Base javet converter.
     *
     * @since 1.0.4
     */
    public BaseJavetConverter() {
        config = new JavetConverterConfig<>();
    }

    @Override
    public JavetConverterConfig<?> getConfig() {
        return config;
    }

    /**
     * Sets config.
     *
     * @param config the config
     * @since 1.0.4
     */
    public void setConfig(JavetConverterConfig<?> config) {
        this.config = Objects.requireNonNull(config);
    }

    @Override
    public final <T> T toObject(V8Value v8Value) throws JavetException {
        return toObject(v8Value, 0);
    }

    /**
     * To object with stack depth.
     *
     * @param <T>     the type parameter
     * @param v8Value the V8 value
     * @param depth   the stack depth
     * @return the object
     * @throws JavetException the javet exception
     * @since 1.0.4
     */
    protected abstract <T> T toObject(V8Value v8Value, final int depth) throws JavetException;

    @Override
    @CheckReturnValue
    public final <T extends V8Value> T toV8Value(V8Runtime v8Runtime, Object object) throws JavetException {
        return toV8Value(v8Runtime, object, 0);
    }

    /**
     * To V8 value with stack depth.
     *
     * @param <T>       the type parameter
     * @param v8Runtime the V8 runtime
     * @param object    the object
     * @param depth     the stack depth
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 1.0.4
     */
    @CheckReturnValue
    protected abstract <T extends V8Value> T toV8Value(
            V8Runtime v8Runtime, Object object, final int depth) throws JavetException;

    /**
     * Validate the stack depth.
     *
     * @param depth the stack depth
     * @throws JavetException the javet exception
     * @since 1.0.4
     */
    protected void validateDepth(final int depth) throws JavetException {
        if (depth >= config.getMaxDepth()) {
            throw JavetConverterException.circularStructure(config.getMaxDepth());
        }
    }
}
