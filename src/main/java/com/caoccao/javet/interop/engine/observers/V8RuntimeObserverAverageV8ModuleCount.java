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

package com.caoccao.javet.interop.engine.observers;

import com.caoccao.javet.interop.V8Runtime;

import java.util.ArrayList;
import java.util.List;

/**
 * The type V8 runtime observer average V8 module count.
 *
 * @since 1.0.6
 */
public class V8RuntimeObserverAverageV8ModuleCount implements IV8RuntimeObserver<Integer> {
    /**
     * The V8 module count list.
     *
     * @since 1.0.6
     */
    protected final List<Integer> v8ModuleCountList;

    /**
     * Instantiates a new V8 runtime observer average V8 module count.
     *
     * @since 1.0.6
     */
    public V8RuntimeObserverAverageV8ModuleCount() {
        this(256);
    }

    /**
     * Instantiates a new V8 runtime observer average V8 module count.
     *
     * @param capacity the capacity
     * @since 1.0.6
     */
    public V8RuntimeObserverAverageV8ModuleCount(int capacity) {
        v8ModuleCountList = new ArrayList<>(capacity);
    }

    @Override
    public Integer getResult() {
        int averageV8ModuleCount = 0;
        if (!v8ModuleCountList.isEmpty()) {
            int totalV8ModuleCount = 0;
            for (int referenceCount : v8ModuleCountList) {
                totalV8ModuleCount += referenceCount;
            }
            final int v8RuntimeCount = v8ModuleCountList.size();
            averageV8ModuleCount = totalV8ModuleCount / v8RuntimeCount;
        }
        return averageV8ModuleCount;
    }

    @Override
    public boolean observe(V8Runtime v8Runtime) {
        v8ModuleCountList.add(v8Runtime.getV8ModuleCount());
        return true;
    }

    @Override
    public void reset() {
        v8ModuleCountList.clear();
    }
}
