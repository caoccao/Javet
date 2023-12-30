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

package com.caoccao.javet.mock;

import com.caoccao.javet.annotations.V8BindingEnabler;
import com.caoccao.javet.annotations.V8Function;
import com.caoccao.javet.annotations.V8Property;
import com.caoccao.javet.annotations.V8RuntimeSetter;
import com.caoccao.javet.enums.V8ValueSymbolType;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.utils.JavetReflectionUtils;
import com.caoccao.javet.values.reference.V8ValueArray;
import com.caoccao.javet.values.reference.V8ValueObject;

import java.io.Serializable;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class MockAnnotationBasedCallbackReceiver {
    private final AtomicInteger count;
    private final Set<String> disabledFunctionSet;
    private String stringValue;
    private String symbolValue;
    private V8Runtime v8Runtime;

    public MockAnnotationBasedCallbackReceiver() {
        count = new AtomicInteger(0);
        disabledFunctionSet = JavetReflectionUtils.getMethodNameSetFromLambdas(
                (Supplier<?> & Serializable) this::disabledFunction,
                (Supplier<?> & Serializable) this::disabledProperty);
        stringValue = null;
        symbolValue = null;
        v8Runtime = null;
    }

    // Static method.
    @V8Function(name = "staticEcho")
    public static String staticEcho(String str) {
        return str;
    }

    @V8Function
    public String disabledFunction() {
        return "I am a disabled function.";
    }

    @V8Property(name = "disabledProperty")
    public String disabledProperty() {
        return "I am a disabled property.";
    }

    // Instance method with same name and same signature.
    @V8Function(name = "echo")
    public String echo(String str) {
        count.incrementAndGet();
        return str;
    }

    // Instance method with converter for non-primitive objects.
    @V8Function(name = "generateArrayWithConverter")
    public Object[] generateArrayWithConverter() {
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

    public int getCount() {
        return count.get();
    }

    @V8Property
    public Integer getIntegerValue() {
        count.incrementAndGet();
        return 123;
    }

    @V8Property
    public String getStringValue() {
        count.incrementAndGet();
        return stringValue;
    }

    @V8Property(thisObjectRequired = true)
    public String getStringValueWithThis(V8ValueObject thisObject) throws JavetException {
        count.incrementAndGet();
        return thisObject.getString("stringValue");
    }

    @V8Property(symbolType = V8ValueSymbolType.Custom)
    public String getSymbolValue() {
        count.incrementAndGet();
        return symbolValue;
    }

    @V8BindingEnabler
    public boolean isV8BindingEnabled(String methodName) {
        return !disabledFunctionSet.contains(methodName);
    }

    // Instance method with different name and same signature.
    @V8Function(name = "add")
    public Integer mathAdd(Integer a, Integer b) {
        count.incrementAndGet();
        return a + b;
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

    // Instance method with primitive type char
    @V8Function(name = "primitiveIncreaseChar")
    public char primitiveIncreaseChar(char c) {
        count.incrementAndGet();
        return (char) ((int) c + 1);
    }

    // Instance method with primitive type boolean
    @V8Function(name = "primitiveRevertBoolean")
    public boolean primitiveRevertBoolean(boolean b) {
        count.incrementAndGet();
        return !b;
    }

    @V8Function(thisObjectRequired = true)
    public V8ValueObject self(V8ValueObject thisObject) {
        count.incrementAndGet();
        return thisObject;
    }

    @V8Property
    public void setStringValue(String stringValue) {
        count.incrementAndGet();
        this.stringValue = stringValue;
    }

    @V8Property(thisObjectRequired = true)
    public void setStringValueWithThis(V8ValueObject thisObject, String stringValue) throws JavetException {
        count.incrementAndGet();
        thisObject.set("stringValue", stringValue);
    }

    @V8Property(symbolType = V8ValueSymbolType.Custom)
    public void setSymbolValue(String symbolValue) {
        count.incrementAndGet();
        this.symbolValue = symbolValue;
    }

    // Declare the V8RuntimeSetter for dependency injection.
    @V8RuntimeSetter
    public void setV8Runtime(V8Runtime v8Runtime) {
        this.v8Runtime = v8Runtime;
    }

    @V8Property(name = "toPrimitive", symbolType = V8ValueSymbolType.BuiltIn)
    public Integer toPrimitive() {
        count.incrementAndGet();
        return 1000;
    }
}
