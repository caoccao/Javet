/*
 *    Copyright 2021. caoccao.com Sam Cao
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

package com.caoccao.javet.interop;

public final class V8ScriptOrigin {

    private String resourceName;
    private int resourceLineOffset;
    private int resourceColumnOffset;
    private int scriptId;
    private boolean wasm;
    private boolean module;

    public V8ScriptOrigin(
            String resourceName,
            int resourceLineOffset,
            int resourceColumnOffset,
            int scriptId,
            boolean wasm,
            boolean module) {
        this.resourceName = resourceName;
        this.resourceLineOffset = resourceLineOffset;
        this.resourceColumnOffset = resourceColumnOffset;
        this.scriptId = scriptId;
        this.wasm = wasm;
        this.module = module;
    }

    public V8ScriptOrigin() {
        this(null);
    }

    public V8ScriptOrigin(
            String resourceName) {
        this(resourceName, 0, 0);
    }

    public V8ScriptOrigin(
            String resourceName,
            int resourceLineOffset,
            int resourceColumnOffset) {
        this(resourceName, resourceLineOffset, resourceColumnOffset,
                0, false, false);
    }

    public String getResourceName() {
        return resourceName;
    }

    public int getResourceLineOffset() {
        return resourceLineOffset;
    }

    public int getResourceColumnOffset() {
        return resourceColumnOffset;
    }

    public int getScriptId() {
        return scriptId;
    }

    public boolean isWasm() {
        return wasm;
    }

    public boolean isModule() {
        return module;
    }
}
