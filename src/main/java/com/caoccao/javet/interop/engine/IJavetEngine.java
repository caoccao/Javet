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

package com.caoccao.javet.interop.engine;

import com.caoccao.javet.annotations.CheckReturnValue;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interfaces.IJavetClosable;
import com.caoccao.javet.interop.V8Runtime;

public interface IJavetEngine<R extends V8Runtime> extends IJavetClosable {
    JavetEngineConfig getConfig();

    @CheckReturnValue
    IJavetEngineGuard getGuard();

    @CheckReturnValue
    IJavetEngineGuard getGuard(long timeoutMillis);

    R getV8Runtime() throws JavetException;

    boolean isActive();

    void resetContext() throws JavetException;

    void resetIsolate() throws JavetException;

    void sendGCNotification();
}
