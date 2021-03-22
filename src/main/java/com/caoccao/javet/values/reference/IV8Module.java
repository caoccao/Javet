package com.caoccao.javet.values.reference;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.IV8Executable;
import com.caoccao.javet.values.V8Value;

@SuppressWarnings("unchecked")
public interface IV8Module extends IV8ValueReference, IV8Executable {
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

    @Override
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

    V8ValueError getException() throws JavetException;

    String getResourceName();

    void setResourceName(String resourceName);

    /**
     * Gets script id.
     *
     * Note: This API is not supported by Node because the V8 version is too low.
     *
     * @return the script id
     * @throws JavetException the javet exception
     */
    int getScriptId() throws JavetException;

    int getStatus() throws JavetException;

    boolean instantiate() throws JavetException;
}
