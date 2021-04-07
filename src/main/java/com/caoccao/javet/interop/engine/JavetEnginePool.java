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
import com.caoccao.javet.interfaces.IJavetLogger;
import com.caoccao.javet.interop.V8Host;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.utils.JavetDateTimeUtils;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("unchecked")
public class JavetEnginePool<R extends V8Runtime> implements IJavetEnginePool<R>, Runnable {
    protected JavetEngineConfig config;
    protected ConcurrentLinkedQueue<JavetEngine<R>> activeEngineList;
    protected Thread daemonThread;
    protected ConcurrentLinkedQueue<JavetEngine<R>> idleEngineList;
    protected Object externalLock;
    protected boolean active;
    protected boolean quitting;

    public JavetEnginePool() {
        this(new JavetEngineConfig());
    }

    public JavetEnginePool(JavetEngineConfig config) {
        Objects.requireNonNull(config);
        this.config = config;
        activeEngineList = new ConcurrentLinkedQueue<>();
        idleEngineList = new ConcurrentLinkedQueue<>();
        externalLock = new Object();
        active = false;
        quitting = false;
        startDaemon();
    }

    protected JavetEngine<R> createEngine() {
        V8Host v8Host = config.getJSRuntimeType().isNode() ? V8Host.getNodeInstance() : V8Host.getV8Instance();
        R v8Runtime = v8Host.createV8Runtime(true, config.getGlobalName());
        v8Runtime.allowEval(config.isAllowEval());
        v8Runtime.setLogger(config.getJavetLogger());
        return new JavetEngine<>(this, v8Runtime);
    }

    @Override
    public void close() throws JavetException {
        stopDaemon();
    }

    @Override
    public int getActiveEngineCount() {
        return activeEngineList.size();
    }

    @Override
    public JavetEngineConfig getConfig() {
        return config;
    }

    @Override
    public IJavetEngine<R> getEngine() {
        IJavetLogger logger = config.getJavetLogger();
        logger.debug("JavetEnginePool.getEngine() begins.");
        JavetEngine<R> engine = null;
        while (!quitting) {
            engine = idleEngineList.poll();
            if (engine == null && getActiveEngineCount() < config.getPoolMaxSize()) {
                engine = createEngine();
            }
            if (engine != null) {
                engine.setActive(true);
                activeEngineList.add(engine);
                break;
            }
            try {
                TimeUnit.MILLISECONDS.sleep(config.getPoolDaemonCheckIntervalMillis());
            } catch (InterruptedException e) {
                logger.logError(e, "Failed to sleep a while to wait for an idle engine.");
            }
        }
        JavetEngineUsage usage = engine.getUsage();
        usage.increaseUsedCount();
        logger.debug("JavetEnginePool.getEngine() ends.");
        return engine;
    }

    @Override
    public int getIdleEngineCount() {
        return idleEngineList.size();
    }

    protected ZonedDateTime getUTCNow() {
        return JavetDateTimeUtils.getUTCNow();
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public boolean isQuitting() {
        return quitting;
    }

    @Override
    public void releaseEngine(IJavetEngine engine) {
        IJavetLogger logger = config.getJavetLogger();
        logger.debug("JavetEnginePool.releaseEngine() begins.");
        synchronized (externalLock) {
            externalLock.notify();
        }
        logger.debug("JavetEnginePool.releaseEngine() ends.");
    }

    @Override
    public void run() {
        IJavetLogger logger = config.getJavetLogger();
        logger.debug("JavetEnginePool.run() begins.");
        while (!quitting) {
            final int activeEngineCount = getActiveEngineCount();
            for (int i = 0; i < activeEngineCount; ++i) {
                JavetEngine<R> engine = activeEngineList.poll();
                if (engine == null) {
                    break;
                }
                if (engine.isActive()) {
                    activeEngineList.add(engine);
                } else {
                    boolean gcRequired = true;
                    if (config.getMaxEngineUsedCount() > 0) {
                        JavetEngineUsage usage = engine.getUsage();
                        ZonedDateTime resetEngineZonedDateTime = usage.getLastActiveZonedDatetime()
                                .plus(config.getResetEngineTimeoutSeconds(), ChronoUnit.SECONDS);
                        if (usage.getEngineUsedCount() >= config.getMaxEngineUsedCount() ||
                                resetEngineZonedDateTime.isBefore(getUTCNow())) {
                            try {
                                logger.debug("JavetEnginePool reset engine begins.");
                                engine.resetContext();
                                gcRequired = false;
                                logger.debug("JavetEnginePool reset engine ends.");
                            } catch (Exception e) {
                                logger.logError(e, "Failed to reset idle engine.");
                            }
                        }
                    }
                    if (gcRequired) {
                        engine.gc();
                    }
                    idleEngineList.add(engine);
                }
            }
            final int idleEngineCount = getIdleEngineCount();
            for (int i = config.getPoolMinSize(); i < idleEngineCount; ++i) {
                JavetEngine<R> engine = idleEngineList.poll();
                if (engine == null) {
                    break;
                }
                JavetEngineUsage usage = engine.getUsage();
                ZonedDateTime expirationZonedDateTime = usage.getLastActiveZonedDatetime()
                        .plus(config.getPoolIdleTimeoutSeconds(), ChronoUnit.SECONDS);
                if (getIdleEngineCount() > config.getPoolMaxSize() || expirationZonedDateTime.isBefore(getUTCNow())) {
                    try {
                        engine.close(true);
                    } catch (Throwable t) {
                        logger.logError(t, "Failed to release idle engine.");
                    }
                } else {
                    idleEngineList.add(engine);
                }
            }
            synchronized (externalLock) {
                try {
                    externalLock.wait(config.getPoolDaemonCheckIntervalMillis());
                } catch (InterruptedException e) {
                    logger.logError(e,
                            "Failed to sleep a while to wait for next round in Javet engine pool daemon.");
                }
            }
        }
        logger.debug("JavetEnginePool daemon is quitting.");
        while (true) {
            JavetEngine<R> engine = idleEngineList.poll();
            if (engine == null) {
                break;
            }
            try {
                engine.close(true);
            } catch (Throwable t) {
                logger.logError(t, "Failed to release idle engine.");
            }
        }
        while (true) {
            JavetEngine<R> engine = activeEngineList.poll();
            if (engine == null) {
                break;
            }
            try {
                engine.close(true);
            } catch (Throwable t) {
                logger.logError(t, "Failed to release active engine.");
            }
        }
        logger.debug("JavetEnginePool.run() ends.");
    }

    protected void startDaemon() {
        IJavetLogger logger = config.getJavetLogger();
        logger.debug("JavetEnginePool.startDaemon() begins.");
        activeEngineList.clear();
        idleEngineList.clear();
        quitting = false;
        config.setExecutorService(Executors.newCachedThreadPool());
        daemonThread = new Thread(this);
        daemonThread.start();
        active = true;
        logger.debug("JavetEnginePool.startDaemon() ends.");
    }

    protected void stopDaemon() {
        IJavetLogger logger = config.getJavetLogger();
        logger.debug("JavetEnginePool.stopDaemon() begins.");
        quitting = true;
        try {
            config.getExecutorService().shutdown();
            config.getExecutorService().awaitTermination(config.getPoolShutdownTimeoutSeconds(), TimeUnit.SECONDS);
        } catch (Exception e) {
            logger.logError(e, e.getMessage());
        } finally {
            config.setExecutorService(null);
        }
        try {
            if (daemonThread != null) {
                daemonThread.join();
            }
        } catch (Exception e) {
            logger.logError(e, e.getMessage());
        } finally {
            daemonThread = null;
        }
        active = false;
        quitting = false;
        logger.debug("JavetEnginePool.stopDaemon() ends.");
    }
}
