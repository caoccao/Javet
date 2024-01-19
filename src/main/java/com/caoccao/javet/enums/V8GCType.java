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

import com.caoccao.javet.interfaces.IEnumBitset;

/**
 * The enum V8 gc type.
 *
 * @since 1.0.3
 */
public enum V8GCType implements IEnumBitset {
    /**
     * GC type scavenge.
     *
     * @since 1.0.3
     */
    GCTypeScavenge(1),
    /**
     * GC type mark sweep compact.
     *
     * @since 1.0.3
     */
    GCTypeMarkSweepCompact(1 << 1),
    /**
     * GC type incremental marking.
     *
     * @since 1.0.3
     */
    GCTypeIncrementalMarking(1 << 2),
    /**
     * GC type process weak callbacks.
     *
     * @since 1.0.3
     */
    GCTypeProcessWeakCallbacks(1 << 3);

    private final int value;

    V8GCType(int value) {
        this.value = value;
    }

    @Override
    public int getValue() {
        return value;
    }
}
