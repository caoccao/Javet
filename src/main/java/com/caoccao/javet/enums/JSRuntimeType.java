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

package com.caoccao.javet.enums;

/**
 * The enum JS runtime type.
 *
 * @since 0.8.0
 */
public enum JSRuntimeType {
    /**
     * Node.js.
     *
     * @since 0.8.0
     */
    Node("node", "9.3.345.19-node.14"),
    /**
     * V8.
     *
     * @since 0.8.0
     */
    V8("v8", "9.4.146.16");

    private final String name;
    private final String version;

    JSRuntimeType(String name, String version) {
        this.name = name;
        this.version = version;
    }

    /**
     * Gets name.
     *
     * @return the name
     * @since 0.8.0
     */
    public String getName() {
        return name;
    }

    /**
     * Gets version.
     *
     * @return the version
     * @since 0.8.0
     */
    public String getVersion() {
        return version;
    }

    /**
     * Is Node.js.
     *
     * @return the boolean
     * @since 0.8.0
     */
    public boolean isNode() {
        return this == Node;
    }

    /**
     * Is V8.
     *
     * @return the boolean
     * @since 0.8.0
     */
    public boolean isV8() {
        return this == V8;
    }
}
