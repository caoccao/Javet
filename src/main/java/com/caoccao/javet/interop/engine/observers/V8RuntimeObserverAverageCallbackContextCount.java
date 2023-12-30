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
 * The type V8 runtime observer average callback context count.
 *
 * @since 1.0.6
 */
public class V8RuntimeObserverAverageCallbackContextCount implements IV8RuntimeObserver<Integer> {
    /**
     * The Callback context count list.
     *
     * @since 1.0.6
     */
    protected final List<Integer> callbackContextCountList;

    /**
     * Instantiates a new V8 runtime observer average callback context count.
     *
     * @since 1.0.6
     */
    public V8RuntimeObserverAverageCallbackContextCount() {
        this(256);
    }

    /**
     * Instantiates a new V8 runtime observer average callback context count.
     *
     * @param capacity the capacity
     * @since 1.0.6
     */
    public V8RuntimeObserverAverageCallbackContextCount(int capacity) {
        callbackContextCountList = new ArrayList<>(capacity);
    }

    @Override
    public Integer getResult() {
        int averageCallbackContextCount = 0;
        if (!callbackContextCountList.isEmpty()) {
            int totalCallbackContextCount = 0;
            for (int callbackContextCount : callbackContextCountList) {
                totalCallbackContextCount += callbackContextCount;
            }
            final int v8RuntimeCount = callbackContextCountList.size();
            averageCallbackContextCount = totalCallbackContextCount / v8RuntimeCount;
        }
        return averageCallbackContextCount;
    }

    @Override
    public boolean observe(V8Runtime v8Runtime) {
        callbackContextCountList.add(v8Runtime.getCallbackContextCount());
        return true;
    }

    @Override
    public void reset() {
        callbackContextCountList.clear();
    }
}
