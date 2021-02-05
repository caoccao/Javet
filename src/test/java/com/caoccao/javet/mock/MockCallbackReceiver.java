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

package com.caoccao.javet.mock;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8CallbackReceiver;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.reference.V8ValueArray;

public class MockCallbackReceiver extends V8CallbackReceiver {
    protected boolean called;

    public MockCallbackReceiver(V8Runtime v8Runtime) {
        super(v8Runtime);
        called = false;
    }

    public boolean isCalled() {
        return called;
    }

    public void setCalled(boolean called) {
        this.called = called;
    }

    public void blank() {
        called = true;
    }

    @Override
    public V8Value echo(V8Value arg) throws JavetException {
        called = true;
        return super.echo(arg);
    }

    @Override
    public String echoString(String str) {
        called = true;
        return super.echoString(str);
    }

    @Override
    public String echoString(V8Value arg) {
        called = true;
        return super.echoString(arg);
    }

    @Override
    public String echoString(V8Value... args) {
        called = true;
        return super.echoString(args);
    }

    public V8ValueArray echo(V8Value... args) throws JavetException {
        called = true;
        return super.echo(args);
    }
}
