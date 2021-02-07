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

import com.caoccao.javet.config.JavetConfig;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.exceptions.JavetOSNotSupportedException;
import com.caoccao.javet.exceptions.JavetV8RuntimeLeakException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public final class V8Host implements AutoCloseable {
    private static final String FLAG_EXPOSE_GC = "--expose_gc";
    private static final String FLAG_USE_STRICT = "--use_strict";
    private static final String SPACE = " ";

    private static V8Host instance = new V8Host();

    private boolean closed;
    private boolean libLoaded;
    private boolean isolateCreated;
    private ConcurrentHashMap<Long, V8Runtime> v8RuntimeMap;
    private JavetException lastException;

    private V8Host() {
        closed = true;
        libLoaded = false;
        lastException = null;
        v8RuntimeMap = new ConcurrentHashMap<>();
        try {
            libLoaded = JavetLibLoader.load();
            closed = false;
        } catch (JavetOSNotSupportedException e) {
            lastException = e;
        }
        isolateCreated = false;
    }

    public static V8Host getInstance() {
        return instance;
    }

    public V8Runtime createV8Runtime() {
        return createV8Runtime(null);
    }

    public V8Runtime createV8Runtime(String globalName) {
        if (closed) {
            return null;
        }
        final long handle = V8Native.createV8Runtime(globalName);
        isolateCreated = true;
        JavetConfig.seal();
        V8Runtime v8Runtime = new V8Runtime(this, handle, globalName);
        v8RuntimeMap.put(handle, v8Runtime);
        return v8Runtime;
    }

    public void closeV8Runtime(V8Runtime v8Runtime) {
        if (v8Runtime != null) {
            V8Native.closeV8Runtime(v8Runtime.getHandle());
        }
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

    public boolean isClosed() {
        return closed;
    }

    public boolean setFlags() {
        if (!closed && libLoaded && !isolateCreated) {
            List<String> flags = new ArrayList<>();
            if (JavetConfig.isUseStrict()) {
                flags.add(FLAG_USE_STRICT);
            }
            if (JavetConfig.isExposeGC()) {
                flags.add(FLAG_EXPOSE_GC);
            }
            V8Native.setFlags(String.join(SPACE, flags));
            return true;
        }
        return false;
    }

    @Override
    public void close() throws JavetException {
        if (closed) {
            return;
        }
        closed = true;
        final int v8RuntimeCount = v8RuntimeMap.size();
        if (v8RuntimeCount != 0) {
            throw new JavetV8RuntimeLeakException(v8RuntimeCount);
        }
    }
}
