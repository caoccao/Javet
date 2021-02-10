/*
 *   Copyright (c) 2021. caoccao.com Sam Cao
 *   All rights reserved.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.caoccao.javet.interop.engine;

import java.time.ZonedDateTime;

public class JavetEngineUsage {
    protected int engineUsedCount;
    protected ZonedDateTime lastActiveZonedDatetime;

    public JavetEngineUsage() {
        reset();
    }

    public ZonedDateTime getLastActiveZonedDatetime() {
        return lastActiveZonedDatetime;
    }

    public void setLastActiveZonedDatetime(ZonedDateTime lastActiveZonedDatetime) {
        this.lastActiveZonedDatetime = lastActiveZonedDatetime;
    }

    public int getEngineUsedCount() {
        return engineUsedCount;
    }

    public void increaseUsedCount() {
        ++engineUsedCount;
    }

    protected void reset() {
        engineUsedCount = 0;
    }
}
