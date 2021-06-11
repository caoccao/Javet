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

package com.caoccao.javet.interfaces;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.values.V8Value;

/**
 * The interface Javet bi-indexed consumer.
 *
 * @param <T1> the type parameter
 * @param <T2> the type parameter
 * @param <E>  the type parameter
 * @since 0.8.10
 */
public interface IJavetBiIndexedConsumer<T1 extends V8Value, T2 extends V8Value, E extends Throwable> {
    /**
     * Accept.
     *
     * @param index  the index
     * @param value1 the value 1
     * @param value2 the value 2
     * @throws JavetException the javet exception
     * @throws E              the e
     * @since 0.8.10
     */
    void accept(int index, T1 value1, T2 value2) throws JavetException, E;
}
