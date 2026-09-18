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
 * The interface Javet microtasks completed callback.
 * <p>
 * This callback is triggered after a microtask checkpoint has drained the microtask queue,
 * either by an explicit checkpoint or by the automatic one that the microtasks policy performs.
 * It is triggered even if the queue was empty and no microtask was actually executed.
 * <p>
 * Executing scripts inside the callback does not re-trigger the microtasks and the callback,
 * because V8 suppresses the microtask execution while the callback is running.
 *
 * @since 6.0.1
 */
public interface IJavetMicrotasksCompletedCallback {
    /**
     * Callback method invoked after the microtask queue has been drained.
     *
     * @since 6.0.1
     */
    void callback();
}
