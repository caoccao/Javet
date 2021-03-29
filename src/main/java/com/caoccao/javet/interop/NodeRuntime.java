/*
 *   Copyright (c) 2021. caoccao.com Sam Cao
 *   All rights reserved.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.caoccao.javet.interop;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.node.NodeObjectStore;

/**
 * Node runtime is a thin wrapper over V8 runtime.
 */
public class NodeRuntime extends V8Runtime {
    protected NodeObjectStore nodeObjectStore;

    /**
     * Instantiates a new Node runtime.
     *
     * @param v8Host the V8 host
     * @param handle the handle
     * @param pooled the pooled
     */
    NodeRuntime(V8Host v8Host, long handle, boolean pooled, IV8Native v8Native) {
        super(v8Host, handle, pooled, v8Native, null);
        nodeObjectStore = new NodeObjectStore(this);
    }

    @Override
    public void close(boolean forceClose) throws JavetException {
        if (handle != INVALID_HANDLE && forceClose) {
            nodeObjectStore.close();
        }
        super.close(forceClose);
    }

    @Override
    public JSRuntimeType getJSRuntimeType() {
        return JSRuntimeType.Node;
    }

    public NodeObjectStore getNodeObjectStore() {
        return nodeObjectStore;
    }
}
