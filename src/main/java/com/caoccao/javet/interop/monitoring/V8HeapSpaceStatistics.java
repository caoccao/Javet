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
 * The type V8 heap space statistics.
 *
 * @since 1.0.0
 */
public final class V8HeapSpaceStatistics {
    private AllocationSpace allocationSpace;
    private int physicalSpaceSize;
    private int spaceAvailableSize;
    private String spaceName;
    private int spaceSize;
    private int spaceUsedSize;

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
    V8HeapSpaceStatistics(
            String spaceName,
            int physicalSpaceSize,
            int spaceAvailableSize,
            int spaceSize,
            int spaceUsedSize) {
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
    public AllocationSpace getAllocationSpace() {
        return allocationSpace;
    }

    /**
     * Gets physical space size.
     *
     * @return the physical space size
     * @since 1.0.0
     */
    public int getPhysicalSpaceSize() {
        return physicalSpaceSize;
    }

    /**
     * Gets space available size.
     *
     * @return the space available size
     * @since 1.0.0
     */
    public int getSpaceAvailableSize() {
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
    public int getSpaceSize() {
        return spaceSize;
    }

    /**
     * Gets space used size.
     *
     * @return the space used size
     * @since 1.0.0
     */
    public int getSpaceUsedSize() {
        return spaceUsedSize;
    }

    /**
     * Sets allocation space.
     *
     * @param allocationSpace the allocation space
     * @return the self
     * @since 1.0.1
     */
    public V8HeapSpaceStatistics setAllocationSpace(AllocationSpace allocationSpace) {
        this.allocationSpace = Objects.requireNonNull(allocationSpace);
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("name = ").append(getClass().getSimpleName());
        sb.append(", ").append("spaceName = ").append(spaceName);
        sb.append(", ").append("physicalSpaceSize = ").append(physicalSpaceSize);
        sb.append(", ").append("spaceAvailableSize = ").append(spaceAvailableSize);
        sb.append(", ").append("spaceSize = ").append(spaceSize);
        sb.append(", ").append("spaceUsedSize = ").append(spaceUsedSize);
        return sb.toString();
    }

    /**
     * The enum Allocation space.
     *
     * @since 1.0.0
     */
    public enum AllocationSpace {
        /**
         * Ro space allocation space.
         * Immortal, immovable and immutable objects.
         *
         * @since 1.0.0
         */
        RO_SPACE(0),
        /**
         * Old space allocation space.
         * Old generation regular object space.
         *
         * @since 1.0.0
         */
        OLD_SPACE(1),
        /**
         * Code space allocation space.
         * Old generation code object space, marked executable.
         *
         * @since 1.0.0
         */
        CODE_SPACE(2),
        /**
         * Map space allocation space.
         * Old generation map object space, non-movable.
         *
         * @since 1.0.0
         */
        MAP_SPACE(3),
        /**
         * Lo space allocation space.
         * Old generation large object space.
         *
         * @since 1.0.0
         */
        LO_SPACE(4),
        /**
         * Code lo space allocation space.
         * Old generation large code object space.
         *
         * @since 1.0.0
         */
        CODE_LO_SPACE(5),
        /**
         * New lo space allocation space.
         * Young generation large object space.
         *
         * @since 1.0.0
         */
        NEW_LO_SPACE(6),
        /**
         * New space allocation space.
         * Young generation semispaces for regular objects collected with Scavenger.
         *
         * @since 1.0.0
         */
        NEW_SPACE(7),
        /**
         * First space allocation space.
         *
         * @since 1.0.0
         */
        FIRST_SPACE(RO_SPACE.getIndex()),
        /**
         * Last space allocation space.
         *
         * @since 1.0.0
         */
        LAST_SPACE(NEW_SPACE.getIndex()),
        /**
         * First mutable space allocation space.
         *
         * @since 1.0.0
         */
        FIRST_MUTABLE_SPACE(OLD_SPACE.getIndex()),
        /**
         * Last mutable space allocation space.
         *
         * @since 1.0.0
         */
        LAST_MUTABLE_SPACE(NEW_SPACE.getIndex()),
        /**
         * First growable paged space allocation space.
         *
         * @since 1.0.0
         */
        FIRST_GROWABLE_PAGED_SPACE(OLD_SPACE.getIndex()),
        /**
         * Last growable paged space allocation space.
         *
         * @since 1.0.0
         */
        LAST_GROWABLE_PAGED_SPACE(MAP_SPACE.getIndex()),
        ;

        private int index;

        AllocationSpace(int index) {
            this.index = index;
        }

        /**
         * Gets index.
         *
         * @return the index
         * @since 1.0.0
         */
        public int getIndex() {
            return index;
        }
    }
}
