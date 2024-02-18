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

import com.caoccao.javet.interfaces.IJavetEntityError;
import com.caoccao.javet.interop.converters.JavetObjectConverter;
import com.caoccao.javet.values.V8Value;

import java.util.Map;

/**
 * The type Javet scripting error.
 *
 * @since 0.7.0
 */
public final class JavetScriptingError {
    private static final JavetObjectConverter CONVERTER = new JavetObjectConverter();
    private static final String DETAILED_MESSAGE = "detailedMessage";
    private static final String MESSAGE = "message";
    private static final String STACK = "stack";
    private final int endColumn;
    private final int endPosition;
    private final int lineNumber;
    private final String message;
    private final String resourceName;
    private final String sourceLine;
    private final String stack;
    private final int startColumn;
    private final int startPosition;
    private Object context;
    private String detailedMessage;

    /**
     * Instantiates a new Javet scripting error.
     *
     * @param v8Value       the V8 value
     * @param resourceName  the resource name
     * @param sourceLine    the source line
     * @param lineNumber    the line number
     * @param startColumn   the start column
     * @param endColumn     the end column
     * @param startPosition the start position
     * @param endPosition   the end position
     * @since 0.7.0
     */
    JavetScriptingError(
            V8Value v8Value, String resourceName, String sourceLine,
            int lineNumber, int startColumn, int endColumn, int startPosition, int endPosition) {
        try {
            context = CONVERTER.toObject(v8Value, true);
        } catch (JavetException e) {
            context = null;
        }
        if (context instanceof IJavetEntityError) {
            IJavetEntityError javetEntityError = (IJavetEntityError) context;
            detailedMessage = javetEntityError.getDetailedMessage();
            message = javetEntityError.getMessage();
            stack = javetEntityError.getStack();
        } else if (context instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) context;
            if (map.containsKey(DETAILED_MESSAGE)) {
                detailedMessage = String.valueOf(map.get(DETAILED_MESSAGE));
            } else {
                detailedMessage = null;
            }
            message = String.valueOf(map.getOrDefault(MESSAGE, null));
            stack = String.valueOf(map.getOrDefault(STACK, null));
        } else {
            detailedMessage = null;
            message = null;
            stack = null;
        }
        if (detailedMessage == null) {
            detailedMessage = message;
        }
        this.endColumn = endColumn;
        this.endPosition = endPosition;
        this.lineNumber = lineNumber;
        this.resourceName = resourceName;
        this.sourceLine = sourceLine;
        this.startColumn = startColumn;
        this.startPosition = startPosition;
    }

    /**
     * Gets context.
     *
     * @return the context
     * @since 1.0.7
     */
    public Object getContext() {
        return context;
    }

    /**
     * Gets detailed message.
     *
     * @return the detailed message
     * @since 1.0.7
     */
    public String getDetailedMessage() {
        return detailedMessage;
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
     * Gets stack.
     *
     * @return the stack
     * @since 1.0.7
     */
    public String getStack() {
        return stack;
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(detailedMessage).append("\n");
        sb.append("Resource: ").append(resourceName).append("\n");
        sb.append("Source Code: ").append(sourceLine).append("\n");
        sb.append("Line Number: ").append(lineNumber).append("\n");
        sb.append("Column: ").append(startColumn).append(", ").append(endColumn).append("\n");
        sb.append("Position: ").append(startPosition).append(", ").append(endPosition);
        return sb.toString();
    }
}
