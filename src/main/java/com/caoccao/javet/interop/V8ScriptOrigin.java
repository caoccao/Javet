/*
 * Copyright (c) 2021-2024. caoccao.com Sam Cao
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

public final class V8ScriptOrigin {

    private boolean module;
    private int resourceColumnOffset;
    private int resourceLineOffset;
    private String resourceName;
    private int scriptId;
    private boolean wasm;

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
                -1, false, false);
    }

    public V8ScriptOrigin(
            String resourceName,
            int scriptId) {
        this(resourceName, 0, 0,
                scriptId, false, false);
    }

    public int getResourceColumnOffset() {
        return resourceColumnOffset;
    }

    public int getResourceLineOffset() {
        return resourceLineOffset;
    }

    public String getResourceName() {
        return resourceName;
    }

    public int getScriptId() {
        return scriptId;
    }

    public boolean isModule() {
        return module;
    }

    public boolean isWasm() {
        return wasm;
    }

    public V8ScriptOrigin setModule(boolean module) {
        this.module = module;
        return this;
    }

    public V8ScriptOrigin setResourceColumnOffset(int resourceColumnOffset) {
        this.resourceColumnOffset = resourceColumnOffset;
        return this;
    }

    public V8ScriptOrigin setResourceLineOffset(int resourceLineOffset) {
        this.resourceLineOffset = resourceLineOffset;
        return this;
    }

    public V8ScriptOrigin setResourceName(String resourceName) {
        this.resourceName = resourceName;
        return this;
    }

    public V8ScriptOrigin setScriptId(int scriptId) {
        this.scriptId = scriptId;
        return this;
    }

    public V8ScriptOrigin setWasm(boolean wasm) {
        this.wasm = wasm;
        return this;
    }
}
