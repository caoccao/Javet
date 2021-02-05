/*
 *    Copyright 2021. caoccao.com Sam Cao
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

package com.caoccao.javet.values.primitive;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.utils.JavetDateTimeUtils;

import java.time.ZonedDateTime;

@SuppressWarnings("unchecked")
public final class V8ValueZonedDateTime extends V8ValuePrimitive<ZonedDateTime> {
    public V8ValueZonedDateTime() {
        this(null);
    }

    public V8ValueZonedDateTime(ZonedDateTime value) {
        super(value);
    }

    public V8ValueZonedDateTime(long jsTimestamp) {
        super(JavetDateTimeUtils.toZonedDateTime(jsTimestamp));
    }

    @Override
    public V8ValueZonedDateTime toClone() throws JavetException {
        return v8Runtime.decorateV8Value(new V8ValueZonedDateTime(value));
    }

    public long toPrimitive() {
        return value.toInstant().toEpochMilli();
    }
}
