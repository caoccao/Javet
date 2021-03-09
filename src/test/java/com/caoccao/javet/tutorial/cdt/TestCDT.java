package com.caoccao.javet.tutorial.cdt;

/*
This is a sample application that demonstrates how Javet works with Chrome Developer Tools (aka. CDT).

Usage:
1. Run this application and the following 2 service endpoints will be open.
    http://localhost:9229/json
    ws://localhost:9229/javet
2. Open URL "chrome://inspect/" in Chrome.
3. Wait a few seconds and click "Javet" to open CDT.
 */
public class TestCDT {
    public static void main(String[] args) {
        CDTWebServer cdtWebServer = new CDTWebServer();
        cdtWebServer.run();
    }
}
