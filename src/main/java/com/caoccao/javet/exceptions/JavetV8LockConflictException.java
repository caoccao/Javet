/*
 *   Copyright (c) 2021. caoccao.com Sam Cao
 *   All rights reserved.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.caoccao.javet.exceptions;

import com.caoccao.javet.utils.SimpleMap;

/**
 * The type Javet v 8 lock conflict exception.
 */
public class JavetV8LockConflictException extends JavetException {

    /**
     * The constant PARAMETER_MESSAGE.
     */
    public static final String PARAMETER_MESSAGE = "message";
    /**
     * The constant PARAMETER_LOCKED_THREAD_ID.
     */
    public static final String PARAMETER_LOCKED_THREAD_ID = "lockedThreadId";
    /**
     * The constant PARAMETER_CURRENT_THREAD_ID.
     */
    public static final String PARAMETER_CURRENT_THREAD_ID = "currentThreadId";

    /**
     * Instantiates a new Javet V8 lock conflict exception.
     * It is for JNI.
     *
     * @param message the message
     */
    public JavetV8LockConflictException(String message) {
        super(JavetError.LockConflict, SimpleMap.of(PARAMETER_MESSAGE, message));
    }

    /**
     * Instantiates a new Javet v 8 lock conflict exception.
     *
     * @param lockedThreadId  the locked thread id
     * @param currentThreadId the current thread id
     */
    public JavetV8LockConflictException(long lockedThreadId, long currentThreadId) {
        super(JavetError.ThreadIdMismatch, SimpleMap.of(
                PARAMETER_LOCKED_THREAD_ID, Long.toString(lockedThreadId),
                PARAMETER_CURRENT_THREAD_ID, Long.toString(currentThreadId)));
    }
}
