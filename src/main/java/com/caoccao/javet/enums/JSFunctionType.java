package com.caoccao.javet.enums;

public enum JSFunctionType {
    Native(0, "Native"),
    API(1, "API"),
    UserDefined(2, "UserDefined"),
    Unknown(3, "Unknown");

    private int id;
    private String name;

    JSFunctionType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static JSFunctionType parse(int id) {
        switch (id) {
            case 0:
                return Native;
            case 1:
                return API;
            case 2:
                return UserDefined;
            default:
                return Unknown;
        }
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isAPI() {
        return this == API;
    }

    public boolean isNative() {
        return this == Native;
    }

    public boolean isUserDefined() {
        return this == UserDefined;
    }
}
