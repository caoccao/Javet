/*
 * Copyright (c) 2021-2024. caoccao.com Sam Cao
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

/**
 * The enum Javet promise reject event represents the corresponding event in V8.
 *
 * @since 0.8.3
 */
public enum JavetPromiseRejectEvent {
    /**
     * PromiseRejectWithNoHandler.
     *
     * @since 0.8.3
     */
    PromiseRejectWithNoHandler(0, "PromiseRejectWithNoHandler"),
    /**
     * PromiseHandlerAddedAfterReject.
     *
     * @since 0.8.3
     */
    PromiseHandlerAddedAfterReject(1, "PromiseHandlerAddedAfterReject"),
    /**
     * PromiseResolveAfterResolved.
     *
     * @since 0.8.3
     */
    PromiseResolveAfterResolved(2, "PromiseResolveAfterResolved"),
    /**
     * PromiseRejectAfterResolved.
     *
     * @since 0.8.3
     */
    PromiseRejectAfterResolved(3, "PromiseRejectAfterResolved");

    private static final JavetPromiseRejectEvent[] EVENTS = new JavetPromiseRejectEvent[]{
            PromiseRejectWithNoHandler,
            PromiseHandlerAddedAfterReject,
            PromiseResolveAfterResolved,
            PromiseRejectAfterResolved};

    private final int code;
    private final String name;

    JavetPromiseRejectEvent(int code, String name) {
        this.code = code;
        this.name = name;
    }

    /**
     * Parse javet promise reject event by code.
     *
     * @param code the event code
     * @return the javet promise reject event
     * @since 0.8.3
     */
    public static JavetPromiseRejectEvent parse(int code) {
        if (code >= 0 && code < EVENTS.length) {
            return EVENTS[code];
        }
        return null;
    }

    /**
     * Gets event code.
     *
     * @return the event code
     * @since 0.8.3
     */
    public int getCode() {
        return code;
    }

    /**
     * Gets event name.
     *
     * @return the event name
     * @since 0.8.3
     */
    public String getName() {
        return name;
    }
}
