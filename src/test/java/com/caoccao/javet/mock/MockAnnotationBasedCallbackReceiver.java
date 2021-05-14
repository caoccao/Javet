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

package com.caoccao.javet.mock;

import com.caoccao.javet.annotations.V8Function;
import com.caoccao.javet.annotations.V8RuntimeSetter;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.values.primitive.V8ValueInteger;
import com.caoccao.javet.values.reference.V8ValueArray;
import com.caoccao.javet.values.reference.V8ValueFunction;

import java.util.concurrent.atomic.AtomicInteger;

public class MockAnnotationBasedCallbackReceiver {
    private AtomicInteger count;
    private V8Runtime v8Runtime;

    public MockAnnotationBasedCallbackReceiver() {
        count = new AtomicInteger(0);
        v8Runtime = null;
    }

    // Static method.
    @V8Function(name = "staticEcho")
    public static String staticEcho(String str) {
        return str;
    }

    @V8Function
    public Integer contextScope(V8ValueFunction v8ValueFunction) throws JavetException {
        try (V8ValueFunction newV8ValueFunction = v8Runtime.getExecutor("() => c['d']").execute()) {
            return ((V8ValueInteger)newV8ValueFunction.call(null)).getValue();
        }
    }

    // Instance method with same name and same signature.
    @V8Function(name = "echo")
    public String echo(String str) {
        count.incrementAndGet();
        return str;
    }

    // Instance method with different name and same signature.
    @V8Function(name = "add")
    public Integer mathAdd(Integer a, Integer b) {
        count.incrementAndGet();
        return a + b;
    }

    // Instance method with converter for non-primitive objects.
    @V8Function(name = "generateArrayWithConverter")
    public Object[] generateArrayWithConverter() throws JavetException {
        count.incrementAndGet();
        // Converter is able to recognize non-primitive types.
        return new Object[]{"a", 1};
    }

    // Instance method requiring V8Runtime without converter.
    @V8Function
    public V8ValueArray generateArrayWithoutConverter() throws JavetException {
        count.incrementAndGet();
        V8ValueArray v8ValueArray = v8Runtime.createV8ValueArray();
        v8ValueArray.push("a");
        v8ValueArray.push(1);
        return v8ValueArray;
    }

    // Instance method with primitive type byte
    @V8Function(name = "primitiveAddByte")
    public byte primitiveAddByte(byte a, byte b) {
        count.incrementAndGet();
        return (byte) (a + b);
    }

    // Instance method with primitive type double
    @V8Function(name = "primitiveAddDouble")
    public double primitiveAddDouble(double a, double b) {
        count.incrementAndGet();
        return a + b;
    }

    // Instance method with primitive type float
    @V8Function(name = "primitiveAddFloat")
    public float primitiveAddFloat(float a, float b) {
        count.incrementAndGet();
        return a + b;
    }

    // Instance method with primitive type int
    @V8Function(name = "primitiveAddInt")
    public int primitiveAddInt(int a, int b) {
        count.incrementAndGet();
        return a + b;
    }

    // Instance method with primitive type long
    @V8Function(name = "primitiveAddLong")
    public long primitiveAddLong(long a, long b) {
        count.incrementAndGet();
        return a + b;
    }

    // Instance method with primitive type short
    @V8Function(name = "primitiveAddShort")
    public short primitiveAddShort(short a, short b) {
        count.incrementAndGet();
        return Integer.valueOf(a + b).shortValue();
    }

    // Instance method with primitive type boolean
    @V8Function(name = "primitiveRevertBoolean")
    public boolean primitiveRevertBoolean(boolean b) {
        count.incrementAndGet();
        return !b;
    }

    // Instance method with primitive type char
    @V8Function(name = "primitiveIncreaseChar")
    public char primitiveIncreaseChar(char c) {
        count.incrementAndGet();
        return (char) ((int) c + 1);
    }

    public int getCount() {
        return count.get();
    }

    // Declare the V8RuntimeSetter for dependency injection.
    @V8RuntimeSetter
    public void setV8Runtime(V8Runtime v8Runtime) {
        this.v8Runtime = v8Runtime;
    }
}
