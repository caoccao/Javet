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

import java.util.Map;

/**
 * The type Javet exception.
 *
 * @since 0.7.0
 */
public class JavetException extends Exception {
    /**
     * The Error.
     *
     * @since 0.8.5
     */
    protected JavetError error;
    /**
     * The Parameters.
     *
     * @since 0.8.5
     */
    protected Map<String, Object> parameters;

    /**
     * Instantiates a new Javet exception.
     *
     * @param error the error
     * @since 0.8.5
     */
    public JavetException(JavetError error) {
        this(error, null, null);
    }

    /**
     * Instantiates a new Javet exception.
     *
     * @param error      the error
     * @param parameters the parameters
     * @since 0.8.5
     */
    public JavetException(JavetError error, Map<String, Object> parameters) {
        this(error, parameters, null);
    }

    /**
     * Instantiates a new Javet exception.
     *
     * @param error the error
     * @param cause the cause
     * @since 0.8.5
     */
    public JavetException(JavetError error, Throwable cause) {
        this(error, null, cause);
    }

    /**
     * Instantiates a new Javet exception.
     *
     * @param error      the error
     * @param parameters the parameters
     * @param cause      the cause
     * @since 0.8.5
     */
    public JavetException(JavetError error, Map<String, Object> parameters, Throwable cause) {
        super(error.getMessage(parameters), cause);
        this.error = error;
        this.parameters = parameters;
    }

    /**
     * Gets error.
     *
     * @return the error
     * @since 0.8.5
     */
    public JavetError getError() {
        return error;
    }

    /**
     * Gets parameters.
     *
     * @return the parameters
     * @since 0.8.5
     */
    public Map<String, Object> getParameters() {
        return parameters;
    }
}
