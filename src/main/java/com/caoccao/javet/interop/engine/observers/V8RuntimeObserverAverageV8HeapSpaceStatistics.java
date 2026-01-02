/*
 * Copyright (c) 2021-2026. caoccao.com Sam Cao
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

import com.caoccao.javet.enums.V8AllocationSpace;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.monitoring.V8HeapSpaceStatistics;
import com.caoccao.javet.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * The type V8 runtime observer average V8 heap space statistics.
 *
 * @since 1.0.5
 */
public class V8RuntimeObserverAverageV8HeapSpaceStatistics implements IV8RuntimeObserver<V8HeapSpaceStatistics> {
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
     * The V8 allocation space.
     *
     * @since 1.0.5
     */
    protected final V8AllocationSpace v8AllocationSpace;
    /**
     * The V8 heap space statistics future list.
     *
     * @since 1.0.5
     */
    protected final List<CompletableFuture<V8HeapSpaceStatistics>> v8HeapSpaceStatisticsFutureList;

    /**
     * Instantiates a new V8 runtime observer for average V8 heap space statistics.
     *
     * @param v8AllocationSpace the V8 allocation space
     * @since 1.0.5
     */
    public V8RuntimeObserverAverageV8HeapSpaceStatistics(V8AllocationSpace v8AllocationSpace) {
        this(v8AllocationSpace, DEFAULT_CAPACITY, DEFAULT_TIMEOUT_MILLIS);
    }

    /**
     * Instantiates a new V8 runtime observer for average V8 heap space statistics.
     *
     * @param v8AllocationSpace the V8 allocation space
     * @param capacity          the capacity
     * @param timeoutMillis     the timeout millis
     * @since 1.0.6
     */
    public V8RuntimeObserverAverageV8HeapSpaceStatistics(
            V8AllocationSpace v8AllocationSpace,
            int capacity,
            int timeoutMillis) {
        this.v8AllocationSpace = Objects.requireNonNull(v8AllocationSpace);
        this.timeoutMillis = timeoutMillis;
        v8HeapSpaceStatisticsFutureList = new ArrayList<>(capacity);
    }

    @Override
    public V8HeapSpaceStatistics getResult() {
        long physicalSpaceSize = 0;
        long spaceAvailableSize = 0;
        String spaceName = StringUtils.EMPTY;
        long spaceSize = 0;
        long spaceUsedSize = 0;
        if (!v8HeapSpaceStatisticsFutureList.isEmpty()) {
            int count = 0;
            final long expectedEndTime = System.currentTimeMillis() + timeoutMillis;
            for (CompletableFuture<V8HeapSpaceStatistics> v8HeapSpaceStatisticsFuture : v8HeapSpaceStatisticsFutureList) {
                try {
                    final long now = System.currentTimeMillis();
                    V8HeapSpaceStatistics v8HeapSpaceStatistics = now < expectedEndTime
                            ? v8HeapSpaceStatisticsFuture.get(expectedEndTime - now, TimeUnit.MILLISECONDS)
                            : v8HeapSpaceStatisticsFuture.getNow(null);
                    if (v8HeapSpaceStatistics != null) {
                        if (spaceName.isEmpty()) {
                            spaceName = v8HeapSpaceStatistics.getSpaceName();
                        }
                        physicalSpaceSize += v8HeapSpaceStatistics.getPhysicalSpaceSize();
                        spaceAvailableSize += v8HeapSpaceStatistics.getSpaceAvailableSize();
                        spaceSize += v8HeapSpaceStatistics.getSpaceSize();
                        spaceUsedSize += v8HeapSpaceStatistics.getSpaceUsedSize();
                        ++count;
                    }
                } catch (Throwable ignored) {
                }
            }
            if (count > 0) {
                physicalSpaceSize = physicalSpaceSize / count;
                spaceAvailableSize = spaceAvailableSize / count;
                spaceSize = spaceSize / count;
                spaceUsedSize = spaceUsedSize / count;
            }
        }
        return new V8HeapSpaceStatistics(
                spaceName,
                physicalSpaceSize,
                spaceAvailableSize,
                spaceSize,
                spaceUsedSize).setAllocationSpace(v8AllocationSpace);
    }

    @Override
    public void observe(V8Runtime v8Runtime) {
        v8HeapSpaceStatisticsFutureList.add(v8Runtime.getV8HeapSpaceStatistics(v8AllocationSpace));
    }

    @Override
    public void reset() {
        v8HeapSpaceStatisticsFutureList.clear();
    }
}
