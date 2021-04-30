/*
 *    Copyright 2021. caoccao.com Sam Cao
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

package com.caoccao.javet.enums;

public enum JavetPromiseRejectEvent {
    PromiseRejectWithNoHandler(0, "PromiseRejectWithNoHandler"),
    PromiseHandlerAddedAfterReject(1, "PromiseHandlerAddedAfterReject"),
    PromiseResolveAfterResolved(2, "PromiseResolveAfterResolved"),
    PromiseRejectAfterResolved(3, "PromiseRejectAfterResolved");

    private static final JavetPromiseRejectEvent[] EVENTS = new JavetPromiseRejectEvent[]{
            PromiseRejectWithNoHandler,
            PromiseHandlerAddedAfterReject,
            PromiseResolveAfterResolved,
            PromiseRejectAfterResolved};

    private int code;
    private String name;

    JavetPromiseRejectEvent(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static JavetPromiseRejectEvent parse(int event) {
        if (event >= 0 && event < EVENTS.length) {
            return EVENTS[event];
        }
        return null;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
