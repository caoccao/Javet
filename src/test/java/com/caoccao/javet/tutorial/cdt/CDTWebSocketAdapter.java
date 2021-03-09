package com.caoccao.javet.tutorial.cdt;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interfaces.IJavetLogger;
import com.caoccao.javet.interop.IV8InspectorListener;
import com.caoccao.javet.interop.V8Host;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.utils.JavetDefaultLogger;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;

public class CDTWebSocketAdapter extends WebSocketAdapter implements IV8InspectorListener {
    protected IJavetLogger logger;
    protected Session session;
    protected V8Runtime v8Runtime;

    public CDTWebSocketAdapter() {
        this.logger = new JavetDefaultLogger(getClass().getName());
        logger.logInfo("CDTWebSocketAdapter()");
        session = null;
        v8Runtime = null;
    }

    @Override
    public void onWebSocketClose(int statusCode, String reason) {
        logger.logInfo("onWebSocketClose(): {0} {1}", Integer.toString(statusCode), reason);
        try {
            v8Runtime.close();
        } catch (JavetException e) {
            logger.logError(e, e.getMessage());
        } finally {
            v8Runtime = null;
        }
    }

    @Override
    public void onWebSocketConnect(Session session) {
        logger.logInfo("onWebSocketConnect(): {0}", session.getRemoteAddress().toString());
        this.session = session;
        try {
            v8Runtime = V8Host.getInstance().createV8Runtime();
            v8Runtime.getV8Inspector().addListeners(this);
        } catch (Throwable t) {
            logger.logError(t, t.getMessage());
        }
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
        } catch (JavetException e) {
            logger.logError(e, e.getMessage());
        }
    }

    @Override
    public void receiveResponse(String message) {
        try {
            session.getRemote().sendString(message);
        } catch (Exception e) {
            logger.logError(e, e.getMessage());
        }
    }

    @Override
    public void sendRequest(String message) {
    }
}
