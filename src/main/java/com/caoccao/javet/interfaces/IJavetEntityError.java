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

package com.caoccao.javet.interfaces;

import com.caoccao.javet.enums.V8ValueErrorType;
import com.caoccao.javet.interop.proxy.IJavetNonProxy;

import java.util.Map;

/**
 * The interface Javet entity error.
 *
 * @since 3.0.4
 */
public interface IJavetEntityError extends IJavetNonProxy {
    /**
     * Gets context.
     *
     * @return the context
     * @since 3.0.4
     */
    Map<String, Object> getContext();

    /**
     * Gets detailed message.
     *
     * @return the detailed message
     * @since 3.0.4
     */
    String getDetailedMessage();

    /**
     * Gets message.
     *
     * @return the message
     * @since 3.0.4
     */
    String getMessage();

    /**
     * Gets stack.
     *
     * @return the stack
     * @since 3.0.4
     */
    String getStack();

    /**
     * Gets type.
     *
     * @return the type
     * @since 3.0.4
     */
    V8ValueErrorType getType();

    /**
     * Sets detailed message.
     *
     * @param detailedMessage the detailed message
     * @since 3.0.4
     */
    void setDetailedMessage(String detailedMessage);

    /**
     * Sets message.
     *
     * @param message the message
     * @since 3.0.4
     */
    void setMessage(String message);

    /**
     * Sets stack.
     *
     * @param stack the stack
     * @since 3.0.4
     */
    void setStack(String stack);

    /**
     * Sets type.
     *
     * @param type the type
     * @since 3.0.4
     */
    void setType(V8ValueErrorType type);
}
