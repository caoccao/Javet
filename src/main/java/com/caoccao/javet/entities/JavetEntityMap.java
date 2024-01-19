/*
 * Copyright (c) 2021-2024. caoccao.com Sam Cao
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

package com.caoccao.javet.entities;

import com.caoccao.javet.interfaces.IJavetEntityMap;

import java.util.HashMap;
import java.util.Map;

/**
 * The type Javet entity map is for converting JS map
 * to or from Java map.
 *
 * @since 0.7.2
 */
public class JavetEntityMap extends HashMap<String, Object> implements IJavetEntityMap {
    /**
     * Instantiates a new Javet entity map.
     *
     * @param initialCapacity the initial capacity
     * @param loadFactor      the load factor
     * @since 0.7.2
     */
    public JavetEntityMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    /**
     * Instantiates a new Javet entity map.
     *
     * @param initialCapacity the initial capacity
     * @since 0.7.2
     */
    public JavetEntityMap(int initialCapacity) {
        super(initialCapacity);
    }

    /**
     * Instantiates a new Javet entity map.
     *
     * @since 0.7.2
     */
    public JavetEntityMap() {
        super();
    }

    /**
     * Instantiates a new Javet entity map.
     *
     * @param m the m
     * @since 0.7.2
     */
    public JavetEntityMap(Map<? extends String, ?> m) {
        super(m);
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public Object clone() {
        return new JavetEntityMap(this);
    }
}
