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

package com.caoccao.javet.interop;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.reference.V8ValueArray;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * The type V8 callback receiver.
 * <p>
 * It is supposed to provide a common ground for customized V8 callback receiver.
 */
public class V8CallbackReceiver implements IV8CallbackReceiver {
    /**
     * The constant LINE_SEPARATOR.
     */
    protected static final String LINE_SEPARATOR = System.getProperty("line.separator");

    /**
     * The V8 runtime.
     */
    protected V8Runtime v8Runtime;

    /**
     * Instantiates a new V8 callback receiver.
     *
     * @param v8Runtime the V8 runtime
     */
    public V8CallbackReceiver(V8Runtime v8Runtime) {
        Objects.requireNonNull(v8Runtime);
        this.v8Runtime = v8Runtime;
    }

    /**
     * Gets method that does not take any arguments by method name.
     *
     * @param methodName the method name
     * @return the method
     * @throws NoSuchMethodException the no such method exception
     */
    public final Method getMethod(String methodName) throws NoSuchMethodException {
        return getMethod(methodName, 0);
    }

    /**
     * Gets method that takes given number of arguments by method name.
     *
     * @param methodName the method name
     * @param argCount   the arg count
     * @return the method
     * @throws NoSuchMethodException the no such method exception
     */
    public final Method getMethod(String methodName, int argCount) throws NoSuchMethodException {
        if (argCount < 0) {
            return getClass().getMethod(methodName, V8Value[].class);
        } else if (argCount == 0) {
            return getClass().getMethod(methodName);
        } else {
            Class[] classes = new Class[argCount];
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
    public final Method getMethod(String methodName, Class<?>... parameterTypes) throws NoSuchMethodException {
        return getClass().getMethod(methodName, parameterTypes);
    }

    /**
     * Gets method that takes an arbitrary number of arguments by method name.
     *
     * @param methodName the method name
     * @return the method varargs
     * @throws NoSuchMethodException the no such method exception
     */
    public final Method getMethodVarargs(String methodName) throws NoSuchMethodException {
        return getMethod(methodName, -1);
    }

    @Override
    public V8Runtime getV8Runtime() {
        return v8Runtime;
    }

    /**
     * Echo the given V8 value.
     *
     * @param arg the arg
     * @return the V8 value
     * @throws JavetException the javet exception
     */
    public V8Value echo(V8Value arg) throws JavetException {
        return arg.toClone();
    }

    /**
     * Echo the given V8 value array.
     * <p>
     * Note: Lifecycle of the input and return arrays is managed by the caller.
     *
     * @param args the args
     * @return the V8 value array
     * @throws JavetException the javet exception
     */
    public V8ValueArray echo(V8Value... args) throws JavetException {
        V8ValueArray v8ValueArray = v8Runtime.createV8ValueArray();
        for (V8Value arg : args) {
            v8ValueArray.push(arg.toClone());
        }
        return v8ValueArray;
    }

    /**
     * Echo string from input string.
     *
     * @param str the str
     * @return the string
     */
    public String echoString(String str) {
        return str;
    }

    /**
     * Echo string from input V8 value.
     *
     * @param arg the arg
     * @return the string
     */
    public String echoString(V8Value arg) {
        return arg == null ? null : arg.toString();
    }

    /**
     * Echo string from the given V8 value array.
     * <p>
     * Note: Lifecycle of the input and return arrays is managed by the caller.
     *
     * @param args the args
     * @return the string
     */
    public String echoString(V8Value... args) {
        List<String> stringList = new ArrayList<>(args.length);
        for (V8Value arg : args) {
            stringList.add(arg == null ? null : arg.toString());
        }
        return String.join(LINE_SEPARATOR, stringList);
    }
}
