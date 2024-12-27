/*
 * Copyright (c) 2021-2025. caoccao.com Sam Cao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.caoccao.javet.interop;

/**
 * The type Node native is the pure interface that defines the JNI C++ implementation.
 * It has to be public so that dynamic library loading can work.
 *
 * @since 0.8.0
 */
class NodeNative extends V8Native implements INodeNative {
    NodeNative() {
    }

    @Override
    public native boolean await(long nodeRuntimeHandle, int v8AwaitMode);

    @Override
    public native boolean isStopping(long nodeRuntimeHandle);

    @Override
    public native void setStopping(long nodeRuntimeHandle, boolean stopping);
}
