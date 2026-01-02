/*
 * Copyright (c)2025-2026. caoccao.com Sam Cao
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
 * The enum V8 runtime termination mode.
 *
 * @since 4.1.6
 */
public enum V8RuntimeTerminationMode {
    /**
     * The asynchronous termination mode.
     * <p>
     * This mode indicates that the V8 runtime will be terminated asynchronously.
     * It creates a new thread that executes the termination.
     *
     * @since 4.1.6
     */
    Asynchronous,
    /**
     * The synchronous termination mode.
     * <p>
     * This mode indicates that the V8 runtime will be terminated synchronously.
     * It blocks the current thread until the termination call is complete.
     *
     * @since 4.1.6
     */
    Synchronous,
}
