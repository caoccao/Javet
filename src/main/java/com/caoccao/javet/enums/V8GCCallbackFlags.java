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
 * The enum V8 GC callback flags.
 *
 * @since 1.0.3
 */
public enum V8GCCallbackFlags implements IEnumBitset {
    /**
     * No GC callback flags.
     *
     * @since 1.0.3
     */
    NoGCCallbackFlags(0),
    /**
     * GC callback flag construct retained object infos.
     *
     * @since 1.0.3
     */
    GCCallbackFlagConstructRetainedObjectInfos(1 << 1),
    /**
     * GC callback flag forced.
     *
     * @since 1.0.3
     */
    GCCallbackFlagForced(1 << 2),
    /**
     * GC callback flag synchronous phantom callback processing.
     *
     * @since 1.0.3
     */
    GCCallbackFlagSynchronousPhantomCallbackProcessing(1 << 3),
    /**
     * GC callback flag collect all available garbage.
     *
     * @since 1.0.3
     */
    GCCallbackFlagCollectAllAvailableGarbage(1 << 4),
    /**
     * GC callback flag collect all external memory.
     *
     * @since 1.0.3
     */
    GCCallbackFlagCollectAllExternalMemory(1 << 5),
    /**
     * GC callback schedule idle garbage collection.
     *
     * @since 1.0.3
     */
    GCCallbackScheduleIdleGarbageCollection(1 << 6);

    private int value;

    V8GCCallbackFlags(int value) {
        this.value = value;
    }

    @Override
    public int getValue() {
        return value;
    }
}
