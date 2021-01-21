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

package com.caoccao.javet.interop;

import com.caoccao.javet.V8Object;
import com.caoccao.javet.exceptions.JavetOSNotSupportedException;

public final class V8Runtime extends V8Object {

    private V8Runtime() {
    }

    public static V8Runtime create()
            throws JavetOSNotSupportedException {
        return create(null);
    }

    public static V8Runtime create(String isolate)
            throws JavetOSNotSupportedException {
        JavetLibLoader.load();
        return null;
    }

    @Override
    public V8Runtime getV8Runtime() {
        return this;
    }
}
