/*
 * Copyright (c) 2021-2023. caoccao.com Sam Cao
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

package com.caoccao.javet.interop.options;

/**
 * The type V8 runtime options.
 *
 * @since 1.0.0
 */
public final class V8RuntimeOptions extends RuntimeOptions<V8RuntimeOptions> {
    /**
     * The constant GLOBAL_THIS.
     *
     * @since 1.0.0
     */
    public static final String GLOBAL_THIS = "globalThis";
    /**
     * The constant V8_FLAGS.
     *
     * @since 1.1.7
     */
    public static final V8Flags V8_FLAGS = new V8Flags();
    private String globalName;

    /**
     * Instantiates a new V8 runtime options.
     *
     * @since 1.0.0
     */
    public V8RuntimeOptions() {
        super();
        setGlobalName(null);
    }

    /**
     * Gets global name.
     *
     * @return the global name
     * @since 1.0.0
     */
    public String getGlobalName() {
        return globalName;
    }

    /**
     * Sets global name.
     *
     * @param globalName the global name
     * @return the self
     * @since 1.0.0
     */
    public V8RuntimeOptions setGlobalName(String globalName) {
        this.globalName = globalName == null || globalName.isEmpty() ? null : globalName;
        return this;
    }
}
