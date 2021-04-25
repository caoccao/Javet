/*
 *   Copyright (c) 2021. caoccao.com Sam Cao
 *   All rights reserved.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.caoccao.javet.interop.converters;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.values.V8Value;

@SuppressWarnings("unchecked")
public interface IJavetConverter {
    default boolean getDefaultBoolean() {
        return false;
    }

    default byte getDefaultByte() {
        return (byte) 0;
    }

    default char getDefaultChar() {
        return '\0';
    }

    default double getDefaultDouble() {
        return 0D;
    }

    default float getDefaultFloat() {
        return 0F;
    }

    default int getDefaultInt() {
        return 0;
    }

    default long getDefaultLong() {
        return 0L;
    }

    default short getDefaultShort() {
        return (short) 0;
    }

    Object toObject(V8Value v8Value) throws JavetException;

    <T extends V8Value> T toV8Value(V8Runtime v8Runtime, Object object) throws JavetException;
}
