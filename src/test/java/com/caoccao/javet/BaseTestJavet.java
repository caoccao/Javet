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

package com.caoccao.javet;

import com.caoccao.javet.config.JavetConfig;
import com.caoccao.javet.interop.V8Host;
import org.junit.jupiter.api.BeforeAll;

public abstract class BaseTestJavet {
    @BeforeAll
    public static void beforeAll() {
        if (!JavetConfig.isSealed()) {
            JavetConfig.setExposeGC(true);
            JavetConfig.setUseStrict(true);
        }
        V8Host.getInstance().setFlags();
    }

    protected void sleep(long milliSeconds) {
        sleep(milliSeconds, 1);
    }

    protected void sleep(long milliSeconds, int rounds) {
        assert milliSeconds > 0L;
        assert rounds > 0L;
        for (int i = 0; i < rounds; ++i) {
            try {
                Thread.sleep(milliSeconds);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
