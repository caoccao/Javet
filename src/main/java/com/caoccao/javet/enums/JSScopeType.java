/*
 * Copyright (c) 2021-2025. caoccao.com Sam Cao
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

import java.util.Objects;
import java.util.stream.Stream;

/**
 * The enum JS scope type.
 *
 * @since 0.8.8
 */
public enum JSScopeType {
    Script(0, "Script"),
    ReplMode(1, "ReplMode"),
    Class(2, "Class"),
    Eval(3, "Eval"),
    Function(4, "Function"),
    Module(5, "Module"),
    Catch(6, "Catch"),
    Block(7, "Block"),
    With(8, "With"),
    ShadowRealm(9, "ShadowRealm"),
    Unknown(10, "Unknown");

    private static final int NODE_TYPE_LENGTH = values().length - 2;
    private static final JSScopeType[] NODE_TYPES = new JSScopeType[NODE_TYPE_LENGTH];
    private static final int V8_TYPE_LENGTH = values().length + 1;
    private static final JSScopeType[] V8_TYPES = new JSScopeType[V8_TYPE_LENGTH];

    static {
        NODE_TYPES[0] = Class;
        NODE_TYPES[1] = Eval;
        NODE_TYPES[2] = Function;
        NODE_TYPES[3] = Module;
        NODE_TYPES[4] = Script;
        NODE_TYPES[5] = Catch;
        NODE_TYPES[6] = Block;
        NODE_TYPES[7] = With;
        NODE_TYPES[8] = Unknown;
        Stream.of(values()).forEach(v -> V8_TYPES[v.getId()] = v);
    }

    private final int id;
    private final String name;

    JSScopeType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static JSScopeType parse(int id, JSRuntimeType jsRuntimeType) {
        if (Objects.requireNonNull(jsRuntimeType).isNode()) {
            return id >= 0 && id < NODE_TYPE_LENGTH ? NODE_TYPES[id] : Unknown;
        }
        return id >= 0 && id < V8_TYPE_LENGTH ? V8_TYPES[id] : Unknown;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isBlock() {
        return this == Block;
    }

    public boolean isCatch() {
        return this == Catch;
    }

    public boolean isClass() {
        return this == Class;
    }

    public boolean isEval() {
        return this == Eval;
    }

    public boolean isFunction() {
        return this == Function;
    }

    public boolean isModule() {
        return this == Module;
    }

    public boolean isScript() {
        return this == Script;
    }

    public boolean isUnknown() {
        return this == Unknown;
    }

    public boolean isWith() {
        return this == With;
    }
}
