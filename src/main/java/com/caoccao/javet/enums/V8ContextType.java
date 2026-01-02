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
    Await(0, "Await"),
    Block(1, "Block"),
    Catch(2, "Catch"),
    DebugEvaluate(3, "DebugEvaluate"),
    Declaration(4, "Declaration"),
    Eval(5, "Eval"),
    Function(6, "Function"),
    Module(7, "Module"),
    Script(8, "Script"),
    With(9, "With");

    private static final int LENGTH = values().length;
    private static final V8ContextType[] TYPES = new V8ContextType[LENGTH];

    static {
        Stream.of(values()).forEach(v -> TYPES[v.getId()] = v);
    }

    private final int id;
    private final String name;

    V8ContextType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static V8ContextType parse(int id) {
        return id >= 0 && id < LENGTH ? TYPES[id] : Await;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
