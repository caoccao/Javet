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

import com.caoccao.javet.annotations.CheckReturnValue;
import com.caoccao.javet.exceptions.JavetException;

/**
 * Interface for V8 typed array values (e.g., Int8Array, Uint8Array, Float64Array).
 */
public interface IV8ValueTypedArray extends IV8ValueObject {
    /**
     * Returns the underlying array buffer of this typed array.
     *
     * @return the backing {@link V8ValueArrayBuffer}
     * @throws JavetException the javet exception
     */
    @CheckReturnValue
    V8ValueArrayBuffer getBuffer() throws JavetException;

    /**
     * Returns the byte length of this typed array.
     *
     * @return the byte length
     * @throws JavetException the javet exception
     */
    int getByteLength() throws JavetException;

    /**
     * Returns the byte offset of this typed array within its underlying buffer.
     *
     * @return the byte offset
     * @throws JavetException the javet exception
     */
    int getByteOffset() throws JavetException;

    /**
     * Returns the number of elements in this typed array.
     *
     * @return the element count
     * @throws JavetException the javet exception
     */
    int getLength() throws JavetException;

    /**
     * Returns the size in bytes of each element in this typed array.
     *
     * @return the element size in bytes
     */
    int getSizeInBytes();

    /**
     * Returns whether this typed array is valid.
     *
     * @return {@code true} if this typed array is valid, {@code false} otherwise
     */
    boolean isValid();
}
