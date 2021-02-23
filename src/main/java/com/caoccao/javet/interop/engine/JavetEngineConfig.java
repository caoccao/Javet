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

import com.caoccao.javet.interfaces.IJavetLogger;
import com.caoccao.javet.utils.JavetDefaultLogger;
import com.caoccao.javet.utils.JavetOSUtils;

import java.util.Objects;
import java.util.concurrent.ExecutorService;

public final class JavetEngineConfig {
    public static final int DEFAULT_ENGINE_GUARD_TIMEOUT_MILLIS = 30000;
    public static final int DEFAULT_ENGINE_GUARD_CHECK_INTERVAL_MILLIS = 1000;
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
    private int defaultEngineGuardTimeoutMillis;
    private int engineGuardCheckIntervalMillis;
    private int maxEngineUsedCount;
    private int poolMaxSize;
    private int poolMinSize;
    private int poolIdleTimeoutSeconds;
    private int poolShutdownTimeoutSeconds;
    private int poolDaemonCheckIntervalMillis;
    private int resetEngineTimeoutSeconds;
    private ExecutorService executorService;

    public JavetEngineConfig() {
        reset();
    }

    public void reset() {
        javetLogger = DEFAULT_JAVET_LOGGER;
        globalName = DEFAULT_GLOBAL_NAME;
        defaultEngineGuardTimeoutMillis = DEFAULT_ENGINE_GUARD_TIMEOUT_MILLIS;
        engineGuardCheckIntervalMillis = DEFAULT_ENGINE_GUARD_CHECK_INTERVAL_MILLIS;
        maxEngineUsedCount = DEFAULT_MAX_ENGINE_USED_COUNT;
        final int cpuCount = JavetOSUtils.getCPUCount();
        poolMinSize = Math.max(DEFAULT_POOL_MIN_SIZE, cpuCount >> 1);
        poolMaxSize = Math.max(DEFAULT_POOL_MIN_SIZE, cpuCount);
        poolIdleTimeoutSeconds = DEFAULT_POOL_IDLE_TIMEOUT_SECONDS;
        poolShutdownTimeoutSeconds = DEFAULT_POOL_SHUTDOWN_TIMEOUT_SECONDS;
        poolDaemonCheckIntervalMillis = DEFAULT_POOL_DAEMON_CHECK_INTERVAL_MILLIS;
        resetEngineTimeoutSeconds = DEFAULT_RESET_ENGINE_TIMEOUT_SECONDS;
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    public int getPoolShutdownTimeoutSeconds() {
        return poolShutdownTimeoutSeconds;
    }

    public void setPoolShutdownTimeoutSeconds(int poolShutdownTimeoutSeconds) {
        this.poolShutdownTimeoutSeconds = poolShutdownTimeoutSeconds;
    }

    public int getEngineGuardCheckIntervalMillis() {
        return engineGuardCheckIntervalMillis;
    }

    public void setEngineGuardCheckIntervalMillis(int engineGuardCheckIntervalMillis) {
        this.engineGuardCheckIntervalMillis = engineGuardCheckIntervalMillis;
    }

    public String getGlobalName() {
        return globalName;
    }

    public void setGlobalName(String globalName) {
        this.globalName = globalName;
    }

    public int getDefaultEngineGuardTimeoutMillis() {
        return defaultEngineGuardTimeoutMillis;
    }

    public void setDefaultEngineGuardTimeoutMillis(int defaultEngineGuardTimeoutMillis) {
        this.defaultEngineGuardTimeoutMillis = defaultEngineGuardTimeoutMillis;
    }

    public int getResetEngineTimeoutSeconds() {
        return resetEngineTimeoutSeconds;
    }

    public void setResetEngineTimeoutSeconds(int resetEngineTimeoutSeconds) {
        this.resetEngineTimeoutSeconds = resetEngineTimeoutSeconds;
    }

    public int getMaxEngineUsedCount() {
        return maxEngineUsedCount;
    }

    public void setMaxEngineUsedCount(int maxEngineUsedCount) {
        this.maxEngineUsedCount = maxEngineUsedCount;
    }

    public IJavetLogger getJavetLogger() {
        return javetLogger;
    }

    public void setJavetLogger(IJavetLogger javetLogger) {
        Objects.requireNonNull(javetLogger);
        this.javetLogger = javetLogger;
    }

    public int getPoolMaxSize() {
        return poolMaxSize;
    }

    public void setPoolMaxSize(int poolMaxSize) {
        this.poolMaxSize = poolMaxSize;
    }

    public int getPoolMinSize() {
        return poolMinSize;
    }

    public void setPoolMinSize(int poolMinSize) {
        this.poolMinSize = poolMinSize;
    }

    public int getPoolIdleTimeoutSeconds() {
        return poolIdleTimeoutSeconds;
    }

    public void setPoolIdleTimeoutSeconds(int poolIdleTimeoutSeconds) {
        this.poolIdleTimeoutSeconds = poolIdleTimeoutSeconds;
    }

    public int getPoolDaemonCheckIntervalMillis() {
        return poolDaemonCheckIntervalMillis;
    }

    public void setPoolDaemonCheckIntervalMillis(int poolDaemonCheckIntervalMillis) {
        this.poolDaemonCheckIntervalMillis = poolDaemonCheckIntervalMillis;
    }
}
