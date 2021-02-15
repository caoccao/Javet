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

import java.text.MessageFormat;

public class JavetV8CallbackSignatureMismatchException extends JavetException {
    protected JavetV8CallbackSignatureMismatchException(String format, Object... objects) {
        super(MessageFormat.format("V8 callback signature mismatches: " + format, objects));
    }

    protected JavetV8CallbackSignatureMismatchException(String message, Throwable cause) {
        super(message, cause);
    }

    public static JavetV8CallbackSignatureMismatchException parameterSizeMismatch(
            int expectedSize, int actualSize) {
        return new JavetV8CallbackSignatureMismatchException(
                "expected parameter size is {0}, actual parameter size is {1}",
                expectedSize, actualSize);
    }

    public static JavetV8CallbackSignatureMismatchException parameterTypeMismatch(
            Class expectedType, Class actualType) {
        return new JavetV8CallbackSignatureMismatchException(
                "expected parameter type is {0}, actual parameter type is {1}",
                expectedType.getName(), actualType.getName());
    }

    public static JavetV8CallbackSignatureMismatchException unknown(
            Throwable cause) {
        return new JavetV8CallbackSignatureMismatchException(cause.getMessage(), cause);
    }
}
