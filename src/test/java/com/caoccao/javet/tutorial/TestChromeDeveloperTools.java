package com.caoccao.javet.tutorial;

import com.caoccao.javet.interfaces.IJavetLogger;
import com.caoccao.javet.utils.JavetDefaultLogger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

import java.io.IOException;

public class TestChromeDeveloperTools {
    protected IJavetLogger logger;

    public TestChromeDeveloperTools() {
        logger = new JavetDefaultLogger(getClass().getName());
    }

    public static void main(String[] args) {
        TestChromeDeveloperTools testChromeDeveloperTools = new TestChromeDeveloperTools();
        testChromeDeveloperTools.test();
    }

    public void test() {
        Server server = new Server(9229);
        WebSocketHandler webSocketHandler = new WebSocketHandler() {
            @Override
            public void configure(WebSocketServletFactory factory) {
                factory.register(WebsocketHandler.class);
            }
        };
        server.setHandler(webSocketHandler);
        try {
            server.start();
            server.join();
        } catch (Throwable t) {
            t.printStackTrace(System.err);
        }
    }

    @WebSocket
    public static class WebsocketHandler {
        protected IJavetLogger logger;
        protected Session session;

        public WebsocketHandler() {
            this.logger = new JavetDefaultLogger(getClass().getName());
            logger.logInfo("TestJavetWebsocketEndpoint()");
            session = null;
        }

        @OnWebSocketConnect
        public void onWebSocketConnect(Session session) {
            logger.logInfo("onWebSocketConnect(): {0}:{1}",
                    session.getRemoteAddress().getAddress(),
                    Integer.toString(session.getRemoteAddress().getPort()));
            this.session = session;
        }

        @OnWebSocketMessage
        public void onWebSocketMessage(String message) {
            logger.logInfo("onWebSocketMessage(): {0}", message);
            try {
                session.getRemote().sendString("Hello Client");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @OnWebSocketClose
        public void onWebSocketClose(int statusCode, String reason) {
            logger.logInfo("onWebSocketClose(): {0} {1}", statusCode, reason);
        }

        @OnWebSocketError
        public void onWebSocketError(Throwable cause) {
            logger.logError(cause.getMessage());
            cause.printStackTrace(System.err);
        }
    }
}
