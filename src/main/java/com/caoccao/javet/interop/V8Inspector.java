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

package com.caoccao.javet.interop;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interfaces.IJavetLogger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public final class V8Inspector {
    private IJavetLogger logger;
    private String name;
    private List<IV8InspectorListener> listeners;
    private V8Runtime v8Runtime;

    V8Inspector(V8Runtime v8Runtime, String name) {
        logger = v8Runtime.getLogger();
        this.name = name;
        listeners = new ArrayList<>();
        this.v8Runtime = v8Runtime;
        V8Native.createV8Inspector(v8Runtime.getHandle(), this);
    }

    public void addListeners(IV8InspectorListener... listeners) {
        Objects.requireNonNull(listeners);
        this.listeners.addAll(Arrays.asList(listeners));
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

    public IJavetLogger getLogger() {
        return logger;
    }

    public void setLogger(IJavetLogger logger) {
        Objects.requireNonNull(logger);
        this.logger = logger;
    }

    public String getName() {
        return name;
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
        Objects.requireNonNull(listeners);
        this.listeners.removeAll(Arrays.asList(listeners));
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

    public void sendRequest(String message) throws JavetException {
        logger.logDebug("Sending request: {0}", message);
        for (IV8InspectorListener listener : listeners) {
            try {
                listener.sendRequest(message);
            } catch (Throwable t) {
                logger.logError(t, t.getMessage());
            }
        }
        V8Native.v8InspectorSend(v8Runtime.getHandle(), message);
    }
}
