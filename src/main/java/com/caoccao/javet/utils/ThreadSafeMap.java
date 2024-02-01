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

package com.caoccao.javet.utils;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The type Thread safe map.
 *
 * @since 1.1.7
 */
public final class ThreadSafeMap<TKey, TValue> {
    private Map<TKey, TValue> map;

    private Type type;

    /**
     * Instantiates a new Thread safe map.
     *
     * @since 1.1.7
     */
    public ThreadSafeMap() {
        this(Type.Permanent);
    }

    /**
     * Instantiates a new Thread safe map.
     *
     * @param type the type
     * @since 1.1.7
     */
    public ThreadSafeMap(Type type) {
        setType(type);
    }

    public void clear() {
        map.clear();
    }

    /**
     * Get value.
     *
     * @param key the key
     * @return the value
     */
    public TValue get(TKey key) {
        return map.get(key);
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
     * Put value by key.
     *
     * @param key   the key
     * @param value the value
     * @return the value
     */
    public TValue put(TKey key, TValue value) {
        return map.put(key, value);
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
            if (type == Type.Weak) {
                map = Collections.synchronizedMap(new WeakHashMap<>());
            } else {
                map = new ConcurrentHashMap<>();
            }
        } else if (this.type != type) {
            if (type == Type.Weak) {
                map = Collections.synchronizedMap(new WeakHashMap<>(map));
            } else {
                map = new ConcurrentHashMap<>(map);
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
