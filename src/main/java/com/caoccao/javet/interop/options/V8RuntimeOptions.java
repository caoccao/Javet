/*
 * Copyright (c) 2021. caoccao.com Sam Cao
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

import java.util.Objects;

/**
 * The type V8 runtime options.
 *
 * @param <Options> the type parameter
 * @since 1.0.0
 */
@SuppressWarnings("unchecked")
public class V8RuntimeOptions<Options extends V8RuntimeOptions<?>> {
    /**
     * The constant GLOBAL_THIS.
     *
     * @since 1.0.0
     */
    public static final String GLOBAL_THIS = "globalThis";
    /**
     * The Global name.
     *
     * @since 1.0.0
     */
    protected String globalName;

    /**
     * Instantiates a new V8 runtime options.
     *
     * @since 1.0.0
     */
    public V8RuntimeOptions() {
        setGlobalName(GLOBAL_THIS);
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
     * @return the global name
     * @since 1.0.0
     */
    public Options setGlobalName(String globalName) {
        this.globalName = Objects.requireNonNull(globalName);
        return (Options) this;
    }
}
