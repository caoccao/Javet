package com.caoccao.javet.interop;

public enum JSRuntimeType {
    Node("node", "8.4.371.19-node.18"),
    V8("v8", "8.9.255");

    private String name;
    private String version;

    JSRuntimeType(String name, String version) {
        this.name = name;
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public boolean isNode() {
        return this == Node;
    }

    public boolean isV8() {
        return this == V8;
    }
}
