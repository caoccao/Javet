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

package com.caoccao.javet.utils.converters;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.primitive.*;

import java.time.ZonedDateTime;

@SuppressWarnings("unchecked")
public class JavetPrimitiveConverter implements IJavetConverter {
    public JavetPrimitiveConverter() {
    }

    @Override
    public Object toObject(V8Value v8Value) throws JavetException {
        if (v8Value == null || v8Value.isNull() || v8Value.isUndefined()) {
            return null;
        } else if (v8Value instanceof V8ValuePrimitive) {
            return ((V8ValuePrimitive) v8Value).getValue();
        }
        return v8Value;
    }

    @Override
    public <T extends V8Value> T toV8Value(V8Runtime v8Runtime, Object object) throws JavetException {
        V8Value v8Value = null;
        if (object == null) {
            v8Value = v8Runtime.createV8ValueNull();
        } else if (object instanceof V8Value) {
            v8Value = (V8Value) object;
        } else if (object instanceof Boolean) {
            v8Value = v8Runtime.createV8ValueBoolean((Boolean) object);
        } else if (object instanceof Double) {
            v8Value = new V8ValueDouble((Double) object);
        } else if (object instanceof Float) {
            v8Value = new V8ValueDouble((Float) object);
        } else if (object instanceof Integer) {
            v8Value = v8Runtime.createV8ValueInteger((Integer) object);
        } else if (object instanceof Long) {
            v8Value = v8Runtime.createV8ValueLong((Long) object);
        } else if (object instanceof Short) {
            v8Value = v8Runtime.createV8ValueInteger((Short) object);
        } else if (object instanceof String) {
            v8Value = new V8ValueString((String) object);
        } else if (object instanceof ZonedDateTime) {
            v8Value = new V8ValueZonedDateTime((ZonedDateTime) object);
        } else if (object instanceof Byte) {
            v8Value = v8Runtime.createV8ValueInteger((Byte) object);
        } else {
            v8Value = v8Runtime.createV8ValueUndefined();
        }
        return (T) v8Runtime.decorateV8Value(v8Value);
    }

}
