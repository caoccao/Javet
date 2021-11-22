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
import com.caoccao.javet.enums.V8AllocationSpace;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interfaces.IJavetClosable;
import com.caoccao.javet.interfaces.IV8RuntimeObserver;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.monitoring.V8HeapSpaceStatistics;
import com.caoccao.javet.interop.monitoring.V8HeapStatistics;
import com.caoccao.javet.interop.monitoring.V8SharedMemoryStatistics;

import java.util.ArrayList;
import java.util.List;

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
     * Gets average V8 heap space statistics.
     *
     * @param v8AllocationSpace the V8 allocation space
     * @return the average V8 heap space statistics
     * @since 1.0.5
     */
    default V8HeapSpaceStatistics getAverageV8HeapSpaceStatistics(final V8AllocationSpace v8AllocationSpace) {
        final List<V8HeapSpaceStatistics> v8HeapSpaceStatisticsList = new ArrayList<>();
        observe(v8Runtime -> v8HeapSpaceStatisticsList.add(v8Runtime.getV8HeapSpaceStatistics(v8AllocationSpace)));
        long physicalSpaceSize = 0;
        long spaceAvailableSize = 0;
        String spaceName = "";
        long spaceSize = 0;
        long spaceUsedSize = 0;
        if (!v8HeapSpaceStatisticsList.isEmpty()) {
            for (V8HeapSpaceStatistics v8HeapSpaceStatistics : v8HeapSpaceStatisticsList) {
                if (spaceName.length() == 0) {
                    spaceName = v8HeapSpaceStatistics.getSpaceName();
                }
                physicalSpaceSize += v8HeapSpaceStatistics.getPhysicalSpaceSize();
                spaceAvailableSize += v8HeapSpaceStatistics.getSpaceAvailableSize();
                spaceSize += v8HeapSpaceStatistics.getSpaceSize();
                spaceUsedSize += v8HeapSpaceStatistics.getSpaceUsedSize();
            }
            final int v8RuntimeCount = v8HeapSpaceStatisticsList.size();
            physicalSpaceSize = physicalSpaceSize / v8RuntimeCount;
            spaceAvailableSize = spaceAvailableSize / v8RuntimeCount;
            spaceSize = spaceSize / v8RuntimeCount;
            spaceUsedSize = spaceUsedSize / v8RuntimeCount;
        }
        return new V8HeapSpaceStatistics(
                spaceName,
                physicalSpaceSize,
                spaceAvailableSize,
                spaceSize,
                spaceUsedSize);
    }

    /**
     * Gets average V8 heap statistics.
     *
     * @return the average V8 heap statistics
     * @since 1.0.5
     */
    default V8HeapStatistics getAverageV8HeapStatistics() {
        final List<V8HeapStatistics> v8HeapStatisticsList = new ArrayList<>();
        observe(v8Runtime -> v8HeapStatisticsList.add(v8Runtime.getV8HeapStatistics()));
        long doesZapGarbage = 0;
        long externalMemory = 0;
        long heapSizeLimit = 0;
        long mallocedMemory = 0;
        long numberOfDetachedContexts = 0;
        long numberOfNativeContexts = 0;
        long peakMallocedMemory = 0;
        long totalAvailableSize = 0;
        long totalGlobalHandlesSize = 0;
        long totalHeapSize = 0;
        long totalHeapSizeExecutable = 0;
        long totalPhysicalSize = 0;
        long usedGlobalHandlesSize = 0;
        long usedHeapSize = 0;
        if (!v8HeapStatisticsList.isEmpty()) {
            for (V8HeapStatistics v8HeapStatistics : v8HeapStatisticsList) {
                doesZapGarbage += v8HeapStatistics.getDoesZapGarbage();
                externalMemory += v8HeapStatistics.getExternalMemory();
                heapSizeLimit += v8HeapStatistics.getHeapSizeLimit();
                mallocedMemory += v8HeapStatistics.getMallocedMemory();
                numberOfDetachedContexts += v8HeapStatistics.getNumberOfDetachedContexts();
                numberOfNativeContexts += v8HeapStatistics.getNumberOfNativeContexts();
                peakMallocedMemory += v8HeapStatistics.getPeakMallocedMemory();
                totalAvailableSize += v8HeapStatistics.getTotalAvailableSize();
                totalGlobalHandlesSize += v8HeapStatistics.getTotalGlobalHandlesSize();
                totalHeapSize += v8HeapStatistics.getTotalHeapSize();
                totalHeapSizeExecutable += v8HeapStatistics.getTotalHeapSizeExecutable();
                totalPhysicalSize += v8HeapStatistics.getTotalPhysicalSize();
                usedGlobalHandlesSize += v8HeapStatistics.getUsedGlobalHandlesSize();
                usedHeapSize += v8HeapStatistics.getUsedHeapSize();
            }
            final int v8RuntimeCount = v8HeapStatisticsList.size();
            doesZapGarbage = doesZapGarbage / v8RuntimeCount;
            externalMemory = externalMemory / v8RuntimeCount;
            heapSizeLimit = heapSizeLimit / v8RuntimeCount;
            mallocedMemory = mallocedMemory / v8RuntimeCount;
            numberOfDetachedContexts = numberOfDetachedContexts / v8RuntimeCount;
            numberOfNativeContexts = numberOfNativeContexts / v8RuntimeCount;
            peakMallocedMemory = peakMallocedMemory / v8RuntimeCount;
            totalAvailableSize = totalAvailableSize / v8RuntimeCount;
            totalGlobalHandlesSize = totalGlobalHandlesSize / v8RuntimeCount;
            totalHeapSize = totalHeapSize / v8RuntimeCount;
            totalHeapSizeExecutable = totalHeapSizeExecutable / v8RuntimeCount;
            totalPhysicalSize = totalPhysicalSize / v8RuntimeCount;
            usedGlobalHandlesSize = usedGlobalHandlesSize / v8RuntimeCount;
            usedHeapSize = usedHeapSize / v8RuntimeCount;
        }
        return new V8HeapStatistics(
                doesZapGarbage,
                externalMemory,
                heapSizeLimit,
                mallocedMemory,
                numberOfDetachedContexts,
                numberOfNativeContexts,
                peakMallocedMemory,
                totalAvailableSize,
                totalGlobalHandlesSize,
                totalHeapSize,
                totalHeapSizeExecutable,
                totalPhysicalSize,
                usedGlobalHandlesSize,
                usedHeapSize);
    }

    /**
     * Gets average V8 shared memory statistics.
     *
     * @return the average V8 shared memory statistics
     * @since 1.0.5
     */
    default V8SharedMemoryStatistics getAverageV8SharedMemoryStatistics() {
        final List<V8SharedMemoryStatistics> v8SharedMemoryStatisticsList = new ArrayList<>();
        observe(v8Runtime -> v8SharedMemoryStatisticsList.add(v8Runtime.getV8SharedMemoryStatistics()));
        long readOnlySpacePhysicalSize = 0;
        long readOnlySpaceSize = 0;
        long readOnlySpaceUsedSize = 0;
        if (!v8SharedMemoryStatisticsList.isEmpty()) {
            for (V8SharedMemoryStatistics v8SharedMemoryStatistics : v8SharedMemoryStatisticsList) {
                readOnlySpacePhysicalSize += v8SharedMemoryStatistics.getReadOnlySpacePhysicalSize();
                readOnlySpaceSize += v8SharedMemoryStatistics.getReadOnlySpaceSize();
                readOnlySpaceUsedSize += v8SharedMemoryStatistics.getReadOnlySpaceUsedSize();
            }
            final int v8RuntimeCount = v8SharedMemoryStatisticsList.size();
            readOnlySpacePhysicalSize = readOnlySpacePhysicalSize / v8RuntimeCount;
            readOnlySpaceSize = readOnlySpaceSize / v8RuntimeCount;
            readOnlySpaceUsedSize = readOnlySpaceUsedSize / v8RuntimeCount;
        }
        return new V8SharedMemoryStatistics(
                readOnlySpacePhysicalSize,
                readOnlySpaceSize,
                readOnlySpaceUsedSize);
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
     * @param observer the observer
     * @return the int
     * @since 1.0.5
     */
    int observe(IV8RuntimeObserver observer);

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
