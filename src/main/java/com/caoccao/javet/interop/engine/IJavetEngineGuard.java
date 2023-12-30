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

import com.caoccao.javet.interfaces.IJavetClosable;

/**
 * The interface Javet engine guard is the one guarding the script execution with a timeout.
 * <p>
 * Usage:
 * <pre>
 * try (IJavetEngineGuard iJavetEngineGuard = iJavetEngine.getGuard(5000)) {
 *     v8Runtime.getExecutor("while (true) {}").executeVoid();
 *     // That infinite loop will be terminated in 5 seconds by the guard.
 * }
 * </pre>
 *
 * @since 0.7.2
 */
public interface IJavetEngineGuard extends IJavetClosable, Runnable {
    /**
     * Cancel.
     *
     * @since 0.7.2
     */
    void cancel();

    /**
     * Disable in debug mode.
     *
     * @since 0.8.9
     */
    void disableInDebugMode();

    /**
     * Enable in debug mode.
     *
     * @since 0.8.9
     */
    void enableInDebugMode();

    /**
     * Gets timeout millis.
     *
     * @return the timeout millis
     * @since 0.7.2
     */
    long getTimeoutMillis();

    /**
     * Sets timeout millis.
     *
     * @param timeoutMillis the timeout millis
     * @since 0.7.2
     */
    void setTimeoutMillis(long timeoutMillis);
}
