/*
 * Copyright (c) 2021. caoccao.com Sam Cao
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
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestV8HeapStatistics extends BaseTestJavetRuntime {
    @Test
    public void test() {
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
        assertTrue(v8HeapStatistics.getTotalHeapSizeExecutable() > 0);
        assertTrue(v8HeapStatistics.getTotalPhysicalSize() > 0);
        assertTrue(v8HeapStatistics.getUsedGlobalHandlesSize() > 0);
        assertTrue(v8HeapStatistics.getUsedHeapSize() > 0);
    }
}
