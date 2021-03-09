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
        if (CDTConfig.PATH_JSON.equals(requestURI)) {
            response.getWriter().println("[ {\n" +
                    "  \"description\": \"javet\",\n" +
                    "  \"devtoolsFrontendUrl\": \"devtools://devtools/bundled/js_app.html?experiments=true&v8only=true&" + CDTConfig.getWebSocketUrl() + "\",\n" +
                    "  \"devtoolsFrontendUrlCompat\": \"devtools://devtools/bundled/inspector.html?experiments=true&v8only=true&" + CDTConfig.getWebSocketUrl() + "\",\n" +
                    "  \"id\": \"javet\",\n" +
                    "  \"title\": \"javet\",\n" +
                    "  \"type\": \"javet\",\n" +
                    "  \"url\": \"file://\",\n" +
                    "  \"webSocketDebuggerUrl\": \"" + CDTConfig.getWebSocketUrl() + "\"\n" +
                    "} ]\n");
        } else if (CDTConfig.PATH_JSON_VERSION.equals(requestURI)) {
            response.getWriter().println("{\n" +
                    "  \"Browser\": \"Javet\",\n" +
                    "  \"Protocol-Version\": \"1.1\"\n" +
                    "} \n");
        } else {
            response.getWriter().println("{}");
        }
    }
}
