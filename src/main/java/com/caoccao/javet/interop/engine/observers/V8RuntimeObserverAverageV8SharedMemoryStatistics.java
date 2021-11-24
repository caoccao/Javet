/*
 * Copyright (c) 2021. caoccao.com Sam Cao
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
import com.caoccao.javet.interop.monitoring.V8SharedMemoryStatistics;

import java.util.ArrayList;
import java.util.List;

/**
 * The type V8 runtime observer average V8 shared memory statistics.
 *
 * @since 1.0.5
 */
public class V8RuntimeObserverAverageV8SharedMemoryStatistics implements IV8RuntimeObserver<V8SharedMemoryStatistics> {
    /**
     * The V8 shared memory statistics list.
     *
     * @since 1.0.5
     */
    protected final List<V8SharedMemoryStatistics> v8SharedMemoryStatisticsList;

    /**
     * Instantiates a new V8 runtime observer average V8 shared memory statistics.
     *
     * @since 1.0.5
     */
    public V8RuntimeObserverAverageV8SharedMemoryStatistics() {
        v8SharedMemoryStatisticsList = new ArrayList<>();
    }

    @Override
    public V8SharedMemoryStatistics getResult() {
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

    @Override
    public boolean observe(V8Runtime v8Runtime) {
        v8SharedMemoryStatisticsList.add(v8Runtime.getV8SharedMemoryStatistics());
        return true;
    }

    @Override
    public void reset() {
        v8SharedMemoryStatisticsList.clear();
    }
}
