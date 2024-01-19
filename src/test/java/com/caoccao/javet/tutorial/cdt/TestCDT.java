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

package com.caoccao.javet.tutorial.cdt;

/*
This is a sample application that demonstrates how Javet works with Chrome Developer Tools (aka. CDT).

Usage:
1. Run this application and the following 2 service endpoints will be open.
    http://localhost:9229/json
    ws://localhost:9229/javet
2. Open URL "chrome://inspect/" in Chrome.
3. Wait a few seconds and click "Javet" to open CDT.
 */
public class TestCDT {
    public static void main(String[] args) {
        CDTShell cdtShell = new CDTShell();
        cdtShell.run();
    }
}
