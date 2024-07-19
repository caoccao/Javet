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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * The type V8 runtime observer average V8 heap statistics.
 *
 * @since 1.0.5
 */
public class V8RuntimeObserverAverageV8HeapStatistics implements IV8RuntimeObserver<V8HeapStatistics> {
    /**
     * The constant DEFAULT_CAPACITY.
     *
     * @since 3.1.4
     */
    protected static final int DEFAULT_CAPACITY = 256;
    /**
     * The constant DEFAULT_TIMEOUT_MILLIS.
     *
     * @since 3.1.4
     */
    protected static final int DEFAULT_TIMEOUT_MILLIS = 5000;
    /**
     * The Timeout millis.
     *
     * @since 3.1.4
     */
    protected final int timeoutMillis;
    /**
     * The V8 heap statistics future list.
     *
     * @since 1.0.5
     */
    protected final List<CompletableFuture<V8HeapStatistics>> v8HeapStatisticsFutureList;

    /**
     * Instantiates a new V8 runtime observer for average V8 heap statistics.
     *
     * @since 1.0.5
     */
    public V8RuntimeObserverAverageV8HeapStatistics() {
        this(DEFAULT_CAPACITY, DEFAULT_TIMEOUT_MILLIS);
    }

    /**
     * Instantiates a new V8 runtime observer for average V8 heap statistics.
     *
     * @param capacity      the capacity
     * @param timeoutMillis the timeout millis
     * @since 1.0.6
     */
    public V8RuntimeObserverAverageV8HeapStatistics(int capacity, int timeoutMillis) {
        this.timeoutMillis = timeoutMillis;
        v8HeapStatisticsFutureList = new ArrayList<>(capacity);
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
        if (!v8HeapStatisticsFutureList.isEmpty()) {
            int count = 0;
            final long expectedEndTime = System.currentTimeMillis() + timeoutMillis;
            for (CompletableFuture<V8HeapStatistics> v8HeapStatisticsFuture : v8HeapStatisticsFutureList) {
                try {
                    final long now = System.currentTimeMillis();
                    V8HeapStatistics v8HeapStatistics = now < expectedEndTime
                            ? v8HeapStatisticsFuture.get(expectedEndTime - now, TimeUnit.MILLISECONDS)
                            : v8HeapStatisticsFuture.getNow(null);
                    if (v8HeapStatistics != null) {
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
                        ++count;
                    }
                } catch (Throwable ignored) {
                }
            }
            if (count > 0) {
                doesZapGarbage = doesZapGarbage / count;
                externalMemory = externalMemory / count;
                heapSizeLimit = heapSizeLimit / count;
                mallocedMemory = mallocedMemory / count;
                numberOfDetachedContexts = numberOfDetachedContexts / count;
                numberOfNativeContexts = numberOfNativeContexts / count;
                peakMallocedMemory = peakMallocedMemory / count;
                totalAvailableSize = totalAvailableSize / count;
                totalGlobalHandlesSize = totalGlobalHandlesSize / count;
                totalHeapSize = totalHeapSize / count;
                totalHeapSizeExecutable = totalHeapSizeExecutable / count;
                totalPhysicalSize = totalPhysicalSize / count;
                usedGlobalHandlesSize = usedGlobalHandlesSize / count;
                usedHeapSize = usedHeapSize / count;
            }
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
    public void observe(V8Runtime v8Runtime) {
        v8HeapStatisticsFutureList.add(v8Runtime.getV8HeapStatistics());
    }

    @Override
    public void reset() {
        v8HeapStatisticsFutureList.clear();
    }
}
