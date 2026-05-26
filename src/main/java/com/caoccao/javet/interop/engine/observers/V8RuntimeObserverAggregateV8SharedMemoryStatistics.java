/*
 * Copyright (c) 2026. caoccao.com Sam Cao
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Sums {@link V8SharedMemoryStatistics} across a pool. In multi-cage pointer
 * compression builds every isolate group has its own read-only space, so the
 * meaningful pool-wide view is a sum rather than an average.
 *
 * @since 5.0.8
 */
public class V8RuntimeObserverAggregateV8SharedMemoryStatistics
        implements IV8RuntimeObserver<V8SharedMemoryStatistics> {
    protected static final int DEFAULT_CAPACITY = 256;
    protected static final int DEFAULT_TIMEOUT_MILLIS = 5000;
    protected final List<CompletableFuture<V8SharedMemoryStatistics>> futures;
    protected final int timeoutMillis;

    public V8RuntimeObserverAggregateV8SharedMemoryStatistics() {
        this(DEFAULT_CAPACITY, DEFAULT_TIMEOUT_MILLIS);
    }

    public V8RuntimeObserverAggregateV8SharedMemoryStatistics(int capacity, int timeoutMillis) {
        this.timeoutMillis = timeoutMillis;
        futures = new ArrayList<>(capacity);
    }

    @Override
    public V8SharedMemoryStatistics getResult() {
        long readOnlySpacePhysicalSize = 0;
        long readOnlySpaceSize = 0;
        long readOnlySpaceUsedSize = 0;
        if (!futures.isEmpty()) {
            final long expectedEndTime = System.currentTimeMillis() + timeoutMillis;
            for (CompletableFuture<V8SharedMemoryStatistics> future : futures) {
                try {
                    final long now = System.currentTimeMillis();
                    V8SharedMemoryStatistics stats = now < expectedEndTime
                            ? future.get(expectedEndTime - now, TimeUnit.MILLISECONDS)
                            : future.getNow(null);
                    if (stats != null) {
                        readOnlySpacePhysicalSize += stats.getReadOnlySpacePhysicalSize();
                        readOnlySpaceSize += stats.getReadOnlySpaceSize();
                        readOnlySpaceUsedSize += stats.getReadOnlySpaceUsedSize();
                    }
                } catch (Throwable ignored) {
                }
            }
        }
        return new V8SharedMemoryStatistics(
                readOnlySpacePhysicalSize,
                readOnlySpaceSize,
                readOnlySpaceUsedSize);
    }

    @Override
    public void observe(V8Runtime v8Runtime) {
        futures.add(v8Runtime.getV8SharedMemoryStatistics());
    }

    @Override
    public void reset() {
        futures.clear();
    }
}
