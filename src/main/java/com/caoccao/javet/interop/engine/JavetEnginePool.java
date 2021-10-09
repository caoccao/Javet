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
import com.caoccao.javet.interop.options.RuntimeOptions;
import com.caoccao.javet.interop.options.V8RuntimeOptions;
import com.caoccao.javet.utils.JavetDateTimeUtils;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * The type Javet engine pool.
 *
 * @param <R> the type parameter
 * @since 0.8.0
 */
public class JavetEnginePool<R extends V8Runtime> implements IJavetEnginePool<R>, Runnable {
    /**
     * The constant JAVET_DAEMON_THREAD_NAME.
     *
     * @since 0.8.10
     */
    protected static final String JAVET_DAEMON_THREAD_NAME = "Javet Daemon";
    /**
     * The Active engine list.
     *
     * @since 0.9.1
     */
    protected final ConcurrentLinkedQueue<JavetEngine<R>> activeEngineList;
    /**
     * The External lock.
     *
     * @since 0.8.10
     */
    protected final Object externalLock;
    /**
     * The Idle engine list.
     *
     * @since 0.9.1
     */
    protected final ConcurrentLinkedQueue<JavetEngine<R>> idleEngineList;
    /**
     * The Active.
     *
     * @since 0.7.0
     */
    protected volatile boolean active;
    /**
     * The Config.
     *
     * @since 0.7.0
     */
    protected JavetEngineConfig config;
    /**
     * The Daemon thread.
     *
     * @since 0.7.0
     */
    protected Thread daemonThread;
    /**
     * The Quitting.
     *
     * @since 0.7.0
     */
    protected volatile boolean quitting;

    /**
     * Instantiates a new Javet engine pool.
     *
     * @since 0.7.0
     */
    public JavetEnginePool() {
        this(new JavetEngineConfig());
    }

    /**
     * Instantiates a new Javet engine pool.
     *
     * @param config the config
     * @since 0.7.0
     */
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

    @Override
    public void close() throws JavetException {
        stopDaemon();
    }

    /**
     * Create engine javet engine.
     *
     * @return the javet engine
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    protected JavetEngine<R> createEngine() throws JavetException {
        V8Host v8Host = V8Host.getInstance(config.getJSRuntimeType());
        RuntimeOptions<?> runtimeOptions = config.getJSRuntimeType().getRuntimeOptions();
        if (runtimeOptions instanceof V8RuntimeOptions) {
            V8RuntimeOptions v8RuntimeOptions = (V8RuntimeOptions) runtimeOptions;
            v8RuntimeOptions.setGlobalName(config.getGlobalName());
        }
        @SuppressWarnings("ConstantConditions")
        R v8Runtime = v8Host.createV8Runtime(true, runtimeOptions);
        v8Runtime.allowEval(config.isAllowEval());
        v8Runtime.setLogger(config.getJavetLogger());
        return new JavetEngine<>(this, v8Runtime);
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
    public IJavetEngine<R> getEngine() throws JavetException {
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
        @SuppressWarnings("ConstantConditions")
        JavetEngineUsage usage = engine.getUsage();
        usage.increaseUsedCount();
        logger.debug("JavetEnginePool.getEngine() ends.");
        return engine;
    }

    @Override
    public int getIdleEngineCount() {
        return idleEngineList.size();
    }

    /**
     * Gets utc now.
     *
     * @return the utc now
     * @since 0.7.0
     */
    protected ZonedDateTime getUTCNow() {
        return JavetDateTimeUtils.getUTCNow();
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public boolean isClosed() {
        return !active;
    }

    @Override
    public boolean isQuitting() {
        return quitting;
    }

    @Override
    public void releaseEngine(IJavetEngine<R> engine) {
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
                    if (config.isAutoSendGCNotification()) {
                        engine.sendGCNotification();
                    }
                    idleEngineList.add(engine);
                }
            }
            final int idleEngineCount = getIdleEngineCount();
            for (int i = config.getPoolMinSize(); i < idleEngineCount; ++i) {
                final int immediateIdleEngineCount = getIdleEngineCount();
                JavetEngine<R> engine = idleEngineList.poll();
                if (engine == null) {
                    break;
                }
                JavetEngineUsage usage = engine.getUsage();
                ZonedDateTime expirationZonedDateTime = usage.getLastActiveZonedDatetime()
                        .plus(config.getPoolIdleTimeoutSeconds(), ChronoUnit.SECONDS);
                if (immediateIdleEngineCount > config.getPoolMaxSize() || expirationZonedDateTime.isBefore(getUTCNow())) {
                    try {
                        engine.close(true);
                    } catch (Throwable t) {
                        logger.logError(t, "Failed to release idle engine.");
                    }
                } else {
                    if (config.getResetEngineTimeoutSeconds() > 0) {
                        ZonedDateTime resetEngineZonedDateTime = usage.getLastActiveZonedDatetime()
                                .plus(config.getResetEngineTimeoutSeconds(), ChronoUnit.SECONDS);
                        if (resetEngineZonedDateTime.isBefore(getUTCNow())) {
                            try {
                                logger.debug("JavetEnginePool reset engine begins.");
                                engine.resetContext();
                                logger.debug("JavetEnginePool reset engine ends.");
                            } catch (Exception e) {
                                logger.logError(e, "Failed to reset idle engine.");
                            }
                        }
                    }
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

    /**
     * Start daemon.
     *
     * @since 0.7.0
     */
    protected void startDaemon() {
        IJavetLogger logger = config.getJavetLogger();
        logger.debug("JavetEnginePool.startDaemon() begins.");
        activeEngineList.clear();
        idleEngineList.clear();
        quitting = false;
        config.setExecutorService(Executors.newCachedThreadPool());
        daemonThread = new Thread(this);
        daemonThread.setDaemon(true);
        daemonThread.setName(JAVET_DAEMON_THREAD_NAME);
        daemonThread.start();
        active = true;
        logger.debug("JavetEnginePool.startDaemon() ends.");
    }

    /**
     * Stop daemon.
     *
     * @since 0.7.0
     */
    protected void stopDaemon() {
        IJavetLogger logger = config.getJavetLogger();
        logger.debug("JavetEnginePool.stopDaemon() begins.");
        quitting = true;
        try {
            config.getExecutorService().shutdown();
            //noinspection ResultOfMethodCallIgnored
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
