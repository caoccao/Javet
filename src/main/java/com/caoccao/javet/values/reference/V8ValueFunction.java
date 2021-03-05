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
import com.caoccao.javet.exceptions.JavetV8CallbackSignatureMismatchException;
import com.caoccao.javet.utils.JavetCallbackContext;
import com.caoccao.javet.utils.JavetResourceUtils;
import com.caoccao.javet.utils.converters.IJavetConverter;
import com.caoccao.javet.utils.receivers.IJavetCallbackReceiver;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.V8ValueReferenceType;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("unchecked")
public class V8ValueFunction extends V8ValueObject implements IV8ValueFunction {
    /*
     * The lifecycle of V8ValueFunction depends on V8CallbackContext which is
     * managed by JNI native implementation.
     */
    protected JavetCallbackContext javetCallbackContext;

    V8ValueFunction(long handle) {
        super(handle);
        javetCallbackContext = null;
    }

    public JavetCallbackContext getV8CallbackContext() {
        return javetCallbackContext;
    }

    public void setV8CallbackContext(JavetCallbackContext javetCallbackContext)
            throws JavetV8CallbackAlreadyRegisteredException {
        Objects.requireNonNull(javetCallbackContext);
        if (this.javetCallbackContext == null) {
            javetCallbackContext.setCallbackOwnerFunction(this);
            this.javetCallbackContext = javetCallbackContext;
        } else if (this.javetCallbackContext != javetCallbackContext) {
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
    public <T extends V8Value> T callAsConstructor(V8Value... v8Values) throws JavetException {
        checkV8Runtime();
        return v8Runtime.callAsConstructor(this, v8Values);
    }

    @Override
    public void close(boolean forceClose) throws JavetException {
        if (forceClose || !isWeak()) {
            if (javetCallbackContext != null) {
                v8Runtime.removeJNIGlobalRef(javetCallbackContext.getHandle());
                javetCallbackContext = null;
            } else {
                /*
                 * Function from V8 loses the callback context.
                 * So there is no need to recycle anything.
                 */
            }
            super.close(forceClose);
        }
    }

    protected Object convert(IJavetConverter converter, Class expectedClass, V8Value v8Value)
            throws JavetException {
        if (v8Value == null) {
            // Skip null
        } else if (expectedClass.isAssignableFrom(v8Value.getClass())) {
            // Skip assignable
        } else {
            Object convertedObject = converter.toObject(v8Value);
            if (expectedClass.isAssignableFrom(convertedObject.getClass())) {
                return convertedObject;
            } else {
                throw JavetV8CallbackSignatureMismatchException.parameterTypeMismatch(
                        expectedClass, convertedObject.getClass());
            }
        }
        return v8Value;
    }

    @Override
    public int getType() {
        return V8ValueReferenceType.Function;
    }

    @Override
    public V8Value receiveCallback(V8Value thisObject, V8ValueArray args) throws Throwable {
        if (javetCallbackContext != null) {
            checkV8Runtime();
            List<Object> values = new ArrayList<>();
            try {
                v8Runtime.decorateV8Values(thisObject, args);
                IJavetConverter converter = javetCallbackContext.getConverter();
                Method method = javetCallbackContext.getCallbackMethod();
                IJavetCallbackReceiver callbackReceiver = javetCallbackContext.getCallbackReceiver();
                Object resultObject = null;
                if (javetCallbackContext.isThisObjectRequired()) {
                    values.add(thisObject);
                }
                if (args != null) {
                    final int length = args.getLength();
                    for (int i = 0; i < length; ++i) {
                        values.add(args.get(i));
                    }
                }
                if (values.isEmpty()) {
                    resultObject = method.invoke(callbackReceiver);
                } else {
                    final int length = values.size();
                    List<Object> objectValues = new ArrayList<>();
                    Class<?>[] parameterTypes = method.getParameterTypes();
                    if (method.isVarArgs()) {
                        for (int i = 0; i < parameterTypes.length; ++i) {
                            Class<?> parameterClass = parameterTypes[i];
                            if (parameterClass.isArray() && i == parameterTypes.length - 1) {
                                // VarArgs is special. It requires special API to manipulate the array.
                                Class componentType = parameterClass.getComponentType();
                                Object varObject = Array.newInstance(componentType, length - i);
                                for (int j = i; j < length; ++j) {
                                    Array.set(varObject, j - i,
                                            convert(converter, componentType, (V8Value) values.get(j)));
                                }
                                objectValues.add(varObject);
                            } else {
                                objectValues.add(convert(converter, parameterClass, (V8Value) values.get(i)));
                            }
                        }
                    } else {
                        if (method.getParameterCount() != length) {
                            throw JavetV8CallbackSignatureMismatchException.parameterSizeMismatch(
                                    length, method.getParameterCount());
                        }
                        for (int i = 0; i < parameterTypes.length; ++i) {
                            objectValues.add(convert(converter, parameterTypes[i], (V8Value) values.get(i)));
                        }
                    }
                    resultObject = method.invoke(callbackReceiver, objectValues.toArray());
                }
                if (javetCallbackContext.isReturnResult()) {
                    if (resultObject != null) {
                        if (resultObject instanceof V8Value) {
                            v8Runtime.decorateV8Value((V8Value) resultObject);
                        } else {
                            resultObject = converter.toV8Value(v8Runtime, resultObject);
                        }
                    } else {
                        resultObject = converter.toV8Value(v8Runtime, null);
                    }
                    // The lifecycle of the result is handed over to JNI native implementation.
                    // So, close() or setWeak() must not be called.
                    return (V8Value) resultObject;
                } else {
                    JavetResourceUtils.safeClose(resultObject);
                }
            } catch (Throwable t) {
                if (t instanceof InvocationTargetException) {
                    throw t.getCause();
                } else {
                    throw t;
                }
            } finally {
                if (!javetCallbackContext.isThisObjectRequired()) {
                    JavetResourceUtils.safeClose(thisObject);
                }
                JavetResourceUtils.safeClose(args);
                JavetResourceUtils.safeClose(values);
            }
        }
        return v8Runtime.createV8ValueUndefined();
    }
}
