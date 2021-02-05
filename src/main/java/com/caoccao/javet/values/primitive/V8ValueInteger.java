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
public final class V8ValueInteger extends V8ValuePrimitive<Integer> {
    public V8ValueInteger() {
        this(Integer.valueOf(0));
    }

    public V8ValueInteger(int value) {
        super(value);
    }

    @Override
    public V8ValueInteger toClone() throws JavetException {
        return v8Runtime.decorateV8Value(new V8ValueInteger(value));
    }

    public int toPrimitive() {
        return value.intValue();
    }
}
