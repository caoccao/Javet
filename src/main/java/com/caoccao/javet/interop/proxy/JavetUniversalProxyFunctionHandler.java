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

import com.caoccao.javet.annotations.V8Function;
import com.caoccao.javet.exceptions.JavetError;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.utils.JavetResourceUtils;
import com.caoccao.javet.utils.SimpleMap;
import com.caoccao.javet.utils.V8ValueUtils;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.reference.V8ValueArray;

/**
 * The type Javet universal proxy function handler.
 *
 * @param <T> the type parameter
 * @since 1.1.7
 */
public class JavetUniversalProxyFunctionHandler<T> extends JavetUniversalProxyObjectHandler<T> {

    /**
     * The constant METHOD_NAME_APPLY.
     *
     * @since 1.1.7
     */
    protected static final String METHOD_NAME_APPLY = "apply";

    /**
     * Instantiates a new Javet universal proxy function handler.
     *
     * @param v8Runtime    the V8 runtime
     * @param targetObject the target object
     * @since 1.1.7
     */
    public JavetUniversalProxyFunctionHandler(V8Runtime v8Runtime, T targetObject) {
        super(v8Runtime, targetObject);
    }

    @V8Function
    @Override
    public V8Value apply(V8Value target, V8Value thisObject, V8ValueArray arguments) throws JavetException {
        if (!classDescriptor.getApplyFunctions().isEmpty()) {
            V8Value[] v8Values = null;
            try {
                v8Values = arguments.toArray();
                return v8Runtime.toV8Value(execute(
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
                    JavetResourceUtils.safeClose((Object[]) v8Values);
                }
            }
        }
        return v8Runtime.createV8ValueUndefined();
    }
}
