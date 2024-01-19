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

package com.caoccao.javet.interop;

import com.caoccao.javet.enums.JSRuntimeType;
import com.caoccao.javet.exceptions.JavetError;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interfaces.IJavetLogger;
import com.caoccao.javet.interop.loader.JavetLibLoader;
import com.caoccao.javet.interop.monitoring.V8SharedMemoryStatistics;
import com.caoccao.javet.interop.options.RuntimeOptions;
import com.caoccao.javet.interop.options.V8Flags;
import com.caoccao.javet.utils.JavetDefaultLogger;
import com.caoccao.javet.utils.SimpleMap;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryType;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The type V8 host.
 *
 * @since 0.7.0
 */
@SuppressWarnings("unchecked")
public final class V8Host implements AutoCloseable {
    private static final long INVALID_HANDLE = 0L;
    private static boolean libraryReloadable = false;
    private static volatile double memoryUsageThresholdRatio = 0.7;
    private final JSRuntimeType jsRuntimeType;
    private final IJavetLogger logger;
    private final V8Notifier v8Notifier;
    private final ConcurrentHashMap<Long, V8Runtime> v8RuntimeMap;
    private boolean isolateCreated;
    private JavetClassLoader javetClassLoader;
    private JavetException lastException;
    private volatile boolean libraryLoaded;
    private IV8Native v8Native;

    private V8Host(JSRuntimeType jsRuntimeType) {
        Objects.requireNonNull(jsRuntimeType);
        javetClassLoader = null;
        lastException = null;
        libraryLoaded = false;
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
     * Gets memory usage threshold ratio.
     *
     * @return the memory usage threshold ratio
     * @since 0.8.3
     */
    public static double getMemoryUsageThresholdRatio() {
        return memoryUsageThresholdRatio;
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

    private static void setMemoryUsageThreshold() {
        /*
         * @see <a href="https://docs.oracle.com/javase/8/docs/api/java/lang/management/MemoryPoolMXBean.html">Memory Usage Monitoring</a>
         */
        /* if not defined ANDROID */
        if (memoryUsageThresholdRatio > 0) {
            MemoryPoolMXBean heapMemoryPoolMXBean = null;
            List<MemoryPoolMXBean> memoryPoolMXBeans = ManagementFactory.getMemoryPoolMXBeans();
            for (MemoryPoolMXBean memoryPoolMXBean : memoryPoolMXBeans) {
                if (memoryPoolMXBean.getType() == MemoryType.HEAP && memoryPoolMXBean.isUsageThresholdSupported()) {
                    heapMemoryPoolMXBean = memoryPoolMXBean;
                    break;
                }
            }
            if (heapMemoryPoolMXBean != null) {
                final long memoryUsageThreshold = (long) Math.floor(
                        heapMemoryPoolMXBean.getUsage().getMax() * memoryUsageThresholdRatio);
                heapMemoryPoolMXBean.setUsageThreshold(memoryUsageThreshold);
            }
        }
        /* end if */
    }

    /**
     * Sets memory usage threshold ratio.
     * <p>
     * This manageable usage threshold attribute is designed for monitoring
     * the increasing trend of memory usage with low overhead.
     *
     * @param memoryUsageThresholdRatio the memory usage threshold ratio
     * @since 0.8.3
     */
    public static void setMemoryUsageThresholdRatio(double memoryUsageThresholdRatio) {
        assert 0 <= memoryUsageThresholdRatio && memoryUsageThresholdRatio < 1;
        V8Host.memoryUsageThresholdRatio = memoryUsageThresholdRatio;
    }

    /**
     * Clear internal statistic for internal test purpose.
     *
     * @since 0.8.3
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
     * Close V8 runtime.
     *
     * @param v8Runtime the V8 runtime
     * @since 0.7.0
     */
    public void closeV8Runtime(V8Runtime v8Runtime) {
        if (!libraryLoaded) {
            return;
        }
        if (v8Runtime != null) {
            final long handle = v8Runtime.getHandle();
            if (handle != INVALID_HANDLE && v8RuntimeMap.containsKey(handle)) {
                v8Native.closeV8Runtime(handle);
                v8RuntimeMap.remove(handle);
            }
        }
    }

    /**
     * Create V8 runtime.
     *
     * @param <R> the type parameter
     * @return the V8 runtime
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    public <R extends V8Runtime> R createV8Runtime() throws JavetException {
        return createV8Runtime(getJSRuntimeType().getRuntimeOptions());
    }

    /**
     * Create V8 runtime by custom global name.
     *
     * @param <R>            the type parameter
     * @param runtimeOptions the runtime options
     * @return the V8 runtime
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    public <R extends V8Runtime> R createV8Runtime(RuntimeOptions<?> runtimeOptions) throws JavetException {
        return createV8Runtime(false, runtimeOptions);
    }

    /**
     * Create V8 runtime by pooled flag and custom global name.
     *
     * @param <R>            the type parameter
     * @param pooled         the pooled
     * @param runtimeOptions the runtime options
     * @return the V8 runtime
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    public <R extends V8Runtime> R createV8Runtime(
            boolean pooled, RuntimeOptions<?> runtimeOptions) throws JavetException {
        assert getJSRuntimeType().isRuntimeOptionsValid(runtimeOptions);
        if (!libraryLoaded) {
            if (lastException == null) {
                throw new JavetException(
                        JavetError.LibraryNotLoaded,
                        SimpleMap.of(JavetError.PARAMETER_REASON, "there are unknown errors"));
            } else {
                throw lastException;
            }
        }
        final long handle = v8Native.createV8Runtime(runtimeOptions);
        isolateCreated = true;
        V8Runtime v8Runtime;
        if (jsRuntimeType.isNode()) {
            v8Runtime = new NodeRuntime(this, handle, pooled, v8Native, runtimeOptions);
        } else {
            v8Runtime = new V8Runtime(this, handle, pooled, v8Native, runtimeOptions);
        }
        v8Native.registerV8Runtime(handle, v8Runtime);
        v8RuntimeMap.put(handle, v8Runtime);
        return (R) v8Runtime;
    }

    /**
     * Disable GC notification.
     *
     * @return the self
     * @since 0.8.3
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
     * @since 0.8.3
     */
    public V8Host enableGCNotification() {
        setMemoryUsageThreshold();
        // Javet {@link V8Notifier} listens to this notification to notify {@link V8Runtime} to perform GC.
        v8Notifier.registerListeners();
        return this;
    }

    /**
     * Get internal statistic for test purpose.
     *
     * @return the internal statistic
     * @since 0.8.3
     */
    public long[] getInternalStatistic() {
        return v8Native.getInternalStatistic();
    }

    /**
     * Gets JS runtime type.
     *
     * @return the JS runtime type
     * @since 0.8.0
     */
    public JSRuntimeType getJSRuntimeType() {
        return jsRuntimeType;
    }

    /**
     * Gets javet version.
     *
     * @return the javet version
     * @since 0.7.1
     */
    public String getJavetVersion() {
        return JavetLibLoader.LIB_VERSION;
    }

    /**
     * Gets last exception.
     *
     * @return the last exception
     * @since 0.7.0
     */
    public JavetException getLastException() {
        return lastException;
    }

    /**
     * Gets logger.
     *
     * @return the logger
     * @since 0.7.3
     */
    public IJavetLogger getLogger() {
        return logger;
    }

    /**
     * Gets V8 native.
     *
     * @return the V8 native
     * @since 0.8.0
     */
    IV8Native getV8Native() {
        return v8Native;
    }

    /**
     * Gets V8 runtime count.
     *
     * @return the V8 runtime count
     * @since 0.8.0
     */
    public int getV8RuntimeCount() {
        return v8RuntimeMap.size();
    }

    /**
     * Gets V8 shared memory statistics.
     *
     * @return the V8 shared memory statistics
     * @since 1.0.6
     */
    public V8SharedMemoryStatistics getV8SharedMemoryStatistics() {
        return (V8SharedMemoryStatistics) v8Native.getV8SharedMemoryStatistics();
    }

    /**
     * Is isolate created.
     *
     * @return true : created, false: not created
     * @since 0.8.0
     */
    public boolean isIsolateCreated() {
        return isolateCreated;
    }

    /**
     * Is library loaded.
     *
     * @return true : loaded, false: not loaded
     * @since 0.8.0
     */
    public boolean isLibraryLoaded() {
        return libraryLoaded;
    }

    /**
     * Load library.
     * <p>
     * Note: setLibraryReloadable(true) must be called, otherwise, JVM will crash.
     *
     * @return true : library is loaded, false: library is not loaded
     * @since 0.9.1
     */
    public synchronized boolean loadLibrary() {
        if (!libraryLoaded) {
            try {
                logger.logDebug(
                        "[{0}] Loading library.",
                        jsRuntimeType.getName());
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
        return libraryLoaded;
    }

    /**
     * Unload library.
     * <p>
     * Note: setLibraryReloadable(true) must be called, otherwise, JVM will crash.
     *
     * @return true : library is unloaded, false: library is loaded
     * @since 0.9.1
     */
    public synchronized boolean unloadLibrary() {
        if (libraryLoaded && v8RuntimeMap.isEmpty()) {
            logger.logDebug(
                    "[{0}] Unloading library.",
                    jsRuntimeType.getName());
            isolateCreated = false;
            v8Native = null;
            javetClassLoader = null;
            System.gc();
            System.runFinalization();
            libraryLoaded = false;
            lastException = null;
        }
        return !libraryLoaded;
    }

    private static class NodeInstanceHolder {
        private static final V8Host INSTANCE = new V8Host(JSRuntimeType.Node);
    }

    private static class V8InstanceHolder {
        private static final V8Host INSTANCE = new V8Host(JSRuntimeType.V8);
    }
}
