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

package com.caoccao.javet.exceptions;

import com.caoccao.javet.interop.monitoring.V8HeapStatistics;
import com.caoccao.javet.utils.SimpleMap;

import java.util.Objects;

/**
 * The type Javet out of memory exception.
 *
 * @since 1.0.4
 */
public class JavetOutOfMemoryException extends JavetException {
    /**
     * The V8 heap statistics.
     *
     * @since 1.0.4
     */
    protected V8HeapStatistics v8HeapStatistics;

    /**
     * Instantiates a new Javet out of memory exception.
     *
     * @param message          the message
     * @param v8HeapStatistics the V8 heap statistics
     * @since 1.0.4
     */
    public JavetOutOfMemoryException(String message, V8HeapStatistics v8HeapStatistics) {
        super(JavetError.RuntimeOutOfMemory, SimpleMap.of(
                JavetError.PARAMETER_MESSAGE, message,
                JavetError.PARAMETER_HEAP_STATISTICS, Objects.requireNonNull(v8HeapStatistics).toString()));
        this.v8HeapStatistics = v8HeapStatistics;
    }

    /**
     * Gets V8 heap statistics.
     *
     * @return the V8 heap statistics
     * @since 1.0.4
     */
    public V8HeapStatistics getV8HeapStatistics() {
        return v8HeapStatistics;
    }
}
