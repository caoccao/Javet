package com.caoccao.javet.values.reference;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.values.V8Value;

@SuppressWarnings("unchecked")
public interface IV8Module extends IV8ValueReference {
    int Uninstantiated = 0;
    int Instantiating = 1;
    int Instantiated = 2;
    int Evaluating = 3;
    int Evaluated = 4;
    int Errored = 5;

    default <T extends V8Value> T evaluate() throws JavetException {
        return evaluate(true);
    }

    <T extends V8Value> T evaluate(boolean resultRequired) throws JavetException;

    default void evaluateVoid() throws JavetException {
        evaluate(false);
    }

    default <T extends V8Value> T execute() throws JavetException {
        return execute(true);
    }

    default <T extends V8Value> T execute(boolean resultRequired) throws JavetException {
        if (getStatus() == Uninstantiated) {
            if (!instantiate()) {
                return (T) getV8Runtime().createV8ValueUndefined();
            }
        }
        if (getStatus() == Instantiated) {
            return (T) evaluate(resultRequired);
        }
        return (T) getV8Runtime().createV8ValueUndefined();
    }


    default void executeVoid() throws JavetException {
        execute(false);
    }

    V8ValueError getException() throws JavetException;

    String getResourceName();

    void setResourceName(String resourceName);

    int getScriptId() throws JavetException;

    int getStatus() throws JavetException;

    boolean instantiate() throws JavetException;
}
