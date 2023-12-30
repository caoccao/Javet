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

package com.caoccao.javet.tutorial.cdt;

import com.caoccao.javet.interfaces.IJavetLogger;
import com.caoccao.javet.interop.IV8InspectorListener;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.utils.JavetDefaultLogger;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;

public class CDTWebSocketAdapter extends WebSocketAdapter implements IV8InspectorListener {
    protected IJavetLogger logger;
    protected V8Runtime v8Runtime;

    public CDTWebSocketAdapter(V8Runtime v8Runtime) {
        this.logger = new JavetDefaultLogger(getClass().getName());
        this.v8Runtime = v8Runtime;
        logger.logInfo("CDTWebSocketAdapter()");
    }

    @Override
    public void flushProtocolNotifications() {
    }

    public V8Runtime getV8Runtime() {
        return v8Runtime;
    }

    @Override
    public void onWebSocketClose(int statusCode, String reason) {
        logger.logInfo("onWebSocketClose(): {0} {1}", Integer.toString(statusCode), reason);
        v8Runtime.getV8Inspector().removeListeners(this);
        super.onWebSocketClose(statusCode, reason);
    }

    @Override
    public void onWebSocketConnect(Session session) {
        super.onWebSocketConnect(session);
        logger.logInfo("onWebSocketConnect(): {0}", session.getRemoteAddress().toString());
        v8Runtime.getV8Inspector().addListeners(this);
    }

    @Override
    public void onWebSocketError(Throwable cause) {
        logger.logError(cause, cause.getMessage());
    }

    @Override
    public void onWebSocketText(String message) {
        logger.logInfo("onWebSocketText(): {0}", message);
        try {
            v8Runtime.getV8Inspector().sendRequest(message);
        } catch (Exception e) {
            logger.logError(e, e.getMessage());
        }
    }

    @Override
    public void receiveNotification(String message) {
        logger.logInfo("receiveNotification(): {0}", message);
        try {
            getRemote().sendString(message);
        } catch (Exception e) {
            logger.logError(e, e.getMessage());
        }
    }

    @Override
    public void receiveResponse(String message) {
        logger.logInfo("receiveResponse(): {0}", message);
        try {
            getRemote().sendString(message);
        } catch (Exception e) {
            logger.logError(e, e.getMessage());
        }
    }

    @Override
    public void runIfWaitingForDebugger(int contextGroupId) {
        try {
            v8Runtime.getExecutor(
                    "console.log('Welcome to Javet Debugging Environment!');").executeVoid();
        } catch (Exception e) {
            logger.logError(e, e.getMessage());
        }
    }

    @Override
    public void sendRequest(String message) {
    }
}
