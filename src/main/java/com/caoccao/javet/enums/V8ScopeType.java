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
    Global(0, "Global"),
    Local(1, "Local"),
    With(2, "With"),
    Closure(3, "Closure"),
    Catch(4, "Catch"),
    Block(5, "Block"),
    Script(6, "Script"),
    Eval(7, "Eval"),
    Module(8, "Module");

    private static final int LENGTH = values().length;
    private static final V8ScopeType[] TYPES = new V8ScopeType[LENGTH];

    static {
        Stream.of(values()).forEach(v -> TYPES[v.getId()] = v);
    }

    private final int id;
    private final String name;

    V8ScopeType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static V8ScopeType parse(int id) {
        return id >= 0 && id < LENGTH ? TYPES[id] : Global;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
