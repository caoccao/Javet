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

package com.caoccao.javet.interfaces;

import java.text.MessageFormat;

/**
 * The interface Javet logger.
 * @since 0.7.0
 */
public interface IJavetLogger {
    /**
     * Debug.
     *
     * @param message the message
     * @since 0.7.0
     */
    void debug(String message);

    /**
     * Error.
     *
     * @param message the message
     * @since 0.7.0
     */
    void error(String message);

    /**
     * Error.
     *
     * @param message the message
     * @param cause   the cause
     * @since 0.7.0
     */
    void error(String message, Throwable cause);

    /**
     * Info.
     *
     * @param message the message
     * @since 0.7.0
     */
    void info(String message);

    /**
     * Log debug.
     *
     * @param format  the format
     * @param objects the objects
     * @since 0.7.0
     */
    default void logDebug(String format, Object... objects) {
        debug(MessageFormat.format(format, objects));
    }

    /**
     * Log error.
     *
     * @param cause   the cause
     * @param format  the format
     * @param objects the objects
     * @since 0.7.0
     */
    default void logError(Throwable cause, String format, Object... objects) {
        error(MessageFormat.format(format, objects), cause);
    }

    /**
     * Log error.
     *
     * @param format  the format
     * @param objects the objects
     * @since 0.7.0
     */
    default void logError(String format, Object... objects) {
        error(MessageFormat.format(format, objects));
    }

    /**
     * Log info.
     *
     * @param format  the format
     * @param objects the objects
     * @since 0.7.0
     */
    default void logInfo(String format, Object... objects) {
        info(MessageFormat.format(format, objects));
    }

    /**
     * Log warn.
     *
     * @param format  the format
     * @param objects the objects
     * @since 0.7.0
     */
    default void logWarn(String format, Object... objects) {
        warn(MessageFormat.format(format, objects));
    }

    /**
     * Warn.
     *
     * @param message the message
     * @since 0.7.0
     */
    void warn(String message);
}
