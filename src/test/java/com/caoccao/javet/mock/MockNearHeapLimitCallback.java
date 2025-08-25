/*
 * Copyright (c) 2025. caoccao.com Sam Cao
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

package com.caoccao.javet.mock;

import com.caoccao.javet.interop.callback.IJavetNearHeapLimitCallback;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class MockNearHeapLimitCallback implements IJavetNearHeapLimitCallback {
    private boolean callbackCalled;
    private boolean getDefaultHeapLimitCalled;

    public MockNearHeapLimitCallback() {
        callbackCalled = false;
        getDefaultHeapLimitCalled = false;
    }

    @Override
    public long callback(long currentHeapLimit, long initialHeapLimit) {
        callbackCalled = true;
        assertEquals(INITIAL_HEAP_LIMIT, initialHeapLimit);
        assertEquals(INITIAL_HEAP_LIMIT, currentHeapLimit);
        return currentHeapLimit * 2;
    }

    @Override
    public long getDefaultHeapLimit() {
        getDefaultHeapLimitCalled = true;
        return IJavetNearHeapLimitCallback.super.getDefaultHeapLimit();
    }

    public boolean isCallbackCalled() {
        return callbackCalled;
    }

    public boolean isGetDefaultHeapLimitCalled() {
        return getDefaultHeapLimitCalled;
    }

    public void setCallbackCalled(boolean callbackCalled) {
        this.callbackCalled = callbackCalled;
    }

    public void setGetDefaultHeapLimitCalled(boolean getDefaultHeapLimitCalled) {
        this.getDefaultHeapLimitCalled = getDefaultHeapLimitCalled;
    }
}
