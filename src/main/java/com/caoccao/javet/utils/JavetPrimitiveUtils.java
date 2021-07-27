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

package com.caoccao.javet.utils;

public final class JavetPrimitiveUtils {
    public static Object toExactPrimitive(Class<?> expectedClass, Object object) {
        if (expectedClass == int.class && object instanceof Integer) {
            return ((Integer) object).intValue();
        }
        if (expectedClass == long.class && object instanceof Long) {
            return ((Long) object).longValue();
        }
        if (expectedClass == double.class && object instanceof Double) {
            return ((Double) object).doubleValue();
        }
        if (expectedClass == boolean.class && object instanceof Boolean) {
            return ((Boolean) object).booleanValue();
        }
        if (expectedClass == float.class && object instanceof Float) {
            return ((Float) object).floatValue();
        }
        if (expectedClass == byte.class && object instanceof Byte) {
            return ((Byte) object).byteValue();
        }
        if (expectedClass == short.class && object instanceof Short) {
            return ((Short) object).shortValue();
        }
        if (expectedClass == char.class && object instanceof Character) {
            return ((Character) object).charValue();
        }
        return null;
    }
}
