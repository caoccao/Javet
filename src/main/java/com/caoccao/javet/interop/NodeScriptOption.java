package com.caoccao.javet.interop;

public final class NodeScriptOption {
    public static final int NODE_SCRIPT_MODE_STRING = 0;
    public static final int NODE_SCRIPT_MODE_FILE = 1;
    private int mode;

    public NodeScriptOption() {
        this(NODE_SCRIPT_MODE_STRING);
    }

    public NodeScriptOption(int mode) {
        this.mode = mode;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }
}
