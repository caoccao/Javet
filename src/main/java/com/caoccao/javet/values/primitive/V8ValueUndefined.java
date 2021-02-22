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
import com.caoccao.javet.values.V8Value;

@SuppressWarnings("unchecked")
public final class V8ValueUndefined extends V8Value {

    public static final String UNDEFINED = "undefined";

    public V8ValueUndefined() {
        super();
    }

    @Override
    protected void addReference() {
    }

    @Override
    public boolean equals(V8Value v8Value) {
        return v8Value instanceof V8ValueUndefined;
    }

    @Override
    protected void removeReference() {
    }

    @Override
    public boolean sameValue(V8Value v8Value) {
        return equals(v8Value);
    }

    @Override
    public boolean strictEquals(V8Value v8Value) {
        return equals(v8Value);
    }

    @Override
    public V8ValueUndefined toClone() throws JavetException {
        return v8Runtime.decorateV8Value(new V8ValueUndefined());
    }

    @Override
    public String toString() {
        return UNDEFINED;
    }
}
