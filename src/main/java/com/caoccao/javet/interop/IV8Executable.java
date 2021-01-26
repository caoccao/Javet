package com.caoccao.javet.interop;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.primitive.V8ValuePrimitive;

@SuppressWarnings("unchecked")
public interface IV8Executable {

    default <T extends V8Value> T execute(String scriptString) throws JavetException {
        return execute(scriptString, new V8ScriptOrigin());
    }

    default <T extends V8Value> T execute(String scriptString, V8ScriptOrigin v8ScriptOrigin) throws JavetException {
        return execute(scriptString, v8ScriptOrigin, true);
    }

    <T extends V8Value> T execute(
            String scriptString, V8ScriptOrigin v8ScriptOrigin, boolean resultRequired) throws JavetException;

    default Boolean executeBoolean(String scriptString) throws JavetException {
        return executeBoolean(scriptString, new V8ScriptOrigin());
    }

    default Boolean executeBoolean(String scriptString, V8ScriptOrigin v8ScriptOrigin) throws JavetException {
        return executeObject(scriptString, v8ScriptOrigin);
    }

    default Integer executeInteger(String scriptString) throws JavetException {
        return executeInteger(scriptString, new V8ScriptOrigin());
    }

    default Integer executeInteger(String scriptString, V8ScriptOrigin v8ScriptOrigin) throws JavetException {
        return executeObject(scriptString, v8ScriptOrigin);
    }

    default Long executeLong(String scriptString) throws JavetException {
        return executeLong(scriptString, new V8ScriptOrigin());
    }

    default Long executeLong(String scriptString, V8ScriptOrigin v8ScriptOrigin) throws JavetException {
        return executeObject(scriptString, v8ScriptOrigin);
    }

    default String executeString(String scriptString) throws JavetException {
        return executeString(scriptString, new V8ScriptOrigin());
    }

    default String executeString(String scriptString, V8ScriptOrigin v8ScriptOrigin)
            throws JavetException {
        return executeObject(scriptString, v8ScriptOrigin);
    }

    default <R extends Object, T extends V8ValuePrimitive<R>> R executeObject(String scriptString)
            throws JavetException {
        return executeObject(scriptString, new V8ScriptOrigin());
    }

    default <R extends Object, T extends V8ValuePrimitive<R>> R executeObject(String scriptString, V8ScriptOrigin v8ScriptOrigin)
            throws JavetException {
        V8Value v8Value = execute(scriptString, v8ScriptOrigin, true);
        try {
            return ((T) v8Value).getValue();
        } catch (Throwable t) {
        }
        return null;
    }

    default void executeVoid(String scriptString) throws JavetException {
        executeVoid(scriptString, new V8ScriptOrigin());
    }

    default void executeVoid(String scriptString, V8ScriptOrigin v8ScriptOrigin) throws JavetException {
        execute(scriptString, v8ScriptOrigin, false);
    }
}
