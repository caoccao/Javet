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

package com.caoccao.javet.utils.receivers;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.reference.V8ValueArray;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The type Javet explicit callback receiver.
 * <p>
 * It is supposed to provide a common ground for customized V8 callback receiver.
 */
public class JavetCallbackReceiver implements IJavetCallbackReceiver {
    protected static final String COMMA = ",";
    /**
     * The V8 runtime.
     */
    protected V8Runtime v8Runtime;

    /**
     * Instantiates a new V8 callback receiver.
     *
     * @param v8Runtime the V8 runtime
     */
    public JavetCallbackReceiver(V8Runtime v8Runtime) {
        Objects.requireNonNull(v8Runtime);
        this.v8Runtime = v8Runtime;
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
            try (V8Value clonedArg = arg.toClone()) {
                v8ValueArray.push(clonedArg);
            }
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
        return String.join(COMMA, stringList);
    }
}
