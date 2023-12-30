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
import com.caoccao.javet.enums.V8AllocationSpace;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.utils.JavetResourceUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestV8HeapSpaceStatistics extends BaseTestJavetRuntime {

    protected void printV8HeapSpaceStatistics(V8Runtime v8Runtime, String prefix) {
        for (V8AllocationSpace v8AllocationSpace : V8AllocationSpace.getDistinctValues()) {
            System.out.printf(
                    "%s: %s%n", prefix, v8Runtime.getV8HeapSpaceStatistics(v8AllocationSpace).toString());
        }
    }

    @Test
    @Tag("performance")
    public void testCorrelations() throws JavetException {
        printV8HeapSpaceStatistics(v8Runtime, "Baseline");
        List<V8Runtime> v8Runtimes = new ArrayList<>();
        for (int i = 0; i < 100; ++i) {
            V8Runtime newV8Runtime = v8Host.createV8Runtime();
            v8Runtimes.add(newV8Runtime);
            printV8HeapSpaceStatistics(v8Runtime, Integer.toString(i));
        }
        JavetResourceUtils.safeClose(v8Runtimes);
    }

    @Test
    public void testGetV8HeapSpaceStatistics() {
        for (V8AllocationSpace v8AllocationSpace : V8AllocationSpace.values()) {
            V8HeapSpaceStatistics v8HeapSpaceStatistics = v8Runtime.getV8HeapSpaceStatistics(v8AllocationSpace);
            assertNotNull(v8HeapSpaceStatistics);
            String detailString = v8HeapSpaceStatistics.toString();
            assertNotNull(detailString);
            assertEquals(v8AllocationSpace, v8HeapSpaceStatistics.getAllocationSpace());
            assertTrue(v8HeapSpaceStatistics.getSpaceName().length() > 0);
            assertTrue(v8HeapSpaceStatistics.getPhysicalSpaceSize() >= 0);
            assertTrue(v8HeapSpaceStatistics.getSpaceAvailableSize() >= 0);
            assertTrue(v8HeapSpaceStatistics.getSpaceSize() >= 0);
            assertTrue(v8HeapSpaceStatistics.getSpaceUsedSize() >= 0);
        }
    }
}
