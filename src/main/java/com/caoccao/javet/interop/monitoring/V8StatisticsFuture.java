/*
 * Copyright (c) 2024. caoccao.com Sam Cao
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

import java.util.concurrent.CompletableFuture;

/**
 * The type V8 statistics future.
 *
 * @param <T> the type parameter
 * @since 3.1.4
 */
public final class V8StatisticsFuture<T> extends CompletableFuture<T> {
    /**
     * The constant INVALID_HANDLE.
     *
     * @since 3.1.4
     */
    public static final long INVALID_HANDLE = 0L;
    private final long creationTime;
    private long handle;

    /**
     * Instantiates a new V 8 statistics future.
     *
     * @since 3.1.4
     */
    V8StatisticsFuture() {
        super();
        creationTime = System.currentTimeMillis();
        handle = INVALID_HANDLE;
    }

    /**
     * Gets creation time.
     *
     * @return the creation time
     * @since 3.1.4
     */
    public long getCreationTime() {
        return creationTime;
    }

    /**
     * Gets handle.
     *
     * @return the handle
     * @since 3.1.4
     */
    public long getHandle() {
        return handle;
    }

    /**
     * Is valid.
     *
     * @return true : yes, false : no
     * @since 3.1.4
     */
    public boolean isValid() {
        return handle != INVALID_HANDLE;
    }

    /**
     * Sets handle.
     *
     * @param handle the handle
     * @since 3.1.4
     */
    void setHandle(long handle) {
        this.handle = handle;
    }
}
