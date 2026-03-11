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

package com.caoccao.javet.utils;

import java.lang.management.ManagementFactory;

/**
 * Utility class providing OS and platform detection constants and methods.
 */
public final class JavetOSUtils {
    /**
     * The OS architecture string from the {@code os.arch} system property.
     */
    public static final String OS_ARCH = System.getProperty("os.arch");
    /**
     * The OS name string from the {@code os.name} system property.
     */
    public static final String OS_NAME = System.getProperty("os.name");
    /**
     * The Java VM name string from the {@code java.vm.name} system property.
     */
    public static final String JAVA_VM_NAME = System.getProperty("java.vm.name");
    /**
     * Whether the current platform is Android (Dalvik VM).
     */
    public static final boolean IS_ANDROID = JAVA_VM_NAME.startsWith("Dalvik");
    /**
     * Whether the current platform is Linux (and not Android).
     */
    public static final boolean IS_LINUX = OS_NAME.startsWith("Linux") && !IS_ANDROID;
    /**
     * Whether the current platform is macOS (and not Android).
     */
    public static final boolean IS_MACOS = OS_NAME.startsWith("Mac OS") && !IS_ANDROID;
    /**
     * Whether the current platform is Windows (and not Android).
     */
    public static final boolean IS_WINDOWS = OS_NAME.startsWith("Windows") && !IS_ANDROID;
    /**
     * Whether the current architecture is 32-bit ARM.
     */
    public static final boolean IS_ARM =
            OS_ARCH.startsWith("armeabi") || OS_ARCH.startsWith("armv7") ||
                    OS_ARCH.startsWith("arm32") || OS_ARCH.equals("arm");
    /**
     * Whether the current architecture is 64-bit ARM (aarch64).
     */
    public static final boolean IS_ARM64 =
            OS_ARCH.startsWith("arm64") || OS_ARCH.startsWith("armv8") ||
                    OS_ARCH.equals("aarch64");
    /**
     * Whether the current architecture is 32-bit x86.
     */
    public static final boolean IS_X86 = OS_ARCH.matches("^(x86_32|x8632|x86|i[3-6]86|ia32|x32)$");
    /**
     * Whether the current architecture is 64-bit x86 (x86_64/amd64).
     */
    public static final boolean IS_X86_64 = OS_ARCH.matches("^(x86_64|x8664|amd64|ia32e|em64t|x64)$");
    /**
     * The current process ID.
     */
    public static final long PROCESS_ID;
    /**
     * The platform-specific line separator string.
     */
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");
    /**
     * The system temporary directory path.
     */
    public static final String TEMP_DIRECTORY = System.getProperty("java.io.tmpdir");
    /**
     * The current working directory path.
     */
    public static final String WORKING_DIRECTORY = System.getProperty("user.dir");

    static {
        /* if defined ANDROID
        PROCESS_ID = 1L;
        /* end if */
        /* if not defined ANDROID */
        String processName = ManagementFactory.getRuntimeMXBean().getName();
        int positionOfSeparator = processName.indexOf("@");
        if (positionOfSeparator > 0) {
            processName = processName.substring(0, positionOfSeparator);
        }
        PROCESS_ID = Long.parseLong(processName);
        /* end if */
    }

    private JavetOSUtils() {
    }

    /**
     * Gets the number of available CPU processors.
     *
     * @return the number of available processors
     */
    public static int getCPUCount() {
        return Runtime.getRuntime().availableProcessors();
    }
}
