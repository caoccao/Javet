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

package com.caoccao.javet;

import com.caoccao.javet.enums.JSRuntimeType;
import com.caoccao.javet.interfaces.IJavetLogger;
import com.caoccao.javet.interop.V8Host;
import com.caoccao.javet.interop.loader.JavetLibLoader;
import com.caoccao.javet.interop.options.NodeRuntimeOptions;
import com.caoccao.javet.interop.options.V8Flags;
import com.caoccao.javet.interop.options.V8RuntimeOptions;
import com.caoccao.javet.utils.JavetDefaultLogger;
import com.caoccao.javet.utils.JavetOSUtils;

import java.io.File;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;

public abstract class BaseTestJavet {
    public static final double DELTA = 0.001;
    public static final long DEFAULT_INTERVAL_IN_MILLISECONDS = 10;
    protected IJavetLogger logger;
    protected V8Host v8Host;

    public BaseTestJavet() {
        this(null);
    }

    public BaseTestJavet(JSRuntimeType jsRuntimeType) {
        try {
            logger = new JavetDefaultLogger(getClass().getName());
            if (jsRuntimeType == null) {
                JavetLibLoader javetLibLoaderNode = new JavetLibLoader(JSRuntimeType.Node);
                JavetLibLoader javetLibLoaderV8 = new JavetLibLoader(JSRuntimeType.V8);
                String resourceDirPath = new File(
                        JavetOSUtils.WORKING_DIRECTORY, "src/main/resources").getAbsolutePath();
                File nodeLibFile = new File(resourceDirPath, javetLibLoaderNode.getResourceFileName());
                File v8LibFile = new File(resourceDirPath, javetLibLoaderV8.getResourceFileName());
                if (nodeLibFile.lastModified() > v8LibFile.lastModified()) {
                    jsRuntimeType = JSRuntimeType.Node;
                } else {
                    jsRuntimeType = JSRuntimeType.V8;
                }
            }
            for (V8Flags v8Flags : new V8Flags[]{NodeRuntimeOptions.V8_FLAGS, V8RuntimeOptions.V8_FLAGS}) {
                if (!v8Flags.isSealed()) {
                    v8Flags.setAllowNativesSyntax(true);
                    v8Flags.setExposeGC(false);
                    v8Flags.setExposeInspectorScripts(true);
                    v8Flags.setMaxHeapSize(768);
                    v8Flags.setMaxOldSpaceSize(512);
                    v8Flags.setUseStrict(true);
                    v8Flags.setTrackRetainingPath(true);
                }
            }
            if (jsRuntimeType.isNode()) {
                v8Host = V8Host.getNodeInstance();
                assertTrue(NodeRuntimeOptions.V8_FLAGS.isSealed());
            } else {
                v8Host = V8Host.getV8Instance();
                assertTrue(V8RuntimeOptions.V8_FLAGS.isSealed());
            }
            assertEquals(jsRuntimeType, v8Host.getJSRuntimeType());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    public int runAndWait(
            long timeOutInMilliseconds,
            long intervalInMilliseconds,
            IRunner runner)
            throws TimeoutException {
        int count = 0;
        ZonedDateTime startZonedDateTime = ZonedDateTime.now();
        ZonedDateTime endZonedDateTime = startZonedDateTime.plus(timeOutInMilliseconds, ChronoUnit.MILLIS);
        while (true) {
            if (runner.run()) {
                return count;
            }
            if (timeOutInMilliseconds > 0 && endZonedDateTime.isBefore(ZonedDateTime.now())) {
                break;
            }
            try {
                TimeUnit.MILLISECONDS.sleep(intervalInMilliseconds);
                ++count;
            } catch (InterruptedException e) {
                throw new TimeoutException("Failed to sleep");
            }
        }
        throw new TimeoutException("Runner failed");
    }

    public int runAndWait(long timeOutInMilliseconds, IRunner runner) throws TimeoutException {
        return runAndWait(timeOutInMilliseconds, DEFAULT_INTERVAL_IN_MILLISECONDS, runner);
    }

    public interface IRunner {
        boolean run();
    }
}
