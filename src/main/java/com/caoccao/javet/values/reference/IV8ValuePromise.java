package com.caoccao.javet.values.reference;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.primitive.*;

import java.time.ZonedDateTime;
import java.util.Objects;

public interface IV8ValuePromise extends IV8ValueObject {
    int STATE_PENDING = 0;
    int STATE_FULFILLED = 1;
    int STATE_REJECTED = 2;

    V8ValuePromise except(V8ValueFunction function) throws JavetException;

    default V8ValuePromise except(String codeString) throws JavetException {
        Objects.requireNonNull(codeString);
        try (V8ValueFunction v8ValueFunction = getV8Runtime().getExecutor(codeString).execute()) {
            return except(v8ValueFunction);
        }
    }

    <Value extends V8Value> Value getResult() throws JavetException;

    default boolean getResultBoolean() throws JavetException {
        return ((V8ValueBoolean) getResult()).getValue();
    }

    default double getResultDouble() throws JavetException {
        return ((V8ValueDouble) getResult()).getValue();
    }

    default int getResultInteger() throws JavetException {
        return ((V8ValueInteger) getResult()).getValue();
    }

    default long getResultLong() throws JavetException {
        return ((V8ValueLong) getResult()).getValue();
    }

    default String getResultString() throws JavetException {
        return ((V8ValueString) getResult()).getValue();
    }

    default ZonedDateTime getResultZonedDateTime() throws JavetException {
        return ((V8ValueZonedDateTime) getResult()).getValue();
    }

    int getState() throws JavetException;

    boolean hasHandler() throws JavetException;

    default boolean isFulfilled() throws JavetException {
        return STATE_FULFILLED == getState();
    }

    default boolean isPending() throws JavetException {
        return STATE_PENDING == getState();
    }

    default boolean isRejected() throws JavetException {
        return STATE_REJECTED == getState();
    }

    void markAsHandled() throws JavetException;

    default V8ValuePromise then(IV8ValueFunction functionFulfilled) throws JavetException {
        return then(functionFulfilled, null);
    }

    V8ValuePromise then(IV8ValueFunction functionFulfilled, IV8ValueFunction functionRejected) throws JavetException;

    default V8ValuePromise then(String codeStringFulfilled) throws JavetException {
        return then(codeStringFulfilled, null);
    }

    default V8ValuePromise then(String codeStringFulfilled, String codeStringRejected) throws JavetException {
        Objects.requireNonNull(codeStringFulfilled);
        try (V8ValueFunction v8ValueFunctionFulfilled = getV8Runtime().getExecutor(codeStringFulfilled).execute()) {
            if (codeStringRejected == null) {
                return then(v8ValueFunctionFulfilled, null);
            } else {
                try (V8ValueFunction v8ValueFunctionRejected = getV8Runtime().getExecutor(codeStringRejected).execute()) {
                    return then(v8ValueFunctionFulfilled, v8ValueFunctionRejected);
                }
            }
        }
    }
}
