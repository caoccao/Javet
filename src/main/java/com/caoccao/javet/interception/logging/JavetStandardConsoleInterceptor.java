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

import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.values.V8Value;

import java.io.PrintStream;

/**
 * The type Javet standard console interceptor.
 *
 * @since 0.7.0
 */
public class JavetStandardConsoleInterceptor extends BaseJavetConsoleInterceptor {
    /**
     * The Debug.
     *
     * @since 0.7.0
     */
    protected PrintStream debug;
    /**
     * The Error.
     *
     * @since 0.7.0
     */
    protected PrintStream error;
    /**
     * The Info.
     *
     * @since 0.7.0
     */
    protected PrintStream info;
    /**
     * The Log.
     *
     * @since 0.7.0
     */
    protected PrintStream log;
    /**
     * The Trace.
     *
     * @since 0.7.0
     */
    protected PrintStream trace;
    /**
     * The Warn.
     *
     * @since 0.7.0
     */
    protected PrintStream warn;

    /**
     * Instantiates a new Javet standard console interceptor.
     *
     * @param v8Runtime the V8 runtime
     * @since 0.7.0
     */
    public JavetStandardConsoleInterceptor(V8Runtime v8Runtime) {
        super(v8Runtime);
        debug = info = log = trace = warn = System.out;
        error = System.err;
    }

    @Override
    public void consoleDebug(V8Value... v8Values) {
        debug.println(concat(v8Values));
    }

    @Override
    public void consoleError(V8Value... v8Values) {
        error.println(concat(v8Values));
    }

    @Override
    public void consoleInfo(V8Value... v8Values) {
        info.println(concat(v8Values));
    }

    @Override
    public void consoleLog(V8Value... v8Values) {
        log.println(concat(v8Values));
    }

    @Override
    public void consoleTrace(V8Value... v8Values) {
        trace.println(concat(v8Values));
    }

    @Override
    public void consoleWarn(V8Value... v8Values) {
        warn.println(concat(v8Values));
    }

    /**
     * Gets debug.
     *
     * @return the debug
     * @since 0.7.0
     */
    public PrintStream getDebug() {
        return debug;
    }

    /**
     * Gets error.
     *
     * @return the error
     * @since 0.7.0
     */
    public PrintStream getError() {
        return error;
    }

    /**
     * Gets info.
     *
     * @return the info
     * @since 0.7.0
     */
    public PrintStream getInfo() {
        return info;
    }

    /**
     * Gets log.
     *
     * @return the log
     * @since 0.7.0
     */
    public PrintStream getLog() {
        return log;
    }

    /**
     * Gets trace.
     *
     * @return the trace
     * @since 0.7.0
     */
    public PrintStream getTrace() {
        return trace;
    }

    /**
     * Gets warn.
     *
     * @return the warn
     * @since 0.7.0
     */
    public PrintStream getWarn() {
        return warn;
    }

    /**
     * Sets debug.
     *
     * @param debug the debug
     * @since 0.7.0
     */
    public void setDebug(PrintStream debug) {
        this.debug = debug;
    }

    /**
     * Sets error.
     *
     * @param error the error
     * @since 0.7.0
     */
    public void setError(PrintStream error) {
        this.error = error;
    }

    /**
     * Sets info.
     *
     * @param info the info
     * @since 0.7.0
     */
    public void setInfo(PrintStream info) {
        this.info = info;
    }

    /**
     * Sets log.
     *
     * @param log the log
     * @since 0.7.0
     */
    public void setLog(PrintStream log) {
        this.log = log;
    }

    /**
     * Sets trace.
     *
     * @param trace the trace
     * @since 0.7.0
     */
    public void setTrace(PrintStream trace) {
        this.trace = trace;
    }

    /**
     * Sets warn.
     *
     * @param warn the warn
     * @since 0.7.0
     */
    public void setWarn(PrintStream warn) {
        this.warn = warn;
    }
}
