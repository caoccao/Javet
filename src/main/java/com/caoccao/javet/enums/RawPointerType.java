/*
 * Copyright (c) 2024-2026. caoccao.com Sam Cao
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
 * The enum raw pointer type is for internal use only.
 *
 * @since 3.1.4
 */
public enum RawPointerType {
    /**
     * Heap statistics container javet raw pointer type.
     *
     * @since 3.1.4
     */
    HeapStatisticsContainer(1),
    /**
     * Heap space statistics container javet raw pointer type.
     *
     * @since 3.1.4
     */
    HeapSpaceStatisticsContainer(2),
    /**
     * Invalid raw pointer type.
     *
     * @since 3.1.4
     */
    Invalid(0);

    private final int id;

    RawPointerType(int id) {
        this.id = id;
    }

    /**
     * Parse raw pointer type by id.
     *
     * @param id the id
     * @return the raw pointer type
     * @since 3.1.4
     */
    public static RawPointerType parse(int id) {
        switch (id) {
            case 1:
                return HeapStatisticsContainer;
            case 2:
                return HeapSpaceStatisticsContainer;
            default:
                return Invalid;
        }
    }

    /**
     * Gets id.
     *
     * @return the id
     * @since 3.1.4
     */
    public int getId() {
        return id;
    }
}
