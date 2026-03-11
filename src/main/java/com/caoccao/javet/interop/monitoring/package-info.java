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

/**
 * V8 runtime monitoring and memory statistics.
 * <p>
 * Use these classes to inspect V8 heap usage and diagnose memory issues:
 * <ul>
 *   <li>{@link com.caoccao.javet.interop.monitoring.V8HeapStatistics} - Overall V8 heap memory usage (total size, used size, heap limit, etc.).</li>
 *   <li>{@link com.caoccao.javet.interop.monitoring.V8HeapSpaceStatistics} - Per-space heap statistics (old space, new space, code space, etc.).</li>
 *   <li>{@link com.caoccao.javet.interop.monitoring.V8SharedMemoryStatistics} - Shared per-process V8 memory statistics.</li>
 *   <li>{@link com.caoccao.javet.interop.monitoring.V8StatisticsFuture} - Asynchronous future for collecting statistics from the engine pool daemon.</li>
 * </ul>
 *
 * @since 1.0.0
 * @author Sam Cao
 */
package com.caoccao.javet.interop.monitoring;