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

import com.caoccao.javet.interop.proxy.IJavetReflectionObjectFactory;

/**
 * The type Runtime options.
 *
 * @param <Options> the type parameter
 * @since 1.0.0
 */
public abstract class RuntimeOptions<Options extends RuntimeOptions<Options>> {
    /**
     * The reflection object factory.
     *
     * @since 2.0.1
     */
    protected IJavetReflectionObjectFactory reflectionObjectFactory;

    /**
     * Instantiates a new Runtime options.
     *
     * @since 1.0.0
     */
    public RuntimeOptions() {
        reflectionObjectFactory = null;
    }

    /**
     * Gets reflection object factory.
     *
     * @return the reflection object factory
     * @since 2.0.1
     */
    public IJavetReflectionObjectFactory getReflectionObjectFactory() {
        return reflectionObjectFactory;
    }

    /**
     * Sets reflection object factory.
     *
     * @param reflectionObjectFactory the reflection object factory
     * @return the self
     * @since 2.0.1
     */
    public RuntimeOptions<Options> setReflectionObjectFactory(IJavetReflectionObjectFactory reflectionObjectFactory) {
        this.reflectionObjectFactory = reflectionObjectFactory;
        return this;
    }
}
