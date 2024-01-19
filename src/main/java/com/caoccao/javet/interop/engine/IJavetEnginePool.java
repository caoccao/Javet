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
import com.caoccao.javet.enums.V8AllocationSpace;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interfaces.IJavetClosable;
import com.caoccao.javet.interop.V8Host;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.engine.observers.*;
import com.caoccao.javet.interop.monitoring.V8HeapSpaceStatistics;
import com.caoccao.javet.interop.monitoring.V8HeapStatistics;
import com.caoccao.javet.interop.monitoring.V8SharedMemoryStatistics;

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
     * Gets average callback context count.
     *
     * @return the average callback context count
     * @since 1.0.6
     */
    default int getAverageCallbackContextCount() {
        V8RuntimeObserverAverageCallbackContextCount observer = new V8RuntimeObserverAverageCallbackContextCount(
                getConfig().getPoolMaxSize());
        observe(observer);
        return observer.getResult();
    }

    /**
     * Gets average reference count.
     *
     * @return the average reference count
     * @since 1.0.6
     */
    default int getAverageReferenceCount() {
        V8RuntimeObserverAverageReferenceCount observer = new V8RuntimeObserverAverageReferenceCount(
                getConfig().getPoolMaxSize());
        observe(observer);
        return observer.getResult();
    }

    /**
     * Gets average V8 heap space statistics.
     *
     * @param v8AllocationSpace the V8 allocation space
     * @return the average V8 heap space statistics
     * @since 1.0.5
     */
    default V8HeapSpaceStatistics getAverageV8HeapSpaceStatistics(final V8AllocationSpace v8AllocationSpace) {
        V8RuntimeObserverAverageV8HeapSpaceStatistics observer = new V8RuntimeObserverAverageV8HeapSpaceStatistics(
                v8AllocationSpace, getConfig().getPoolMaxSize());
        observe(observer);
        return observer.getResult();
    }

    /**
     * Gets average V8 heap statistics.
     *
     * @return the average V8 heap statistics
     * @since 1.0.5
     */
    default V8HeapStatistics getAverageV8HeapStatistics() {
        V8RuntimeObserverAverageV8HeapStatistics observer = new V8RuntimeObserverAverageV8HeapStatistics(
                getConfig().getPoolMaxSize());
        observe(observer);
        return observer.getResult();
    }

    /**
     * Gets average V8 module count.
     *
     * @return the average V8 module count
     */
    default int getAverageV8ModuleCount() {
        V8RuntimeObserverAverageV8ModuleCount observer = new V8RuntimeObserverAverageV8ModuleCount(
                getConfig().getPoolMaxSize());
        observe(observer);
        return observer.getResult();
    }

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
     * Gets released engine count.
     *
     * @return the released engine count
     * @since 1.0.5
     */
    int getReleasedEngineCount();

    /**
     * Gets V8 shared memory statistics.
     *
     * @return the V8 shared memory statistics
     * @since 1.0.5
     */
    V8SharedMemoryStatistics getV8SharedMemoryStatistics();

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
     * Traverse the internal V8 runtimes, apply the observer and return the observed V8 runtime count.
     * This API is for collecting statistics.
     * Executing code or changing the V8 runtime may result in inconsistent pool state or core dump.
     *
     * @param observers the observers
     * @return the int
     * @since 1.0.5
     */
    int observe(IV8RuntimeObserver<?>... observers);

    /**
     * Release engine.
     *
     * @param iJavetEngine the javet engine
     * @since 0.7.0
     */
    void releaseEngine(IJavetEngine<R> iJavetEngine);

    /**
     * Wake up the daemon thread explicitly.
     *
     * @since 1.0.5
     */
    void wakeUpDaemon();
}
