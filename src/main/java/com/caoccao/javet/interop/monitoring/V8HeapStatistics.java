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

package com.caoccao.javet.interop.monitoring;

/**
 * The type V8 heap statistics is a collection of V8 heap information.
 *
 * @since 1.0.0
 */
public final class V8HeapStatistics {
    private long doesZapGarbage;
    private long externalMemory;
    private long heapSizeLimit;
    private long mallocedMemory;
    private long numberOfDetachedContexts;
    private long numberOfNativeContexts;
    private long peakMallocedMemory;
    private long totalAvailableSize;
    private long totalGlobalHandlesSize;
    private long totalHeapSize;
    private long totalHeapSizeExecutable;
    private long totalPhysicalSize;
    private long usedGlobalHandlesSize;
    private long usedHeapSize;

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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("name = ").append(getClass().getSimpleName());
        sb.append(", ").append("doesZapGarbage = ").append(doesZapGarbage);
        sb.append(", ").append("externalMemory = ").append(externalMemory);
        sb.append(", ").append("heapSizeLimit = ").append(heapSizeLimit);
        sb.append(", ").append("mallocedMemory = ").append(mallocedMemory);
        sb.append(", ").append("numberOfDetachedContexts = ").append(numberOfDetachedContexts);
        sb.append(", ").append("numberOfNativeContexts = ").append(numberOfNativeContexts);
        sb.append(", ").append("peakMallocedMemory = ").append(peakMallocedMemory);
        sb.append(", ").append("totalAvailableSize = ").append(totalAvailableSize);
        sb.append(", ").append("totalGlobalHandlesSize = ").append(totalGlobalHandlesSize);
        sb.append(", ").append("totalHeapSize = ").append(totalHeapSize);
        sb.append(", ").append("totalHeapSizeExecutable = ").append(totalHeapSizeExecutable);
        sb.append(", ").append("totalPhysicalSize = ").append(totalPhysicalSize);
        sb.append(", ").append("usedGlobalHandlesSize = ").append(usedGlobalHandlesSize);
        sb.append(", ").append("usedHeapSize = ").append(usedHeapSize);
        return sb.toString();
    }
}
