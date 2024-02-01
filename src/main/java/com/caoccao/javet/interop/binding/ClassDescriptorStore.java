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
 * The type class descriptor store is for storing the class descriptor maps used
 * by reflection proxy handlers. It leaves a backdoor for applications to tweak
 * the class descriptors freely.
 *
 * @since 3.0.4
 */
public final class ClassDescriptorStore {
    private static final ThreadSafeMap<Class<?>, ClassDescriptor> classMap = new ThreadSafeMap<>();
    private static final ThreadSafeMap<Class<?>, ClassDescriptor> objectMap = new ThreadSafeMap<>();

    private ClassDescriptorStore() {
    }

    /**
     * Gets class map.
     *
     * @return the class map
     * @since 3.0.4
     */
    public static ThreadSafeMap<Class<?>, ClassDescriptor> getClassMap() {
        return classMap;
    }

    /**
     * Gets object map.
     *
     * @return the object map
     * @since 3.0.4
     */
    public static ThreadSafeMap<Class<?>, ClassDescriptor> getObjectMap() {
        return objectMap;
    }
}
