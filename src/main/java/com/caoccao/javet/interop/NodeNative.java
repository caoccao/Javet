package com.caoccao.javet.interop;

final class NodeNative {
    private NodeNative() {
    }

    native static void await(long v8RuntimeHandle);
}
