/*
 * Copyright (c) 2022. caoccao.com Sam Cao
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

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The type Binding context store.
 *
 * @since 1.1.7
 */
public final class BindingContextStore {
    private Map<Class<?>, BindingContext> bindingContextMap;

    private Type type;

    /**
     * Instantiates a new Binding context store.
     *
     * @param type the type
     * @since 1.1.7
     */
    public BindingContextStore(Type type) {
        setType(type);
    }

    public void clear() {
        bindingContextMap.clear();
    }

    /**
     * Get binding context.
     *
     * @param classKey the class key
     * @return the binding context
     */
    public BindingContext get(Class<?> classKey) {
        return bindingContextMap.get(classKey);
    }

    /**
     * Gets type.
     *
     * @return the type
     * @since 1.1.7
     */
    public Type getType() {
        return type;
    }

    /**
     * Put binding context.
     *
     * @param classKey       the class key
     * @param bindingContext the binding context
     * @return the binding context
     */
    public BindingContext put(Class<?> classKey, BindingContext bindingContext) {
        return bindingContextMap.put(classKey, bindingContext);
    }

    /**
     * Sets type.
     *
     * @param type the type
     * @since 1.1.7
     */
    public void setType(Type type) {
        Objects.requireNonNull(type);
        if (this.type == null) {
            switch (type) {
                case Weak:
                    bindingContextMap = Collections.synchronizedMap(new WeakHashMap<>());
                    break;
                default:
                    bindingContextMap = new ConcurrentHashMap<>();
                    break;
            }
        } else if (this.type != type) {
            switch (type) {
                case Weak:
                    bindingContextMap = Collections.synchronizedMap(new WeakHashMap<>(bindingContextMap));
                    break;
                default:
                    bindingContextMap = new ConcurrentHashMap<>(bindingContextMap);
                    break;
            }
        }
        this.type = type;
    }

    /**
     * The enum Type.
     *
     * @since 1.1.7
     */
    public enum Type {
        /**
         * Permanent type: Values are store in {@link ConcurrentHashMap} permanently.
         *
         * @since 1.1.7
         */
        Permanent,
        /**
         * Weak type: Values are stored in {@link WeakHashMap}.
         *
         * @since 1.1.7
         */
        Weak,
    }
}
