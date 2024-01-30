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

package com.caoccao.javet.exceptions;

import com.caoccao.javet.utils.SimpleMap;

/**
 * The type Base javet scripting exception.
 *
 * @since 0.7.0
 */
public abstract class BaseJavetScriptingException extends JavetException {
    /**
     * The Scripting error.
     *
     * @since 0.8.5
     */
    protected JavetScriptingError scriptingError;

    /**
     * Instantiates a new Base javet scripting exception.
     *
     * @param error          the error
     * @param scriptingError the scripting error
     * @param cause          the cause
     * @since 0.8.5
     */
    protected BaseJavetScriptingException(
            JavetError error,
            JavetScriptingError scriptingError,
            Throwable cause) {
        super(
                error,
                SimpleMap.of(
                        JavetError.PARAMETER_MESSAGE, scriptingError.getDetailedMessage(),
                        JavetError.PARAMETER_RESOURCE_NAME, scriptingError.getResourceName(),
                        JavetError.PARAMETER_SOURCE_LINE, scriptingError.getSourceLine(),
                        JavetError.PARAMETER_LINE_NUMBER, scriptingError.getLineNumber(),
                        JavetError.PARAMETER_START_COLUMN, scriptingError.getStartColumn(),
                        JavetError.PARAMETER_END_COLUMN, scriptingError.getEndColumn(),
                        JavetError.PARAMETER_START_POSITION, scriptingError.getStartPosition(),
                        JavetError.PARAMETER_END_POSITION, scriptingError.getEndPosition()),
                cause);
        this.scriptingError = scriptingError;
    }

    /**
     * Gets scripting error.
     *
     * @return the scripting error
     * @since 0.8.5
     */
    public JavetScriptingError getScriptingError() {
        return scriptingError;
    }
}
