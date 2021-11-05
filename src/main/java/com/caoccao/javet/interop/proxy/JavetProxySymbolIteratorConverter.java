/*
 * Copyright (c) 2021. caoccao.com Sam Cao
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

import com.caoccao.javet.interop.V8Runtime;

import java.util.Iterator;

/**
 * The type Javet proxy symbol iterator converter.
 *
 * @param <T> the type parameter
 * @since 1.0.4
 */
public class JavetProxySymbolIteratorConverter<T extends Iterator> extends BaseJavetProxySymbolConverter<T> {

    /**
     * Instantiates a new Javet proxy symbol iterator converter.
     *
     * @param v8Runtime    the V8 runtime
     * @param targetObject the target object
     * @since 1.0.4
     */
    public JavetProxySymbolIteratorConverter(V8Runtime v8Runtime, T targetObject) {
        super(v8Runtime, targetObject);
    }
}
