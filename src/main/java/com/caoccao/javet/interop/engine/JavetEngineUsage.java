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

package com.caoccao.javet.interop.engine;

import java.time.ZonedDateTime;

/**
 * Tracks usage statistics for a Javet engine instance.
 */
public class JavetEngineUsage {
    /**
     * The number of times the engine has been used.
     */
    protected int engineUsedCount;
    /**
     * The last active zoned date time of the engine.
     */
    protected ZonedDateTime lastActiveZonedDatetime;

    /**
     * Instantiates a new Javet engine usage with default values.
     */
    public JavetEngineUsage() {
        reset();
    }

    /**
     * Gets the engine used count.
     *
     * @return the engine used count
     */
    public int getEngineUsedCount() {
        return engineUsedCount;
    }

    /**
     * Gets the last active zoned date time.
     *
     * @return the last active zoned date time
     */
    public ZonedDateTime getLastActiveZonedDatetime() {
        return lastActiveZonedDatetime;
    }

    /**
     * Increases the engine used count by one.
     */
    public void increaseUsedCount() {
        ++engineUsedCount;
    }

    /**
     * Resets the engine usage to its initial state.
     */
    protected void reset() {
        engineUsedCount = 0;
    }

    /**
     * Sets the last active zoned date time.
     *
     * @param lastActiveZonedDatetime the last active zoned date time
     */
    public void setLastActiveZonedDatetime(ZonedDateTime lastActiveZonedDatetime) {
        this.lastActiveZonedDatetime = lastActiveZonedDatetime;
    }
}
