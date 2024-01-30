/*
 * Copyright (c) 2024. caoccao.com Sam Cao
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

package com.caoccao.javet.entities;

import com.caoccao.javet.enums.V8ValueErrorType;

import java.util.HashMap;
import java.util.Map;

/**
 * The type Javet entity error.
 *
 * @since 3.0.4
 */
public class JavetEntityError {
    /**
     * The Context.
     *
     * @since 3.0.4
     */
    protected final Map<String, Object> context;
    /**
     * The Detailed message.
     *
     * @since 3.0.4
     */
    protected String detailedMessage;
    /**
     * The Message.
     *
     * @since 3.0.4
     */
    protected String message;
    /**
     * The Stack.
     *
     * @since 3.0.4
     */
    protected String stack;
    /**
     * The Type.
     */
    protected V8ValueErrorType type;

    /**
     * Instantiates a new Javet entity error.
     *
     * @param type            the type
     * @param message         the message
     * @param detailedMessage the detailed message
     * @param stack           the stack
     * @since 3.0.4
     */
    public JavetEntityError(V8ValueErrorType type, String message, String detailedMessage, String stack) {
        context = new HashMap<>();
        this.detailedMessage = detailedMessage;
        this.message = message;
        this.stack = stack;
        this.type = type;
    }

    /**
     * Gets context.
     *
     * @return the context
     * @since 3.0.4
     */
    public Map<String, Object> getContext() {
        return context;
    }

    /**
     * Gets detailed message.
     *
     * @return the detailed message
     */
    public String getDetailedMessage() {
        return detailedMessage;
    }

    /**
     * Gets message.
     *
     * @return the message
     * @since 3.0.4
     */
    public String getMessage() {
        return message;
    }

    /**
     * Gets stack.
     *
     * @return the stack
     * @since 3.0.4
     */
    public String getStack() {
        return stack;
    }

    /**
     * Gets type.
     *
     * @return the type
     * @since 3.0.4
     */
    public V8ValueErrorType getType() {
        return type;
    }

    /**
     * Sets detailed message.
     *
     * @param detailedMessage the detailed message
     * @since 3.0.4
     */
    public void setDetailedMessage(String detailedMessage) {
        this.detailedMessage = detailedMessage;
    }

    /**
     * Sets message.
     *
     * @param message the message
     * @since 3.0.4
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Sets stack.
     *
     * @param stack the stack
     * @since 3.0.4
     */
    public void setStack(String stack) {
        this.stack = stack;
    }

    /**
     * Sets type.
     *
     * @param type the type
     * @since 3.0.4
     */
    public void setType(V8ValueErrorType type) {
        this.type = type;
    }
}
