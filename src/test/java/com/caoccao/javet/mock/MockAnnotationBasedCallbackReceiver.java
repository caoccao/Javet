package com.caoccao.javet.mock;

import com.caoccao.javet.annotations.V8Function;
import com.caoccao.javet.annotations.V8RuntimeSetter;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.values.reference.V8ValueArray;

import java.util.concurrent.atomic.AtomicInteger;

public class MockAnnotationBasedCallbackReceiver {
    private AtomicInteger count;
    private V8Runtime v8Runtime;

    public MockAnnotationBasedCallbackReceiver() {
        count = new AtomicInteger(0);
        v8Runtime = null;
    }

    @V8Function(name = "echo")
    public String echo(String str) {
        count.incrementAndGet();
        return str;
    }

    @V8Function(name = "add")
    public Integer mathAdd(Integer a, Integer b) {
        count.incrementAndGet();
        return a + b;
    }

    @V8Function(name = "generateArray")
    public V8ValueArray generateArray() throws JavetException {
        count.incrementAndGet();
        V8ValueArray v8ValueArray = v8Runtime.createV8ValueArray();
        v8ValueArray.push("a");
        v8ValueArray.push(1);
        return v8ValueArray;
    }

    public int getCount() {
        return count.get();
    }

    @V8RuntimeSetter
    public void setV8Runtime(V8Runtime v8Runtime) {
        this.v8Runtime = v8Runtime;
    }
}
