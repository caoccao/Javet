/*
 * Copyright (c) 2021-2025. caoccao.com Sam Cao
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

public interface IV8ValueReference extends IV8Value {
    void clearWeak() throws JavetException;

    void close(boolean forceClose) throws JavetException;

    long getHandle();

    V8ValueReferenceType getType();

    boolean isClosed();

    boolean isWeak() throws JavetException;

    boolean isWeak(boolean forceSync) throws JavetException;

    void setWeak() throws JavetException;
}
