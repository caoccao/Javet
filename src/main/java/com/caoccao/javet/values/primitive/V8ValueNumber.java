package com.caoccao.javet.values.primitive;

public abstract class V8ValueNumber<T extends Object> extends V8ValuePrimitive<T> {
    protected boolean unsigned;

    public V8ValueNumber() {
        this(null);
    }

    public V8ValueNumber(T value) {
        this(value, false);
    }

    public V8ValueNumber(T value, boolean unsigned) {
        super(value);
        this.unsigned = unsigned;
    }

    public boolean isUnsigned() {
        return unsigned;
    }

    public void setUnsigned(boolean unsigned) {
        this.unsigned = unsigned;
    }
}
