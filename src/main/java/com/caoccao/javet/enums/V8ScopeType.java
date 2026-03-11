/*
 * Copyright (c) 2021-2026. caoccao.com Sam Cao
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

import java.util.stream.Stream;

/**
 * The enum V8 scope type.
 *
 * @since 2.0.2
 */
public enum V8ScopeType {
    /** The V8 Global scope type. */
    Global(0, "Global"),
    /** The V8 Local scope type. */
    Local(1, "Local"),
    /** The V8 With scope type. */
    With(2, "With"),
    /** The V8 Closure scope type. */
    Closure(3, "Closure"),
    /** The V8 Catch scope type. */
    Catch(4, "Catch"),
    /** The V8 Block scope type. */
    Block(5, "Block"),
    /** The V8 Script scope type. */
    Script(6, "Script"),
    /** The V8 Eval scope type. */
    Eval(7, "Eval"),
    /** The V8 Module scope type. */
    Module(8, "Module");

    /** The total number of scope types. */
    private static final int LENGTH = values().length;
    /** The array of scope types indexed by ID. */
    private static final V8ScopeType[] TYPES = new V8ScopeType[LENGTH];

    static {
        Stream.of(values()).forEach(v -> TYPES[v.getId()] = v);
    }

    /** The type ID. */
    private final int id;
    /** The type name. */
    private final String name;

    V8ScopeType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Parse V8 scope type by ID.
     *
     * @param id the id
     * @return the V8 scope type
     */
    public static V8ScopeType parse(int id) {
        return id >= 0 && id < LENGTH ? TYPES[id] : Global;
    }

    /**
     * Gets the type ID.
     *
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the type name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }
}
