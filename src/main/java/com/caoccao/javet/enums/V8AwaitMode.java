/*
 * Copyright (c) 2023-2024. caoccao.com Sam Cao
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
     * Run once tells Javet to drain the tasks once and return.
     * It only works in Node.js mode.
     *
     * @since 2.0.4
     */
    RunOnce(0),
    /**
     * Run till no more tasks tells Javet to keep waiting till there are no more tasks.
     * It only works in Node.js mode.
     *
     * @since 2.0.4
     */
    RunTillNoMoreTasks(1);

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
