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
        Server inspectorServer = new Server(CDTConfig.getPort());
        ServletContextHandler inspectorServletContextHandler = new ServletContextHandler(
                ServletContextHandler.SESSIONS | ServletContextHandler.NO_SECURITY);
        inspectorServletContextHandler.setContextPath(CDTConfig.PATH_ROOT);
        inspectorServer.setHandler(inspectorServletContextHandler);
        try (V8Runtime v8Runtime = V8Host.getV8Instance().createV8Runtime()) {
            inspectorServletContextHandler.addServlet(CDTHttpServlet.class, CDTConfig.PATH_ROOT);
            NativeWebSocketServletContainerInitializer.configure(inspectorServletContextHandler,
                    (servletContext, nativeWebSocketConfiguration) -> {
                        nativeWebSocketConfiguration.getPolicy().setMaxTextMessageBufferSize(0xFFFFFF);
                        nativeWebSocketConfiguration.addMapping(
                                CDTConfig.PATH_JAVET,
                                new CDTWebSocketCreator(v8Runtime));
                    });
            WebSocketUpgradeFilter.configure(inspectorServletContextHandler);
            inspectorServer.start();
            logger.logInfo("Server is started. Please press any key to stop the server.");
            System.out.println("Welcome to CDT Shell!");
            System.out.println("Input the script or '" + CDTConfig.COMMAND_EXIT + "' to exit.");
            try (Scanner scanner = new Scanner(System.in)) {
                while (true) {
                    System.out.print("> ");
                    String command = scanner.nextLine();
                    if (CDTConfig.COMMAND_EXIT.equals(command)) {
                        break;
                    }
                    try (V8Value v8Value = v8Runtime.getExecutor(command).execute()) {
                        if (v8Value != null) {
                            System.out.println(v8Value);
                        }
                    } catch (Throwable t) {
                        System.err.println(t.getMessage());
                    }
                }
            }
        } catch (Throwable t) {
            logger.logError(t, t.getMessage());
        } finally {
            if (inspectorServer.isStarted() || inspectorServer.isStarting()) {
                logger.logInfo("Server is being stopped.");
                try {
                    inspectorServer.stop();
                } catch (Throwable t) {
                    logger.logError(t, t.getMessage());
                }
                logger.logInfo("Server is stopped.");
            }
        }

    }

}
