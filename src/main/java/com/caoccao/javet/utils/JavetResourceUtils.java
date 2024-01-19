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

package com.caoccao.javet.utils;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interfaces.IJavetClosable;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.reference.IV8ValueReference;

import java.util.Collection;

/**
 * The type Javet resource utils.
 *
 * @since 0.7.0
 */
public final class JavetResourceUtils {
    private JavetResourceUtils() {
    }

    /**
     * Is closed.
     *
     * @param object the object
     * @return the boolean
     * @since 0.9.10
     */
    public static boolean isClosed(Object object) {
        if (object instanceof IJavetClosable) {
            return ((IJavetClosable) object).isClosed();
        }
        return true;
    }

    /**
     * Safe close.
     *
     * @param objects the objects
     * @since 0.7.1
     */
    public static void safeClose(Object... objects) {
        for (Object object : objects) {
            safeClose(object);
        }
    }

    /**
     * Safe close.
     *
     * @param v8Values the V8 values
     * @since 2.2.0
     */
    public static void safeClose(V8Value... v8Values) {
        for (V8Value v8Value : v8Values) {
            safeClose(v8Value);
        }
    }

    /**
     * Safe close.
     *
     * @param object the object
     * @since 0.7.1
     */
    public static void safeClose(Object object) {
        if (object == null) {
            return;
        }
        if (object instanceof IV8ValueReference) {
            try {
                IV8ValueReference iV8ValueReference = (IV8ValueReference) object;
                if (!iV8ValueReference.isClosed()) {
                    iV8ValueReference.close();
                }
            } catch (JavetException ignored) {
            }
        } else if (object instanceof IJavetClosable) {
            try {
                ((IJavetClosable) object).close();
            } catch (JavetException ignored) {
            }
        } else if (object instanceof V8Value[]) {
            for (V8Value v8Value : (V8Value[]) object) {
                safeClose(v8Value);
            }
        } else if (object.getClass().isArray()) {
            for (Object o : (Object[]) object) {
                safeClose(o);
            }
        } else if (object instanceof Collection) {
            for (Object o : (Collection<?>) object) {
                safeClose(o);
            }
        }
    }

    /**
     * To clone V8 value array.
     *
     * @param v8Values the V8 values
     * @return the V8 value array
     * @throws JavetException the javet exception
     * @since 0.9.11
     */
    public static V8Value[] toClone(V8Value[] v8Values) throws JavetException {
        if (v8Values == null) {
            return null;
        }
        V8Value[] clonedV8Values = new V8Value[v8Values.length];
        for (int i = 0; i < v8Values.length; ++i) {
            clonedV8Values[i] = v8Values[i].toClone();
        }
        return clonedV8Values;
    }
}
