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
import com.caoccao.javet.interop.monitoring.V8HeapStatistics;

import java.util.ArrayList;
import java.util.List;

/**
 * The type V8 runtime observer average V8 heap statistics.
 *
 * @since 1.0.5
 */
public class V8RuntimeObserverAverageV8HeapStatistics implements IV8RuntimeObserver<V8HeapStatistics> {
    /**
     * The V8 heap statistics list.
     *
     * @since 1.0.5
     */
    protected final List<V8HeapStatistics> v8HeapStatisticsList;

    /**
     * Instantiates a new V8 runtime observer for average V8 heap statistics.
     *
     * @since 1.0.5
     */
    public V8RuntimeObserverAverageV8HeapStatistics() {
        this(256);
    }

    /**
     * Instantiates a new V8 runtime observer for average V8 heap statistics.
     *
     * @param capacity the capacity
     * @since 1.0.6
     */
    public V8RuntimeObserverAverageV8HeapStatistics(int capacity) {
        v8HeapStatisticsList = new ArrayList<>(capacity);
    }

    @Override
    public V8HeapStatistics getResult() {
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

    @Override
    public boolean observe(V8Runtime v8Runtime) {
        v8HeapStatisticsList.add(v8Runtime.getV8HeapStatistics());
        return true;
    }

    @Override
    public void reset() {
        v8HeapStatisticsList.clear();
    }
}
