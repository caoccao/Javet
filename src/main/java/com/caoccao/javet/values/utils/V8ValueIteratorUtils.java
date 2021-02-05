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

package com.caoccao.javet.values.utils;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.reference.V8ValueObject;
import com.caoccao.javet.values.virtual.V8VirtualList;

@SuppressWarnings("unchecked")
public final class V8ValueIteratorUtils {
    public static final String FUNCTION_NEXT = "next";
    public static final String PROPERTY_DONE = "done";
    public static final String PROPERTY_VALUE = "value";

    private V8ValueIteratorUtils() {
    }

    public static V8VirtualList<Integer> convertIteratorToIntegerList(V8ValueObject iterator) throws JavetException {
        V8VirtualList<Integer> resultList = new V8VirtualList<>();
        while (true) {
            try (V8ValueObject next = iterator.invoke(FUNCTION_NEXT)) {
                if (next.getBoolean(PROPERTY_DONE)) {
                    break;
                }
                resultList.add(next.getInteger(PROPERTY_VALUE));
            }
        }
        return resultList;
    }

    public static V8VirtualList<V8Value> convertIteratorToV8ValueList(V8ValueObject iterator) throws JavetException {
        V8VirtualList<V8Value> resultList = new V8VirtualList<>();
        while (true) {
            try (V8ValueObject next = iterator.invoke(FUNCTION_NEXT)) {
                if (next.getBoolean(PROPERTY_DONE)) {
                    break;
                }
                resultList.add(next.get(PROPERTY_VALUE));
            }
        }
        return resultList;
    }
}
