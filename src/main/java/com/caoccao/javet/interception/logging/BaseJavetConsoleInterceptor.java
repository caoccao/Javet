/*
 * Copyright (c) 2021-2024. caoccao.com Sam Cao
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

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interception.BaseJavetDirectCallableInterceptor;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.callback.JavetCallbackContext;
import com.caoccao.javet.interop.callback.JavetCallbackType;
import com.caoccao.javet.utils.V8ValueUtils;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.reference.IV8ValueObject;
import com.caoccao.javet.values.reference.V8ValueObject;

/**
 * The type Base javet console interceptor.
 *
 * @since 0.7.0
 */
public abstract class BaseJavetConsoleInterceptor extends BaseJavetDirectCallableInterceptor {
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
    public JavetCallbackContext[] getCallbackContexts() {
        return new JavetCallbackContext[]{
                new JavetCallbackContext(
                        JS_FUNCTION_DEBUG, this, JavetCallbackType.DirectCallNoThisAndNoResult,
                        (NoThisAndNoResult<?>) this::consoleDebug),
                new JavetCallbackContext(
                        JS_FUNCTION_ERROR, this, JavetCallbackType.DirectCallNoThisAndNoResult,
                        (NoThisAndNoResult<?>) this::consoleError),
                new JavetCallbackContext(
                        JS_FUNCTION_INFO, this, JavetCallbackType.DirectCallNoThisAndNoResult,
                        (NoThisAndNoResult<?>) this::consoleInfo),
                new JavetCallbackContext(
                        JS_FUNCTION_LOG, this, JavetCallbackType.DirectCallNoThisAndNoResult,
                        (NoThisAndNoResult<?>) this::consoleLog),
                new JavetCallbackContext(
                        JS_FUNCTION_TRACE, this, JavetCallbackType.DirectCallNoThisAndNoResult,
                        (NoThisAndNoResult<?>) this::consoleTrace),
                new JavetCallbackContext(
                        JS_FUNCTION_WARN, this, JavetCallbackType.DirectCallNoThisAndNoResult,
                        (NoThisAndNoResult<?>) this::consoleWarn),
        };
    }

    @Override
    public boolean register(IV8ValueObject... iV8ValueObjects) throws JavetException {
        boolean successful = true;
        try (V8ValueObject v8ValueObject = v8Runtime.createV8ValueObject()) {
            v8ValueObject.bind(this);
            for (IV8ValueObject iV8ValueObject : iV8ValueObjects) {
                successful = iV8ValueObject.set(PROPERTY_CONSOLE, v8ValueObject) & successful;
            }
            return successful;
        }
    }

    @Override
    public boolean unregister(IV8ValueObject... iV8ValueObjects) throws JavetException {
        boolean successful = true;
        for (IV8ValueObject iV8ValueObject : iV8ValueObjects) {
            successful = iV8ValueObject.delete(PROPERTY_CONSOLE) & successful;
        }
        return successful;
    }
}
