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

import java.util.stream.Stream;

/**
 * The enum JS function type.
 *
 * @since 0.8.8
 */
public enum JSFunctionType {
    /**
     * Native.
     *
     * @since 0.8.8
     */
    Native(0, "Native"),
    /**
     * API.
     *
     * @since 0.8.8
     */
    API(1, "API"),
    /**
     * User defined.
     *
     * @since 0.8.8
     */
    UserDefined(2, "UserDefined"),
    /**
     * Unknown.
     *
     * @since 0.8.8
     */
    Unknown(3, "Unknown");

    private static final int LENGTH = values().length;
    private static final JSFunctionType[] TYPES = new JSFunctionType[LENGTH];

    static {
        Stream.of(values()).forEach(v -> TYPES[v.getId()] = v);
    }

    private final int id;
    private final String name;

    JSFunctionType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Parse JS function type by id.
     *
     * @param id the id
     * @return the JS function type
     * @since 0.8.8
     */
    public static JSFunctionType parse(int id) {
        return id >= 0 && id < LENGTH ? TYPES[id] : Unknown;
    }

    /**
     * Gets id.
     *
     * @return the id
     * @since 0.8.8
     */
    public int getId() {
        return id;
    }

    /**
     * Gets name.
     *
     * @return the name
     * @since 0.8.8
     */
    public String getName() {
        return name;
    }

    /**
     * Is API.
     *
     * @return true : API, false : not API
     * @since 0.8.8
     */
    public boolean isAPI() {
        return this == API;
    }

    /**
     * Is native.
     *
     * @return true : native, false : not native
     * @since 0.8.8
     */
    public boolean isNative() {
        return this == Native;
    }

    /**
     * Is user.
     *
     * @return true : user defined, false : not user defined
     * @since 0.8.8
     */
    public boolean isUserDefined() {
        return this == UserDefined;
    }
}
