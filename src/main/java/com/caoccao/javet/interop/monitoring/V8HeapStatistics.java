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

import java.util.Objects;

/**
 * The type V8 heap statistics is a collection of V8 heap information.
 *
 * @since 1.0.0
 */
public final class V8HeapStatistics {
    private int doesZapGarbage;
    private int externalMemory;
    private int heapSizeLimit;
    private int mallocedMemory;
    private int numberOfDetachedContexts;
    private int numberOfNativeContexts;
    private int peakMallocedMemory;
    private int totalAvailableSize;
    private int totalGlobalHandlesSize;
    private int totalHeapSize;
    private int totalHeapSizeExecutable;
    private int totalPhysicalSize;
    private int usedGlobalHandlesSize;
    private int usedHeapSize;

    /**
     * Instantiates a new V8 heap statistics.
     *
     * @param intArray the int array
     * @since 1.0.0
     */
    public V8HeapStatistics(int[] intArray) {
        Objects.requireNonNull(intArray);
        doesZapGarbage = intArray[0];
        externalMemory = intArray[1];
        heapSizeLimit = intArray[2];
        mallocedMemory = intArray[3];
        numberOfDetachedContexts = intArray[4];
        numberOfNativeContexts = intArray[5];
        peakMallocedMemory = intArray[6];
        totalAvailableSize = intArray[7];
        totalGlobalHandlesSize = intArray[8];
        totalHeapSize = intArray[9];
        totalHeapSizeExecutable = intArray[10];
        totalPhysicalSize = intArray[11];
        usedGlobalHandlesSize = intArray[12];
        usedHeapSize = intArray[13];
    }

    /**
     * Gets does zap garbage.
     *
     * @return the does zap garbage
     * @since 1.0.0
     */
    public int getDoesZapGarbage() {
        return doesZapGarbage;
    }

    /**
     * Gets external memory.
     *
     * @return the external memory
     * @since 1.0.0
     */
    public int getExternalMemory() {
        return externalMemory;
    }

    /**
     * Gets heap size limit.
     *
     * @return the heap size limit
     * @since 1.0.0
     */
    public int getHeapSizeLimit() {
        return heapSizeLimit;
    }

    /**
     * Gets malloced memory.
     *
     * @return the malloced memory
     * @since 1.0.0
     */
    public int getMallocedMemory() {
        return mallocedMemory;
    }

    /**
     * Gets number of detached contexts.
     *
     * @return the number of detached contexts
     * @since 1.0.0
     */
    public int getNumberOfDetachedContexts() {
        return numberOfDetachedContexts;
    }

    /**
     * Gets number of native contexts.
     *
     * @return the number of native contexts
     * @since 1.0.0
     */
    public int getNumberOfNativeContexts() {
        return numberOfNativeContexts;
    }

    /**
     * Gets peak malloced memory.
     *
     * @return the peak malloced memory
     * @since 1.0.0
     */
    public int getPeakMallocedMemory() {
        return peakMallocedMemory;
    }

    /**
     * Gets total available size.
     *
     * @return the total available size
     * @since 1.0.0
     */
    public int getTotalAvailableSize() {
        return totalAvailableSize;
    }

    /**
     * Gets total global handles size.
     *
     * @return the total global handles size
     * @since 1.0.0
     */
    public int getTotalGlobalHandlesSize() {
        return totalGlobalHandlesSize;
    }

    /**
     * Gets total heap size.
     *
     * @return the total heap size
     * @since 1.0.0
     */
    public int getTotalHeapSize() {
        return totalHeapSize;
    }

    /**
     * Gets total heap size executable.
     *
     * @return the total heap size executable
     * @since 1.0.0
     */
    public int getTotalHeapSizeExecutable() {
        return totalHeapSizeExecutable;
    }

    /**
     * Gets total physical size.
     *
     * @return the total physical size
     * @since 1.0.0
     */
    public int getTotalPhysicalSize() {
        return totalPhysicalSize;
    }

    /**
     * Gets used global handles size.
     *
     * @return the used global handles size
     * @since 1.0.0
     */
    public int getUsedGlobalHandlesSize() {
        return usedGlobalHandlesSize;
    }

    /**
     * Gets used heap size.
     *
     * @return the used heap size
     * @since 1.0.0
     */
    public int getUsedHeapSize() {
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
