/*
 * Copyright (c) 2021-2026. caoccao.com Sam Cao
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

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interfaces.IJavetClosable;
import com.caoccao.javet.interfaces.IJavetLogger;
import com.caoccao.javet.utils.SimpleList;
import com.caoccao.javet.values.V8Value;

import com.caoccao.javet.values.reference.IV8ValueObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents an inspector session connected to a V8 runtime.
 * <p>
 * Each {@code V8Inspector} instance maps to a separate V8 inspector session
 * with its own CDP channel. Multiple sessions can be created on the same
 * {@link V8Runtime}, allowing multiple DevTools clients to connect simultaneously.
 * <p>
 * Use {@link V8Runtime#createV8Inspector(String)} to create sessions.
 * Multiple sessions can be created on the same runtime.
 * Sessions implement {@link IJavetClosable} so they can be explicitly disconnected
 * when no longer needed.
 *
 * @since 0.7.3
 */
public final class V8Inspector implements IJavetClosable {
    private final List<IV8InspectorListener> listeners;
    private final String name;
    private final int sessionId;
    private final IV8Native v8Native;
    private final V8Runtime v8Runtime;
    private boolean closed;
    private IJavetLogger logger;

    V8Inspector(V8Runtime v8Runtime, String name, IV8Native v8Native) {
        this(v8Runtime, name, v8Native, false);
    }

    V8Inspector(V8Runtime v8Runtime, String name, IV8Native v8Native, boolean waitForDebugger) {
        logger = v8Runtime.getLogger();
        this.name = Objects.requireNonNull(name);
        listeners = new ArrayList<>();
        this.v8Runtime = v8Runtime;
        this.v8Native = v8Native;
        this.closed = false;
        this.sessionId = v8Native.createV8Inspector(v8Runtime.getHandle(), this, this.name, waitForDebugger);
    }

    public void addListeners(IV8InspectorListener... listeners) {
        Collections.addAll(this.listeners, Objects.requireNonNull(listeners));
    }

    /**
     * Forces the V8 runtime to break (pause) immediately, as if a breakpoint
     * were hit. This triggers a {@code Debugger.paused} notification with the
     * specified break reason.
     * <p>
     * Unlike {@link #schedulePauseOnNextStatement(String, String)}, this method
     * breaks <em>immediately</em> rather than waiting for the next JavaScript
     * statement to execute.
     * <p>
     * The caller must hold the V8 isolate lock (e.g., call from a thread that
     * is currently executing JavaScript).
     *
     * @param breakReason  a short reason string (e.g., "embedder-requested")
     * @param breakDetails additional JSON details (can be empty)
     * @since 5.0.5
     */
    public void breakProgram(String breakReason, String breakDetails) {
        if (!closed && !v8Runtime.isClosed()) {
            v8Native.v8InspectorBreakProgram(v8Runtime.getHandle(), sessionId,
                    Objects.requireNonNull(breakReason), Objects.requireNonNull(breakDetails));
        }
    }

    /**
     * Closes this inspector session, disconnecting it from the V8 runtime.
     * After closing, calls to {@link #sendRequest(String)} are silently ignored.
     * Other sessions on the same runtime are not affected.
     *
     * @since 5.0.5
     */
    @Override
    public void close() throws JavetException {
        if (!closed) {
            closed = true;
            if (!v8Runtime.isClosed()) {
                v8Native.v8InspectorCloseSession(v8Runtime.getHandle(), sessionId);
            }
        }
    }

    /**
     * Cancels a previously scheduled pause on the next statement.
     *
     * @see #schedulePauseOnNextStatement(String, String)
     * @since 5.0.5
     */
    public void cancelPauseOnNextStatement() {
        if (!closed && !v8Runtime.isClosed()) {
            v8Native.v8InspectorCancelPauseOnNextStatement(v8Runtime.getHandle(), sessionId);
        }
    }

    public void consoleAPIMessage(
            int contextGroupId, int level, String message,
            String url, int lineNumber, int columnNumber) {
        logger.logDebug("Receiving consoleAPIMessage: {0}", message);
        for (IV8InspectorListener listener : listeners) {
            try {
                listener.consoleAPIMessage(contextGroupId, level, message, url, lineNumber, columnNumber);
            } catch (Throwable t) {
                logger.logError(t, t.getMessage());
            }
        }
    }

    /**
     * Evaluates a JavaScript expression directly through the inspector session,
     * bypassing CDP JSON serialization. This returns the raw V8 value, which is
     * faster than using {@code Runtime.evaluate} via {@link #sendRequest(String)}.
     * <p>
     * The caller must hold the V8 isolate lock. The returned value is a Javet
     * V8 value that must be closed when no longer needed (if it is a reference type).
     *
     * @param <T>                   the expected V8 value type
     * @param expression           the JavaScript expression to evaluate
     * @param includeCommandLineAPI whether to include the command-line API scope
     * @return the evaluation result as a V8 value, or {@code null} if the session
     *         is closed, the expression could not be run, or the result is empty
     * @since 5.0.5
     */
    @SuppressWarnings("unchecked")
    public <T extends V8Value> T evaluate(String expression, boolean includeCommandLineAPI) {
        if (!closed && !v8Runtime.isClosed()) {
            return (T) v8Native.v8InspectorEvaluate(v8Runtime.getHandle(), sessionId,
                    Objects.requireNonNull(expression), includeCommandLineAPI);
        }
        return null;
    }

    public void flushProtocolNotifications() {
        logger.logDebug("Receiving flushProtocolNotifications");
        for (IV8InspectorListener listener : listeners) {
            try {
                listener.flushProtocolNotifications();
            } catch (Throwable t) {
                logger.logError(t, t.getMessage());
            }
        }
    }

    public void installAdditionalCommandLineAPI(IV8ValueObject commandLineAPI) {
        logger.logDebug("Receiving installAdditionalCommandLineAPI");
        try (IV8ValueObject api = commandLineAPI) {
            for (IV8InspectorListener listener : listeners) {
                try {
                    listener.installAdditionalCommandLineAPI(api);
                } catch (Throwable t) {
                    logger.logError(t, t.getMessage());
                }
            }
        } catch (Throwable t) {
            logger.logError(t, t.getMessage());
        }
    }

    public IJavetLogger getLogger() {
        return logger;
    }

    public String getName() {
        return name;
    }

    /**
     * Gets the native session ID for this inspector session.
     *
     * @return the session ID
     * @since 5.0.5
     */
    public int getSessionId() {
        return sessionId;
    }

    /**
     * Returns whether this session has been closed.
     *
     * @return true if closed
     * @since 5.0.5
     */
    public boolean isClosed() {
        return closed;
    }

    public void receiveNotification(String message) {
        logger.logDebug("Receiving notification: {0}", message);
        for (IV8InspectorListener listener : listeners) {
            try {
                listener.receiveNotification(message);
            } catch (Throwable t) {
                logger.logError(t, t.getMessage());
            }
        }
    }

    public void receiveResponse(String message) {
        logger.logDebug("Receiving response: {0}", message);
        for (IV8InspectorListener listener : listeners) {
            try {
                listener.receiveResponse(message);
            } catch (Throwable t) {
                logger.logError(t, t.getMessage());
            }
        }
    }

    public void removeListeners(IV8InspectorListener... listeners) {
        this.listeners.removeAll(SimpleList.of(listeners));
    }

    public void runIfWaitingForDebugger(int contextGroupId) {
        logger.logDebug("Receiving runIfWaitingForDebugger(): {0}", Integer.toString(contextGroupId));
        for (IV8InspectorListener listener : listeners) {
            try {
                listener.runIfWaitingForDebugger(contextGroupId);
            } catch (Throwable t) {
                logger.logError(t, t.getMessage());
            }
        }
    }

    /**
     * Schedules a pause (breakpoint) on the next JavaScript statement that
     * executes. The pause happens asynchronously — the next time V8 is about
     * to execute a statement, it will trigger a {@code Debugger.paused}
     * notification with the specified reason.
     * <p>
     * This is useful for programmatic "break on next" functionality without
     * needing to set a breakpoint at a specific source location.
     * <p>
     * Call {@link #cancelPauseOnNextStatement()} to revoke a scheduled pause
     * before it fires.
     *
     * @param breakReason  a short reason string (e.g., "ambiguous")
     * @param breakDetails additional JSON details (can be empty)
     * @since 5.0.5
     */
    public void schedulePauseOnNextStatement(String breakReason, String breakDetails) {
        if (!closed && !v8Runtime.isClosed()) {
            v8Native.v8InspectorSchedulePauseOnNextStatement(v8Runtime.getHandle(), sessionId,
                    Objects.requireNonNull(breakReason), Objects.requireNonNull(breakDetails));
        }
    }

    @SuppressWarnings("RedundantThrows")
    public void sendRequest(String message) throws JavetException {
        if (!closed && !v8Runtime.isClosed()) {
            logger.logDebug("Sending request: {0}", message);
            for (IV8InspectorListener listener : listeners) {
                try {
                    listener.sendRequest(message);
                } catch (Throwable t) {
                    logger.logError(t, t.getMessage());
                }
            }
            v8Native.v8InspectorSend(v8Runtime.getHandle(), sessionId, message);
        }
    }

    public void setLogger(IJavetLogger logger) {
        Objects.requireNonNull(logger);
        this.logger = logger;
    }

    /**
     * Tells V8 to skip all breakpoints (pauses) for this session.
     * This is useful for temporarily disabling debugging without removing
     * breakpoints. Call {@code setSkipAllPauses(false)} to re-enable pausing.
     *
     * @param skip {@code true} to skip all pauses, {@code false} to re-enable
     * @since 5.0.5
     */
    public void setSkipAllPauses(boolean skip) {
        if (!closed && !v8Runtime.isClosed()) {
            v8Native.v8InspectorSetSkipAllPauses(v8Runtime.getHandle(), sessionId, skip);
        }
    }

    /**
     * Blocks the calling thread until all connected sessions have sent
     * {@code Runtime.runIfWaitingForDebugger}. This method must be called
     * from a thread that does <b>not</b> already hold the V8 isolate lock
     * (typically an execution thread, before running any JavaScript).
     * <p>
     * The inspector must have been created with {@code waitForDebugger = true}
     * (via {@link V8Runtime#createV8Inspector(String, boolean)}) for this to work.
     * When called, the method acquires the V8 lock and enters a message-pumping
     * loop, dispatching any incoming CDP messages while waiting. Once V8 invokes
     * the {@code runIfWaitingForDebugger} callback, the loop exits and this
     * method returns, allowing the thread to proceed with script execution.
     *
     * @since 5.0.5
     */
    public void waitForDebugger() {
        if (!closed && !v8Runtime.isClosed()) {
            v8Native.v8InspectorWaitForDebugger(v8Runtime.getHandle());
        }
    }
}
