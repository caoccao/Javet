/*
 * Copyright (c) 2021-2023. caoccao.com Sam Cao
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

package com.caoccao.javet.interception.logging;

import com.caoccao.javet.exceptions.JavetError;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interception.BaseJavetInterceptor;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.callback.JavetCallbackContext;
import com.caoccao.javet.utils.SimpleMap;
import com.caoccao.javet.utils.V8ValueUtils;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.reference.IV8ValueObject;
import com.caoccao.javet.values.reference.V8ValueObject;

/**
 * The type Base javet console interceptor.
 *
 * @since 0.7.0
 */
public abstract class BaseJavetConsoleInterceptor extends BaseJavetInterceptor {
    /**
     * The constant JAVA_CONSOLE_DEBUG.
     *
     * @since 0.7.0
     */
    protected static final String JAVA_CONSOLE_DEBUG = "consoleDebug";
    /**
     * The constant JAVA_CONSOLE_ERROR.
     *
     * @since 0.7.0
     */
    protected static final String JAVA_CONSOLE_ERROR = "consoleError";
    /**
     * The constant JAVA_CONSOLE_INFO.
     *
     * @since 0.7.0
     */
    protected static final String JAVA_CONSOLE_INFO = "consoleInfo";
    /**
     * The constant JAVA_CONSOLE_LOG.
     *
     * @since 0.7.0
     */
    protected static final String JAVA_CONSOLE_LOG = "consoleLog";
    /**
     * The constant JAVA_CONSOLE_TRACE.
     *
     * @since 0.7.0
     */
    protected static final String JAVA_CONSOLE_TRACE = "consoleTrace";
    /**
     * The constant JAVA_CONSOLE_WARN.
     *
     * @since 0.7.0
     */
    protected static final String JAVA_CONSOLE_WARN = "consoleWarn";
    /**
     * The constant JS_FUNCTION_DEBUG.
     *
     * @since 0.7.0
     */
    protected static final String JS_FUNCTION_DEBUG = "debug";
    /**
     * The constant JS_FUNCTION_ERROR.
     *
     * @since 0.7.0
     */
    protected static final String JS_FUNCTION_ERROR = "error";
    /**
     * The constant JS_FUNCTION_INFO.
     *
     * @since 0.7.0
     */
    protected static final String JS_FUNCTION_INFO = "info";
    /**
     * The constant JS_FUNCTION_LOG.
     *
     * @since 0.7.0
     */
    protected static final String JS_FUNCTION_LOG = "log";
    /**
     * The constant JS_FUNCTION_TRACE.
     *
     * @since 0.7.0
     */
    protected static final String JS_FUNCTION_TRACE = "trace";
    /**
     * The constant JS_FUNCTION_WARN.
     *
     * @since 0.7.0
     */
    protected static final String JS_FUNCTION_WARN = "warn";
    /**
     * The constant PROPERTY_CONSOLE.
     *
     * @since 0.7.0
     */
    protected static final String PROPERTY_CONSOLE = "console";
    /**
     * The constant SPACE.
     *
     * @since 0.7.1
     */
    protected static final String SPACE = " ";

    /**
     * Instantiates a new Base javet console interceptor.
     *
     * @param v8Runtime the V8 runtime
     * @since 0.7.0
     */
    public BaseJavetConsoleInterceptor(V8Runtime v8Runtime) {
        super(v8Runtime);
    }

    /**
     * Concat string.
     *
     * @param v8Values the V8 values
     * @return the string
     * @since 0.7.0
     */
    public String concat(V8Value... v8Values) {
        return V8ValueUtils.concat(SPACE, v8Values);
    }

    /**
     * Console debug.
     *
     * @param v8Values the V8 values
     * @since 0.7.0
     */
    public abstract void consoleDebug(V8Value... v8Values);

    /**
     * Console error.
     *
     * @param v8Values the V8 values
     * @since 0.7.0
     */
    public abstract void consoleError(V8Value... v8Values);

    /**
     * Console info.
     *
     * @param v8Values the V8 values
     * @since 0.7.0
     */
    public abstract void consoleInfo(V8Value... v8Values);

    /**
     * Console log.
     *
     * @param v8Values the V8 values
     * @since 0.7.0
     */
    public abstract void consoleLog(V8Value... v8Values);

    /**
     * Console trace.
     *
     * @param v8Values the V8 values
     * @since 0.7.0
     */
    public abstract void consoleTrace(V8Value... v8Values);

    /**
     * Console warn.
     *
     * @param v8Values the V8 values
     * @since 0.7.0
     */
    public abstract void consoleWarn(V8Value... v8Values);

    @Override
    public boolean register(IV8ValueObject... iV8ValueObjects) throws JavetException {
        try (V8ValueObject console = v8Runtime.createV8ValueObject()) {
            for (IV8ValueObject iV8ValueObject : iV8ValueObjects) {
                iV8ValueObject.set(PROPERTY_CONSOLE, console);
                register(console, JS_FUNCTION_DEBUG, JAVA_CONSOLE_DEBUG);
                register(console, JS_FUNCTION_ERROR, JAVA_CONSOLE_ERROR);
                register(console, JS_FUNCTION_INFO, JAVA_CONSOLE_INFO);
                register(console, JS_FUNCTION_LOG, JAVA_CONSOLE_LOG);
                register(console, JS_FUNCTION_TRACE, JAVA_CONSOLE_TRACE);
                register(console, JS_FUNCTION_WARN, JAVA_CONSOLE_WARN);
            }
            return true;
        }
    }

    /**
     * Register a JS function by name.
     *
     * @param iV8ValueObject   the V8 value object
     * @param jsFunctionName   the JS function name
     * @param javaFunctionName the Java function name
     * @throws JavetException the Javet exception
     * @since 0.7.0
     */
    protected void register(IV8ValueObject iV8ValueObject, String jsFunctionName, String javaFunctionName)
            throws JavetException {
        try {
            iV8ValueObject.bindFunction(
                    jsFunctionName,
                    new JavetCallbackContext(this,
                            getClass().getMethod(javaFunctionName, V8Value[].class)));
        } catch (NoSuchMethodException e) {
            throw new JavetException(
                    JavetError.CallbackRegistrationFailure,
                    SimpleMap.of(
                            JavetError.PARAMETER_METHOD_NAME, javaFunctionName,
                            JavetError.PARAMETER_MESSAGE, e.getMessage()),
                    e);
        }
    }

    @Override
    public boolean unregister(IV8ValueObject... iV8ValueObjects) throws JavetException {
        boolean successful = true;
        for (IV8ValueObject iV8ValueObject : iV8ValueObjects) {
            try (V8ValueObject console = iV8ValueObject.get(PROPERTY_CONSOLE)) {
                console.delete(JS_FUNCTION_DEBUG);
                console.delete(JS_FUNCTION_ERROR);
                console.delete(JS_FUNCTION_INFO);
                console.delete(JS_FUNCTION_LOG);
                console.delete(JS_FUNCTION_TRACE);
                console.delete(JS_FUNCTION_WARN);
            }
            successful &= iV8ValueObject.delete(PROPERTY_CONSOLE);
        }
        return successful;
    }
}
