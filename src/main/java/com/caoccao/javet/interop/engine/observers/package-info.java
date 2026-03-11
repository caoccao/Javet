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
 * Observers for monitoring V8 runtime health and resource usage within an engine pool.
 * <p>
 * Each observer implements {@link com.caoccao.javet.interop.engine.observers.IV8RuntimeObserver}
 * and computes aggregate metrics across pooled runtimes:
 * <ul>
 *   <li>{@link com.caoccao.javet.interop.engine.observers.V8RuntimeObserverAverageV8HeapStatistics} - Average heap memory usage.</li>
 *   <li>{@link com.caoccao.javet.interop.engine.observers.V8RuntimeObserverAverageV8HeapSpaceStatistics} - Average per-space heap statistics.</li>
 *   <li>{@link com.caoccao.javet.interop.engine.observers.V8RuntimeObserverAverageReferenceCount} - Average live V8 reference count.</li>
 *   <li>{@link com.caoccao.javet.interop.engine.observers.V8RuntimeObserverAverageCallbackContextCount} - Average active callback context count.</li>
 *   <li>{@link com.caoccao.javet.interop.engine.observers.V8RuntimeObserverAverageV8ModuleCount} - Average loaded ES module count.</li>
 * </ul>
 *
 * @author Sam Cao
 * @since 1.0.5
 */
package com.caoccao.javet.interop.engine.observers;