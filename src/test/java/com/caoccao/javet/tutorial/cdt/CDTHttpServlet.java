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
import com.caoccao.javet.utils.JavetDefaultLogger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CDTHttpServlet extends HttpServlet {
    public static final String APPLICATION_JSON_CHARSET_UTF_8 = "application/json; charset=UTF-8";
    protected IJavetLogger logger;

    public CDTHttpServlet() {
        this.logger = new JavetDefaultLogger(getClass().getName());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        response.setContentType(APPLICATION_JSON_CHARSET_UTF_8);
        response.setStatus(HttpServletResponse.SC_OK);
        if (CDTConfig.PATH_JSON.equals(requestURI) || CDTConfig.PATH_JSON_LIST.equals(requestURI)) {
            response.getWriter().println("[ {\n" +
                    "  \"description\": \"javet\",\n" +
                    "  \"devtoolsFrontendUrl\": \"devtools://devtools/bundled/js_app.html?experiments=true&v8only=true&ws=" + CDTConfig.getWebSocketUrl() + "\",\n" +
                    "  \"devtoolsFrontendUrlCompat\": \"devtools://devtools/bundled/inspector.html?experiments=true&v8only=true&ws=" + CDTConfig.getWebSocketUrl() + "\",\n" +
                    "  \"id\": \"javet\",\n" +
                    "  \"title\": \"javet\",\n" +
                    "  \"type\": \"node\",\n" + // Type must be node
                    "  \"url\": \"file://\",\n" +
                    "  \"webSocketDebuggerUrl\": \"ws://" + CDTConfig.getWebSocketUrl() + "\"\n" +
                    "} ]\n");
        } else if (CDTConfig.PATH_JSON_VERSION.equals(requestURI)) {
            response.getWriter().println("{\n" +
                    "  \"Browser\": \"Javet\",\n" +
                    "  \"Protocol-Version\": \"1.3\"\n" +
                    "} \n");
        } else {
            response.getWriter().println("{}");
        }
    }
}
