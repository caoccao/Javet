/*
 * Copyright (c) 2024. caoccao.com Sam Cao
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

package com.caoccao.javet.interop.binding;

import com.caoccao.javet.utils.ThreadSafeMap;

/**
 * The type binding context store is for storing the binding context maps used
 * by V8 value object. It leaves a backdoor for applications to tweak
 * the binding contexts freely.
 *
 * @since 3.0.4
 */
public final class BindingContextStore {
    private static final ThreadSafeMap<Class<?>, BindingContext> bindingContextMap = new ThreadSafeMap<>();

    private BindingContextStore() {
    }

    /**
     * Gets map.
     *
     * @return the map
     * @since 3.0.4
     */
    public static ThreadSafeMap<Class<?>, BindingContext> getMap() {
        return bindingContextMap;
    }
}
