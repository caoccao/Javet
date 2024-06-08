/*
 * Copyright (c) 2024-2024. caoccao.com Sam Cao
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

package com.caoccao.javet.interop.monitoring;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Host;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.options.V8RuntimeOptions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TestV8HeapStatisticsOOM {
    @Test
    @Tag("performance")
    public void testOOM() throws JavetException, InterruptedException {
        V8RuntimeOptions.V8_FLAGS.setMaxHeapSize(8096);
        try (V8Runtime v8Runtime = V8Host.getV8Instance().createV8Runtime()) {
            final ScheduledExecutorService timerService = Executors.newSingleThreadScheduledExecutor();
            timerService.scheduleAtFixedRate(() -> {
                V8HeapStatistics v8HeapStatistics = v8Runtime.getV8HeapStatistics();
                double used = v8HeapStatistics.getUsedHeapSize();
                double limit = v8HeapStatistics.getHeapSizeLimit();
                double ratio = used / limit;
                System.out.println("Limit: " + v8HeapStatistics.getHeapSizeLimit() +
                        ", Used: " + v8HeapStatistics.getUsedHeapSize() +
                        ", Ratio: " + ratio);
            }, 250, 1000, TimeUnit.MILLISECONDS);
            v8Runtime.getExecutor("const a = [];" +
                    "for (let i = 0; i < 100000000; i++) {" +
                    "  a.push({test:'test'});" +
                    "}").executeVoid();
            timerService.shutdown();
            timerService.awaitTermination(30, TimeUnit.SECONDS);
        }
    }
}
