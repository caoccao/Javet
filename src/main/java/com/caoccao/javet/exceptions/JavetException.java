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

import java.util.Map;

public class JavetException extends Exception {
    protected JavetError error;
    protected Map<String, Object> parameters;

    public JavetException(JavetError error) {
        this(error, null, null);
    }

    public JavetException(JavetError error, Map<String, Object> parameters) {
        this(error, parameters, null);
    }

    public JavetException(JavetError error, Throwable cause) {
        this(error, null, cause);
    }

    public JavetException(JavetError error, Map<String, Object> parameters, Throwable cause) {
        super(error.getMessage(parameters), cause);
        this.error = error;
        this.parameters = parameters;
    }

    public JavetError getError() {
        return error;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }
}
