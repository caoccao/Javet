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

import java.text.MessageFormat;

public final class JavetScriptingError {
    private int endColumn;
    private int endPosition;
    private int lineNumber;
    private String message;
    private String resourceName;
    private String sourceLine;
    private int startColumn;
    private int startPosition;

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

    public int getEndColumn() {
        return endColumn;
    }

    public int getEndPosition() {
        return endPosition;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public String getMessage() {
        return message;
    }

    public String getResourceName() {
        return resourceName;
    }

    public String getSourceLine() {
        return sourceLine;
    }

    public int getStartColumn() {
        return startColumn;
    }

    public int getStartPosition() {
        return startPosition;
    }

    public void setEndColumn(int endColumn) {
        this.endColumn = endColumn;
    }

    public void setEndPosition(int endPosition) {
        this.endPosition = endPosition;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public void setSourceLine(String sourceLine) {
        this.sourceLine = sourceLine;
    }

    public void setStartColumn(int startColumn) {
        this.startColumn = startColumn;
    }

    public void setStartPosition(int startPosition) {
        this.startPosition = startPosition;
    }

    @Override
    public String toString() {
        return MessageFormat.format(
                "Error: {0}\nResource: {1}\nSource Code: {2}\nLine Number: {3}\nColumn: {4}, {5}\nPosition: {6}, {7}",
                message, resourceName, sourceLine, Integer.toString(lineNumber),
                Integer.toString(startColumn), Integer.toString(endColumn),
                Integer.toString(startPosition), Integer.toString(endPosition));
    }
}
