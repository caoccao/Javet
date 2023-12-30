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

import java.util.Map;

/**
 * The type Javet converter exception is for JNI.
 *
 * @since 0.7.0
 */
public class JavetConverterException extends JavetException {
    /**
     * Instantiates a new Javet converter exception.
     *
     * @param error      the error
     * @param parameters the parameters
     * @since 0.9.3
     */
    protected JavetConverterException(JavetError error, Map<String, Object> parameters) {
        super(error, parameters);
    }

    /**
     * Instantiates a new Javet converter exception.
     *
     * @param message the message
     * @since 0.9.3
     */
    public JavetConverterException(String message) {
        this(JavetError.ConverterFailure, SimpleMap.of(JavetError.PARAMETER_MESSAGE, message));
    }

    /**
     * Circular structure.
     *
     * @param maxDepth the max depth
     * @return the javet converter exception
     * @since 0.9.3
     */
    public static JavetConverterException circularStructure(int maxDepth) {
        return new JavetConverterException(
                JavetError.ConverterCircularStructure,
                SimpleMap.of(JavetError.PARAMETER_MAX_DEPTH, maxDepth));
    }
}
