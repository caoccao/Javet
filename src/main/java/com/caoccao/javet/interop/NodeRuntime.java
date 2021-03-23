package com.caoccao.javet.interop;

/**
 * Node runtime is a thin wrapper over V8 runtime.
 */
public class NodeRuntime extends V8Runtime {
    /**
     * Instantiates a new Node runtime.
     *
     * @param v8Host the V8 host
     * @param handle the handle
     * @param pooled the pooled
     */
    NodeRuntime(V8Host v8Host, long handle, boolean pooled) {
        super(v8Host, handle, pooled, null);
    }

    public void await() {
        NodeNative.await(handle);
    }

    @Override
    public JSRuntimeType getJSRuntimeType() {
        return JSRuntimeType.Node;
    }
}
