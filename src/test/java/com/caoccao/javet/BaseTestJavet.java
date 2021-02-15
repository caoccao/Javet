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

import com.caoccao.javet.interfaces.IJavetLogger;
import com.caoccao.javet.interop.V8Flags;
import com.caoccao.javet.interop.V8Host;
import com.caoccao.javet.utils.JavetDefaultLogger;
import org.junit.jupiter.api.BeforeAll;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public abstract class BaseTestJavet {
    public static final long DEFAULT_INTERVAL_IN_MILLISECONDS = 10;
    protected IJavetLogger logger;

    public BaseTestJavet() {
        logger = new JavetDefaultLogger(getClass().getName());
    }

    @BeforeAll
    public static void beforeAll() {
        V8Flags flags = V8Host.getInstance().getFlags();
        if (!flags.isSealed()) {
            flags.setAllowNativesSyntax(true);
            flags.setExposeGC(true);
            flags.setUseStrict(true);
            flags.setTrackRetainingPath(true);
        }
        V8Host.getInstance().setFlags();
    }

    public void runAndWait(
            long timeOutInMilliseconds,
            long intervalInMilliseconds,
            IRunner runner)
            throws TimeoutException {
        ZonedDateTime startZonedDateTime = ZonedDateTime.now();
        ZonedDateTime endZonedDateTime = startZonedDateTime.plus(timeOutInMilliseconds, ChronoUnit.MILLIS);
        while (true) {
            if (runner.run()) {
                return;
            }
            if (timeOutInMilliseconds > 0 && endZonedDateTime.isBefore(ZonedDateTime.now())) {
                break;
            }
            try {
                TimeUnit.MILLISECONDS.sleep(intervalInMilliseconds);
            } catch (InterruptedException e) {
                throw new TimeoutException("Failed to sleep");
            }
        }
        throw new TimeoutException("Runner failed");
    }

    public void runAndWait(long timeOutInMilliseconds, IRunner runner) throws TimeoutException {
        runAndWait(timeOutInMilliseconds, DEFAULT_INTERVAL_IN_MILLISECONDS, runner);
    }

    public interface IRunner {
        boolean run();
    }
}
