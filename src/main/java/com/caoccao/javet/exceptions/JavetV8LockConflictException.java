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

import java.text.MessageFormat;

public class JavetV8LockConflictException extends JavetException {
    public JavetV8LockConflictException(String message) {
        super(message);
    }

    public static JavetV8LockConflictException threadIdMismatch(long lockedThreadId, long currentThreadId) {
        return new JavetV8LockConflictException(MessageFormat.format(
                "V8 runtime lock conflict is detected with locked thread ID {0} and current thread ID {1}",
                Long.toString(lockedThreadId), Long.toString(currentThreadId)));
    }
}
