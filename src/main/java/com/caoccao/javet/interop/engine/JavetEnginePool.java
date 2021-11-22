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
import com.caoccao.javet.interfaces.IV8RuntimeObserver;
import com.caoccao.javet.interop.V8Host;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.options.RuntimeOptions;
import com.caoccao.javet.interop.options.V8RuntimeOptions;
import com.caoccao.javet.utils.JavetDateTimeUtils;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ArrayBlockingQueue;
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
     * The External lock.
     *
     * @since 0.8.10
     */
    protected final Object externalLock;
    /**
     * The Idle engine index list.
     *
     * @since 1.0.5
     */
    protected final ArrayBlockingQueue<Integer> idleEngineIndexList;
    /**
     * The Internal lock.
     *
     * @since 1.0.5
     */
    protected final Object internalLock;
    /**
     * The Released engine index list.
     *
     * @since 1.0.5
     */
    protected final ArrayBlockingQueue<Integer> releasedEngineIndexList;
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
     * The Engines.
     *
     * @since 1.0.5
     */
    protected JavetEngine<R>[] engines;
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
    @SuppressWarnings("unchecked")
    public JavetEnginePool(JavetEngineConfig config) {
        this.config = Objects.requireNonNull(config).freezePoolSize();
        idleEngineIndexList = new ArrayBlockingQueue<>(config.getPoolMaxSize(), true);
        releasedEngineIndexList = new ArrayBlockingQueue<>(config.getPoolMaxSize(), true);
        engines = new JavetEngine[config.getPoolMaxSize()];
        externalLock = new Object();
        internalLock = new Object();
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
        return engines.length - getIdleEngineCount() - getReleasedEngineCount();
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
            Integer index = idleEngineIndexList.poll();
            if (index == null) {
                index = releasedEngineIndexList.poll();
                if (index != null) {
                    engine = createEngine();
                    engine.setIndex(index);
                    engines[index] = engine;
                    break;
                }
            } else {
                engine = Objects.requireNonNull(engines[index], "The idle engine must not be null.");
                break;
            }
            try {
                TimeUnit.MILLISECONDS.sleep(config.getPoolDaemonCheckIntervalMillis());
            } catch (InterruptedException e) {
                logger.logError(e, "Failed to sleep a while to wait for an idle engine.");
            }
        }
        engine.setActive(true);
        @SuppressWarnings("ConstantConditions")
        JavetEngineUsage usage = engine.getUsage();
        usage.increaseUsedCount();
        logger.debug("JavetEnginePool.getEngine() ends.");
        return engine;
    }

    @Override
    public int getIdleEngineCount() {
        return idleEngineIndexList.size();
    }

    @Override
    public int getReleasedEngineCount() {
        return releasedEngineIndexList.size();
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
    public int observe(IV8RuntimeObserver observer) {
        int processedCount = 0;
        if (observer != null) {
            synchronized (internalLock) {
                for (int index = 0; index < engines.length; ++index) {
                    JavetEngine<R> engine = engines[index];
                    if (engine != null) {
                        boolean isContinuable = true;
                        try {
                            isContinuable = observer.observe(engine.v8Runtime);
                        } catch (Throwable t) {
                            IJavetLogger logger = config.getJavetLogger();
                            logger.error(t.getMessage(), t);
                        } finally {
                            ++processedCount;
                        }
                        if (!isContinuable) {
                            break;
                        }
                    }
                }
            }
        }
        return processedCount;
    }

    @Override
    public void releaseEngine(IJavetEngine<R> iJavetEngine) {
        IJavetLogger logger = config.getJavetLogger();
        logger.debug("JavetEnginePool.releaseEngine() begins.");
        JavetEngine<R> engine = (JavetEngine<R>) Objects.requireNonNull(iJavetEngine);
        engine.setActive(false);
        if (config.isAutoSendGCNotification()) {
            engine.sendGCNotification();
        }
        idleEngineIndexList.add(engine.getIndex());
        wakeUpDaemon();
        logger.debug("JavetEnginePool.releaseEngine() ends.");
    }

    @Override
    public void run() {
        IJavetLogger logger = config.getJavetLogger();
        logger.debug("JavetEnginePool.run() begins.");
        while (!quitting) {
            synchronized (internalLock) {
                final int initialIdleEngineCount = idleEngineIndexList.size();
                for (int i = config.getPoolMinSize(); i < initialIdleEngineCount; ++i) {
                    final int immediateIdleEngineCount = idleEngineIndexList.size();
                    Integer index = idleEngineIndexList.poll();
                    if (index == null) {
                        break;
                    }
                    JavetEngine<R> engine = Objects.requireNonNull(engines[index], "The idle engine must not be null.");
                    JavetEngineUsage usage = engine.getUsage();
                    ZonedDateTime expirationZonedDateTime = usage.getLastActiveZonedDatetime()
                            .plus(config.getPoolIdleTimeoutSeconds(), ChronoUnit.SECONDS);
                    if (immediateIdleEngineCount > config.getPoolMaxSize()
                            || expirationZonedDateTime.isBefore(getUTCNow())) {
                        try {
                            engine.close(true);
                        } catch (Throwable t) {
                            logger.logError(t, "Failed to release idle engine.");
                        } finally {
                            engines[index] = null;
                            releasedEngineIndexList.add(index);
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
                                } catch (Throwable t) {
                                    logger.logError(t, "Failed to reset idle engine.");
                                }
                            }
                        }
                        idleEngineIndexList.add(index);
                    }
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
        logger.logDebug(
                "JavetEnginePool daemon is quitting with {0}/{1}/{2} engines.",
                Integer.toString(getActiveEngineCount()),
                Integer.toString(getIdleEngineCount()),
                Integer.toString(engines.length));
        synchronized (internalLock) {
            Set<Integer> idleEngineIndexSet = new TreeSet<>(idleEngineIndexList);
            Set<Integer> releasedEngineIndexSet = new TreeSet<>(releasedEngineIndexList);
            for (int index = 0; index < engines.length; ++index) {
                JavetEngine<R> engine = engines[index];
                if (engine != null) {
                    try {
                        if (engine.isActive()) {
                            try {
                                engine.getV8Runtime().terminateExecution();
                            } catch (Throwable t) {
                                logger.logError(t, "Failed to terminate active engine.");
                            }
                        }
                        engine.close(true);
                    } catch (Throwable t) {
                        logger.logError(t, "Failed to release engine.");
                    } finally {
                        engines[index] = null;
                    }
                }
                if (idleEngineIndexSet.contains(index)) {
                    idleEngineIndexSet.remove(index);
                    idleEngineIndexList.remove(index);
                }
                if (!releasedEngineIndexSet.contains(index)) {
                    releasedEngineIndexSet.add(index);
                    releasedEngineIndexList.add(index);
                }
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
        idleEngineIndexList.clear();
        releasedEngineIndexList.clear();
        for (int i = 0; i < engines.length; ++i) {
            releasedEngineIndexList.add(i);
        }
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

    @Override
    public void wakeUpDaemon() {
        synchronized (externalLock) {
            externalLock.notify();
        }
    }
}
