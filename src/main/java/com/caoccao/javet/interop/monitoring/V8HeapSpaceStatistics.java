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

package com.caoccao.javet.interop.monitoring;

import com.caoccao.javet.enums.V8AllocationSpace;

import java.util.Objects;

/**
 * The type V8 heap space statistics.
 *
 * @since 1.0.0
 */
public final class V8HeapSpaceStatistics {
    private final long physicalSpaceSize;
    private final long spaceAvailableSize;
    private final String spaceName;
    private final long spaceSize;
    private final long spaceUsedSize;
    private V8AllocationSpace v8AllocationSpace;

    /**
     * Instantiates a new V8 heap space statistics.
     *
     * @param spaceName          the space name
     * @param physicalSpaceSize  the physical space size
     * @param spaceAvailableSize the space available size
     * @param spaceSize          the space size
     * @param spaceUsedSize      the space used size
     * @since 1.0.1
     */
    public V8HeapSpaceStatistics(
            String spaceName,
            long physicalSpaceSize,
            long spaceAvailableSize,
            long spaceSize,
            long spaceUsedSize) {
        this.spaceName = spaceName;
        this.physicalSpaceSize = physicalSpaceSize;
        this.spaceAvailableSize = spaceAvailableSize;
        this.spaceSize = spaceSize;
        this.spaceUsedSize = spaceUsedSize;
    }

    /**
     * Gets allocation space.
     *
     * @return the allocation space
     * @since 1.0.0
     */
    public V8AllocationSpace getAllocationSpace() {
        return v8AllocationSpace;
    }

    /**
     * Gets physical space size.
     *
     * @return the physical space size
     * @since 1.0.0
     */
    public long getPhysicalSpaceSize() {
        return physicalSpaceSize;
    }

    /**
     * Gets space available size.
     *
     * @return the space available size
     * @since 1.0.0
     */
    public long getSpaceAvailableSize() {
        return spaceAvailableSize;
    }

    /**
     * Gets space name.
     *
     * @return the space name
     * @since 1.0.1
     */
    public String getSpaceName() {
        return spaceName;
    }

    /**
     * Gets space size.
     *
     * @return the space size
     * @since 1.0.0
     */
    public long getSpaceSize() {
        return spaceSize;
    }

    /**
     * Gets space used size.
     *
     * @return the space used size
     * @since 1.0.0
     */
    public long getSpaceUsedSize() {
        return spaceUsedSize;
    }

    /**
     * Minus the input V8 heap space statistics to produce a diff.
     *
     * @param v8HeapSpaceStatistics the V8 heap space statistics
     * @return the V8 heap space statistics diff
     */
    public V8HeapSpaceStatistics minus(V8HeapSpaceStatistics v8HeapSpaceStatistics) {
        return new V8HeapSpaceStatistics(
                spaceName,
                this.physicalSpaceSize - v8HeapSpaceStatistics.physicalSpaceSize,
                this.spaceAvailableSize - v8HeapSpaceStatistics.spaceAvailableSize,
                this.spaceSize - v8HeapSpaceStatistics.spaceSize,
                this.spaceUsedSize - v8HeapSpaceStatistics.spaceUsedSize);
    }

    /**
     * Sets allocation space.
     *
     * @param v8AllocationSpace the allocation space
     * @return the self
     * @since 1.0.1
     */
    public V8HeapSpaceStatistics setAllocationSpace(V8AllocationSpace v8AllocationSpace) {
        this.v8AllocationSpace = Objects.requireNonNull(v8AllocationSpace);
        return this;
    }

    @Override
    public String toString() {
        return toString(false);
    }

    /**
     * To string with zero value ignored or not.
     *
     * @param ignoreZero ignore zero
     * @return the string
     * @since 1.0.7
     */
    public String toString(boolean ignoreZero) {
        StringBuilder sb = new StringBuilder();
        sb.append("name = ").append(getClass().getSimpleName());
        sb.append(", ").append("spaceName = ").append(spaceName);
        if (!ignoreZero || physicalSpaceSize != 0)
            sb.append(", ").append("physicalSpaceSize = ").append(physicalSpaceSize);
        if (!ignoreZero || spaceAvailableSize != 0)
            sb.append(", ").append("spaceAvailableSize = ").append(spaceAvailableSize);
        if (!ignoreZero || spaceSize != 0)
            sb.append(", ").append("spaceSize = ").append(spaceSize);
        if (!ignoreZero || spaceUsedSize != 0)
            sb.append(", ").append("spaceUsedSize = ").append(spaceUsedSize);
        return sb.toString();
    }
}
