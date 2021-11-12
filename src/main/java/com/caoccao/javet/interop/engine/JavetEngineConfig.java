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

package com.caoccao.javet.interop.engine;

import com.caoccao.javet.enums.JSRuntimeType;
import com.caoccao.javet.interfaces.IJavetLogger;
import com.caoccao.javet.utils.JavetDefaultLogger;
import com.caoccao.javet.utils.JavetOSUtils;

import java.util.Objects;
import java.util.concurrent.ExecutorService;

/**
 * The type Javet engine config.
 *
 * @since 0.7.0
 */
public final class JavetEngineConfig {
    /**
     * The constant DEFAULT_ENGINE_GUARD_TIMEOUT_MILLIS.
     *
     * @since 0.7.2
     */
    public static final int DEFAULT_ENGINE_GUARD_TIMEOUT_MILLIS = 30000;
    /**
     * The constant DEFAULT_ENGINE_GUARD_CHECK_INTERVAL_MILLIS.
     *
     * @since 0.7.2
     */
    public static final int DEFAULT_ENGINE_GUARD_CHECK_INTERVAL_MILLIS = 1000;
    /**
     * The constant DEFAULT_JS_RUNTIME_TYPE.
     *
     * @since 0.8.0
     */
    public static final JSRuntimeType DEFAULT_JS_RUNTIME_TYPE = JSRuntimeType.V8;
    /**
     * The constant DEFAULT_POOL_MIN_SIZE.
     *
     * @since 0.7.0
     */
    public static final int DEFAULT_POOL_MIN_SIZE = 1;
    /**
     * The constant DEFAULT_POOL_IDLE_TIMEOUT_SECONDS.
     *
     * @since 0.7.0
     */
    public static final int DEFAULT_POOL_IDLE_TIMEOUT_SECONDS = 60;
    /**
     * The constant DEFAULT_POOL_DAEMON_CHECK_INTERVAL_MILLIS.
     *
     * @since 0.7.0
     */
    public static final int DEFAULT_POOL_DAEMON_CHECK_INTERVAL_MILLIS = 1000;
    /**
     * The constant DEFAULT_RESET_ENGINE_TIMEOUT_SECONDS.
     *
     * @since 0.7.0
     */
    public static final int DEFAULT_RESET_ENGINE_TIMEOUT_SECONDS = 3600;
    /**
     * The constant DEFAULT_POOL_SHUTDOWN_TIMEOUT_SECONDS.
     *
     * @since 0.7.2
     */
    public static final int DEFAULT_POOL_SHUTDOWN_TIMEOUT_SECONDS = 5;
    /**
     * The constant MAX_POOL_SIZE.
     *
     * @since 1.0.4
     */
    public static final int MAX_POOL_SIZE = 4096;
    /**
     * The constant DEFAULT_JAVET_LOGGER.
     *
     * @since 0.7.0
     */
    public static IJavetLogger DEFAULT_JAVET_LOGGER = new JavetDefaultLogger(JavetEnginePool.class.getName());
    private boolean allowEval;
    private boolean autoSendGCNotification;
    private int defaultEngineGuardTimeoutMillis;
    private int engineGuardCheckIntervalMillis;
    private ExecutorService executorService;
    private boolean gcBeforeEngineClose;
    private String globalName;
    private IJavetLogger javetLogger;
    private JSRuntimeType jsRuntimeType;
    private int poolDaemonCheckIntervalMillis;
    private int poolIdleTimeoutSeconds;
    private int poolMaxSize;
    private int poolMinSize;
    private int poolShutdownTimeoutSeconds;
    private int resetEngineTimeoutSeconds;

    /**
     * Instantiates a new Javet engine config.
     *
     * @since 0.7.0
     */
    public JavetEngineConfig() {
        reset();
    }

    /**
     * Gets default engine guard timeout millis.
     *
     * @return the default engine guard timeout millis
     * @since 0.9.1
     */
    public int getDefaultEngineGuardTimeoutMillis() {
        return defaultEngineGuardTimeoutMillis;
    }

    /**
     * Gets engine guard check interval millis.
     *
     * @return the engine guard check interval millis
     * @since 0.9.1
     */
    public int getEngineGuardCheckIntervalMillis() {
        return engineGuardCheckIntervalMillis;
    }

    /**
     * Gets executor service.
     *
     * @return the executor service
     * @since 0.9.1
     */
    public ExecutorService getExecutorService() {
        return executorService;
    }

    /**
     * Gets global name.
     *
     * @return the global name
     * @since 0.9.1
     */
    public String getGlobalName() {
        return globalName;
    }

    /**
     * Gets JS runtime type.
     *
     * @return the JS runtime type
     * @since 0.9.1
     */
    public JSRuntimeType getJSRuntimeType() {
        return jsRuntimeType;
    }

    /**
     * Gets javet logger.
     *
     * @return the javet logger
     * @since 0.9.1
     */
    public IJavetLogger getJavetLogger() {
        return javetLogger;
    }

    /**
     * Gets pool daemon check interval millis.
     *
     * @return the pool daemon check interval millis
     * @since 0.9.1
     */
    public int getPoolDaemonCheckIntervalMillis() {
        return poolDaemonCheckIntervalMillis;
    }

    /**
     * Gets pool idle timeout seconds.
     *
     * @return the pool idle timeout seconds
     * @since 0.9.1
     */
    public int getPoolIdleTimeoutSeconds() {
        return poolIdleTimeoutSeconds;
    }

    /**
     * Gets pool max size.
     *
     * @return the pool max size
     * @since 0.9.1
     */
    public int getPoolMaxSize() {
        return poolMaxSize;
    }

    /**
     * Gets pool min size.
     *
     * @return the pool min size
     * @since 0.9.1
     */
    public int getPoolMinSize() {
        return poolMinSize;
    }

    /**
     * Gets pool shutdown timeout seconds.
     *
     * @return the pool shutdown timeout seconds
     * @since 0.7.2
     */
    public int getPoolShutdownTimeoutSeconds() {
        return poolShutdownTimeoutSeconds;
    }

    /**
     * Gets reset engine timeout seconds.
     *
     * @return the reset engine timeout seconds
     * @since 0.9.1
     */
    public int getResetEngineTimeoutSeconds() {
        return resetEngineTimeoutSeconds;
    }

    /**
     * Is allow eval().
     *
     * @return true : eval() is allowed, false : eval() is disallowed
     * @since 0.9.1
     */
    public boolean isAllowEval() {
        return allowEval;
    }

    /**
     * Is auto send GC notification.
     *
     * @return true : auto send GC notification, false : no auto send GC notification
     * @since 0.9.1
     */
    public boolean isAutoSendGCNotification() {
        return autoSendGCNotification;
    }

    /**
     * Is GC before engine close.
     *
     * @return true : enforce GC before engine close, false : no GC before engine close
     * @since 0.9.1
     */
    public boolean isGCBeforeEngineClose() {
        return gcBeforeEngineClose;
    }

    /**
     * Reset javet engine config.
     *
     * @return the javet engine config
     * @since 0.9.1
     */
    @SuppressWarnings("UnusedReturnValue")
    public JavetEngineConfig reset() {
        javetLogger = DEFAULT_JAVET_LOGGER;
        globalName = null;
        allowEval = false;
        autoSendGCNotification = true;
        defaultEngineGuardTimeoutMillis = DEFAULT_ENGINE_GUARD_TIMEOUT_MILLIS;
        engineGuardCheckIntervalMillis = DEFAULT_ENGINE_GUARD_CHECK_INTERVAL_MILLIS;
        gcBeforeEngineClose = false;
        jsRuntimeType = DEFAULT_JS_RUNTIME_TYPE;
        final int cpuCount = JavetOSUtils.getCPUCount();
        poolMinSize = Math.max(DEFAULT_POOL_MIN_SIZE, cpuCount >> 1);
        poolMaxSize = Math.max(DEFAULT_POOL_MIN_SIZE, cpuCount);
        poolIdleTimeoutSeconds = DEFAULT_POOL_IDLE_TIMEOUT_SECONDS;
        poolShutdownTimeoutSeconds = DEFAULT_POOL_SHUTDOWN_TIMEOUT_SECONDS;
        poolDaemonCheckIntervalMillis = DEFAULT_POOL_DAEMON_CHECK_INTERVAL_MILLIS;
        resetEngineTimeoutSeconds = DEFAULT_RESET_ENGINE_TIMEOUT_SECONDS;
        return this;
    }

    /**
     * Sets allow eval().
     *
     * @param allowEval true : allow eval(), false : disallow eval()
     * @return the self
     * @since 0.9.1
     */
    public JavetEngineConfig setAllowEval(boolean allowEval) {
        this.allowEval = allowEval;
        return this;
    }

    /**
     * Sets auto send GC notification.
     *
     * @param autoSendGCNotification the auto send GC notification
     * @return the self
     * @since 0.9.1
     */
    public JavetEngineConfig setAutoSendGCNotification(boolean autoSendGCNotification) {
        this.autoSendGCNotification = autoSendGCNotification;
        return this;
    }

    /**
     * Sets default engine guard timeout millis.
     *
     * @param defaultEngineGuardTimeoutMillis the default engine guard timeout millis
     * @return the self
     * @since 0.7.2
     */
    public JavetEngineConfig setDefaultEngineGuardTimeoutMillis(int defaultEngineGuardTimeoutMillis) {
        this.defaultEngineGuardTimeoutMillis = defaultEngineGuardTimeoutMillis;
        return this;
    }

    /**
     * Sets engine guard check interval millis.
     *
     * @param engineGuardCheckIntervalMillis the engine guard check interval millis
     * @return the self
     * @since 0.9.1
     */
    @SuppressWarnings("UnusedReturnValue")
    public JavetEngineConfig setEngineGuardCheckIntervalMillis(int engineGuardCheckIntervalMillis) {
        this.engineGuardCheckIntervalMillis = engineGuardCheckIntervalMillis;
        return this;
    }

    /**
     * Sets executor service.
     *
     * @param executorService the executor service
     * @return the self
     * @since 0.9.1
     */
    @SuppressWarnings("UnusedReturnValue")
    JavetEngineConfig setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
        return this;
    }

    /**
     * Sets GC before engine close.
     *
     * @param gcBeforeEngineClose the GC before engine close
     * @return the self
     * @since 0.9.1
     */
    public JavetEngineConfig setGcBeforeEngineClose(boolean gcBeforeEngineClose) {
        this.gcBeforeEngineClose = gcBeforeEngineClose;
        return this;
    }

    /**
     * Sets global name.
     *
     * @param globalName the global name
     * @return the self
     * @since 0.9.1
     */
    public JavetEngineConfig setGlobalName(String globalName) {
        this.globalName = globalName;
        return this;
    }

    /**
     * Sets JS runtime type.
     *
     * @param jsRuntimeType the JS runtime type
     * @return the self
     * @since 0.9.1
     */
    public JavetEngineConfig setJSRuntimeType(JSRuntimeType jsRuntimeType) {
        Objects.requireNonNull(jsRuntimeType);
        this.jsRuntimeType = jsRuntimeType;
        return this;
    }

    /**
     * Sets javet logger.
     *
     * @param javetLogger the javet logger
     * @return the self
     * @since 0.7.0
     */
    public JavetEngineConfig setJavetLogger(IJavetLogger javetLogger) {
        Objects.requireNonNull(javetLogger);
        this.javetLogger = javetLogger;
        return this;
    }

    /**
     * Sets pool daemon check interval millis.
     *
     * @param poolDaemonCheckIntervalMillis the pool daemon check interval millis
     * @return the self
     * @since 0.9.1
     */
    @SuppressWarnings("UnusedReturnValue")
    public JavetEngineConfig setPoolDaemonCheckIntervalMillis(int poolDaemonCheckIntervalMillis) {
        this.poolDaemonCheckIntervalMillis = poolDaemonCheckIntervalMillis;
        return this;
    }

    /**
     * Sets pool idle timeout seconds.
     *
     * @param poolIdleTimeoutSeconds the pool idle timeout seconds
     * @return the self
     * @since 0.9.1
     */
    public JavetEngineConfig setPoolIdleTimeoutSeconds(int poolIdleTimeoutSeconds) {
        this.poolIdleTimeoutSeconds = poolIdleTimeoutSeconds;
        return this;
    }

    /**
     * Sets pool max size.
     *
     * @param poolMaxSize the pool max size
     * @return the self
     * @since 0.9.1
     */
    public JavetEngineConfig setPoolMaxSize(int poolMaxSize) {
        this.poolMaxSize = poolMaxSize;
        return this;
    }

    /**
     * Sets pool min size.
     *
     * @param poolMinSize the pool min size
     * @return the self
     * @since 0.7.0
     */
    public JavetEngineConfig setPoolMinSize(int poolMinSize) {
        this.poolMinSize = poolMinSize;
        return this;
    }

    /**
     * Sets pool shutdown timeout seconds.
     *
     * @param poolShutdownTimeoutSeconds the pool shutdown timeout seconds
     * @return the self
     * @since 0.9.1
     */
    public JavetEngineConfig setPoolShutdownTimeoutSeconds(int poolShutdownTimeoutSeconds) {
        this.poolShutdownTimeoutSeconds = poolShutdownTimeoutSeconds;
        return this;
    }

    /**
     * Sets reset engine timeout seconds.
     *
     * @param resetEngineTimeoutSeconds the reset engine timeout seconds
     * @return the self
     * @since 0.9.1
     */
    public JavetEngineConfig setResetEngineTimeoutSeconds(int resetEngineTimeoutSeconds) {
        this.resetEngineTimeoutSeconds = resetEngineTimeoutSeconds;
        return this;
    }
}
