package com.caoccao.javet.values.primitive;

public class V8ValueDouble extends V8ValuePrimitive<Double> {
    public V8ValueDouble() {
        this(0D);
    }

    public V8ValueDouble(double value) {
        super(value);
    }

    public double toPrimitive() {
        return value.doubleValue();
    }
}
