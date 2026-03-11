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

package com.caoccao.javet.values.reference;

import com.caoccao.javet.enums.V8ValueReferenceType;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.values.IV8Value;

/**
 * Interface for V8 reference values that require explicit lifecycle management.
 */
public interface IV8ValueReference extends IV8Value {
    /**
     * Clears the weak reference state of this V8 value, making it strong again.
     *
     * @throws JavetException the javet exception
     */
    void clearWeak() throws JavetException;

    /**
     * Closes this V8 reference value.
     *
     * @param forceClose whether to force close even if the reference is still in use
     * @throws JavetException the javet exception
     */
    void close(boolean forceClose) throws JavetException;

    /**
     * Returns the native handle of this V8 reference.
     *
     * @return the native handle
     */
    long getHandle();

    /**
     * Returns the reference type of this V8 value.
     *
     * @return the V8 value reference type
     */
    V8ValueReferenceType getType();

    /**
     * Returns whether this V8 reference has been closed.
     *
     * @return {@code true} if this reference is closed, {@code false} otherwise
     */
    boolean isClosed();

    /**
     * Returns whether this V8 reference is weak.
     *
     * @return {@code true} if this reference is weak, {@code false} otherwise
     * @throws JavetException the javet exception
     */
    boolean isWeak() throws JavetException;

    /**
     * Returns whether this V8 reference is weak, optionally forcing a sync with the native layer.
     *
     * @param forceSync whether to force synchronization with the native state
     * @return {@code true} if this reference is weak, {@code false} otherwise
     * @throws JavetException the javet exception
     */
    boolean isWeak(boolean forceSync) throws JavetException;

    /**
     * Sets this V8 reference to weak, allowing it to be garbage collected by V8.
     *
     * @throws JavetException the javet exception
     */
    void setWeak() throws JavetException;
}
