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
import com.caoccao.javet.exceptions.JavetIOException;
import com.caoccao.javet.exceptions.JavetOSNotSupportedException;
import com.caoccao.javet.exceptions.JavetV8RuntimeLeakException;
import com.caoccao.javet.interfaces.IJavetLogger;
import com.caoccao.javet.utils.JavetDefaultLogger;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public final class V8Host implements AutoCloseable {
    public static final String GLOBAL_THIS = "globalThis";
    private static final long INVALID_HANDLE = 0L;
    private static final String FLAG_ALLOW_NATIVES_SYNTAX = "--allow-natives-syntax";
    private static final String FLAG_EXPOSE_GC = "--expose-gc";
    private static final String FLAG_EXPOSE_INSPECTOR_SCRIPTS = "--expose-inspector-scripts";
    private static final String FLAG_TRACK_RETAINING_PATH = "--track-retaining-path";
    private static final String FLAG_USE_STRICT = "--use-strict";
    private static final String SPACE = " ";
    private static V8Host instance = new V8Host();

    private boolean closed;
    private boolean libLoaded;
    private boolean isolateCreated;
    private ConcurrentHashMap<Long, V8Runtime> v8RuntimeMap;
    private JavetException lastException;
    private V8Flags flags;
    private IJavetLogger logger;

    private V8Host() {
        closed = true;
        libLoaded = false;
        lastException = null;
        flags = new V8Flags();
        logger = new JavetDefaultLogger(getClass().getName());
        v8RuntimeMap = new ConcurrentHashMap<>();
        try {
            libLoaded = JavetLibLoader.load();
            closed = false;
        } catch (JavetOSNotSupportedException | JavetIOException e) {
            logger.logError(e, "Failed to load Javet lib with error {0}.", e.getMessage());
            lastException = e;
        }
        isolateCreated = false;
    }

    public static V8Host getInstance() {
        return instance;
    }

    public V8Flags getFlags() {
        return flags;
    }

    public String getJavetVersion() {
        return JavetLibLoader.LIB_VERSION;
    }

    public V8Runtime createV8Runtime() {
        return createV8Runtime(GLOBAL_THIS);
    }

    public V8Runtime createV8Runtime(String globalName) {
        return createV8Runtime(false, globalName);
    }

    public V8Runtime createV8Runtime(boolean pooled, String globalName) {
        if (closed) {
            return null;
        }
        final long handle = V8Native.createV8Runtime(globalName);
        isolateCreated = true;
        flags.seal();
        V8Runtime v8Runtime = new V8Runtime(this, handle, pooled, globalName);
        v8RuntimeMap.put(handle, v8Runtime);
        return v8Runtime;
    }

    public void closeV8Runtime(V8Runtime v8Runtime) {
        if (closed) {
            return;
        }
        if (v8Runtime != null) {
            final long handle = v8Runtime.getHandle();
            if (handle > INVALID_HANDLE && v8RuntimeMap.containsKey(handle)) {
                V8Native.closeV8Runtime(v8Runtime.getHandle());
                v8RuntimeMap.remove(handle);
            }
        }
    }

    public int getV8RuntimeCount() {
        return v8RuntimeMap.size();
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
            if (this.flags.isAllowNativesSyntax()) {
                flags.add(FLAG_ALLOW_NATIVES_SYNTAX);
            }
            if (this.flags.isExposeGC()) {
                flags.add(FLAG_EXPOSE_GC);
            }
            if (this.flags.isExposeInspectorScripts()) {
                flags.add(FLAG_EXPOSE_INSPECTOR_SCRIPTS);
            }
            if (this.flags.isUseStrict()) {
                flags.add(FLAG_USE_STRICT);
            }
            if (this.flags.isTrackRetainingPath()) {
                flags.add(FLAG_TRACK_RETAINING_PATH);
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
        final int v8RuntimeCount = getV8RuntimeCount();
        if (v8RuntimeCount != 0) {
            throw new JavetV8RuntimeLeakException(v8RuntimeCount);
        }
    }
}
