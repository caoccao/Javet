package com.caoccao.javet.interop;

public interface IV8InspectorListener {

    void receiveNotification(String message);

    void receiveResponse(String message);

    void sendRequest(String message);
}
