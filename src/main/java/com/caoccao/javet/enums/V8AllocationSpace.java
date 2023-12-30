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

package com.caoccao.javet.enums;

/**
 * The enum V8 allocation space.
 *
 * @since 1.0.0
 */
public enum V8AllocationSpace {
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

    V8AllocationSpace(int index) {
        this.index = index;
    }

    /**
     * Get distinct values.
     *
     * @return the distinct values
     * @since 1.0.5
     */
    public static V8AllocationSpace[] getDistinctValues() {
        return new V8AllocationSpace[]{
                RO_SPACE,
                OLD_SPACE,
                CODE_SPACE,
                MAP_SPACE,
                LO_SPACE,
                CODE_LO_SPACE,
                NEW_LO_SPACE,
                NEW_SPACE};
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
