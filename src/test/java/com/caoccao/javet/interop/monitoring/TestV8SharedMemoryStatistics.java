/*
 * Copyright (c) 2021-2022. caoccao.com Sam Cao
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

import com.caoccao.javet.BaseTestJavetRuntime;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.utils.JavetResourceUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestV8SharedMemoryStatistics extends BaseTestJavetRuntime {

    protected void printV8SharedMemoryStatistics(V8Runtime v8Runtime, String prefix) {
        System.out.printf("%s: %s%n", prefix, v8Runtime.getV8SharedMemoryStatistics().toString());
    }

    @Test
    @Tag("performance")
    public void testCorrelations() throws JavetException {
        printV8SharedMemoryStatistics(v8Runtime, "Baseline");
        List<V8Runtime> v8Runtimes = new ArrayList<>();
        for (int i = 0; i < 100; ++i) {
            V8Runtime newV8Runtime = v8Host.createV8Runtime();
            v8Runtimes.add(newV8Runtime);
            printV8SharedMemoryStatistics(newV8Runtime, Integer.toString(i));
        }
        JavetResourceUtils.safeClose(v8Runtimes);
    }

    @Test
    public void testGetV8SharedMemoryStatistics() {
        V8SharedMemoryStatistics v8SharedMemoryStatistics = v8Runtime.getV8SharedMemoryStatistics();
        assertNotNull(v8SharedMemoryStatistics);
        String detailString = v8SharedMemoryStatistics.toString();
        assertNotNull(detailString);
        assertTrue(v8SharedMemoryStatistics.getReadOnlySpacePhysicalSize() >= 0);
        assertTrue(v8SharedMemoryStatistics.getReadOnlySpaceSize() >= 0);
        assertTrue(v8SharedMemoryStatistics.getReadOnlySpaceUsedSize() >= 0);
    }
}
