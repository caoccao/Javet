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
 * The enum V8 priority represents the different priorities that an isolate can have.
 *
 * @since 5.0.3
 */
public enum V8Priority {
    /**
     * The isolate does not relate to content that is currently important to the user.
     * Lowest priority.
     *
     * @since 5.0.3
     */
    BestEffort(0, "BestEffort"),
    /**
     * The isolate contributes to content that is visible to the user, like a
     * visible iframe that's not interacted directly with. High priority.
     *
     * @since 5.0.3
     */
    UserVisible(1, "UserVisible"),
    /**
     * The isolate contributes to content that is of the utmost importance to
     * the user, like visible content in the focused window. Highest priority.
     *
     * @since 5.0.3
     */
    UserBlocking(2, "UserBlocking");

    private static final int LENGTH = 3;
    private static final V8Priority[] PRIORITIES = new V8Priority[LENGTH];

    static {
        Stream.of(values()).forEach(v -> PRIORITIES[v.getId()] = v);
    }

    private final int id;
    private final String name;

    V8Priority(int id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Parse priority from id.
     *
     * @param id the id
     * @return the V8 priority
     * @since 5.0.3
     */
    public static V8Priority parse(int id) {
        return id >= 0 && id < LENGTH ? PRIORITIES[id] : BestEffort;
    }

    /**
     * Gets id.
     *
     * @return the id
     * @since 5.0.3
     */
    public int getId() {
        return id;
    }

    /**
     * Gets name.
     *
     * @return the name
     * @since 5.0.3
     */
    public String getName() {
        return name;
    }
}
