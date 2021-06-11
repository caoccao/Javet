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

package com.caoccao.javet.utils;

public final class JavetOSUtils {
    public static final String OS_NAME = System.getProperty("os.name");
    public static final boolean IS_LINUX = OS_NAME.startsWith("Linux");
    public static final boolean IS_WINDOWS = OS_NAME.startsWith("Windows");
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");
    public static final String TEMP_DIRECTORY = System.getProperty("java.io.tmpdir");
    public static final String WORKING_DIRECTORY = System.getProperty("user.dir");

    private JavetOSUtils() {
    }

    public static int getCPUCount() {
        return Runtime.getRuntime().availableProcessors();
    }
}
