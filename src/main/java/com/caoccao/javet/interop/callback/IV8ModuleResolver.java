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

package com.caoccao.javet.interop.callback;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.values.reference.IV8Module;

/**
 * The interface V8 module resolver is for resolving V8 module look-up in dynamic import.
 *
 * @since 0.9.3
 */
public interface IV8ModuleResolver {
    /**
     * Resolve V8 module.
     *
     * @param v8Runtime        the V8 runtime
     * @param resourceName     the resource name
     * @param v8ModuleReferrer the V8 module referrer
     * @return the V8 module
     * @throws JavetException the javet exception
     * @since 0.9.3
     */
    IV8Module resolve(V8Runtime v8Runtime, String resourceName, IV8Module v8ModuleReferrer) throws JavetException;
}
