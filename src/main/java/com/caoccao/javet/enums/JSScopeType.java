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
 * The enum JS scope type.
 *
 * @since 0.8.8
 */
public enum JSScopeType {
    /** The script scope type. */
    Script(0, "Script"),
    /** The REPL mode scope type. */
    ReplMode(1, "ReplMode"),
    /** The class scope type. */
    Class(2, "Class"),
    /** The eval scope type. */
    Eval(3, "Eval"),
    /** The function scope type. */
    Function(4, "Function"),
    /** The module scope type. */
    Module(5, "Module"),
    /** The catch scope type. */
    Catch(6, "Catch"),
    /** The block scope type. */
    Block(7, "Block"),
    /** The with scope type. */
    With(8, "With"),
    /** The shadow realm scope type. */
    ShadowRealm(9, "ShadowRealm"),
    /** The unknown scope type. */
    Unknown(10, "Unknown");

    /** The total number of enum values. */
    private static final int LENGTH = values().length;
    /** The cached array of scope types indexed by id. */
    private static final JSScopeType[] TYPES = new JSScopeType[LENGTH];

    static {
        Stream.of(values()).forEach(v -> TYPES[v.getId()] = v);
    }

    /** The numeric id of this scope type. */
    private final int id;
    /** The name of this scope type. */
    private final String name;

    JSScopeType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Parses the scope type from its numeric id.
     *
     * @param id the numeric id
     * @return the corresponding scope type, or {@link #Unknown} if the id is out of range
     */
    public static JSScopeType parse(int id) {
        return id >= 0 && id < LENGTH ? TYPES[id] : Unknown;
    }

    /**
     * Gets the numeric id of this scope type.
     *
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the name of this scope type.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns whether this scope type is {@link #Block}.
     *
     * @return {@code true} if this is a block scope, {@code false} otherwise
     */
    public boolean isBlock() {
        return this == Block;
    }

    /**
     * Returns whether this scope type is {@link #Catch}.
     *
     * @return {@code true} if this is a catch scope, {@code false} otherwise
     */
    public boolean isCatch() {
        return this == Catch;
    }

    /**
     * Returns whether this scope type is {@link #Class}.
     *
     * @return {@code true} if this is a class scope, {@code false} otherwise
     */
    public boolean isClass() {
        return this == Class;
    }

    /**
     * Returns whether this scope type is {@link #Eval}.
     *
     * @return {@code true} if this is an eval scope, {@code false} otherwise
     */
    public boolean isEval() {
        return this == Eval;
    }

    /**
     * Returns whether this scope type is {@link #Function}.
     *
     * @return {@code true} if this is a function scope, {@code false} otherwise
     */
    public boolean isFunction() {
        return this == Function;
    }

    /**
     * Returns whether this scope type is {@link #Module}.
     *
     * @return {@code true} if this is a module scope, {@code false} otherwise
     */
    public boolean isModule() {
        return this == Module;
    }

    /**
     * Returns whether this scope type is {@link #Script}.
     *
     * @return {@code true} if this is a script scope, {@code false} otherwise
     */
    public boolean isScript() {
        return this == Script;
    }

    /**
     * Returns whether this scope type is {@link #Unknown}.
     *
     * @return {@code true} if this is an unknown scope, {@code false} otherwise
     */
    public boolean isUnknown() {
        return this == Unknown;
    }

    /**
     * Returns whether this scope type is {@link #With}.
     *
     * @return {@code true} if this is a with scope, {@code false} otherwise
     */
    public boolean isWith() {
        return this == With;
    }
}
