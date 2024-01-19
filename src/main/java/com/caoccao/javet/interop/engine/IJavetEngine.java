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

package com.caoccao.javet.interop.engine;

import com.caoccao.javet.annotations.CheckReturnValue;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interfaces.IJavetClosable;
import com.caoccao.javet.interop.V8Runtime;

/**
 * The interface Javet engine.
 *
 * @param <R> the type parameter
 * @since 0.7.0
 */
public interface IJavetEngine<R extends V8Runtime> extends IJavetClosable {
    /**
     * Gets config.
     *
     * @return the config
     * @since 0.7.2
     */
    JavetEngineConfig getConfig();

    /**
     * Gets guard.
     *
     * @return the guard
     * @since 0.7.2
     */
    @CheckReturnValue
    IJavetEngineGuard getGuard();

    /**
     * Gets guard.
     *
     * @param timeoutMillis the timeout millis
     * @return the guard
     * @since 0.7.2
     */
    @CheckReturnValue
    IJavetEngineGuard getGuard(long timeoutMillis);

    /**
     * Gets V8 runtime.
     *
     * @return the V8 runtime
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    R getV8Runtime() throws JavetException;

    /**
     * Is active boolean.
     *
     * @return true : active, false : inactive
     * @since 0.7.0
     */
    boolean isActive();

    /**
     * Reset context.
     *
     * @throws JavetException the javet exception
     * @since 0.7.1
     */
    void resetContext() throws JavetException;

    /**
     * Reset isolate.
     *
     * @throws JavetException the javet exception
     * @since 0.8.0
     */
    void resetIsolate() throws JavetException;

    /**
     * Send GC notification.
     * @since 0.8.3
     */
    void sendGCNotification();
}
