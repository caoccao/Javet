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

package com.caoccao.javet.values.primitive;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.utils.JavetDateTimeUtils;

import java.time.ZonedDateTime;
import java.util.Objects;

@SuppressWarnings("unchecked")
public final class V8ValueZonedDateTime extends V8ValuePrimitive<ZonedDateTime> {
    public V8ValueZonedDateTime(V8Runtime v8Runtime) throws JavetException {
        this(v8Runtime, null);
    }

    public V8ValueZonedDateTime(V8Runtime v8Runtime, long jsTimestamp) throws JavetException {
        this(v8Runtime, JavetDateTimeUtils.toZonedDateTime(jsTimestamp));
    }

    public V8ValueZonedDateTime(V8Runtime v8Runtime, ZonedDateTime value) throws JavetException {
        super(v8Runtime, Objects.requireNonNull(value));
    }

    @Override
    public V8ValueZonedDateTime toClone(boolean referenceCopy) throws JavetException {
        return this;
    }

    public long toPrimitive() {
        return value.toInstant().toEpochMilli();
    }
}
