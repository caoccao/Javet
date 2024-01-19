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

package com.caoccao.javet.interop.engine.observers;

import com.caoccao.javet.interop.V8Runtime;

/**
 * The interface V8 runtime observer.
 *
 * @param <R> the type parameter for the result
 * @since 1.0.5
 */
public interface IV8RuntimeObserver<R> {
    /**
     * Gets result.
     *
     * @return the result
     * @since 1.0.5
     */
    default R getResult() {
        return null;
    }

    /**
     * Observe the input V8 runtime.
     * The return value notifies the owner to whether continue or break.
     *
     * @param v8Runtime the V8 runtime
     * @return true : continue, false : break
     * @since 1.0.5
     */
    boolean observe(V8Runtime v8Runtime);

    /**
     * Reset.
     *
     * @since 1.0.5
     */
    default void reset() {
    }
}
