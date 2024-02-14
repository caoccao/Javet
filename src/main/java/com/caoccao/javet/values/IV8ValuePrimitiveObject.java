/*
 * Copyright (c) 2024. caoccao.com Sam Cao
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

package com.caoccao.javet.values;

import com.caoccao.javet.exceptions.JavetException;

/**
 * The interface V8 value primitive object.
 *
 * @param <T> the type parameter
 * @since 3.0.4
 */
public interface IV8ValuePrimitiveObject<T extends V8Value> extends IV8Value {
    @Override
    default boolean asBoolean() throws JavetException {
        return valueOf().asBoolean();
    }

    @Override
    default double asDouble() throws JavetException {
        return valueOf().asDouble();
    }

    @Override
    default int asInt() throws JavetException {
        return valueOf().asInt();
    }

    @Override
    default long asLong() throws JavetException {
        return valueOf().asLong();
    }

    @Override
    default String asString() throws JavetException {
        return valueOf().asString();
    }

    /**
     * Get the primitive value of the primitive object.
     *
     * @return the primitive value
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    T valueOf() throws JavetException;
}
