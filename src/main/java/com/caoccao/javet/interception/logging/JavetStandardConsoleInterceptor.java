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

import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.values.V8Value;

import java.io.PrintStream;

public class JavetStandardConsoleInterceptor extends BaseJavetConsoleInterceptor {
    protected PrintStream debug;
    protected PrintStream error;
    protected PrintStream info;
    protected PrintStream log;
    protected PrintStream trace;
    protected PrintStream warn;

    public JavetStandardConsoleInterceptor(V8Runtime v8Runtime) {
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
}
