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

public final class JavetEngineConfig {
    public static final int DEFAULT_ENGINE_GUARD_TIMEOUT_MILLIS = 30000;
    public static final int DEFAULT_ENGINE_GUARD_CHECK_INTERVAL_MILLIS = 1000;
    public static final JSRuntimeType DEFAULT_JS_RUNTIME_TYPE = JSRuntimeType.V8;
    public static final int DEFAULT_MAX_ENGINE_USED_COUNT = 100;
    public static final int DEFAULT_POOL_MIN_SIZE = 1;
    public static final int DEFAULT_POOL_IDLE_TIMEOUT_SECONDS = 60;
    public static final int DEFAULT_POOL_DAEMON_CHECK_INTERVAL_MILLIS = 1000;
    public static final int DEFAULT_RESET_ENGINE_TIMEOUT_SECONDS = 3600;
    public static final String DEFAULT_GLOBAL_NAME = "window";
    public static final int DEFAULT_POOL_SHUTDOWN_TIMEOUT_SECONDS = 5;
    public static IJavetLogger DEFAULT_JAVET_LOGGER = new JavetDefaultLogger(JavetEnginePool.class.getName());
    private IJavetLogger javetLogger;
    private String globalName;
    private boolean allowEval;
    private boolean autoSendGCNotification;
    private int defaultEngineGuardTimeoutMillis;
    private int engineGuardCheckIntervalMillis;
    private boolean gcBeforeEngineClose;
    private JSRuntimeType jsRuntimeType;
    private int maxEngineUsedCount;
    private int poolDaemonCheckIntervalMillis;
    private int poolMaxSize;
    private int poolMinSize;
    private int poolIdleTimeoutSeconds;
    private int poolShutdownTimeoutSeconds;
    private int resetEngineTimeoutSeconds;
    private ExecutorService executorService;

    public JavetEngineConfig() {
        reset();
    }

    @SuppressWarnings("UnusedReturnValue")
    public JavetEngineConfig reset() {
        javetLogger = DEFAULT_JAVET_LOGGER;
        globalName = DEFAULT_GLOBAL_NAME;
        allowEval = false;
        autoSendGCNotification = true;
        defaultEngineGuardTimeoutMillis = DEFAULT_ENGINE_GUARD_TIMEOUT_MILLIS;
        engineGuardCheckIntervalMillis = DEFAULT_ENGINE_GUARD_CHECK_INTERVAL_MILLIS;
        gcBeforeEngineClose = false;
        jsRuntimeType = DEFAULT_JS_RUNTIME_TYPE;
        maxEngineUsedCount = DEFAULT_MAX_ENGINE_USED_COUNT;
        final int cpuCount = JavetOSUtils.getCPUCount();
        poolMinSize = Math.max(DEFAULT_POOL_MIN_SIZE, cpuCount >> 1);
        poolMaxSize = Math.max(DEFAULT_POOL_MIN_SIZE, cpuCount);
        poolIdleTimeoutSeconds = DEFAULT_POOL_IDLE_TIMEOUT_SECONDS;
        poolShutdownTimeoutSeconds = DEFAULT_POOL_SHUTDOWN_TIMEOUT_SECONDS;
        poolDaemonCheckIntervalMillis = DEFAULT_POOL_DAEMON_CHECK_INTERVAL_MILLIS;
        resetEngineTimeoutSeconds = DEFAULT_RESET_ENGINE_TIMEOUT_SECONDS;
        return this;
    }

    public boolean isGcBeforeEngineClose() {
        return gcBeforeEngineClose;
    }

    public JavetEngineConfig setGcBeforeEngineClose(boolean gcBeforeEngineClose) {
        this.gcBeforeEngineClose = gcBeforeEngineClose;
        return this;
    }

    public boolean isAllowEval() {
        return allowEval;
    }

    public JavetEngineConfig setAllowEval(boolean allowEval) {
        this.allowEval = allowEval;
        return this;
    }

    public boolean isAutoSendGCNotification() {
        return autoSendGCNotification;
    }

    public JavetEngineConfig setAutoSendGCNotification(boolean autoSendGCNotification) {
        this.autoSendGCNotification = autoSendGCNotification;
        return this;
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    @SuppressWarnings("UnusedReturnValue")
    JavetEngineConfig setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
        return this;
    }

    public JSRuntimeType getJSRuntimeType() {
        return jsRuntimeType;
    }

    public JavetEngineConfig setJSRuntimeType(JSRuntimeType jsRuntimeType) {
        Objects.requireNonNull(jsRuntimeType);
        this.jsRuntimeType = jsRuntimeType;
        return this;
    }

    public int getPoolShutdownTimeoutSeconds() {
        return poolShutdownTimeoutSeconds;
    }

    public JavetEngineConfig setPoolShutdownTimeoutSeconds(int poolShutdownTimeoutSeconds) {
        this.poolShutdownTimeoutSeconds = poolShutdownTimeoutSeconds;
        return this;
    }

    public int getEngineGuardCheckIntervalMillis() {
        return engineGuardCheckIntervalMillis;
    }

    @SuppressWarnings("UnusedReturnValue")
    public JavetEngineConfig setEngineGuardCheckIntervalMillis(int engineGuardCheckIntervalMillis) {
        this.engineGuardCheckIntervalMillis = engineGuardCheckIntervalMillis;
        return this;
    }

    public String getGlobalName() {
        return globalName;
    }

    public JavetEngineConfig setGlobalName(String globalName) {
        this.globalName = globalName;
        return this;
    }

    public int getDefaultEngineGuardTimeoutMillis() {
        return defaultEngineGuardTimeoutMillis;
    }

    public JavetEngineConfig setDefaultEngineGuardTimeoutMillis(int defaultEngineGuardTimeoutMillis) {
        this.defaultEngineGuardTimeoutMillis = defaultEngineGuardTimeoutMillis;
        return this;
    }

    public int getResetEngineTimeoutSeconds() {
        return resetEngineTimeoutSeconds;
    }

    public JavetEngineConfig setResetEngineTimeoutSeconds(int resetEngineTimeoutSeconds) {
        this.resetEngineTimeoutSeconds = resetEngineTimeoutSeconds;
        return this;
    }

    public int getMaxEngineUsedCount() {
        return maxEngineUsedCount;
    }

    public JavetEngineConfig setMaxEngineUsedCount(int maxEngineUsedCount) {
        this.maxEngineUsedCount = maxEngineUsedCount;
        return this;
    }

    public IJavetLogger getJavetLogger() {
        return javetLogger;
    }

    public JavetEngineConfig setJavetLogger(IJavetLogger javetLogger) {
        Objects.requireNonNull(javetLogger);
        this.javetLogger = javetLogger;
        return this;
    }

    public int getPoolMaxSize() {
        return poolMaxSize;
    }

    public JavetEngineConfig setPoolMaxSize(int poolMaxSize) {
        this.poolMaxSize = poolMaxSize;
        return this;
    }

    public int getPoolMinSize() {
        return poolMinSize;
    }

    public JavetEngineConfig setPoolMinSize(int poolMinSize) {
        this.poolMinSize = poolMinSize;
        return this;
    }

    public int getPoolIdleTimeoutSeconds() {
        return poolIdleTimeoutSeconds;
    }

    public JavetEngineConfig setPoolIdleTimeoutSeconds(int poolIdleTimeoutSeconds) {
        this.poolIdleTimeoutSeconds = poolIdleTimeoutSeconds;
        return this;
    }

    public int getPoolDaemonCheckIntervalMillis() {
        return poolDaemonCheckIntervalMillis;
    }

    @SuppressWarnings("UnusedReturnValue")
    public JavetEngineConfig setPoolDaemonCheckIntervalMillis(int poolDaemonCheckIntervalMillis) {
        this.poolDaemonCheckIntervalMillis = poolDaemonCheckIntervalMillis;
        return this;
    }
}
