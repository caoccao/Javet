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

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Host;
import com.caoccao.javet.utils.JavetDateTimeUtils;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class JavetEnginePool implements IJavetEnginePool, Runnable {
    protected JavetEngineConfig config;
    protected LinkedList<JavetEngine> activeEngineList;
    protected Thread daemonThread;
    protected LinkedList<JavetEngine> idleEngineList;
    protected Object externalLock;
    protected Object internalLock;
    protected boolean active;
    protected boolean quitting;

    public JavetEnginePool() {
        this(new JavetEngineConfig());
    }

    public JavetEnginePool(JavetEngineConfig config) {
        Objects.requireNonNull(config);
        this.config = config;
        activeEngineList = new LinkedList<>();
        idleEngineList = new LinkedList<>();
        externalLock = new Object();
        internalLock = new Object();
        active = false;
        quitting = false;
        startDaemon();
    }

    protected JavetEngine createEngine() {
        JavetEngine engine = new JavetEngine(this);
        engine.setV8Runtime(V8Host.getInstance().createV8Runtime(true, config.getGlobalName()));
        return engine;
    }

    @Override
    public void close() throws JavetException {
        stopDaemon();
    }

    @Override
    public int getActiveEngineCount() {
        synchronized (internalLock) {
            return activeEngineList.size();
        }
    }

    @Override
    public JavetEngineConfig getConfig() {
        return config;
    }

    @Override
    public IJavetEngine getEngine() {
        config.getEventListener().getEngineBegin();
        JavetEngine engine = null;
        while (!quitting) {
            synchronized (internalLock) {
                if (idleEngineList.isEmpty()) {
                    if (getActiveEngineCount() < config.getPoolMaxSize()) {
                        engine = createEngine();
                    }
                } else {
                    engine = idleEngineList.pop();
                }
                if (engine != null) {
                    engine.setActive(true);
                    activeEngineList.push(engine);
                    break;
                }
            }
            try {
                TimeUnit.MILLISECONDS.sleep(config.getPoolDaemonCheckIntervalMillis());
            } catch (InterruptedException e) {
                config.getEventListener().error("Failed to sleep a while to wait for an idle engine.", e);
            }
        }
        JavetEngineUsage usage = engine.getUsage();
        usage.increaseUsedCount();
        config.getEventListener().getEngineEnd();
        return engine;
    }

    @Override
    public int getIdleEngineCount() {
        synchronized (internalLock) {
            return idleEngineList.size();
        }
    }

    protected ZonedDateTime getUTCNow() {
        return JavetDateTimeUtils.getUTCNow();
    }

    public boolean isActive() {
        return active;
    }

    @Override
    public void releaseEngine(IJavetEngine engine) {
        config.getEventListener().releaseEngineBegin();
        synchronized (externalLock) {
            externalLock.notify();
        }
        config.getEventListener().releaseEngineEnd();
    }

    @Override
    public void run() {
        config.getEventListener().runDaemonBegin();
        while (!quitting) {
            synchronized (internalLock) {
                if (!activeEngineList.isEmpty()) {
                    final int activeEngineCount = getActiveEngineCount();
                    for (int i = 0; i < activeEngineCount; ++i) {
                        JavetEngine engine = activeEngineList.pop();
                        if (engine.isActive()) {
                            activeEngineList.push(engine);
                        } else {
                            JavetEngineUsage usage = engine.getUsage();
                            ZonedDateTime resetEngineZonedDateTime = usage.getLastActiveZonedDatetime()
                                    .plus(config.getResetEngineTimeoutSeconds(), ChronoUnit.SECONDS);
                            if (true || usage.getEngineUsedCount() >= config.getMaxEngineUsedCount() ||
                                    resetEngineZonedDateTime.isBefore(getUTCNow())) {
                                try {
                                    config.getEventListener().resetEngineBegin();
                                    engine.reset();
                                    config.getEventListener().resetEngineEnd();
                                } catch (Exception e) {
                                    config.getEventListener().error("Failed to reset idle engine.", e);
                                }
                            }
                            idleEngineList.push(engine);
                        }
                    }
                }
                if (!idleEngineList.isEmpty()) {
                    final int idleEngineCount = getIdleEngineCount();
                    for (int i = 0; i < idleEngineCount; ++i) {
                        if (getIdleEngineCount() <= config.getPoolMinSize()) {
                            break;
                        }
                        JavetEngine engine = idleEngineList.pop();
                        JavetEngineUsage usage = engine.getUsage();
                        ZonedDateTime expirationZonedDateTime = usage.getLastActiveZonedDatetime()
                                .plus(config.getPoolIdleTimeoutSeconds(), ChronoUnit.SECONDS);
                        if (expirationZonedDateTime.isBefore(getUTCNow())) {
                            try {
                                engine.close(true);
                            } catch (Throwable t) {
                                config.getEventListener().error("Failed to release idle engine.", t);
                            }
                        } else {
                            idleEngineList.push(engine);
                        }
                    }
                }
            }
            synchronized (externalLock) {
                try {
                    externalLock.wait(config.getPoolDaemonCheckIntervalMillis());
                } catch (InterruptedException e) {
                    config.getEventListener().error(
                            "Failed to sleep a while to wait for next round in Javet engine pool daemon.", e);
                }
            }
        }
        config.getEventListener().runDaemonQuitting();
        synchronized (internalLock) {
            if (!idleEngineList.isEmpty()) {
                final int idleEngineCount = getIdleEngineCount();
                for (int i = 0; i < idleEngineCount; ++i) {
                    JavetEngine engine = idleEngineList.pop();
                    try {
                        engine.close(true);
                    } catch (Throwable t) {
                        config.getEventListener().error(
                                "Failed to release idle engine.", t);
                    }
                }
            }
            while (!activeEngineList.isEmpty()) {
                final int activeEngineCount = getActiveEngineCount();
                for (int i = 0; i < activeEngineCount; ++i) {
                    JavetEngine engine = activeEngineList.pop();
                    try {
                        engine.close(true);
                    } catch (Throwable t) {
                        config.getEventListener().error(
                                "Failed to release active engine.", t);
                    }
                }

            }
        }
        config.getEventListener().runDaemonEnd();
    }

    protected void startDaemon() {
        config.getEventListener().startDaemonBegin();
        activeEngineList.clear();
        idleEngineList.clear();
        quitting = false;
        daemonThread = new Thread(this);
        daemonThread.start();
        active = true;
        config.getEventListener().startDaemonEnd();
    }

    protected void stopDaemon() {
        config.getEventListener().stopDaemonBegin();
        quitting = true;
        try {
            if (daemonThread != null) {
                daemonThread.join();
            }
        } catch (Exception e) {
            config.getEventListener().error(e.getMessage(), e);
        } finally {
            daemonThread = null;
        }
        active = false;
        quitting = false;
        config.getEventListener().stopDaemonEnd();
    }
}
