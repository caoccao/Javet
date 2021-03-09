package com.caoccao.javet.tutorial.cdt;

public final class CDTConfig {
    public static final String PATH_ROOT = "/";
    public static final String PATH_JSON = "/json";
    public static final String PATH_JSON_VERSION = "/json/version";
    public static final String PATH_JAVET = "/javet";
    private static int port = 0;
    private static String webSocketUrl = null;

    static {
        setPort(9229);
    }

    public static int getPort() {
        return port;
    }

    public static void setPort(int port) {
        CDTConfig.port = port;
        webSocketUrl = "ws://127.0.0.1:" + Integer.toString(port) + PATH_JAVET;
    }

    public static String getWebSocketUrl() {
        return webSocketUrl;
    }
}
