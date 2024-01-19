/*
 * Copyright (c) 2022. caoccao.com Sam Cao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.caoccao.javet.interop.proxy;

import com.caoccao.javet.exceptions.JavetError;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.callback.JavetCallbackContext;
import com.caoccao.javet.interop.callback.JavetCallbackType;
import com.caoccao.javet.utils.JavetResourceUtils;
import com.caoccao.javet.utils.SimpleMap;
import com.caoccao.javet.utils.V8ValueUtils;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.reference.V8ValueArray;

/**
 * The type Javet reflection proxy function handler.
 *
 * @param <T> the type parameter
 * @param <E> the type parameter
 * @since 1.1.7
 */
public class JavetReflectionProxyFunctionHandler<T, E extends Exception>
        extends JavetReflectionProxyObjectHandler<T, E> {

    /**
     * The constant METHOD_NAME_APPLY.
     *
     * @since 1.1.7
     */
    protected static final String METHOD_NAME_APPLY = "apply";

    /**
     * Instantiates a new Javet reflection proxy function handler.
     *
     * @param v8Runtime               the V8 runtime
     * @param reflectionObjectFactory the reflection object factory
     * @param targetObject            the target object
     * @since 1.1.7
     */
    public JavetReflectionProxyFunctionHandler(
            V8Runtime v8Runtime,
            IJavetReflectionObjectFactory reflectionObjectFactory,
            T targetObject) {
        super(v8Runtime, reflectionObjectFactory, targetObject);
    }

    @Override
    public V8Value apply(V8Value target, V8Value thisObject, V8ValueArray arguments) throws JavetException {
        if (!classDescriptor.getApplyFunctions().isEmpty()) {
            V8Value[] v8Values = null;
            try {
                v8Values = arguments.toArray();
                return v8Runtime.toV8Value(execute(
                        reflectionObjectFactory,
                        targetObject,
                        null,
                        classDescriptor.getApplyFunctions(),
                        V8ValueUtils.convertToVirtualObjects(v8Values)));
            } catch (JavetException e) {
                throw e;
            } catch (Throwable t) {
                throw new JavetException(JavetError.CallbackMethodFailure,
                        SimpleMap.of(
                                JavetError.PARAMETER_METHOD_NAME, METHOD_NAME_APPLY,
                                JavetError.PARAMETER_MESSAGE, t.getMessage()), t);
            } finally {
                if (v8Values != null) {
                    JavetResourceUtils.safeClose(v8Values);
                }
            }
        }
        return v8Runtime.createV8ValueUndefined();
    }

    @Override
    public JavetCallbackContext[] getCallbackContexts() {
        if (callbackContexts == null) {
            callbackContexts = new JavetCallbackContext[]{
                    new JavetCallbackContext(
                            PROXY_FUNCTION_NAME_APPLY, this, JavetCallbackType.DirectCallNoThisAndResult,
                            (NoThisAndResult<?>) (v8Values) -> apply(v8Values[0], v8Values[1], (V8ValueArray) v8Values[2])),
                    new JavetCallbackContext(
                            PROXY_FUNCTION_NAME_DELETE_PROPERTY, this, JavetCallbackType.DirectCallNoThisAndResult,
                            (NoThisAndResult<?>) (v8Values) -> deleteProperty(v8Values[0], v8Values[1])),
                    new JavetCallbackContext(
                            PROXY_FUNCTION_NAME_GET, this, JavetCallbackType.DirectCallNoThisAndResult,
                            (NoThisAndResult<?>) (v8Values) -> get(v8Values[0], v8Values[1], v8Values[2])),
                    new JavetCallbackContext(
                            PROXY_FUNCTION_NAME_HAS, this, JavetCallbackType.DirectCallNoThisAndResult,
                            (NoThisAndResult<?>) (v8Values) -> has(v8Values[0], v8Values[1])),
                    new JavetCallbackContext(
                            PROXY_FUNCTION_NAME_OWN_KEYS, this, JavetCallbackType.DirectCallNoThisAndResult,
                            (NoThisAndResult<?>) (v8Values) -> ownKeys(v8Values[0])),
                    new JavetCallbackContext(
                            PROXY_FUNCTION_NAME_SET, this, JavetCallbackType.DirectCallNoThisAndResult,
                            (NoThisAndResult<?>) (v8Values) -> set(v8Values[0], v8Values[1], v8Values[2], v8Values[3])),
            };
        }
        return callbackContexts;
    }
}
