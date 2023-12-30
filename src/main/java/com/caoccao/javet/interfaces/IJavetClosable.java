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

package com.caoccao.javet.interfaces;

import com.caoccao.javet.exceptions.JavetException;

/**
 * The interface Javet closable is for recycling the unmanaged resource.
 * <p>
 * Why {@link AutoCloseable} is not feasible?
 * {@link AutoCloseable} requires the implementation to handle checked exception,
 * however, {@link JavetException} is supposed to be handled by the caller.
 * This interface allows throwing the {@link JavetException} in <code>close()</code>.
 *
 * @since 0.7.0
 */
public interface IJavetClosable extends AutoCloseable {
    void close() throws JavetException;

    /**
     * Is closed.
     *
     * @return true : closed, false: not closed
     * @since 0.9.10
     */
    boolean isClosed();
}
