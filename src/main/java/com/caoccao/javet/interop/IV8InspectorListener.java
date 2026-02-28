/*
 * Copyright (c) 2021-2026. caoccao.com Sam Cao
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

package com.caoccao.javet.interop;

public interface IV8InspectorListener {

    /**
     * Called when JavaScript calls a console API method (e.g., {@code console.log()},
     * {@code console.warn()}, {@code console.error()}).
     *
     * @param contextGroupId the context group that produced the message
     * @param level          the message severity (1 = log, 2 = debug, 4 = info, 8 = error, 16 = warning)
     * @param message        the console message text
     * @param url            the source URL where the call was made
     * @param lineNumber     the 0-based line number
     * @param columnNumber   the 0-based column number
     * @since 5.0.5
     */
    default void consoleAPIMessage(
            int contextGroupId, int level, String message,
            String url, int lineNumber, int columnNumber) {
    }

    void flushProtocolNotifications();

    void receiveNotification(String message);

    void receiveResponse(String message);

    void runIfWaitingForDebugger(int contextGroupId);

    void sendRequest(String message);
}
