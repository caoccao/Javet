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

public class TestV8HeapSpaceStatistics extends BaseTestJavetRuntime {
    @Test
    public void test() {
        for (V8HeapSpaceStatistics.AllocationSpace allocationSpace : V8HeapSpaceStatistics.AllocationSpace.values()) {
            V8HeapSpaceStatistics v8HeapSpaceStatistics = v8Runtime.getV8HeapSpaceStatistics(allocationSpace);
            assertNotNull(v8HeapSpaceStatistics);
            String detailString = v8HeapSpaceStatistics.toString();
            assertNotNull(detailString);
            assertEquals(allocationSpace, v8HeapSpaceStatistics.getAllocationSpace());
            assertTrue(v8HeapSpaceStatistics.getPhysicalSpaceSize() >= 0);
            assertTrue(v8HeapSpaceStatistics.getSpaceAvailableSize() >= 0);
            assertTrue(v8HeapSpaceStatistics.getSpaceSize() >= 0);
            assertTrue(v8HeapSpaceStatistics.getSpaceUsedSize() >= 0);
        }
    }
}
