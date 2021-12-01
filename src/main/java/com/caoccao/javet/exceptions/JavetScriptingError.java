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

package com.caoccao.javet.exceptions;

/**
 * The type Javet scripting error.
 *
 * @since 0.7.0
 */
public final class JavetScriptingError {
    private int endColumn;
    private int endPosition;
    private int lineNumber;
    private String message;
    private String resourceName;
    private String sourceLine;
    private int startColumn;
    private int startPosition;

    /**
     * Instantiates a new Javet scripting error.
     *
     * @param message       the message
     * @param resourceName  the resource name
     * @param sourceLine    the source line
     * @param lineNumber    the line number
     * @param startColumn   the start column
     * @param endColumn     the end column
     * @param startPosition the start position
     * @param endPosition   the end position
     * @since 0.7.0
     */
    public JavetScriptingError(
            String message, String resourceName, String sourceLine,
            int lineNumber, int startColumn, int endColumn, int startPosition, int endPosition) {
        this.message = message;
        this.resourceName = resourceName;
        this.sourceLine = sourceLine;
        this.lineNumber = lineNumber;
        this.startColumn = startColumn;
        this.endColumn = endColumn;
        this.startPosition = startPosition;
        this.endPosition = endPosition;
    }

    /**
     * Gets end column.
     *
     * @return the end column
     * @since 0.9.1
     */
    public int getEndColumn() {
        return endColumn;
    }

    /**
     * Gets end position.
     *
     * @return the end position
     * @since 0.9.1
     */
    public int getEndPosition() {
        return endPosition;
    }

    /**
     * Gets line number.
     *
     * @return the line number
     * @since 0.9.1
     */
    public int getLineNumber() {
        return lineNumber;
    }

    /**
     * Gets message.
     *
     * @return the message
     * @since 0.9.1
     */
    public String getMessage() {
        return message;
    }

    /**
     * Gets resource name.
     *
     * @return the resource name
     * @since 0.9.1
     */
    public String getResourceName() {
        return resourceName;
    }

    /**
     * Gets source line.
     *
     * @return the source line
     */
    public String getSourceLine() {
        return sourceLine;
    }

    /**
     * Gets start column.
     *
     * @return the start column
     * @since 0.9.1
     */
    public int getStartColumn() {
        return startColumn;
    }

    /**
     * Gets start position.
     *
     * @return the start position
     * @since 0.9.1
     */
    public int getStartPosition() {
        return startPosition;
    }

    /**
     * Sets end column.
     *
     * @param endColumn the end column
     * @return the self
     * @since 0.9.1
     */
    public JavetScriptingError setEndColumn(int endColumn) {
        this.endColumn = endColumn;
        return this;
    }

    /**
     * Sets end position.
     *
     * @param endPosition the end position
     * @return the self
     * @since 0.9.1
     */
    public JavetScriptingError setEndPosition(int endPosition) {
        this.endPosition = endPosition;
        return this;
    }

    /**
     * Sets line number.
     *
     * @param lineNumber the line number
     * @return the self
     * @since 0.9.1
     */
    public JavetScriptingError setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
        return this;
    }

    /**
     * Sets message.
     *
     * @param message the message
     * @return the self
     * @since 0.9.1
     */
    public JavetScriptingError setMessage(String message) {
        this.message = message;
        return this;
    }

    /**
     * Sets resource name.
     *
     * @param resourceName the resource name
     * @return the self
     * @since 0.9.1
     */
    public JavetScriptingError setResourceName(String resourceName) {
        this.resourceName = resourceName;
        return this;
    }

    /**
     * Sets source line.
     *
     * @param sourceLine the source line
     * @return the self
     * @since 0.9.1
     */
    public JavetScriptingError setSourceLine(String sourceLine) {
        this.sourceLine = sourceLine;
        return this;
    }

    /**
     * Sets start column.
     *
     * @param startColumn the start column
     * @return the self
     * @since 0.9.1
     */
    public JavetScriptingError setStartColumn(int startColumn) {
        this.startColumn = startColumn;
        return this;
    }

    /**
     * Sets start position.
     *
     * @param startPosition the start position
     * @return the self
     * @since 0.9.1
     */
    public JavetScriptingError setStartPosition(int startPosition) {
        this.startPosition = startPosition;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(message).append("\n");
        sb.append("Resource: ").append(resourceName).append("\n");
        sb.append("Source Code: ").append(sourceLine).append("\n");
        sb.append("Line Number: ").append(lineNumber).append("\n");
        sb.append("Column: ").append(startColumn).append(", ").append(endColumn).append("\n");
        sb.append("Position: ").append(startPosition).append(", ").append(endPosition);
        return sb.toString();
    }
}
