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

package com.caoccao.javet.interop.engine;

import com.caoccao.javet.enums.JSRuntimeType;
import com.caoccao.javet.interfaces.IJavetLogger;
import com.caoccao.javet.utils.JavetDefaultLogger;
import com.caoccao.javet.utils.JavetOSUtils;

import java.util.Arrays;
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
     * The constant DEFAULT_WAIT_FOR_ENGINE_LOG_INTERVAL_MILLIS.
     *
     * @since 1.0.5
     */
    public static final int DEFAULT_WAIT_FOR_ENGINE_LOG_INTERVAL_MILLIS = 1000;
    /**
     * The constant MAX_POOL_SIZE.
     *
     * @since 1.0.4
     */
    public static final int MAX_POOL_SIZE = 4096;
    /**
     * The constant DEFAULT_WAIT_FOR_ENGINE_MAX_RETRY_COUNT.
     *
     * @since 1.1.6
     */
    public static final int DEFAULT_WAIT_FOR_ENGINE_MAX_RETRY_COUNT = 500;
    /**
     * The constant DEFAULT_WAIT_FOR_ENGINE_SHEEP_INTERVAL_MILLIS.
     *
     * @since 1.0.5
     */
    protected static final int[] DEFAULT_WAIT_FOR_ENGINE_SLEEP_INTERVAL_MILLIS = new int[]{5, 6, 7, 8, 9, 10};
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
    private boolean poolSizeFrozen;
    private int resetEngineTimeoutSeconds;
    private int waitForEngineLogIntervalMillis;
    private int waitForEngineMaxRetryCount;
    private int[] waitForEngineSleepIntervalMillis;

    /**
     * Instantiates a new Javet engine config.
     *
     * @since 0.7.0
     */
    public JavetEngineConfig() {
        setJavetLogger(DEFAULT_JAVET_LOGGER);
        setGlobalName(null);
        setAllowEval(false);
        setAutoSendGCNotification(true);
        setDefaultEngineGuardTimeoutMillis(DEFAULT_ENGINE_GUARD_TIMEOUT_MILLIS);
        setEngineGuardCheckIntervalMillis(DEFAULT_ENGINE_GUARD_CHECK_INTERVAL_MILLIS);
        setGCBeforeEngineClose(false);
        setJSRuntimeType(DEFAULT_JS_RUNTIME_TYPE);
        poolSizeFrozen = false;
        final int cpuCount = JavetOSUtils.getCPUCount();
        setPoolMinSize(Math.max(DEFAULT_POOL_MIN_SIZE, cpuCount >> 1));
        setPoolMaxSize(Math.max(DEFAULT_POOL_MIN_SIZE, cpuCount));
        setPoolIdleTimeoutSeconds(DEFAULT_POOL_IDLE_TIMEOUT_SECONDS);
        setPoolShutdownTimeoutSeconds(DEFAULT_POOL_SHUTDOWN_TIMEOUT_SECONDS);
        setPoolDaemonCheckIntervalMillis(DEFAULT_POOL_DAEMON_CHECK_INTERVAL_MILLIS);
        setResetEngineTimeoutSeconds(DEFAULT_RESET_ENGINE_TIMEOUT_SECONDS);
        setWaitForEngineLogIntervalMillis(DEFAULT_WAIT_FOR_ENGINE_LOG_INTERVAL_MILLIS);
        setWaitForEngineMaxRetryCount(DEFAULT_WAIT_FOR_ENGINE_MAX_RETRY_COUNT);
        setWaitForEngineSleepIntervalMillis(DEFAULT_WAIT_FOR_ENGINE_SLEEP_INTERVAL_MILLIS);
    }

    /**
     * Freeze pool size javet engine config.
     *
     * @return the javet engine config
     */
    public JavetEngineConfig freezePoolSize() {
        if (!poolSizeFrozen) {
            poolSizeFrozen = true;
        }
        return this;
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
     * Gets wait for engine log interval millis.
     *
     * @return the wait for engine log interval millis
     * @since 1.0.5
     */
    public int getWaitForEngineLogIntervalMillis() {
        return waitForEngineLogIntervalMillis;
    }

    /**
     * Gets wait for engine max retry count.
     *
     * @return the wait for engine max retry count
     * @since 1.1.6
     */
    public int getWaitForEngineMaxRetryCount() {
        return waitForEngineMaxRetryCount;
    }

    /**
     * Gets wait for engine sleep interval millis.
     *
     * @return the wait for engine sleep interval millis
     * @since 1.0.5
     */
    public int[] getWaitForEngineSleepIntervalMillis() {
        return waitForEngineSleepIntervalMillis;
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
        assert defaultEngineGuardTimeoutMillis > 0 : "The default engine guard timeout millis must be greater than 0.";
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
        assert engineGuardCheckIntervalMillis > 0 : "The engine guard check interval millis must be greater than 0.";
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
    public JavetEngineConfig setGCBeforeEngineClose(boolean gcBeforeEngineClose) {
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
        this.jsRuntimeType = Objects.requireNonNull(jsRuntimeType);
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
        this.javetLogger = Objects.requireNonNull(javetLogger);
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
        assert poolDaemonCheckIntervalMillis > 0 : "The pool daemon check interval millis must be greater than 0.";
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
        assert poolIdleTimeoutSeconds > 0 : "The pool idle timeout seconds must be greater than 0.";
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
        assert poolMaxSize > 0 : "Pool max size must be greater than 0.";
        assert poolMaxSize <= MAX_POOL_SIZE : "Pool max size must be no greater than " + MAX_POOL_SIZE + ".";
        if (!poolSizeFrozen) {
            this.poolMaxSize = poolMaxSize;
        }
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
        assert poolMinSize > 0 : "Pool min size must be greater than 0.";
        assert poolMinSize <= MAX_POOL_SIZE : "Pool min size must be no greater than " + MAX_POOL_SIZE + ".";
        if (!poolSizeFrozen) {
            this.poolMinSize = poolMinSize;
        }
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
        assert poolShutdownTimeoutSeconds > 0 : "The pool shutdown timeout seconds must be greater than 0.";
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
        assert resetEngineTimeoutSeconds > 0 : "The reset engine timeout seconds must be greater than 0.";
        this.resetEngineTimeoutSeconds = resetEngineTimeoutSeconds;
        return this;
    }

    /**
     * Sets wait for engine log interval millis.
     *
     * @param waitForEngineLogIntervalMillis the wait for engine log interval millis
     * @return the self
     * @since 1.0.5
     */
    public JavetEngineConfig setWaitForEngineLogIntervalMillis(int waitForEngineLogIntervalMillis) {
        this.waitForEngineLogIntervalMillis = waitForEngineLogIntervalMillis;
        return this;
    }

    /**
     * Sets wait for engine max retry count.
     *
     * @param waitForEngineMaxRetryCount the wait for engine max retry count
     * @return the self
     * @since 1.1.6
     */
    public JavetEngineConfig setWaitForEngineMaxRetryCount(int waitForEngineMaxRetryCount) {
        this.waitForEngineMaxRetryCount = waitForEngineMaxRetryCount;
        return this;
    }

    /**
     * Sets wait for engine sleep interval millis.
     *
     * @param waitForEngineSleepIntervalMillis the wait for engine sleep interval millis
     * @return the self
     * @since 1.0.5
     */
    public JavetEngineConfig setWaitForEngineSleepIntervalMillis(int[] waitForEngineSleepIntervalMillis) {
        Objects.requireNonNull(waitForEngineSleepIntervalMillis);
        assert waitForEngineSleepIntervalMillis.length > 0;
        this.waitForEngineSleepIntervalMillis = Arrays.copyOf(
                waitForEngineSleepIntervalMillis,
                waitForEngineSleepIntervalMillis.length);
        return this;
    }
}
