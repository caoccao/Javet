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

import com.caoccao.javet.enums.V8AllocationSpace;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.monitoring.V8HeapSpaceStatistics;
import com.caoccao.javet.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The type V8 runtime observer average V8 heap space statistics.
 *
 * @since 1.0.5
 */
public class V8RuntimeObserverAverageV8HeapSpaceStatistics implements IV8RuntimeObserver<V8HeapSpaceStatistics> {
    /**
     * The V8 allocation space.
     *
     * @since 1.0.5
     */
    protected final V8AllocationSpace v8AllocationSpace;
    /**
     * The V8 heap space statistics list.
     *
     * @since 1.0.5
     */
    protected final List<V8HeapSpaceStatistics> v8HeapSpaceStatisticsList;

    /**
     * Instantiates a new V8 runtime observer for average V8 heap space statistics.
     *
     * @param v8AllocationSpace the V8 allocation space
     * @since 1.0.5
     */
    public V8RuntimeObserverAverageV8HeapSpaceStatistics(V8AllocationSpace v8AllocationSpace) {
        this(v8AllocationSpace, 256);
    }

    /**
     * Instantiates a new V8 runtime observer for average V8 heap space statistics.
     *
     * @param v8AllocationSpace the V8 allocation space
     * @param capacity          the capacity
     * @since 1.0.6
     */
    public V8RuntimeObserverAverageV8HeapSpaceStatistics(V8AllocationSpace v8AllocationSpace, int capacity) {
        this.v8AllocationSpace = Objects.requireNonNull(v8AllocationSpace);
        v8HeapSpaceStatisticsList = new ArrayList<>(capacity);
    }

    @Override
    public V8HeapSpaceStatistics getResult() {
        long physicalSpaceSize = 0;
        long spaceAvailableSize = 0;
        String spaceName = StringUtils.EMPTY;
        long spaceSize = 0;
        long spaceUsedSize = 0;
        if (!v8HeapSpaceStatisticsList.isEmpty()) {
            for (V8HeapSpaceStatistics v8HeapSpaceStatistics : v8HeapSpaceStatisticsList) {
                if (spaceName.isEmpty()) {
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
                spaceUsedSize).setAllocationSpace(v8AllocationSpace);
    }

    @Override
    public boolean observe(V8Runtime v8Runtime) {
        v8HeapSpaceStatisticsList.add(v8Runtime.getV8HeapSpaceStatistics(v8AllocationSpace));
        return true;
    }

    @Override
    public void reset() {
        v8HeapSpaceStatisticsList.clear();
    }
}
