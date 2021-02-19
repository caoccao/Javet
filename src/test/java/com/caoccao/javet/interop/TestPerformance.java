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

package com.caoccao.javet.interop;

import com.caoccao.javet.BaseTestJavet;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.engine.IJavetEngine;
import com.caoccao.javet.interop.engine.IJavetEnginePool;
import com.caoccao.javet.interop.engine.JavetEnginePool;
import com.caoccao.javet.interop.executors.IV8Executor;
import com.caoccao.javet.utils.JavetOSUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestPerformance extends BaseTestJavet {
    protected IJavetEnginePool javetEnginePool;

    @BeforeEach
    public void beforeEach() {
        javetEnginePool = new JavetEnginePool();
    }

    @AfterEach
    public void afterEach() throws JavetException {
        javetEnginePool.close();
    }

    @Test
    @Tag("performance")
    public void testAdHocContextAnd1Thread() throws Exception {
        final int iterations = 10000;
        String codeString = "1 + 1";
        final long startTime = System.currentTimeMillis();
        try (IJavetEngine javetEngine = javetEnginePool.getEngine()) {
            V8Runtime v8Runtime = javetEngine.getV8Runtime();
            IV8Executor v8Executor = v8Runtime.getExecutor(codeString);
            for (int i = 0; i < iterations; i++) {
                javetEngine.resetContext();
                assertEquals(2, v8Executor.executeInteger());
            }
        }
        final long stopTime = System.currentTimeMillis();
        final long tps = iterations * 1000 / (stopTime - startTime);
        logger.logInfo("Ad-hoc Context with 1 Thread: {0}", tps);
        updateDoc("Ad-hoc Context with 1 Thread", tps);
    }

    @Test
    @Tag("performance")
    public void testSingleContextAnd1Thread() throws Exception {
        final int iterations = 2000000;
        String codeString = "1 + 1";
        final long startTime = System.currentTimeMillis();
        try (IJavetEngine javetEngine = javetEnginePool.getEngine()) {
            V8Runtime v8Runtime = javetEngine.getV8Runtime();
            IV8Executor v8Executor = v8Runtime.getExecutor(codeString);
            for (int i = 0; i < iterations; i++) {
                assertEquals(2, v8Executor.executeInteger());
            }
        }
        final long stopTime = System.currentTimeMillis();
        final long tps = iterations * 1000 / (stopTime - startTime);
        logger.logInfo("Single Context with 1 Thread: {0}", tps);
        updateDoc("Single Context with 1 Thread", tps);
    }

    @Test
    @Tag("performance")
    public void testAdHocContextAnd8Threads() throws Exception {
        final int threadCount = 8;
        final int iterations = 5000;
        String codeString = "1 + 1";
        Thread[] threads = new Thread[threadCount];
        for (int i = 0; i < threadCount; i++) {
            threads[i] = new Thread(() -> {
                try (IJavetEngine javetEngine = javetEnginePool.getEngine()) {
                    V8Runtime v8Runtime = javetEngine.getV8Runtime();
                    IV8Executor v8Executor = v8Runtime.getExecutor(codeString);
                    for (int j = 0; j < iterations; j++) {
                        javetEngine.resetContext();
                        assertEquals(2, v8Executor.executeInteger());
                    }
                } catch (Exception e) {
                    logger.logError(e, "Failed to execute.");
                }
            });
        }
        final long startTime = System.currentTimeMillis();
        for (Thread thread : threads) {
            thread.start();
        }
        for (Thread thread : threads) {
            thread.join();
        }
        final long stopTime = System.currentTimeMillis();
        final long tps = iterations * (long) threadCount * 1000 / (stopTime-startTime);
        logger.logInfo("Ad-hoc Context with 8 Threads: {0}", tps);
        updateDoc("Ad-hoc Context with 8 Threads", tps);
    }

    @Test
    @Tag("performance")
    public void testSingleContextAnd8Threads() throws Exception {
        final int threadCount = 8;
        final int iterations = 1000000;
        String codeString = "1 + 1";
        Thread[] threads = new Thread[threadCount];
        for (int i = 0; i < threadCount; i++) {
            threads[i] = new Thread(() -> {
                try (IJavetEngine javetEngine = javetEnginePool.getEngine()) {
                    V8Runtime v8Runtime = javetEngine.getV8Runtime();
                    IV8Executor v8Executor = v8Runtime.getExecutor(codeString);
                    for (int j = 0; j < iterations; j++) {
                        assertEquals(2, v8Executor.executeInteger());
                    }
                } catch (Exception e) {
                    logger.logError(e, "Failed to execute.");
                }
            });
        }
        final long startTime = System.currentTimeMillis();
        for (Thread thread : threads) {
            thread.start();
        }
        for (Thread thread : threads) {
            thread.join();
        }
        final long stopTime = System.currentTimeMillis();
        final long tps = iterations * (long) threadCount * 1000 / (stopTime-startTime);
        logger.logInfo("Single Context with 8 Threads: {0}", tps);
        updateDoc("Single Context with 8 Threads", tps);
    }

    protected void updateDoc(String prefix, long tps) throws IOException {
        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        File docFile = new File(
                JavetOSUtils.WORKING_DIRECTORY,
                "docs/performance.rst");
        List<String> lines = new ArrayList<>();
        for (String line : Files.readAllLines(docFile.toPath(), StandardCharsets.UTF_8)) {
            if (line.startsWith(prefix)) {
                line = line.substring(0, line.lastIndexOf(" ") + 1) + decimalFormat.format(tps);
            }
            lines.add(line);
        }
        lines.add("");
        try (FileWriter fileWriter = new FileWriter(docFile, false)) {
            fileWriter.write(String.join("\n", lines));
        }
    }
}
