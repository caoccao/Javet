package com.caoccao.javet.tutorial.cdt;

import com.caoccao.javet.interfaces.IJavetLogger;
import com.caoccao.javet.interop.V8Host;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.utils.JavetDefaultLogger;
import com.caoccao.javet.values.V8Value;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.websocket.server.NativeWebSocketServletContainerInitializer;
import org.eclipse.jetty.websocket.server.WebSocketUpgradeFilter;

import java.util.Scanner;

public class CDTShell {
    protected IJavetLogger logger;

    public CDTShell() {
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
                    (servletContext, nativeWebSocketConfiguration) -> {
                        nativeWebSocketConfiguration.getPolicy().setMaxTextMessageBufferSize(0xFFFFFF);
                        nativeWebSocketConfiguration.addMapping(CDTConfig.PATH_JAVET, CDTWebSocketAdapter.class);
                    });
            WebSocketUpgradeFilter.configure(jsonServletContextHandler);
            try (V8Runtime v8Runtime = V8Host.getInstance().createV8Runtime()) {
                jsonServer.start();
                logger.logInfo("Server is started. Please press any key to stop the server.");
                System.out.println("Welcome to CDT Shell!");
                System.out.println("Input the script or '" + CDTConfig.COMMAND_EXIT + "' to exit.");
                CDTConfig.setV8Runtime(v8Runtime);
                try (Scanner scanner = new Scanner(System.in)) {
                    while (true) {
                        System.out.print("> ");
                        String command = scanner.nextLine();
                        if (CDTConfig.COMMAND_EXIT.equals(command)) {
                            break;
                        }
                        try (V8Value v8Value = v8Runtime.getExecutor(command).execute()) {
                            if (v8Value != null) {
                                System.out.println(v8Value.toString());
                            }
                        } catch (Throwable t) {
                            System.err.println(t.getMessage());
                        }
                    }
                }
            } finally {
                CDTConfig.setV8Runtime(null);
            }
        } catch (Throwable t) {
            logger.logError(t, t.getMessage());
        } finally {
            if (jsonServer.isStarted() || jsonServer.isStarting()) {
                logger.logInfo("Server is being stopped.");
                try {
                    jsonServer.stop();
                } catch (Throwable t) {
                    logger.logError(t, t.getMessage());
                }
                logger.logInfo("Server is stopped.");
            }
        }

    }

}
