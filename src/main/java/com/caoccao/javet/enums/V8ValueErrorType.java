/*
 * Copyright (c) 2024-2026. caoccao.com Sam Cao
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

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * The enum V8 value error type.
 *
 * @since 3.0.4
 */
public enum V8ValueErrorType {
    /** The V8 Error type. */
    Error(0, "Error"),
    /** The V8 RangeError type. */
    RangeError(1, "RangeError"),
    /** The V8 ReferenceError type. */
    ReferenceError(2, "ReferenceError"),
    /** The V8 SyntaxError type. */
    SyntaxError(3, "SyntaxError"),
    /** The V8 TypeError type. */
    TypeError(4, "TypeError"),
    /** The V8 WasmCompileError type. */
    WasmCompileError(5, "CompileError"),
    /** The V8 WasmLinkError type. */
    WasmLinkError(6, "LinkError"),
    /** The V8 WasmRuntimeError type. */
    WasmRuntimeError(7, "RuntimeError"),
    /** The V8 UnknownError type. */
    UnknownError(8, "UnknownError");

    /** The total number of error types. */
    private static final int LENGTH = values().length;
    /** The map of error types indexed by name. */
    private static final Map<String, V8ValueErrorType> NAME_MAP = new HashMap<>();
    /** The array of error types indexed by ID. */
    private static final V8ValueErrorType[] TYPES = new V8ValueErrorType[LENGTH];

    static {
        Stream.of(values()).forEach(v -> {
            TYPES[v.getId()] = v;
            NAME_MAP.put(v.getName(), v);
        });
    }

    /** The type ID. */
    private final int id;
    /** The type name. */
    private final String name;

    V8ValueErrorType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Parse V8 value error type.
     *
     * @param id the id
     * @return the V8 value error type
     * @since 3.0.4
     */
    public static V8ValueErrorType parse(int id) {
        return id >= 0 && id < LENGTH ? TYPES[id] : UnknownError;
    }

    /**
     * Parse V8 value error type.
     *
     * @param name the name
     * @return the V8 value error type
     * @since 3.0.4
     */
    public static V8ValueErrorType parse(String name) {
        return NAME_MAP.getOrDefault(name, V8ValueErrorType.UnknownError);
    }

    /**
     * Gets id.
     *
     * @return the id
     * @since 3.0.4
     */
    public int getId() {
        return id;
    }

    /**
     * Gets name.
     *
     * @return the name
     * @since 3.0.4
     */
    public String getName() {
        return name;
    }
}
