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

package com.caoccao.javet.interfaces;

import java.text.MessageFormat;

public interface IJavetLogger {
    void debug(String message);

    void error(String message);

    void error(String message, Throwable cause);

    void info(String message);

    default void logDebug(String format, Object... objects) {
        debug(MessageFormat.format(format, objects));
    }

    default void logError(Throwable cause, String format, Object... objects) {
        error(MessageFormat.format(format, objects), cause);
    }

    default void logError(String format, Object... objects) {
        error(MessageFormat.format(format, objects));
    }

    default void logInfo(String format, Object... objects) {
        info(MessageFormat.format(format, objects));
    }

    default void logWarn(String format, Object... objects) {
        warn(MessageFormat.format(format, objects));
    }

    void warn(String message);
}
