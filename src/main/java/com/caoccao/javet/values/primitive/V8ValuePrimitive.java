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
public abstract class V8ValuePrimitive<T extends Object> extends V8Value {
    protected T value;

    public V8ValuePrimitive() {
        this(null);
    }

    public V8ValuePrimitive(T value) {
        this.value = value;
    }

    @Override
    protected void addReference() {
    }

    @Override
    public boolean equals(V8Value v8Value) {
        if (v8Value == null || !(v8Value instanceof V8ValuePrimitive)) {
            return false;
        }
        if (v8Value.getClass() != this.getClass()) {
            return false;
        }
        return getValue().equals(((V8ValuePrimitive<?>) v8Value).getValue());
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public boolean isEmpty() {
        return value == null;
    }

    public boolean isPresent() {
        return value != null;
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
    public String toString() {
        return isEmpty() ? null : value.toString();
    }
}
