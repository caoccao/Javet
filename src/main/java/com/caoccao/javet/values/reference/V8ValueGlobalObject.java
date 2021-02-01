package com.caoccao.javet.values.reference;

public final class V8ValueGlobalObject extends V8ValueObject {
    public V8ValueGlobalObject(long handle) {
        super(handle);
    }

    @Override
    protected void addReference() {
        // Global object lives as long as V8 runtime lives.
    }

    @Override
    protected void releaseReference() {
        // Global object lives as long as V8 runtime lives.
    }
}
