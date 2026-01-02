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
    Error(0, "Error"),
    RangeError(1, "RangeError"),
    ReferenceError(2, "ReferenceError"),
    SyntaxError(3, "SyntaxError"),
    TypeError(4, "TypeError"),
    WasmCompileError(5, "CompileError"),
    WasmLinkError(6, "LinkError"),
    WasmRuntimeError(7, "RuntimeError"),
    UnknownError(8, "UnknownError");

    private static final int LENGTH = values().length;
    private static final Map<String, V8ValueErrorType> NAME_MAP = new HashMap<>();
    private static final V8ValueErrorType[] TYPES = new V8ValueErrorType[LENGTH];

    static {
        Stream.of(values()).forEach(v -> {
            TYPES[v.getId()] = v;
            NAME_MAP.put(v.getName(), v);
        });
    }

    private final int id;
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
