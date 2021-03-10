package com.caoccao.javet.interop;

public interface IV8InspectorListener {

    void flushProtocolNotifications();

    void receiveNotification(String message);

    void receiveResponse(String message);

    void runIfWaitingForDebugger(int contextGroupId);

    void sendRequest(String message);
}
