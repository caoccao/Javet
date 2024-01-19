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

package com.caoccao.javet.interop.proxy;

import com.caoccao.javet.annotations.CheckReturnValue;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.converters.JavetProxyConverter;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.virtual.V8VirtualIterator;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * The type Javet proxy symbol iterable converter.
 *
 * @param <T> the type parameter
 * @since 1.0.4
 */
public class JavetProxySymbolIterableConverter<T> extends BaseJavetProxySymbolConverter<T> {
    protected static final JavetProxyConverter PROXY_CONVERTER = new JavetProxyConverter();

    /**
     * Instantiates a new Javet proxy symbol iterable converter.
     *
     * @param v8Runtime    the V8 runtime
     * @param targetObject the target object
     * @since 1.0.4
     */
    public JavetProxySymbolIterableConverter(V8Runtime v8Runtime, T targetObject) {
        super(v8Runtime, targetObject);
    }

    @CheckReturnValue
    @Override
    public V8Value toV8Value(V8Value... v8Values) throws JavetException {
        Iterator<?> iterator = null;
        if (targetObject instanceof Iterable<?>) {
            iterator = ((Iterable<?>) targetObject).iterator();
        } else if (targetObject instanceof Map<?, ?>) {
            iterator = ((Map<?, ?>) targetObject).keySet().iterator();
        } else if (targetObject != null && targetObject.getClass().isArray()) {
            final int length = Array.getLength(targetObject);
            List<Object> list = new ArrayList<>(length);
            for (int i = 0; i < length; i++) {
                list.add(Array.get(targetObject, i));
            }
            iterator = list.iterator();
        }
        if (iterator != null) {
            return PROXY_CONVERTER.toV8Value(v8Runtime, new V8VirtualIterator<>(iterator));
        }
        return v8Runtime.createV8ValueUndefined();
    }
}
