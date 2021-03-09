package com.caoccao.javet.tutorial.cdt;

import com.caoccao.javet.interfaces.IJavetLogger;
import com.caoccao.javet.utils.JavetDefaultLogger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.websocket.server.NativeWebSocketServletContainerInitializer;
import org.eclipse.jetty.websocket.server.WebSocketUpgradeFilter;

import java.util.Scanner;

public class CDTWebServer {
    protected IJavetLogger logger;

    public CDTWebServer() {
        logger = new JavetDefaultLogger(getClass().getName());
    }

    public void run() {
        Server jsonServer = new Server(CDTConfig.getPort());
        ServletContextHandler jsonServletContextHandler = new ServletContextHandler(
                ServletContextHandler.SESSIONS | ServletContextHandler.NO_SECURITY);
        jsonServletContextHandler.setContextPath(CDTConfig.PATH_ROOT);
        jsonServer.setHandler(jsonServletContextHandler);
        try {
            jsonServletContextHandler.addServlet(CDTHttpServlet.class, CDTConfig.PATH_JSON);
            jsonServletContextHandler.addServlet(CDTHttpServlet.class, CDTConfig.PATH_JSON_VERSION);
            NativeWebSocketServletContainerInitializer.configure(jsonServletContextHandler,
                    (servletContext, nativeWebSocketConfiguration) ->
                    {
                        nativeWebSocketConfiguration.getPolicy().setMaxTextMessageBufferSize(0xFFFFFF);
                        nativeWebSocketConfiguration.addMapping(CDTConfig.PATH_JAVET, CDTWebSocketAdapter.class);
                    });
            WebSocketUpgradeFilter.configure(jsonServletContextHandler);
            jsonServer.start();
            logger.logInfo("Server is started. Please press any key to stop the server.");
            try (Scanner scanner = new Scanner(System.in)) {
                scanner.nextLine();
            }
            logger.logInfo("Server is being stopped.");
            jsonServer.stop();
            logger.logInfo("Server is stopped.");
        } catch (Throwable t) {
            logger.logError(t, t.getMessage());
        }
    }

}
