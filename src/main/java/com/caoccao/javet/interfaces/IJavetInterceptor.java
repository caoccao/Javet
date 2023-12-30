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

package com.caoccao.javet.interfaces;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.values.reference.IV8ValueObject;

/**
 * The interface Javet interceptor.
 *
 * @since 0.7.0
 */
public interface IJavetInterceptor {
    /**
     * Register the interceptor to the given V8 value object.
     *
     * @param iV8ValueObjects the V8 value objects
     * @return true: success, false: failure
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    @SuppressWarnings("UnusedReturnValue")
    boolean register(IV8ValueObject... iV8ValueObjects) throws JavetException;

    /**
     * Unregister the interceptor from the given V8 value object.
     *
     * @param iV8ValueObjects the V8 value objects
     * @return true: success, false: failure
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    @SuppressWarnings("UnusedReturnValue")
    boolean unregister(IV8ValueObject... iV8ValueObjects) throws JavetException;
}
