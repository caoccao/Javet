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

package com.caoccao.javet.utils.receivers;

import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.values.V8Value;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * The interface Javet callback receiver.
 */
public interface IJavetCallbackReceiver {
    /**
     * Gets V8 runtime.
     *
     * @return the V8 runtime
     */
    V8Runtime getV8Runtime();
}
