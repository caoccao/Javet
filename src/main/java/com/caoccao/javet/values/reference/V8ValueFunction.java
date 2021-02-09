/*
 *   Copyright (c) 2021. caoccao.com Sam Cao
 *   All rights reserved.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.caoccao.javet.values.reference;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.exceptions.JavetV8CallbackAlreadyRegisteredException;
import com.caoccao.javet.interop.IV8CallbackReceiver;
import com.caoccao.javet.utils.JavetResourceUtils;
import com.caoccao.javet.utils.V8CallbackContext;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.V8ValueReferenceType;
import com.caoccao.javet.values.primitive.V8ValueUndefined;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("unchecked")
public class V8ValueFunction extends V8ValueObject implements IV8ValueFunction {
    protected V8CallbackContext v8CallbackContext;

    public V8ValueFunction(long handle) {
        super(handle);
        v8CallbackContext = null;
    }

    public V8CallbackContext getV8CallbackContext() {
        return v8CallbackContext;
    }

    public void setV8CallbackContext(V8CallbackContext v8CallbackContext)
            throws JavetV8CallbackAlreadyRegisteredException {
        Objects.requireNonNull(v8CallbackContext);
        if (this.v8CallbackContext == null) {
            v8CallbackContext.setCallbackOwnerFunction(this);
            this.v8CallbackContext = v8CallbackContext;
        } else if (this.v8CallbackContext != v8CallbackContext) {
            throw new JavetV8CallbackAlreadyRegisteredException();
        }
    }

    @Override
    public <T extends V8Value> T call(IV8ValueObject receiver, boolean returnResult, V8Value... v8Values)
            throws JavetException {
        checkV8Runtime();
        return v8Runtime.call(this, receiver, returnResult, v8Values);
    }

    @Override
    public void close(boolean forceClose) throws JavetException {
        // V8 lock free
        if (forceClose || !isWeak()) {
            if (v8CallbackContext != null) {
                v8Runtime.removeJNIGlobalRef(v8CallbackContext.getHandle());
                v8CallbackContext = null;
            } else {
                /*
                 * Function from V8 loses the callback context.
                 * So there is no need to recycle anything.
                 */
            }
            super.close(forceClose);
        }
    }

    @Override
    public int getType() {
        return V8ValueReferenceType.Function;
    }

    @Override
    public V8Value receiveCallback(V8Value thisObject, V8ValueArray args) throws Throwable {
        if (v8CallbackContext != null) {
            checkV8Runtime();
            V8Value[] values = null;
            try {
                v8Runtime.decorateV8Values(thisObject, args);
                Method method = v8CallbackContext.getCallbackMethod();
                IV8CallbackReceiver callbackReceiver = v8CallbackContext.getCallbackReceiver();
                Object result = null;
                if (args == null || args.getLength() == 0) {
                    // Invoke method without args
                    if (v8CallbackContext.isThisObjectRequired()) {
                        result = method.invoke(callbackReceiver, thisObject);
                    } else {
                        result = method.invoke(callbackReceiver);
                    }
                } else {
                    final int length = args.getLength();
                    values = new V8Value[length];
                    for (int i = 0; i < length; ++i) {
                        values[i] = args.get(i);
                    }
                    if (method.isVarArgs()) {
                        // Invoke method with varargs
                        if (getV8CallbackContext().isThisObjectRequired()) {
                            result = method.invoke(callbackReceiver, thisObject, values);
                        } else {
                            result = method.invoke(callbackReceiver, new Object[]{values});
                        }
                    } else {
                        List<Object> objectValues = new ArrayList<>();
                        if (getV8CallbackContext().isThisObjectRequired()) {
                            objectValues.add(thisObject);
                        }
                        for (V8Value value : values) {
                            objectValues.add(value);
                        }
                        // Invoke method with regular signature
                        result = method.invoke(callbackReceiver, objectValues.toArray());
                    }
                }
                if (result != null) {
                    if (result instanceof V8Value) {
                        v8Runtime.decorateV8Value((V8Value) result);
                    } else {
                        result = v8CallbackContext.getConverter().toV8Value(v8Runtime, result);
                    }
                } else {
                    result = v8CallbackContext.getConverter().toV8Value(v8Runtime, null);
                }
                if (v8CallbackContext.isReturnResult()) {
                    return (V8Value) result;
                } else {
                    JavetResourceUtils.safeClose(result);
                }
            } catch (Throwable t) {
                if (t instanceof InvocationTargetException) {
                    throw t.getCause();
                } else {
                    throw t;
                }
            } finally {
                JavetResourceUtils.safeClose(thisObject);
                JavetResourceUtils.safeClose(args);
                JavetResourceUtils.safeClose(values);
            }
        }
        return v8Runtime.decorateV8Value(new V8ValueUndefined());
    }
}
