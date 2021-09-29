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
     * @param error         the error
     * @param message       the message
     * @param resourceName  the resource name
     * @param sourceLine    the source line
     * @param lineNumber    the line number
     * @param startColumn   the start column
     * @param endColumn     the end column
     * @param startPosition the start position
     * @param endPosition   the end position
     * @since 0.8.5
     */
    protected BaseJavetScriptingException(
            JavetError error,
            String message, String resourceName, String sourceLine,
            int lineNumber, int startColumn, int endColumn, int startPosition, int endPosition) {
        super(error, SimpleMap.of(
                JavetError.PARAMETER_MESSAGE, message,
                JavetError.PARAMETER_RESOURCE_NAME, resourceName,
                JavetError.PARAMETER_SOURCE_LINE, sourceLine,
                JavetError.PARAMETER_LINE_NUMBER, lineNumber,
                JavetError.PARAMETER_START_COLUMN, startColumn,
                JavetError.PARAMETER_END_COLUMN, endColumn,
                JavetError.PARAMETER_START_POSITION, startPosition,
                JavetError.PARAMETER_END_POSITION, endPosition));
        scriptingError = new JavetScriptingError(message, resourceName, sourceLine,
                lineNumber, startColumn, endColumn, startPosition, endPosition);
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
