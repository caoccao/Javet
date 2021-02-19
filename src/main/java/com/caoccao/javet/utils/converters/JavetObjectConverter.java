/*
 * MCHR CONFIDENTIAL
 *
 * Copyright (c) 2020-2021 mchrcloud.com
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of MCHR Incorporated and its suppliers, if any.
 * The intellectual and technical concepts contained
 * herein are proprietary to MCHR Incorporated and its suppliers
 * and may be covered by Chinese and Foreign Patents, patents in process,
 * and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from MCHR Incorporated.
 */

package com.caoccao.javet.utils.converters;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.primitive.*;
import com.caoccao.javet.values.reference.*;
import com.caoccao.javet.values.virtual.V8VirtualList;

import java.util.*;

public class JavetObjectConverter extends JavetPrimitiveConverter {

    public JavetObjectConverter() {
        super();
    }

    @Override
    public Object toObject(V8Value v8Value) throws JavetException {
        Object returnObject = super.toObject(v8Value);
        if (returnObject == null || !(returnObject instanceof V8Value)) {
            return returnObject;
        }
        if (v8Value instanceof V8ValueArray) {
            V8ValueArray v8ValueArray = (V8ValueArray) v8Value;
            List<Object> list = new ArrayList<>();
            final int length = v8ValueArray.getLength();
            for (int i = 0; i < length; ++i) {
                try (V8Value childV8Value = v8ValueArray.get(i)) {
                    list.add(toObject(childV8Value));
                }
            }
            return list;
        } else if (v8Value instanceof V8ValueArguments) {
            V8ValueArguments v8ValueArguments = (V8ValueArguments) v8Value;
            List<Object> list = new ArrayList<>();
            final int length = v8ValueArguments.getLength();
            for (int i = 0; i < length; ++i) {
                try (V8Value childV8Value = v8ValueArguments.get(i)) {
                    list.add(toObject(childV8Value));
                }
            }
            return list;
        } else if (v8Value instanceof V8ValueSet) {
            V8ValueSet v8ValueSet = (V8ValueSet) v8Value;
            HashSet<Object> set = new HashSet<>();
            try (V8VirtualList<V8Value> items = v8ValueSet.getKeys()) {
                for (V8Value item : items) {
                    set.add(toObject(item));
                }
            }
            return set;
        } else if (v8Value instanceof V8ValueMap || v8Value instanceof V8ValueObject) {
            V8ValueObject v8ValueObject = (V8ValueObject) v8Value;
            Map<String, Object> map = new HashMap<>();
            try (IV8ValueArray iV8ValueArray = v8ValueObject.getOwnPropertyNames()) {
                final int length = iV8ValueArray.getLength();
                for (int i = 0; i < length; ++i) {
                    try (V8Value key = iV8ValueArray.get(i)) {
                        try (V8Value value = v8ValueObject.get(key)) {
                            map.put(toObject(key).toString(), toObject(value));
                        }
                    }
                }
            }
            return map;
        }
        return v8Value;
    }

    @Override
    public V8Value toV8Value(V8Runtime v8Runtime, Object object) throws JavetException {
        V8Value v8Value = super.toV8Value(v8Runtime, object);
        if (v8Value != null && !(v8Value instanceof V8ValueUndefined)) {
            return v8Value;
        }
        if (object instanceof Map) {
            V8ValueObject v8ValueObject = v8Runtime.createV8ValueObject();
            Map mapObject = (Map) object;
            for (Object key : mapObject.keySet()) {
                try (V8Value childV8Value = toV8Value(v8Runtime, mapObject.get(key))) {
                    String childStringKey = key instanceof String ? (String) key : key.toString();
                    v8ValueObject.set(childStringKey, childV8Value);
                }
            }
            v8Value = v8ValueObject;
        } else if (object instanceof Set) {
            V8ValueSet v8ValueSet = v8Runtime.createV8ValueSet();
            Set setObject = (Set) object;
            for (Object item : setObject) {
                try (V8Value childV8Value = toV8Value(v8Runtime, item)) {
                    v8ValueSet.add(childV8Value);
                }
            }
            v8Value = v8ValueSet;
        } else if (object instanceof Collection) {
            V8ValueArray v8ValueArray = v8Runtime.createV8ValueArray();
            for (Object item : (Collection) object) {
                try (V8Value childV8Value = toV8Value(v8Runtime, item)) {
                    v8ValueArray.push(childV8Value);
                }
            }
            v8Value = v8ValueArray;
        } else if (object instanceof boolean[]) {
            V8ValueArray v8ValueArray = v8Runtime.createV8ValueArray();
            for (boolean item : (boolean[]) object) {
                v8ValueArray.push(new V8ValueBoolean(item));
            }
            v8Value = v8ValueArray;
        } else if (object instanceof byte[]) {
            // TODO To support byte[]
        } else if (object instanceof double[]) {
            V8ValueArray v8ValueArray = v8Runtime.createV8ValueArray();
            for (double item : (double[]) object) {
                v8ValueArray.push(new V8ValueDouble(item));
            }
            v8Value = v8ValueArray;
        } else if (object instanceof float[]) {
            V8ValueArray v8ValueArray = v8Runtime.createV8ValueArray();
            for (float item : (float[]) object) {
                v8ValueArray.push(new V8ValueDouble(item));
            }
            v8Value = v8ValueArray;
        } else if (object instanceof int[]) {
            V8ValueArray v8ValueArray = v8Runtime.createV8ValueArray();
            for (int item : (int[]) object) {
                v8ValueArray.push(new V8ValueInteger(item));
            }
            v8Value = v8ValueArray;
        } else if (object instanceof long[]) {
            V8ValueArray v8ValueArray = v8Runtime.createV8ValueArray();
            for (long item : (long[]) object) {
                v8ValueArray.push(new V8ValueLong(item));
            }
            v8Value = v8ValueArray;
        } else if (object instanceof String[]) {
            V8ValueArray v8ValueArray = v8Runtime.createV8ValueArray();
            for (String item : (String[]) object) {
                v8ValueArray.push(new V8ValueString(item));
            }
            v8Value = v8ValueArray;
        } else if (object.getClass().isArray()) {
            V8ValueArray v8ValueArray = v8Runtime.createV8ValueArray();
            for (Object item : (Object[]) object) {
                try (V8Value childV8Value = toV8Value(v8Runtime, item)) {
                    v8ValueArray.push(childV8Value);
                }
            }
            v8Value = v8ValueArray;
        }
        return v8Runtime.decorateV8Value(v8Value);
    }
}
