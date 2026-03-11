/*
 * Copyright (c) 2021-2026. caoccao.com Sam Cao
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
 * Represents the origin information of a V8 script, including resource name,
 * line/column offsets, script ID, and flags for module and WebAssembly scripts.
 */
public final class V8ScriptOrigin {

    private boolean module;
    private int resourceColumnOffset;
    private int resourceLineOffset;
    private String resourceName;
    private int scriptId;
    private boolean wasm;

    /**
     * Instantiates a new V8 script origin with all fields specified.
     *
     * @param resourceName         the resource name (e.g. file path or URL)
     * @param resourceLineOffset   the line offset within the resource
     * @param resourceColumnOffset the column offset within the resource
     * @param scriptId             the script ID assigned by V8
     * @param wasm                 whether the script is WebAssembly
     * @param module               whether the script is an ES module
     */
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

    /**
     * Instantiates a new V8 script origin with default values.
     */
    public V8ScriptOrigin() {
        this(null);
    }

    /**
     * Instantiates a new V8 script origin with the given resource name.
     *
     * @param resourceName the resource name (e.g. file path or URL)
     */
    public V8ScriptOrigin(
            String resourceName) {
        this(resourceName, 0, 0);
    }

    /**
     * Instantiates a new V8 script origin with resource name and offsets.
     *
     * @param resourceName         the resource name (e.g. file path or URL)
     * @param resourceLineOffset   the line offset within the resource
     * @param resourceColumnOffset the column offset within the resource
     */
    public V8ScriptOrigin(
            String resourceName,
            int resourceLineOffset,
            int resourceColumnOffset) {
        this(resourceName, resourceLineOffset, resourceColumnOffset,
                -1, false, false);
    }

    /**
     * Instantiates a new V8 script origin with resource name and script ID.
     *
     * @param resourceName the resource name (e.g. file path or URL)
     * @param scriptId     the script ID assigned by V8
     */
    public V8ScriptOrigin(
            String resourceName,
            int scriptId) {
        this(resourceName, 0, 0,
                scriptId, false, false);
    }

    /**
     * Gets the column offset within the resource.
     *
     * @return the resource column offset
     */
    public int getResourceColumnOffset() {
        return resourceColumnOffset;
    }

    /**
     * Gets the line offset within the resource.
     *
     * @return the resource line offset
     */
    public int getResourceLineOffset() {
        return resourceLineOffset;
    }

    /**
     * Gets the resource name (e.g. file path or URL).
     *
     * @return the resource name
     */
    public String getResourceName() {
        return resourceName;
    }

    /**
     * Gets the script ID assigned by V8.
     *
     * @return the script ID
     */
    public int getScriptId() {
        return scriptId;
    }

    /**
     * Returns whether the script is an ES module.
     *
     * @return true if the script is a module
     */
    public boolean isModule() {
        return module;
    }

    /**
     * Returns whether the script is WebAssembly.
     *
     * @return true if the script is WebAssembly
     */
    public boolean isWasm() {
        return wasm;
    }

    /**
     * Sets whether the script is an ES module.
     *
     * @param module true if the script is a module
     * @return this script origin for fluent chaining
     */
    public V8ScriptOrigin setModule(boolean module) {
        this.module = module;
        return this;
    }

    /**
     * Sets the column offset within the resource.
     *
     * @param resourceColumnOffset the resource column offset
     * @return this script origin for fluent chaining
     */
    public V8ScriptOrigin setResourceColumnOffset(int resourceColumnOffset) {
        this.resourceColumnOffset = resourceColumnOffset;
        return this;
    }

    /**
     * Sets the line offset within the resource.
     *
     * @param resourceLineOffset the resource line offset
     * @return this script origin for fluent chaining
     */
    public V8ScriptOrigin setResourceLineOffset(int resourceLineOffset) {
        this.resourceLineOffset = resourceLineOffset;
        return this;
    }

    /**
     * Sets the resource name (e.g. file path or URL).
     *
     * @param resourceName the resource name
     * @return this script origin for fluent chaining
     */
    public V8ScriptOrigin setResourceName(String resourceName) {
        this.resourceName = resourceName;
        return this;
    }

    /**
     * Sets the script ID assigned by V8.
     *
     * @param scriptId the script ID
     * @return this script origin for fluent chaining
     */
    public V8ScriptOrigin setScriptId(int scriptId) {
        this.scriptId = scriptId;
        return this;
    }

    /**
     * Sets whether the script is WebAssembly.
     *
     * @param wasm true if the script is WebAssembly
     * @return this script origin for fluent chaining
     */
    public V8ScriptOrigin setWasm(boolean wasm) {
        this.wasm = wasm;
        return this;
    }
}
