package com.caoccao.javet.enums;

public enum JavetPromiseRejectEvent {
    PromiseRejectWithNoHandler(0, "PromiseRejectWithNoHandler"),
    PromiseHandlerAddedAfterReject(1, "PromiseHandlerAddedAfterReject"),
    PromiseResolveAfterResolved(2, "PromiseResolveAfterResolved"),
    PromiseRejectAfterResolved(3, "PromiseRejectAfterResolved");

    private static final JavetPromiseRejectEvent[] EVENTS = new JavetPromiseRejectEvent[]{
            PromiseRejectWithNoHandler,
            PromiseHandlerAddedAfterReject,
            PromiseResolveAfterResolved,
            PromiseRejectAfterResolved};

    private int code;
    private String name;

    JavetPromiseRejectEvent(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static JavetPromiseRejectEvent parse(int event) {
        if (event >= 0 && event < EVENTS.length) {
            return EVENTS[event];
        }
        return null;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
