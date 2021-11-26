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
 * The type V8 shared memory statistics is a collection of shared per-process V8 memory information.
 *
 * @since 1.0.0
 */
public final class V8SharedMemoryStatistics {
    private long readOnlySpacePhysicalSize;
    private long readOnlySpaceSize;
    private long readOnlySpaceUsedSize;

    /**
     * Instantiates a new V8 shared memory statistics.
     *
     * @param readOnlySpacePhysicalSize the read only space physical size
     * @param readOnlySpaceSize         the read only space size
     * @param readOnlySpaceUsedSize     the read only space used size
     * @since 1.0.1
     */
    public V8SharedMemoryStatistics(
            long readOnlySpacePhysicalSize,
            long readOnlySpaceSize,
            long readOnlySpaceUsedSize) {
        this.readOnlySpacePhysicalSize = readOnlySpacePhysicalSize;
        this.readOnlySpaceSize = readOnlySpaceSize;
        this.readOnlySpaceUsedSize = readOnlySpaceUsedSize;
    }

    /**
     * Gets read only space physical size.
     *
     * @return the read only space physical size
     * @since 1.0.0
     */
    public long getReadOnlySpacePhysicalSize() {
        return readOnlySpacePhysicalSize;
    }

    /**
     * Gets read only space size.
     *
     * @return the read only space size
     * @since 1.0.0
     */
    public long getReadOnlySpaceSize() {
        return readOnlySpaceSize;
    }

    /**
     * Gets read only space used size.
     *
     * @return the read only space used size
     * @since 1.0.0
     */
    public long getReadOnlySpaceUsedSize() {
        return readOnlySpaceUsedSize;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("name = ").append(getClass().getSimpleName());
        sb.append(", ").append("readOnlySpacePhysicalSize = ").append(readOnlySpacePhysicalSize);
        sb.append(", ").append("readOnlySpaceSize = ").append(readOnlySpaceSize);
        sb.append(", ").append("readOnlySpaceUsedSize = ").append(readOnlySpaceUsedSize);
        return sb.toString();
    }
}
