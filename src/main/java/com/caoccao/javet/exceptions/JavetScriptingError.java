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

import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.utils.JavetResourceUtils;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.reference.V8ValueError;
import com.caoccao.javet.values.reference.V8ValueObject;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The type Javet scripting error.
 *
 * @since 0.7.0
 */
public final class JavetScriptingError {
    private final int endColumn;
    private final int endPosition;
    private final int lineNumber;
    private final String resourceName;
    private final String sourceLine;
    private final int startColumn;
    private final int startPosition;
    private Map<String, Object> context;
    private String detailedMessage;
    private String message;
    private String stack;

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
        context = new LinkedHashMap<>();
        detailedMessage = null;
        message = null;
        stack = null;
        try {
            if (v8Value instanceof V8ValueError) {
                // https://v8.dev/features/error-cause
                V8ValueError v8ValueError = (V8ValueError) v8Value;
                detailedMessage = v8ValueError.getMessage();
                message = v8ValueError.toString();
                stack = v8ValueError.getStack();
                final V8Runtime v8Runtime = v8ValueError.getV8Runtime();
                v8ValueError.forEach((V8Value key, V8Value value) -> {
                    context.put(key.toString(), v8Runtime.toObject(value));
                });
            } else if (v8Value instanceof V8ValueObject) {
                V8ValueObject v8ValueObject = (V8ValueObject) v8Value;
                detailedMessage = v8ValueObject.getString(V8ValueError.MESSAGE);
                message = detailedMessage;
                stack = v8ValueObject.getString(V8ValueError.STACK);
                final V8Runtime v8Runtime = v8ValueObject.getV8Runtime();
                v8ValueObject.forEach((V8Value key, V8Value value) -> {
                    String keyString = key.toString();
                    if (!V8ValueError.MESSAGE.equals(keyString) && !V8ValueError.STACK.equals(keyString)) {
                        context.put(keyString, v8Runtime.toObject(value));
                    }
                });
            }
        } catch (JavetException e) {
            e.printStackTrace();
        } finally {
            JavetResourceUtils.safeClose(v8Value);
        }
        context = Collections.unmodifiableMap(context);
        this.resourceName = resourceName;
        this.sourceLine = sourceLine;
        this.lineNumber = lineNumber;
        this.startColumn = startColumn;
        this.endColumn = endColumn;
        this.startPosition = startPosition;
        this.endPosition = endPosition;
    }

    /**
     * Gets context.
     *
     * @return the context
     * @since 1.0.7
     */
    public Map<String, Object> getContext() {
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
        sb.append(message).append("\n");
        sb.append("Resource: ").append(resourceName).append("\n");
        sb.append("Source Code: ").append(sourceLine).append("\n");
        sb.append("Line Number: ").append(lineNumber).append("\n");
        sb.append("Column: ").append(startColumn).append(", ").append(endColumn).append("\n");
        sb.append("Position: ").append(startPosition).append(", ").append(endPosition);
        return sb.toString();
    }
}
