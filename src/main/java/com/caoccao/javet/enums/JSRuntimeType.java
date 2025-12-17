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

package com.caoccao.javet.enums;

import com.caoccao.javet.interfaces.IJavaFunction;
import com.caoccao.javet.interfaces.IJavaSupplier;
import com.caoccao.javet.interop.options.NodeRuntimeOptions;
import com.caoccao.javet.interop.options.RuntimeOptions;
import com.caoccao.javet.interop.options.V8RuntimeOptions;

import java.util.Objects;

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
    Node(
            "node",
            false,
            "13.6.233.17-node.37", // node -p process.versions.v8
            NodeRuntimeOptions::new,
            o -> o instanceof NodeRuntimeOptions),
    /**
     * Node.js with i18n.
     *
     * @since 4.0.0
     */
    NodeI18n(
            "node",
            true,
            "13.6.233.17-node.37", // node -p process.versions.v8
            NodeRuntimeOptions::new,
            o -> o instanceof NodeRuntimeOptions),
    /**
     * V8.
     *
     * @since 0.8.0
     */
    V8(
            "v8",
            false,
            "14.4.258.13",
            V8RuntimeOptions::new,
            o -> o instanceof V8RuntimeOptions),
    /**
     * V8 with i18n.
     *
     * @since 4.0.0
     */
    V8I18n(
            "v8",
            true,
            "14.4.258.13",
            V8RuntimeOptions::new,
            o -> o instanceof V8RuntimeOptions);

    private final boolean i18nEnabled;
    private final String name;
    private final IJavaSupplier<? extends RuntimeOptions<?>> runtimeOptionsConstructor;
    private final IJavaFunction<RuntimeOptions<?>, Boolean> runtimeOptionsValidator;
    private final String version;

    JSRuntimeType(
            String name,
            boolean i18nEnabled,
            String version,
            IJavaSupplier<RuntimeOptions<?>> runtimeOptionsConstructor,
            IJavaFunction<RuntimeOptions<?>, Boolean> runtimeOptionsValidator) {
        this.runtimeOptionsConstructor = Objects.requireNonNull(runtimeOptionsConstructor);
        this.runtimeOptionsValidator = Objects.requireNonNull(runtimeOptionsValidator);
        this.name = name;
        this.i18nEnabled = i18nEnabled;
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
     * Gets runtime options.
     *
     * @param <Options> the type parameter
     * @return the runtime options
     * @since 1.0.0
     */
    @SuppressWarnings("unchecked")
    public <Options extends RuntimeOptions<?>> Options getRuntimeOptions() {
        return (Options) runtimeOptionsConstructor.get();
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
     * Is i18n enabled.
     *
     * @return true : i18n enabled, false : i18n not enabled
     * @since 4.0.0
     */
    public boolean isI18nEnabled() {
        return i18nEnabled;
    }

    /**
     * Is Node.js mode.
     *
     * @return true : Node.js mode, false : not Node.js mode
     * @since 0.8.0
     */
    public boolean isNode() {
        return this == Node || this == NodeI18n;
    }

    /**
     * Is runtime options valid.
     *
     * @param runtimeOptions the runtime options
     * @return true : valid, false : invalid
     * @since 1.0.0
     */
    public boolean isRuntimeOptionsValid(RuntimeOptions<?> runtimeOptions) {
        return runtimeOptionsValidator.apply(runtimeOptions);
    }

    /**
     * Is V8 mode.
     *
     * @return true : V8 mode, false : not V8 mode
     * @since 0.8.0
     */
    public boolean isV8() {
        return this == V8 || this == V8I18n;
    }

    @Override
    public String toString() {
        return new StringBuilder(name).append(" v").append(version).toString();
    }
}
