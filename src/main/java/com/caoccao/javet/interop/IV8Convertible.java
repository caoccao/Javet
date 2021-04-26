package com.caoccao.javet.interop;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.utils.JavetResourceUtils;
import com.caoccao.javet.values.V8Value;

@SuppressWarnings("unchecked")
public interface IV8Convertible {
    <T extends Object, V extends V8Value> T toObject(V v8Value) throws JavetException;

    default <T extends Object, V extends V8Value> T toObject(V v8Value, boolean autoClose) throws JavetException {
        if (autoClose) {
            try {
                return toObject(v8Value);
            } finally {
                JavetResourceUtils.safeClose(v8Value);
            }
        } else {
            return toObject(v8Value);
        }
    }

    <T extends Object, V extends V8Value> V toV8Value(T object) throws JavetException;
}
