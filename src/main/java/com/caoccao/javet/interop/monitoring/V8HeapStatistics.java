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

/**
 * The type V8 heap statistics is a collection of V8 heap information.
 *
 * @since 1.0.0
 */
public final class V8HeapStatistics {
    private final long doesZapGarbage;
    private final long externalMemory;
    private final long heapSizeLimit;
    private final long mallocedMemory;
    private final long numberOfDetachedContexts;
    private final long numberOfNativeContexts;
    private final long peakMallocedMemory;
    private final long totalAvailableSize;
    private final long totalGlobalHandlesSize;
    private final long totalHeapSize;
    private final long totalHeapSizeExecutable;
    private final long totalPhysicalSize;
    private final long usedGlobalHandlesSize;
    private final long usedHeapSize;

    /**
     * Instantiates a new V8 heap statistics.
     *
     * @param doesZapGarbage           the does zap garbage
     * @param externalMemory           the external memory
     * @param heapSizeLimit            the heap size limit
     * @param mallocedMemory           the malloced memory
     * @param numberOfDetachedContexts the number of detached contexts
     * @param numberOfNativeContexts   the number of native contexts
     * @param peakMallocedMemory       the peak malloced memory
     * @param totalAvailableSize       the total available size
     * @param totalGlobalHandlesSize   the total global handles size
     * @param totalHeapSize            the total heap size
     * @param totalHeapSizeExecutable  the total heap size executable
     * @param totalPhysicalSize        the total physical size
     * @param usedGlobalHandlesSize    the used global handles size
     * @param usedHeapSize             the used heap size
     * @since 1.0.1
     */
    public V8HeapStatistics(
            long doesZapGarbage,
            long externalMemory,
            long heapSizeLimit,
            long mallocedMemory,
            long numberOfDetachedContexts,
            long numberOfNativeContexts,
            long peakMallocedMemory,
            long totalAvailableSize,
            long totalGlobalHandlesSize,
            long totalHeapSize,
            long totalHeapSizeExecutable,
            long totalPhysicalSize,
            long usedGlobalHandlesSize,
            long usedHeapSize) {
        this.doesZapGarbage = doesZapGarbage;
        this.externalMemory = externalMemory;
        this.heapSizeLimit = heapSizeLimit;
        this.mallocedMemory = mallocedMemory;
        this.numberOfDetachedContexts = numberOfDetachedContexts;
        this.numberOfNativeContexts = numberOfNativeContexts;
        this.peakMallocedMemory = peakMallocedMemory;
        this.totalAvailableSize = totalAvailableSize;
        this.totalGlobalHandlesSize = totalGlobalHandlesSize;
        this.totalHeapSize = totalHeapSize;
        this.totalHeapSizeExecutable = totalHeapSizeExecutable;
        this.totalPhysicalSize = totalPhysicalSize;
        this.usedGlobalHandlesSize = usedGlobalHandlesSize;
        this.usedHeapSize = usedHeapSize;
    }

    /**
     * Gets does zap garbage.
     *
     * @return the does zap garbage
     * @since 1.0.0
     */
    public long getDoesZapGarbage() {
        return doesZapGarbage;
    }

    /**
     * Gets external memory.
     *
     * @return the external memory
     * @since 1.0.0
     */
    public long getExternalMemory() {
        return externalMemory;
    }

    /**
     * Gets heap size limit.
     *
     * @return the heap size limit
     * @since 1.0.0
     */
    public long getHeapSizeLimit() {
        return heapSizeLimit;
    }

    /**
     * Gets malloced memory.
     *
     * @return the malloced memory
     * @since 1.0.0
     */
    public long getMallocedMemory() {
        return mallocedMemory;
    }

    /**
     * Gets number of detached contexts.
     *
     * @return the number of detached contexts
     * @since 1.0.0
     */
    public long getNumberOfDetachedContexts() {
        return numberOfDetachedContexts;
    }

    /**
     * Gets number of native contexts.
     *
     * @return the number of native contexts
     * @since 1.0.0
     */
    public long getNumberOfNativeContexts() {
        return numberOfNativeContexts;
    }

    /**
     * Gets peak malloced memory.
     *
     * @return the peak malloced memory
     * @since 1.0.0
     */
    public long getPeakMallocedMemory() {
        return peakMallocedMemory;
    }

    /**
     * Gets total available size.
     *
     * @return the total available size
     * @since 1.0.0
     */
    public long getTotalAvailableSize() {
        return totalAvailableSize;
    }

    /**
     * Gets total global handles size.
     *
     * @return the total global handles size
     * @since 1.0.0
     */
    public long getTotalGlobalHandlesSize() {
        return totalGlobalHandlesSize;
    }

    /**
     * Gets total heap size.
     *
     * @return the total heap size
     * @since 1.0.0
     */
    public long getTotalHeapSize() {
        return totalHeapSize;
    }

    /**
     * Gets total heap size executable.
     *
     * @return the total heap size executable
     * @since 1.0.0
     */
    public long getTotalHeapSizeExecutable() {
        return totalHeapSizeExecutable;
    }

    /**
     * Gets total physical size.
     *
     * @return the total physical size
     * @since 1.0.0
     */
    public long getTotalPhysicalSize() {
        return totalPhysicalSize;
    }

    /**
     * Gets used global handles size.
     *
     * @return the used global handles size
     * @since 1.0.0
     */
    public long getUsedGlobalHandlesSize() {
        return usedGlobalHandlesSize;
    }

    /**
     * Gets used heap size.
     *
     * @return the used heap size
     * @since 1.0.0
     */
    public long getUsedHeapSize() {
        return usedHeapSize;
    }

    /**
     * Minus the input V8 heap statistics to produce a diff.
     *
     * @param v8HeapStatistics the V8 heap statistics
     * @return the V8 heap statistics diff
     */
    public V8HeapStatistics minus(V8HeapStatistics v8HeapStatistics) {
        return new V8HeapStatistics(
                this.doesZapGarbage - v8HeapStatistics.doesZapGarbage,
                this.externalMemory - v8HeapStatistics.externalMemory,
                this.heapSizeLimit - v8HeapStatistics.heapSizeLimit,
                this.mallocedMemory - v8HeapStatistics.mallocedMemory,
                this.numberOfDetachedContexts - v8HeapStatistics.numberOfDetachedContexts,
                this.numberOfNativeContexts - v8HeapStatistics.numberOfNativeContexts,
                this.peakMallocedMemory - v8HeapStatistics.peakMallocedMemory,
                this.totalAvailableSize - v8HeapStatistics.totalAvailableSize,
                this.totalGlobalHandlesSize - v8HeapStatistics.totalGlobalHandlesSize,
                this.totalHeapSize - v8HeapStatistics.totalHeapSize,
                this.totalHeapSizeExecutable - v8HeapStatistics.totalHeapSizeExecutable,
                this.totalPhysicalSize - v8HeapStatistics.totalPhysicalSize,
                this.usedGlobalHandlesSize - v8HeapStatistics.usedGlobalHandlesSize,
                this.usedHeapSize - v8HeapStatistics.usedHeapSize);
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
        if (!ignoreZero || doesZapGarbage != 0)
            sb.append(", ").append("doesZapGarbage = ").append(doesZapGarbage);
        if (!ignoreZero || externalMemory != 0)
            sb.append(", ").append("externalMemory = ").append(externalMemory);
        if (!ignoreZero || heapSizeLimit != 0)
            sb.append(", ").append("heapSizeLimit = ").append(heapSizeLimit);
        if (!ignoreZero || mallocedMemory != 0)
            sb.append(", ").append("mallocedMemory = ").append(mallocedMemory);
        if (!ignoreZero || numberOfDetachedContexts != 0)
            sb.append(", ").append("numberOfDetachedContexts = ").append(numberOfDetachedContexts);
        if (!ignoreZero || numberOfNativeContexts != 0)
            sb.append(", ").append("numberOfNativeContexts = ").append(numberOfNativeContexts);
        if (!ignoreZero || peakMallocedMemory != 0)
            sb.append(", ").append("peakMallocedMemory = ").append(peakMallocedMemory);
        if (!ignoreZero || totalAvailableSize != 0)
            sb.append(", ").append("totalAvailableSize = ").append(totalAvailableSize);
        if (!ignoreZero || totalGlobalHandlesSize != 0)
            sb.append(", ").append("totalGlobalHandlesSize = ").append(totalGlobalHandlesSize);
        if (!ignoreZero || totalHeapSize != 0)
            sb.append(", ").append("totalHeapSize = ").append(totalHeapSize);
        if (!ignoreZero || totalHeapSizeExecutable != 0)
            sb.append(", ").append("totalHeapSizeExecutable = ").append(totalHeapSizeExecutable);
        if (!ignoreZero || totalPhysicalSize != 0)
            sb.append(", ").append("totalPhysicalSize = ").append(totalPhysicalSize);
        if (!ignoreZero || usedGlobalHandlesSize != 0)
            sb.append(", ").append("usedGlobalHandlesSize = ").append(usedGlobalHandlesSize);
        if (!ignoreZero || usedHeapSize != 0)
            sb.append(", ").append("usedHeapSize = ").append(usedHeapSize);
        return sb.toString();
    }
}
