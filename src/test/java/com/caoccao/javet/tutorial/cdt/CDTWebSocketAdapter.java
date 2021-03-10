package com.caoccao.javet.tutorial.cdt;

import com.caoccao.javet.interfaces.IJavetLogger;
import com.caoccao.javet.interop.IV8InspectorListener;
import com.caoccao.javet.utils.JavetDefaultLogger;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;

public class CDTWebSocketAdapter extends WebSocketAdapter implements IV8InspectorListener {
    protected IJavetLogger logger;
    protected Session session;

    public CDTWebSocketAdapter() {
        this.logger = new JavetDefaultLogger(getClass().getName());
        logger.logInfo("CDTWebSocketAdapter()");
        session = null;
    }

    @Override
    public void onWebSocketClose(int statusCode, String reason) {
        logger.logInfo("onWebSocketClose(): {0} {1}", Integer.toString(statusCode), reason);
    }

    @Override
    public void onWebSocketConnect(Session session) {
        logger.logInfo("onWebSocketConnect(): {0}", session.getRemoteAddress().toString());
        this.session = session;
        CDTConfig.getV8Runtime().getV8Inspector().addListeners(this);
    }

    @Override
    public void onWebSocketError(Throwable cause) {
        logger.logError(cause, cause.getMessage());
    }

    @Override
    public void onWebSocketText(String message) {
        logger.logInfo("onWebSocketText(): {0}", message);
        try {
            CDTConfig.getV8Runtime().getV8Inspector().sendRequest(message);
        } catch (Exception e) {
            logger.logError(e, e.getMessage());
        }
    }

    @Override
    public void receiveNotification(String message) {
        logger.logInfo("receiveNotification(): {0}", message);
        try {
            session.getRemote().sendString(message);
        } catch (Exception e) {
            logger.logError(e, e.getMessage());
        }
    }

    @Override
    public void receiveResponse(String message) {
        logger.logInfo("receiveResponse(): {0}", message);
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
