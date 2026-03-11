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
 * The enum V8 context type.
 *
 * @since 2.0.1
 */
public enum V8ContextType {
    /** The V8 Await context type. */
    Await(0, "Await"),
    /** The V8 Block context type. */
    Block(1, "Block"),
    /** The V8 Catch context type. */
    Catch(2, "Catch"),
    /** The V8 DebugEvaluate context type. */
    DebugEvaluate(3, "DebugEvaluate"),
    /** The V8 Declaration context type. */
    Declaration(4, "Declaration"),
    /** The V8 Eval context type. */
    Eval(5, "Eval"),
    /** The V8 Function context type. */
    Function(6, "Function"),
    /** The V8 Module context type. */
    Module(7, "Module"),
    /** The V8 Script context type. */
    Script(8, "Script"),
    /** The V8 With context type. */
    With(9, "With");

    /** The total number of context types. */
    private static final int LENGTH = values().length;
    /** The array of context types indexed by ID. */
    private static final V8ContextType[] TYPES = new V8ContextType[LENGTH];

    static {
        Stream.of(values()).forEach(v -> TYPES[v.getId()] = v);
    }

    /** The type ID. */
    private final int id;
    /** The type name. */
    private final String name;

    V8ContextType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Parse V8 context type by ID.
     *
     * @param id the id
     * @return the V8 context type
     */
    public static V8ContextType parse(int id) {
        return id >= 0 && id < LENGTH ? TYPES[id] : Await;
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
