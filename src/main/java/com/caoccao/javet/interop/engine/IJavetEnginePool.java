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

import com.caoccao.javet.annotations.CheckReturnValue;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interfaces.IJavetClosable;
import com.caoccao.javet.interop.V8Runtime;

/**
 * The interface Javet engine pool.
 *
 * @param <R> the type parameter
 * @since 0.7.0
 */
public interface IJavetEnginePool<R extends V8Runtime> extends IJavetClosable {
    /**
     * Gets active engine count.
     *
     * @return the active engine count
     * @since 0.7.0
     */
    int getActiveEngineCount();

    /**
     * Gets config.
     *
     * @return the config
     * @since 0.7.0
     */
    JavetEngineConfig getConfig();

    /**
     * Gets engine.
     *
     * @return the engine
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    @CheckReturnValue
    IJavetEngine<R> getEngine() throws JavetException;

    /**
     * Gets idle engine count.
     *
     * @return the idle engine count
     * @since 0.7.0
     */
    int getIdleEngineCount();

    /**
     * Is active.
     *
     * @return true : active, false: inactive
     * @since 0.7.2
     */
    boolean isActive();

    /**
     * Is quitting boolean.
     *
     * @return true : quitting, false: not quiting
     * @since 0.7.2
     */
    boolean isQuitting();

    /**
     * Release engine.
     *
     * @param engine the engine
     * @since 0.7.0
     */
    void releaseEngine(IJavetEngine<R> engine);
}
