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

package com.caoccao.javet.interop.monitoring;

import com.caoccao.javet.BaseTestJavetRuntime;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.utils.JavetResourceUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestV8HeapStatistics extends BaseTestJavetRuntime {

    protected void printV8HeapStatistics(V8Runtime v8Runtime, String prefix) {
        System.out.printf("%s: %s%n", prefix, v8Runtime.getV8HeapStatistics().toString());
    }

    @Test
    @Tag("performance")
    public void testCorrelations() throws JavetException {
        printV8HeapStatistics(v8Runtime, "Baseline");
        List<V8Runtime> v8Runtimes = new ArrayList<>();
        for (int i = 0; i < 100; ++i) {
            V8Runtime newV8Runtime = v8Host.createV8Runtime();
            v8Runtimes.add(newV8Runtime);
            printV8HeapStatistics(newV8Runtime, Integer.toString(i));
        }
        JavetResourceUtils.safeClose(v8Runtimes);
    }

    @Test
    public void testGetV8HeapStatistics() {
        V8HeapStatistics v8HeapStatistics = v8Runtime.getV8HeapStatistics();
        assertNotNull(v8HeapStatistics);
        String detailString = v8HeapStatistics.toString();
        assertNotNull(detailString);
        assertEquals(0, v8HeapStatistics.getDoesZapGarbage());
        assertTrue(v8HeapStatistics.getExternalMemory() >= 0);
        assertTrue(v8HeapStatistics.getHeapSizeLimit() > 0);
        assertTrue(v8HeapStatistics.getMallocedMemory() > 0);
        assertEquals(0, v8HeapStatistics.getNumberOfDetachedContexts());
        assertTrue(v8HeapStatistics.getNumberOfNativeContexts() > 0);
        assertTrue(v8HeapStatistics.getPeakMallocedMemory() > 0);
        assertTrue(v8HeapStatistics.getTotalAvailableSize() > 0);
        assertTrue(v8HeapStatistics.getTotalGlobalHandlesSize() > 0);
        assertTrue(v8HeapStatistics.getTotalHeapSize() > 0);
        assertTrue(v8HeapStatistics.getTotalHeapSizeExecutable() >= 0);
        assertTrue(v8HeapStatistics.getTotalPhysicalSize() > 0);
        assertTrue(v8HeapStatistics.getUsedGlobalHandlesSize() > 0);
        assertTrue(v8HeapStatistics.getUsedHeapSize() > 0);
    }
}
