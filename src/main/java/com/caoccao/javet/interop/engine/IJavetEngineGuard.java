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

package com.caoccao.javet.interop.engine;

import com.caoccao.javet.interfaces.IJavetClosable;

/**
 * The interface Javet engine guard is the one guarding the script execution with a timeout.
 * <p>
 * Usage:
 *
 * <code>
 * try (IJavetEngineGuard iJavetEngineGuard = iJavetEngine.getGuard(5000)) {
 * v8Runtime.getExecutor("while (true) {}").executeVoid();
 * // That infinite loop will be terminated in 5 seconds by the guard.
 * }
 * </code>
 */
public interface IJavetEngineGuard extends IJavetClosable, Runnable {
    /**
     * Cancel.
     */
    void cancel();

    /**
     * Gets timeout millis.
     *
     * @return the timeout millis
     */
    long getTimeoutMillis();

    /**
     * Sets timeout millis.
     *
     * @param timeoutMillis the timeout millis
     */
    void setTimeoutMillis(long timeoutMillis);
}
