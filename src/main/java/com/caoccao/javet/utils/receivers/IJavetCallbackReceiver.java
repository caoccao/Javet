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

package com.caoccao.javet.utils.receivers;

import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.values.V8Value;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * The interface Javet callback receiver.
 */
public interface IJavetCallbackReceiver {

    /**
     * Gets method that does not take any arguments by method name.
     *
     * @param methodName the method name
     * @return the method
     * @throws NoSuchMethodException the no such method exception
     */
    default Method getMethod(String methodName) throws NoSuchMethodException {
        return getMethod(methodName, false, 0);
    }

    /**
     * Gets method.
     *
     * @param methodName the method name
     * @param argCount   the arg count
     * @return the method
     * @throws NoSuchMethodException the no such method exception
     */
    default Method getMethod(String methodName, int argCount) throws NoSuchMethodException {
        return getMethod(methodName, false, argCount);
    }

    /**
     * Gets method.
     *
     * @param methodName         the method name
     * @param thisObjectRequired this object required
     * @return the method
     * @throws NoSuchMethodException the no such method exception
     */
    default Method getMethod(String methodName, boolean thisObjectRequired) throws NoSuchMethodException {
        return getMethod(methodName, thisObjectRequired, 0);
    }

    /**
     * Gets method that takes given number of arguments by method name.
     *
     * @param methodName         the method name
     * @param thisObjectRequired this object required
     * @param argCount           the arg count
     * @return the method
     * @throws NoSuchMethodException the no such method exception
     */
    default Method getMethod(String methodName, boolean thisObjectRequired, int argCount)
            throws NoSuchMethodException {
        if (argCount < 0) {
            if (thisObjectRequired) {
                return getClass().getMethod(methodName, V8Value.class, V8Value[].class);
            } else {
                return getClass().getMethod(methodName, V8Value[].class);
            }
        } else if (argCount == 0) {
            if (thisObjectRequired) {
                return getClass().getMethod(methodName, V8Value.class);
            } else {
                return getClass().getMethod(methodName);
            }
        } else {
            Class<?>[] classes = new Class[thisObjectRequired ? argCount + 1 : argCount];
            Arrays.fill(classes, V8Value.class);
            return getClass().getMethod(methodName, classes);
        }
    }

    /**
     * Gets method that is customized to given argument types by method name.
     *
     * @param methodName     the method name
     * @param parameterTypes the parameter types
     * @return the method
     * @throws NoSuchMethodException the no such method exception
     */
    default Method getMethod(String methodName, Class<?>... parameterTypes) throws NoSuchMethodException {
        return getClass().getMethod(methodName, parameterTypes);
    }

    /**
     * Gets method that takes an arbitrary number of arguments by method name.
     *
     * @param methodName the method name
     * @return the method varargs
     * @throws NoSuchMethodException the no such method exception
     */
    default Method getMethodVarargs(String methodName) throws NoSuchMethodException {
        return getMethod(methodName, false, -1);
    }

    /**
     * Gets method that takes an arbitrary number of arguments by method name.
     *
     * @param methodName         the method name
     * @param thisObjectRequired this object required
     * @return the method varargs
     * @throws NoSuchMethodException the no such method exception
     */
    default Method getMethodVarargs(String methodName, boolean thisObjectRequired) throws NoSuchMethodException {
        return getMethod(methodName, thisObjectRequired, -1);
    }

    V8Runtime getV8Runtime();
}
