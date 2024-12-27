/*
 * Copyright (c) 2023-2025. caoccao.com Sam Cao
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
 * The enum V8 await mode.
 *
 * @since 2.0.4
 */
public enum V8AwaitMode {
    /**
     * RunNoWait tells Javet to trigger the task queue execution but do not wait.
     * It is a non-blocking call.
     * It only works in Node.js mode.
     *
     * @since 3.1.2
     */
    RunNoWait(2),
    /**
     * RunOnce tells Javet to drain the tasks once and return.
     * It is a blocking call that prevents other threads from calling V8 runtime.
     * It only works in Node.js mode.
     *
     * @since 2.0.4
     */
    RunOnce(1),
    /**
     * RunTillNoMoreTasks tells Javet to keep waiting till there are no more tasks.
     * It is a non-blocking call. It is the default mode.
     * It only works in Node.js mode.
     *
     * @since 2.0.4
     */
    RunTillNoMoreTasks(0);

    private final int id;

    V8AwaitMode(int id) {
        this.id = id;
    }

    /**
     * Gets id.
     *
     * @return the id
     * @since 2.0.4
     */
    public int getId() {
        return id;
    }
}
