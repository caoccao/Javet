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

package com.caoccao.javet.interception.logging;

import com.caoccao.javet.exceptions.JavetError;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interception.BaseJavetInterceptor;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.utils.JavetCallbackContext;
import com.caoccao.javet.utils.SimpleMap;
import com.caoccao.javet.utils.V8ValueUtils;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.reference.IV8ValueObject;
import com.caoccao.javet.values.reference.V8ValueObject;

public abstract class BaseJavetConsoleInterceptor extends BaseJavetInterceptor {
    protected static final String SPACE = " ";
    protected static final String JS_FUNCTION_DEBUG = "debug";
    protected static final String JS_FUNCTION_ERROR = "error";
    protected static final String JS_FUNCTION_INFO = "info";
    protected static final String JS_FUNCTION_LOG = "log";
    protected static final String JS_FUNCTION_TRACE = "trace";
    protected static final String JS_FUNCTION_WARN = "warn";
    protected static final String JAVA_CONSOLE_DEBUG = "consoleDebug";
    protected static final String JAVA_CONSOLE_ERROR = "consoleError";
    protected static final String JAVA_CONSOLE_INFO = "consoleInfo";
    protected static final String JAVA_CONSOLE_LOG = "consoleLog";
    protected static final String JAVA_CONSOLE_TRACE = "consoleTrace";
    protected static final String JAVA_CONSOLE_WARN = "consoleWarn";
    protected static final String PROPERTY_CONSOLE = "console";

    public BaseJavetConsoleInterceptor(V8Runtime v8Runtime) {
        super(v8Runtime);
    }

    public String concat(V8Value... v8Values) {
        return V8ValueUtils.concat(SPACE, v8Values);
    }

    public abstract void consoleDebug(V8Value... v8Values);

    public abstract void consoleError(V8Value... v8Values);

    public abstract void consoleInfo(V8Value... v8Values);

    public abstract void consoleLog(V8Value... v8Values);

    public abstract void consoleTrace(V8Value... v8Values);

    public abstract void consoleWarn(V8Value... v8Values);

    @Override
    public boolean register(IV8ValueObject iV8ValueObject) throws JavetException {
        try (V8ValueObject console = v8Runtime.createV8ValueObject()) {
            iV8ValueObject.set(PROPERTY_CONSOLE, console);
            register(console, JS_FUNCTION_DEBUG, JAVA_CONSOLE_DEBUG);
            register(console, JS_FUNCTION_ERROR, JAVA_CONSOLE_ERROR);
            register(console, JS_FUNCTION_INFO, JAVA_CONSOLE_INFO);
            register(console, JS_FUNCTION_LOG, JAVA_CONSOLE_LOG);
            register(console, JS_FUNCTION_TRACE, JAVA_CONSOLE_TRACE);
            register(console, JS_FUNCTION_WARN, JAVA_CONSOLE_WARN);
            return true;
        } catch (NoSuchMethodException e) {
            throw new JavetException(
                    JavetError.CallbackMethodNotFound,
                    SimpleMap.of(JavetError.PARAMETER_MESSAGE, e.getMessage()),
                    e);
        }
    }

    protected void register(IV8ValueObject iV8ValueObject, String jsFunctionName, String javaFunctionName)
            throws JavetException, NoSuchMethodException {
        iV8ValueObject.setFunction(
                jsFunctionName,
                new JavetCallbackContext(this,
                        getClass().getMethod(javaFunctionName, V8Value[].class)));
    }

    @Override
    public boolean unregister(IV8ValueObject iV8ValueObject) throws JavetException {
        return iV8ValueObject.delete(PROPERTY_CONSOLE);
    }
}
