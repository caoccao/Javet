/*
 *    Copyright 2021. caoccao.com Sam Cao
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

package com.caoccao.javet.interop;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.exceptions.JavetOSNotSupportedException;
import com.caoccao.javet.exceptions.JavetV8RuntimeLeakException;

import java.util.concurrent.atomic.AtomicInteger;

public final class V8Host implements AutoCloseable {
    private static V8Host instance = new V8Host();
    private boolean libLoaded;
    private boolean isolateCreated;
    private AtomicInteger v8RuntimeCount;
    private JavetException lastException;

    private V8Host() {
        libLoaded = false;
        lastException = null;
        v8RuntimeCount = new AtomicInteger(0);
        try {
            libLoaded = JavetLibLoader.load();
        } catch (JavetOSNotSupportedException e) {
            lastException = e;
        }
        isolateCreated = false;
    }

    public static V8Host getInstance() {
        return instance;
    }

    public boolean isLibLoaded() {
        return libLoaded;
    }

    public JavetException getLastException() {
        return lastException;
    }

    public boolean isIsolateCreated() {
        return isolateCreated;
    }

    public void setFlags(String flags) {
        if (libLoaded && !isolateCreated) {
            V8Native.setFlags(flags);
        }
    }

    @Override
    public void close() throws JavetException {
        final int count = v8RuntimeCount.get();
        if (count != 0) {
            throw new JavetV8RuntimeLeakException(count);
        }
    }
}
