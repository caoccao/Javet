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

package com.caoccao.javet.values.reference;

import com.caoccao.javet.enums.V8ValueErrorType;
import com.caoccao.javet.enums.V8ValueReferenceType;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.utils.StringUtils;
import com.caoccao.javet.values.V8Value;

/**
 * The type V8 value error.
 *
 * @since 0.7.0
 */
public class V8ValueError extends V8ValueObject {
    /**
     * The constant MESSAGE.
     *
     * @since 1.1.6
     */
    public static final String MESSAGE = "message";
    /**
     * The constant STACK.
     *
     * @since 1.1.6
     */
    public static final String STACK = "stack";
    /**
     * The constant METHOD_NAME_CONSTRUCTOR.
     *
     * @since 3.0.4
     */
    protected static final String METHOD_NAME_CONSTRUCTOR = "constructor";

    /**
     * The Optional type.
     *
     * @since 3.0.4
     */
    protected V8ValueErrorType optionalType;

    /**
     * Instantiates a new V 8 value error.
     *
     * @param v8Runtime the v 8 runtime
     * @param handle    the handle
     * @throws JavetException the javet exception
     * @since 1.0.7
     */
    V8ValueError(V8Runtime v8Runtime, long handle) throws JavetException {
        super(v8Runtime, handle);
        optionalType = null;
    }

    /**
     * Gets type.
     *
     * @return the type
     * @since 3.0.4
     */
    public V8ValueErrorType getErrorType() {
        if (optionalType == null) {
            optionalType = V8ValueErrorType.UnknownError;
            try (V8Value v8Value = get(METHOD_NAME_CONSTRUCTOR)) {
                String constructorFunction = v8Value.toString();
                if (StringUtils.isNotEmpty(constructorFunction)) {
                    final int startIndex = constructorFunction.indexOf(" ") + 1;
                    final int endIndex = constructorFunction.indexOf("(");
                    if (startIndex > 0 && endIndex > startIndex) {
                        String constructorName = constructorFunction.substring(startIndex, endIndex);
                        optionalType = V8ValueErrorType.parse(constructorName);
                    }
                }
            } catch (Throwable ignored) {
                ignored.printStackTrace(System.err);
            }
        }
        return optionalType;
    }

    /**
     * Gets message.
     *
     * @return the message
     * @throws JavetException the javet exception
     * @since 1.1.6
     */
    public String getMessage() throws JavetException {
        return getPropertyString(MESSAGE);
    }

    /**
     * Gets stack.
     *
     * @return the stack
     * @throws JavetException the javet exception
     * @since 1.1.6
     */
    public String getStack() throws JavetException {
        return getPropertyString(STACK);
    }

    @Override
    public V8ValueReferenceType getType() {
        return V8ValueReferenceType.Error;
    }

    /**
     * Sets stack.
     *
     * @param stack the stack
     * @return the stack
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public boolean setStack(String stack) throws JavetException {
        return setProperty(STACK, stack);
    }
}
