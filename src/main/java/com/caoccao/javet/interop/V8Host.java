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
import com.caoccao.javet.exceptions.JavetV8RuntimeLeakException;
import com.caoccao.javet.interfaces.IJavetLogger;
import com.caoccao.javet.utils.JavetDefaultLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public final class V8Host implements AutoCloseable {
    public static final String GLOBAL_THIS = "globalThis";
    private static final long INVALID_HANDLE = 0L;
    private static final String FLAG_ALLOW_NATIVES_SYNTAX = "--allow-natives-syntax";
    private static final String FLAG_EXPOSE_GC = "--expose-gc";
    private static final String FLAG_EXPOSE_INSPECTOR_SCRIPTS = "--expose-inspector-scripts";
    private static final String FLAG_HARMONY_TOP_LEVEL_AWAIT = "--harmony-top-level-await";
    private static final String FLAG_TRACK_RETAINING_PATH = "--track-retaining-path";
    private static final String FLAG_USE_STRICT = "--use-strict";
    private static final String SPACE = " ";
    private static final Object nodeLock = new Object();
    private static final Object v8Lock = new Object();
    private static volatile V8Host nodeInstance;
    private static volatile V8Host v8Instance;

    private V8Flags flags;
    private JavetClassLoader javetClassLoader;
    private JSRuntimeType jsRuntimeType;
    private boolean libLoaded;
    private JavetException lastException;
    private IJavetLogger logger;
    private boolean isolateCreated;
    private IV8Native v8Native;
    private ConcurrentHashMap<Long, V8Runtime> v8RuntimeMap;

    private V8Host(JSRuntimeType jsRuntimeType) {
        javetClassLoader = null;
        lastException = null;
        libLoaded = false;
        flags = new V8Flags();
        logger = new JavetDefaultLogger(getClass().getName());
        v8RuntimeMap = new ConcurrentHashMap<>();
        v8Native = null;
        isolateCreated = false;
        setJSRuntimeType(jsRuntimeType);
    }

    public static V8Host getNodeInstance() {
        if (nodeInstance == null) {
            synchronized (nodeLock) {
                if (nodeInstance == null) {
                    nodeInstance = new V8Host(JSRuntimeType.Node);
                }
            }
        }
        return nodeInstance;
    }

    public static V8Host getV8Instance() {
        if (v8Instance == null) {
            synchronized (v8Lock) {
                if (v8Instance == null) {
                    v8Instance = new V8Host(JSRuntimeType.V8);
                }
            }
        }
        return v8Instance;
    }

    @Override
    public void close() throws JavetException {
        final int v8RuntimeCount = getV8RuntimeCount();
        if (v8RuntimeCount != 0) {
            throw new JavetV8RuntimeLeakException(v8RuntimeCount);
        }
    }

    public V8Flags getFlags() {
        return flags;
    }

    public String getJavetVersion() {
        return JavetLibLoader.LIB_VERSION;
    }

    public IJavetLogger getLogger() {
        return logger;
    }

    public IV8Native getV8Native() {
        return v8Native;
    }

    public V8Runtime createV8Runtime() {
        return createV8Runtime(GLOBAL_THIS);
    }

    public V8Runtime createV8Runtime(String globalName) {
        return createV8Runtime(false, globalName);
    }

    public V8Runtime createV8Runtime(boolean pooled, String globalName) {
        if (!isLibLoaded()) {
            return null;
        }
        final long handle = v8Native.createV8Runtime(globalName);
        isolateCreated = true;
        flags.seal();
        V8Runtime v8Runtime = null;
        if (jsRuntimeType.isNode()) {
            v8Runtime = new NodeRuntime(this, handle, pooled, v8Native);
        } else if (jsRuntimeType.isV8()) {
            v8Runtime = new V8Runtime(this, handle, pooled, v8Native, globalName);
        }
        v8Native.registerV8Runtime(handle, v8Runtime);
        v8RuntimeMap.put(handle, v8Runtime);
        return v8Runtime;
    }

    public void closeV8Runtime(V8Runtime v8Runtime) {
        if (!isLibLoaded()) {
            return;
        }
        if (v8Runtime != null) {
            final long handle = v8Runtime.getHandle();
            if (handle > INVALID_HANDLE && v8RuntimeMap.containsKey(handle)) {
                v8Native.closeV8Runtime(v8Runtime.getHandle());
                v8RuntimeMap.remove(handle);
            }
        }
    }

    public JSRuntimeType getJSRuntimeType() {
        return jsRuntimeType;
    }

    private void setJSRuntimeType(JSRuntimeType jsRuntimeType) {
        if (this.jsRuntimeType == jsRuntimeType) {
            return;
        }
        if (libLoaded) {
            javetClassLoader = null;
            System.gc();
            System.runFinalization();
            libLoaded = false;
        }
        this.jsRuntimeType = jsRuntimeType;
        if (jsRuntimeType != null) {
            try {
                javetClassLoader = new JavetClassLoader(getClass().getClassLoader(), jsRuntimeType);
                 javetClassLoader.load();
                 v8Native = javetClassLoader.getNative();
//                JavetLibLoader javetLibLoader = new JavetLibLoader(jsRuntimeType);
//                javetLibLoader.load();
                libLoaded = true;
            } catch (JavetException e) {
                logger.logError(e, "Failed to load Javet lib with error {0}.", e.getMessage());
                lastException = e;
            }
        }
    }

    public JavetException getLastException() {
        return lastException;
    }

    public int getV8RuntimeCount() {
        return v8RuntimeMap.size();
    }

    public boolean isLibLoaded() {
        return libLoaded;
    }

    public boolean isIsolateCreated() {
        return isolateCreated;
    }

    public boolean setFlags() {
        if (libLoaded && !isolateCreated) {
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
            if (this.flags.isHarmonyTopLevelAwait()) {
                flags.add(FLAG_HARMONY_TOP_LEVEL_AWAIT);
            }
            if (this.flags.isUseStrict()) {
                flags.add(FLAG_USE_STRICT);
            }
            if (this.flags.isTrackRetainingPath()) {
                flags.add(FLAG_TRACK_RETAINING_PATH);
            }
            v8Native.setFlags(String.join(SPACE, flags));
            return true;
        }
        return false;
    }
}
