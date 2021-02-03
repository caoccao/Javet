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

package com.caoccao.javet.values.reference;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.V8ValueReferenceType;

import java.util.ArrayList;
import java.util.List;

public class V8ValueObject extends V8ValueReference implements IV8ValueObject {

    public static final String FUNCTION_STRINGIFY = "stringify";
    public static final String FUNCTION_NEXT = "next";
    public static final String OBJECT_JSON = "JSON";
    public static final String PROPERTY_DONE = "done";
    public static final String PROPERTY_VALUE = "value";

    public V8ValueObject(long handle) {
        super(handle);
    }

    protected List<Integer> convertIteratorToIntegerList(V8ValueObject iterator) throws JavetException {
        List<Integer> keys = new ArrayList<>();
        while (true) {
            try (V8ValueObject next = iterator.invoke(FUNCTION_NEXT)) {
                if (next.getBoolean(PROPERTY_DONE)) {
                    break;
                }
                keys.add(next.getInteger(PROPERTY_VALUE));
            }
        }
        return keys;
    }

    protected List<V8Value> convertIteratorToV8ValueList(V8ValueObject iterator) throws JavetException {
        List<V8Value> keys = new ArrayList<>();
        while (true) {
            try (V8ValueObject next = iterator.invoke(FUNCTION_NEXT)) {
                if (next.getBoolean(PROPERTY_DONE)) {
                    break;
                }
                keys.add(next.get(PROPERTY_VALUE));
            }
        }
        return keys;
    }

    @Override
    public boolean delete(V8Value key) throws JavetException {
        checkV8Runtime();
        return v8Runtime.delete(this, key);
    }

    @Override
    public <T extends V8Value> T get(V8Value key) throws JavetException {
        checkV8Runtime();
        return v8Runtime.get(this, key);
    }

    @Override
    public int getType() {
        return V8ValueReferenceType.Object;
    }

    @Override
    public boolean hasOwnProperty(V8Value key) throws JavetException {
        checkV8Runtime();
        return v8Runtime.hasOwnProperty(this, key);
    }

    @Override
    public IV8ValueArray getOwnPropertyNames() throws JavetException {
        checkV8Runtime();
        return v8Runtime.getOwnPropertyNames(this);
    }

    @Override
    public IV8ValueArray getPropertyNames() throws JavetException {
        checkV8Runtime();
        return v8Runtime.getPropertyNames(this);
    }

    @Override
    public <T extends V8Value> T getProperty(V8Value key)
            throws JavetException {
        checkV8Runtime();
        return v8Runtime.getProperty(this, key);
    }

    @Override
    public <T extends V8Value> T invoke(String functionName, boolean returnResult, V8Value... v8Values)
            throws JavetException {
        checkV8Runtime();
        return v8Runtime.invoke(this, functionName, returnResult, v8Values);
    }

    @Override
    public boolean set(V8Value key, V8Value value) throws JavetException {
        checkV8Runtime();
        return v8Runtime.set(this, key, value);
    }

    @Override
    public boolean setProperty(V8Value key, V8Value value) throws JavetException {
        checkV8Runtime();
        return v8Runtime.setProperty(this, key, value);
    }

    @Override
    public String toJsonString() {
        try {
            checkV8Runtime();
            try (V8ValueObject jsonObject = v8Runtime.getGlobalObject().get(OBJECT_JSON)) {
                return jsonObject.invokeString(FUNCTION_STRINGIFY, this);
            }
        } catch (JavetException e) {
            return e.getMessage();
        }
    }
}
