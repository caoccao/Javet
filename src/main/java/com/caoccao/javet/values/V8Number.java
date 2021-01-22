package com.caoccao.javet.values;

public abstract class V8Number<T extends Object> extends V8TypedValue<T> {
    protected boolean unsigned;

    public V8Number() {
        this(null);
    }

    public V8Number(T value) {
        this(value, false);
    }

    public V8Number(T value, boolean unsigned) {
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
