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

package com.caoccao.javet.interop.monitoring;

import com.caoccao.javet.enums.RawPointerType;
import com.caoccao.javet.interfaces.IJavetRawPointer;
import com.caoccao.javet.utils.JavetDateTimeUtils;

import java.time.ZonedDateTime;
import java.util.concurrent.CompletableFuture;

/**
 * The type V8 statistics future.
 *
 * @param <T> the type parameter
 * @since 3.1.4
 */
public final class V8StatisticsFuture<T> extends CompletableFuture<T> implements IJavetRawPointer {
    /**
     * The constant INVALID_HANDLE.
     *
     * @since 3.1.4
     */
    public static final long INVALID_HANDLE = 0L;
    private final ZonedDateTime creationDateTime;
    private final RawPointerType rawPointerType;
    private long handle;

    /**
     * Instantiates a new V8 statistics future.
     *
     * @since 3.1.4
     */
    V8StatisticsFuture(int rawPointerTypeId) {
        super();
        creationDateTime = JavetDateTimeUtils.getUTCNow();
        handle = INVALID_HANDLE;
        rawPointerType = RawPointerType.parse(rawPointerTypeId);
    }

    /**
     * Gets creation date time.
     *
     * @return the creation date time
     * @since 3.1.4
     */
    public ZonedDateTime getCreationDateTime() {
        return creationDateTime;
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

    @Override
    public RawPointerType getRawPointerType() {
        return rawPointerType;
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
