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
import com.caoccao.javet.utils.SimpleMap;
import com.caoccao.javet.utils.V8ValueUtils;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.reference.V8ValueObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * The type Javet universal interceptor.
 *
 * @since 0.9.6
 */
final class JavetUniversalInterceptor {
    private static final String METHOD_NAME_INVOKE = "invoke";
    private final String jsMethodName;
    private final List<Method> methods;
    private final Object targetObject;
    private final V8Runtime v8Runtime;

    /**
     * Instantiates a new Javet universal interceptor.
     *
     * @param v8Runtime    the V8 runtime
     * @param targetObject the target object
     * @param jsMethodName the JS method name
     * @param methods      the methods
     * @since 0.9.6
     */
    public JavetUniversalInterceptor(
            V8Runtime v8Runtime,
            Object targetObject,
            String jsMethodName,
            List<Method> methods) {
        this.jsMethodName = jsMethodName;
        this.methods = methods;
        this.targetObject = targetObject;
        this.v8Runtime = v8Runtime;
    }


    /**
     * Gets callback context.
     *
     * @return the callback context
     * @since 0.9.6
     */
    public JavetCallbackContext getCallbackContext() {
        try {
            return new JavetCallbackContext(
                    this,
                    getClass().getMethod(METHOD_NAME_INVOKE, V8ValueObject.class, V8Value[].class),
                    true);
        } catch (NoSuchMethodException ignored) {
        }
        return null;
    }

    /**
     * Gets JS method name.
     *
     * @return the JS method name
     * @since 0.9.6
     */
    public String getJSMethodName() {
        return jsMethodName;
    }

    /**
     * Gets methods.
     *
     * @return the methods
     * @since 0.9.6
     */
    public List<Method> getMethods() {
        return methods;
    }

    /**
     * Gets target object.
     *
     * @return the target object
     * @since 0.9.6
     */
    public Object getTargetObject() {
        return targetObject;
    }

    /**
     * Invoke.
     *
     * @param thisObject this object
     * @param v8Values   the V8 values
     * @return the object
     * @throws JavetException the javet exception
     * @since 0.9.6
     */
    public Object invoke(V8ValueObject thisObject, V8Value... v8Values) throws JavetException {
        try {
            return BaseJavetProxyHandler.execute(
                    targetObject,
                    thisObject,
                    methods,
                    V8ValueUtils.convertToVirtualObjects(v8Values));
        } catch (JavetException e) {
            throw e;
        } catch (InvocationTargetException e) {
            throw new JavetException(JavetError.CallbackMethodFailure,
                    SimpleMap.of(
                            JavetError.PARAMETER_METHOD_NAME, jsMethodName,
                            JavetError.PARAMETER_MESSAGE, e.getTargetException().getMessage()),
                    e.getTargetException());
        } catch (Throwable t) {
            throw new JavetException(JavetError.CallbackMethodFailure,
                    SimpleMap.of(
                            JavetError.PARAMETER_METHOD_NAME, jsMethodName,
                            JavetError.PARAMETER_MESSAGE, t.getMessage()),
                    t);
        }
    }
}
