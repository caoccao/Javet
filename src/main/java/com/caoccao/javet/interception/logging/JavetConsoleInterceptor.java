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

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interception.BaseJavetInterceptor;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.utils.V8CallbackContext;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.reference.IV8ValueObject;
import com.caoccao.javet.values.reference.V8ValueFunction;
import com.caoccao.javet.values.reference.V8ValueObject;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.stream.Collectors;

public class JavetConsoleInterceptor extends BaseJavetInterceptor {
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
    protected static final String EMPTY = "";
    protected static final String SPACE = " ";
    protected static final String PROPERTY_CONSOLE = "console";
    protected PrintStream debug;
    protected PrintStream error;
    protected PrintStream info;
    protected PrintStream log;
    protected PrintStream trace;
    protected PrintStream warn;

    public JavetConsoleInterceptor(V8Runtime v8Runtime) {
        super(v8Runtime);
        debug = info = log = trace = warn = System.out;
        error = System.err;
    }

    public PrintStream getDebug() {
        return debug;
    }

    public void setDebug(PrintStream debug) {
        this.debug = debug;
    }

    public PrintStream getError() {
        return error;
    }

    public void setError(PrintStream error) {
        this.error = error;
    }

    public PrintStream getInfo() {
        return info;
    }

    public void setInfo(PrintStream info) {
        this.info = info;
    }

    public PrintStream getLog() {
        return log;
    }

    public void setLog(PrintStream log) {
        this.log = log;
    }

    public PrintStream getTrace() {
        return trace;
    }

    public void setTrace(PrintStream trace) {
        this.trace = trace;
    }

    public PrintStream getWarn() {
        return warn;
    }

    public void setWarn(PrintStream warn) {
        this.warn = warn;
    }

    protected String concat(V8Value... v8Values) {
        if (v8Values == null || v8Values.length == 0) {
            return EMPTY;
        }
        return String.join(
                SPACE,
                Arrays.stream(v8Values).map(v8Value -> v8Value.toString()).collect(Collectors.toList()));
    }

    public void consoleDebug(V8Value... v8Values) {
        debug.println(concat(v8Values));
    }

    public void consoleError(V8Value... v8Values) {
        error.println(concat(v8Values));
    }

    public void consoleInfo(V8Value... v8Values) {
        info.println(concat(v8Values));
    }

    public void consoleLog(V8Value... v8Values) {
        log.println(concat(v8Values));
    }

    public void consoleTrace(V8Value... v8Values) {
        trace.println(concat(v8Values));
    }

    public void consoleWarn(V8Value... v8Values) {
        warn.println(concat(v8Values));
    }

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
            e.printStackTrace();
        }
        return false;
    }

    protected void register(IV8ValueObject iV8ValueObject, String jsFunctionName, String javaFunctionName)
            throws JavetException, NoSuchMethodException {
        V8CallbackContext callbackContext = new V8CallbackContext(
                this, getClass().getMethod(javaFunctionName, V8Value[].class));
        V8ValueFunction v8ValueFunction = v8Runtime.createV8ValueFunction(callbackContext);
        iV8ValueObject.set(jsFunctionName, v8ValueFunction);
        v8ValueFunction.setWeak();
    }

    @Override
    public boolean unregister(IV8ValueObject iV8ValueObject) throws JavetException {
        return iV8ValueObject.delete(PROPERTY_CONSOLE);
    }

    @Override
    public V8Runtime getV8Runtime() {
        return null;
    }
}
