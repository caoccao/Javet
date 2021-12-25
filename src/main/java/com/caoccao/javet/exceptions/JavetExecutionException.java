/*
 *    Copyright 2021. caoccao.com Sam Cao
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

package com.caoccao.javet.exceptions;

import java.util.Objects;

/**
 * The type Javet execution exception.
 *
 * @since 0.7.0
 */
public class JavetExecutionException extends BaseJavetScriptingException {
    /**
     * Instantiates a new Javet execution exception.
     *
     * @param scriptingError the scripting error
     * @param cause          the cause
     * @since 0.7.0
     */
    public JavetExecutionException(JavetScriptingError scriptingError, Throwable cause) {
        super(JavetError.ExecutionFailure, Objects.requireNonNull(scriptingError), cause);
    }
}
