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

@SuppressWarnings("unchecked")
public final class V8ValueLong extends V8ValuePrimitive<Long> {
    public V8ValueLong() {
        this(Long.valueOf(0));
    }

    public V8ValueLong(long value) {
        super(value);
    }

    public V8ValueLong(String value) {
        super(Long.valueOf(value));
    }

    @Override
    public V8ValueLong toClone() throws JavetException {
        return this;
    }

    public long toPrimitive() {
        return value.longValue();
    }
}
