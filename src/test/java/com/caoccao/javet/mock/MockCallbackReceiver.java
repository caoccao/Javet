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

package com.caoccao.javet.mock;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.utils.receivers.JavetCallbackReceiver;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.primitive.V8ValueString;
import com.caoccao.javet.values.reference.V8ValueArray;
import com.caoccao.javet.values.reference.V8ValueObject;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class MockCallbackReceiver extends JavetCallbackReceiver {
    protected boolean called;
    protected String value;

    public MockCallbackReceiver(V8Runtime v8Runtime) {
        super(v8Runtime);
        called = false;
        value = null;
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
    public V8ValueArray echo(V8Value... args) throws JavetException {
        called = true;
        return super.echo(args);
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

    public V8Value echoThis(V8Value thisObject) throws JavetException {
        called = true;
        return v8Runtime.createV8ValueString(((V8ValueObject) thisObject).toJsonString());
    }

    public V8ValueString echoThis(V8Value thisObject, V8Value arg) throws JavetException {
        called = true;
        try (V8ValueArray v8ValueArray = v8Runtime.createV8ValueArray()) {
            try (V8Value clonedThisObject = thisObject.toClone()) {
                v8ValueArray.push(clonedThisObject);
            }
            try (V8Value clonedArg = arg.toClone()) {
                v8ValueArray.push(clonedArg);
            }
            return v8Runtime.createV8ValueString(v8ValueArray.toJsonString());
        }
    }

    public V8ValueArray echoThis(V8Value thisObject, V8Value... args) throws JavetException {
        called = true;
        V8ValueArray v8ValueArray = v8Runtime.createV8ValueArray();
        try (V8Value clonedThisObject = thisObject.toClone()) {
            v8ValueArray.push(clonedThisObject);
        }
        for (V8Value arg : args) {
            try (V8Value clonedArg = arg.toClone()) {
                v8ValueArray.push(clonedArg);
            }
        }
        return v8ValueArray;
    }

    public String echoThisString(V8Value thisObject, String arg) throws JavetException {
        called = true;
        try (V8ValueArray v8ValueArray = v8Runtime.createV8ValueArray()) {
            try (V8Value clonedThisObject = thisObject.toClone()) {
                v8ValueArray.push(clonedThisObject);
            }
            v8ValueArray.push(arg);
            return v8ValueArray.toJsonString();
        }
    }

    public void error() throws Exception {
        called = true;
        throw new Exception("Mock error");
    }

    public String getValue() {
        called = true;
        return value;
    }

    public boolean isCalled() {
        return called;
    }

    public String joinIntegerArrayWithThis(
            V8ValueObject thisObject,
            String s, Integer... integers) {
        called = true;
        List<String> lines = new ArrayList<>();
        lines.add(thisObject.toJsonString());
        lines.add(s);
        for (Integer integer : integers) {
            lines.add(integer.toString());
        }
        return String.join(",", lines);
    }

    public String joinWithThis(
            V8ValueObject thisObject,
            Boolean b, Double d, Integer i, Long l, String s, ZonedDateTime z, V8ValueString v) {
        called = true;
        List<String> lines = new ArrayList<>();
        lines.add(thisObject.toJsonString());
        lines.add(b.toString());
        lines.add(d.toString());
        lines.add(i.toString());
        lines.add(l.toString());
        lines.add(s);
        lines.add(z.withZoneSameInstant(ZoneId.of("UTC")).toString());
        lines.add(v.getValue());
        return String.join(",", lines);
    }

    public String joinWithoutThis(
            Boolean b, Double d, Integer i, Long l, String s, ZonedDateTime z, V8ValueString v) {
        called = true;
        List<String> lines = new ArrayList<>();
        lines.add(b.toString());
        lines.add(d.toString());
        lines.add(i.toString());
        lines.add(l.toString());
        lines.add(s);
        if (z != null) {
            lines.add(z.withZoneSameInstant(ZoneId.of("UTC")).toString());
        }
        if (v != null) {
            lines.add(v.getValue());
        }
        return String.join(",", lines);
    }

    public void setCalled(boolean called) {
        this.called = called;
    }

    public void setValue(String value) {
        called = true;
        this.value = value;
    }
}
