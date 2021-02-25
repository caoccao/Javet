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
import com.caoccao.javet.utils.JavetNativeMap;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.primitive.*;
import com.caoccao.javet.values.reference.*;

import java.util.*;

@SuppressWarnings("unchecked")
public class JavetObjectConverter extends JavetPrimitiveConverter {

    public static final String PROPERTY_CONSTRUCTOR = "constructor";
    public static final String PROPERTY_NAME = "name";

    public JavetObjectConverter() {
        super();
    }

    protected Map<String, Object> createNativeMap() {
        return new JavetNativeMap();
    }

    protected boolean isNativeMap(Object object) {
        return object instanceof JavetNativeMap;
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
            try (IV8ValueIterator<V8Value> iterator = v8ValueSet.getKeys()) {
                while (true) {
                    try (V8Value key = iterator.getNext()) {
                        if (key == null) {
                            break;
                        }
                        set.add(toObject(key));
                    }
                }
            }
            return set;
        } else if (v8Value instanceof V8ValueMap) {
            V8ValueMap v8ValueMap = (V8ValueMap) v8Value;
            Map<String, Object> map = createNativeMap();
            try (IV8ValueIterator<V8ValueArray> iterator = v8ValueMap.getEntries()) {
                while (true) {
                    try (V8ValueArray entry = iterator.getNext()) {
                        if (entry == null) {
                            break;
                        }
                        try (V8Value key = entry.get(0); V8Value value = entry.get(1);) {
                            map.put(key.toString(), toObject(value));
                        }
                    }
                }
            }
            return map;
        } else if (v8Value instanceof V8ValueObject) {
            V8ValueObject v8ValueObject = (V8ValueObject) v8Value;
            Map<String, Object> map = new HashMap<>();
            try (IV8ValueArray iV8ValueArray = v8ValueObject.getOwnPropertyNames()) {
                final int length = iV8ValueArray.getLength();
                for (int i = 0; i < length; ++i) {
                    try (V8Value key = iV8ValueArray.get(i)) {
                        String keyString = key.toString();
                        /*
                            Constructor is treated differently because it references to itself.
                            Otherwise stack overflow will take place.
                         */
                        if (PROPERTY_CONSTRUCTOR.equals(keyString)) {
                            try (V8ValueObject v8ValueObjectValue = v8ValueObject.get(keyString)) {
                                map.put(keyString, v8ValueObjectValue.getString(PROPERTY_NAME));
                            }
                        } else {
                            try (V8Value value = v8ValueObject.get(key)) {
                                map.put(keyString, toObject(value));
                            }
                        }
                    }
                }
            }
            return map;
        }
        return v8Value;
    }

    @Override
    public <T extends V8Value> T toV8Value(V8Runtime v8Runtime, Object object) throws JavetException {
        V8Value v8Value = super.toV8Value(v8Runtime, object);
        if (v8Value != null && !(v8Value.isUndefined())) {
            return (T) v8Value;
        }
        if (isNativeMap(object)) {
            V8ValueMap v8ValueMap = v8Runtime.createV8ValueMap();
            Map mapObject = (Map) object;
            for (Object key : mapObject.keySet()) {
                try (V8Value childV8Value = toV8Value(v8Runtime, mapObject.get(key))) {
                    String childStringKey = key instanceof String ? (String) key : key.toString();
                    v8ValueMap.set(childStringKey, childV8Value);
                }
            }
            v8Value = v8ValueMap;
        } else if (object instanceof Map) {
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
        return (T) v8Runtime.decorateV8Value(v8Value);
    }
}
