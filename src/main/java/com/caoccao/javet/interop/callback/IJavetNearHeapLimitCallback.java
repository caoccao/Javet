/*
 * Copyright (c) 2021-2026. caoccao.com Sam Cao
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

package com.caoccao.javet.interop.callback;

/**
 * The interface Javet near heap limit callback.
 * <p>
 * This callback is triggered when V8 heap usage approaches its memory limit.
 * The callback should return a new heap limit (in bytes) to either increase
 * the limit or maintain the current limit.
 *
 * @since 4.1.6
 */
public interface IJavetNearHeapLimitCallback {
    /**
     * The constant INITIAL_HEAP_LIMIT.
     *
     * @since 4.1.6
     */
    long INITIAL_HEAP_LIMIT = 512L * 1024L * 1024L; // 512 MB, initial heap limit in bytes.

    /**
     * Callback method invoked when V8 heap usage approaches its limit.
     *
     * @param currentHeapLimit the current heap limit in bytes
     * @param initialHeapLimit the initial heap limit in bytes
     * @return the new heap limit in bytes
     * @since 4.1.6
     */
    long callback(long currentHeapLimit, long initialHeapLimit);

    /**
     * Gets default heap limit.
     *
     * @return the default heap limit
     * @since 4.1.6
     */
    default long getDefaultHeapLimit() {
        return 0L; // Default implementation returns 0, indicating no change to the heap limit.
    }
}