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

import com.caoccao.javet.values.V8Value;

public final class V8ValueNull extends V8Value {

    public static final String NULL = "null";

    public V8ValueNull() {
        super();
    }

    @Override
    protected void addReference() {
    }

    @Override
    public V8ValueNull clone() {
        return new V8ValueNull();
    }

    @Override
    protected void releaseReference() {
    }

    @Override
    public String toString() {
        return NULL;
    }
}
