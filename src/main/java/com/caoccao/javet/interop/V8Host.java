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

import com.caoccao.javet.enums.JSRuntimeType;
import com.caoccao.javet.exceptions.JavetError;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interfaces.IJavetLogger;
import com.caoccao.javet.utils.JavetDefaultLogger;
import com.caoccao.javet.utils.SimpleMap;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryType;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The type V8 host.
 */
@SuppressWarnings("unchecked")
public final class V8Host implements AutoCloseable {
    /**
     * The constant GLOBAL_THIS.
     */
    public static final String GLOBAL_THIS = "globalThis";
    private static final long INVALID_HANDLE = 0L;
    private static final String FLAG_ALLOW_NATIVES_SYNTAX = "--allow-natives-syntax";
    private static final String FLAG_EXPOSE_GC = "--expose-gc";
    private static final String FLAG_EXPOSE_INSPECTOR_SCRIPTS = "--expose-inspector-scripts";
    private static final String FLAG_TRACK_RETAINING_PATH = "--track-retaining-path";
    private static final String FLAG_USE_STRICT = "--use-strict";
    private static final String SPACE = " ";
    private static boolean libraryReloadable = false;
    private static volatile double memoryUsageThresholdRatio = 0.7;
    private final V8Flags flags;
    private final JSRuntimeType jsRuntimeType;
    private final IJavetLogger logger;
    private final V8Notifier v8Notifier;
    private final ConcurrentHashMap<Long, V8Runtime> v8RuntimeMap;
    private JavetClassLoader javetClassLoader;
    private boolean libraryLoaded;
    private JavetException lastException;
    private boolean isolateCreated;
    private IV8Native v8Native;

    private V8Host(JSRuntimeType jsRuntimeType) {
        Objects.requireNonNull(jsRuntimeType);
        javetClassLoader = null;
        lastException = null;
        libraryLoaded = false;
        flags = new V8Flags();
        logger = new JavetDefaultLogger(getClass().getName());
        v8RuntimeMap = new ConcurrentHashMap<>();
        v8Native = null;
        isolateCreated = false;
        this.jsRuntimeType = jsRuntimeType;
        loadLibrary();
        v8Notifier = new V8Notifier(v8RuntimeMap);
    }

    /**
     * Gets instance by JS runtime type.
     *
     * @param jsRuntimeType the JS runtime type
     * @return the instance
     * @since 0.7.0
     */
    public static V8Host getInstance(JSRuntimeType jsRuntimeType) {
        Objects.requireNonNull(jsRuntimeType);
        if (jsRuntimeType.isV8()) {
            return getV8Instance();
        } else if (jsRuntimeType.isNode()) {
            return getNodeInstance();
        }
        return null;
    }

    /**
     * Gets Node instance.
     * <p>
     * Note: Node runtime library is loaded by a custom class loader.
     *
     * @return the Node instance
     * @since 0.8.0
     */
    public static V8Host getNodeInstance() {
        return NodeInstanceHolder.INSTANCE;
    }

    /**
     * Gets V8 instance.
     * <p>
     * Note: V8 runtime library is loaded by a custom class loader.
     *
     * @return the V8 instance
     * @since 0.8.0
     */
    public static V8Host getV8Instance() {
        return V8InstanceHolder.INSTANCE;
    }

    /**
     * Gets memory usage threshold ratio.
     *
     * @return the memory usage threshold ratio
     */
    public static double getMemoryUsageThresholdRatio() {
        return memoryUsageThresholdRatio;
    }

    /**
     * Sets memory usage threshold ratio.
     * <p>
     * This manageable usage threshold attribute is designed for monitoring
     * the increasing trend of memory usage with low overhead.
     *
     * @param memoryUsageThresholdRatio the memory usage threshold ratio
     */
    public static void setMemoryUsageThresholdRatio(double memoryUsageThresholdRatio) {
        assert 0 <= memoryUsageThresholdRatio && memoryUsageThresholdRatio < 1;
        V8Host.memoryUsageThresholdRatio = memoryUsageThresholdRatio;
    }

    private static void setMemoryUsageThreshold() {
        /*
         * @see <a href="https://docs.oracle.com/javase/8/docs/api/java/lang/management/MemoryPoolMXBean.html">Memory Usage Monitoring</a>
         */
        if (memoryUsageThresholdRatio > 0) {
            Optional<MemoryPoolMXBean> optionalHeapMemoryPoolMXBean = ManagementFactory.getMemoryPoolMXBeans().stream()
                    .filter(pool -> pool.getType() == MemoryType.HEAP)
                    .filter(MemoryPoolMXBean::isUsageThresholdSupported)
                    .findFirst();
            if (optionalHeapMemoryPoolMXBean.isPresent()) {
                final long memoryUsageThreshold = (long) Math.floor(
                        optionalHeapMemoryPoolMXBean.get().getUsage().getMax() * memoryUsageThresholdRatio);
                optionalHeapMemoryPoolMXBean.get().setUsageThreshold(memoryUsageThreshold);
            }
        }
    }

    /**
     * Determines whether the JNI library is reloadable or not.
     *
     * @return true : reloadable, false: not reloadable, default: false
     * @since 0.9.1
     */
    public static boolean isLibraryReloadable() {
        return libraryReloadable;
    }

    /**
     * Sets whether the JNI library is reloadable or not.
     *
     * @param libraryReloadable true: reloadable, false: not reloadable
     * @since 0.9.1
     */
    public static void setLibraryReloadable(boolean libraryReloadable) {
        V8Host.libraryReloadable = libraryReloadable;
    }

    /**
     * Clear internal statistic for internal test purpose.
     */
    public void clearInternalStatistic() {
        v8Native.clearInternalStatistic();
    }

    @Override
    public void close() throws JavetException {
        final int v8RuntimeCount = getV8RuntimeCount();
        if (v8RuntimeCount != 0) {
            throw new JavetException(
                    JavetError.RuntimeLeakageDetected,
                    SimpleMap.of(JavetError.PARAMETER_COUNT, v8RuntimeCount));
        }
        disableGCNotification();
    }

    /**
     * Disable GC notification.
     *
     * @return the self
     */
    @SuppressWarnings("UnusedReturnValue")
    public V8Host disableGCNotification() {
        v8Notifier.unregisterListener();
        return this;
    }

    /**
     * Enable GC notification.
     *
     * @return the self
     */
    public V8Host enableGCNotification() {
        setMemoryUsageThreshold();
        // Javet {@link V8Notifier} listens to this notification to notify {@link V8Runtime} to perform GC.
        v8Notifier.registerListeners();
        return this;
    }

    /**
     * Gets flags.
     *
     * @return the flags
     */
    public V8Flags getFlags() {
        return flags;
    }

    /**
     * Get internal statistic internal for test purpose.
     *
     * @return the long [ ]
     */
    public long[] getInternalStatistic() {
        return v8Native.getInternalStatistic();
    }

    /**
     * Gets javet version.
     *
     * @return the javet version
     */
    public String getJavetVersion() {
        return JavetLibLoader.LIB_VERSION;
    }

    /**
     * Gets logger.
     *
     * @return the logger
     */
    public IJavetLogger getLogger() {
        return logger;
    }

    /**
     * Gets V8 native.
     *
     * @return the V8 native
     */
    IV8Native getV8Native() {
        return v8Native;
    }

    /**
     * Create V8 runtime.
     *
     * @param <R> the type parameter
     * @return the V8 runtime
     * @throws JavetException the javet exception
     */
    public <R extends V8Runtime> R createV8Runtime() throws JavetException {
        return createV8Runtime(GLOBAL_THIS);
    }

    /**
     * Create V8 runtime.
     *
     * @param <R>        the type parameter
     * @param globalName the global name
     * @return the V8 runtime
     * @throws JavetException the javet exception
     */
    public <R extends V8Runtime> R createV8Runtime(String globalName) throws JavetException {
        return createV8Runtime(false, globalName);
    }

    /**
     * Create V8 runtime.
     *
     * @param <R>        the type parameter
     * @param pooled     the pooled
     * @param globalName the global name
     * @return the V8 runtime
     * @throws JavetException the javet exception
     */
    public <R extends V8Runtime> R createV8Runtime(boolean pooled, String globalName) throws JavetException {
        if (!libraryLoaded) {
            if (lastException == null) {
                throw new JavetException(
                        JavetError.LibraryNotLoaded,
                        SimpleMap.of(JavetError.PARAMETER_REASON, "there are unknown errors"));
            } else {
                throw lastException;
            }
        }
        final long handle = v8Native.createV8Runtime(globalName);
        isolateCreated = true;
        flags.seal();
        V8Runtime v8Runtime;
        if (jsRuntimeType.isNode()) {
            v8Runtime = new NodeRuntime(this, handle, pooled, v8Native);
        } else {
            v8Runtime = new V8Runtime(this, handle, pooled, v8Native, globalName);
        }
        v8Native.registerV8Runtime(handle, v8Runtime);
        v8RuntimeMap.put(handle, v8Runtime);
        return (R) v8Runtime;
    }

    /**
     * Close V8 runtime.
     *
     * @param v8Runtime the V8 runtime
     */
    public void closeV8Runtime(V8Runtime v8Runtime) {
        if (!libraryLoaded) {
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

    /**
     * Gets JS runtime type.
     *
     * @return the JS runtime type
     */
    public JSRuntimeType getJSRuntimeType() {
        return jsRuntimeType;
    }

    /**
     * Load library.
     * <p>
     * Note: setLibraryReloadable(true) must be called, otherwise, JVM will crash.
     *
     * @since 0.9.1
     */
    public synchronized void loadLibrary() {
        if (!libraryLoaded) {
            try {
                javetClassLoader = new JavetClassLoader(getClass().getClassLoader(), jsRuntimeType);
                javetClassLoader.load();
                v8Native = javetClassLoader.getNative();
                libraryLoaded = true;
                isolateCreated = false;
            } catch (JavetException e) {
                logger.logError(e, "Failed to load Javet lib with error {0}.", e.getMessage());
                lastException = e;
            }
        }
    }

    /**
     * Unload library.
     * <p>
     * Note: setLibraryReloadable(true) must be called, otherwise, JVM will crash.
     *
     * @since 0.9.1
     */
    public synchronized void unloadLibrary() {
        if (libraryLoaded) {
            isolateCreated = false;
            v8Native = null;
            javetClassLoader = null;
            System.gc();
            System.runFinalization();
            libraryLoaded = false;
            lastException = null;
        }
    }

    /**
     * Gets last exception.
     *
     * @return the last exception
     */
    public JavetException getLastException() {
        return lastException;
    }

    /**
     * Gets V8 runtime count.
     *
     * @return the V8 runtime count
     */
    public int getV8RuntimeCount() {
        return v8RuntimeMap.size();
    }

    /**
     * Is library loaded.
     *
     * @return true: loaded, false: not loaded
     */
    public boolean isLibraryLoaded() {
        return libraryLoaded;
    }

    /**
     * Is isolate created.
     *
     * @return true: created, false: not created
     */
    public boolean isIsolateCreated() {
        return isolateCreated;
    }

    /**
     * Sets flags.
     *
     * @return true: flags are set, false: flags are not set
     * @since 0.7.0
     */
    @SuppressWarnings("UnusedReturnValue")
    public boolean setFlags() {
        if (libraryLoaded && !isolateCreated) {
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
            v8Native.setFlags(String.join(SPACE, flags));
            return true;
        }
        return false;
    }

    private static class NodeInstanceHolder {
        private static V8Host INSTANCE = new V8Host(JSRuntimeType.Node);
    }

    private static class V8InstanceHolder {
        private static V8Host INSTANCE = new V8Host(JSRuntimeType.V8);
    }
}
