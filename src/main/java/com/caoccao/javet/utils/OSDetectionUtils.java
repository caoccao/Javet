/*
 * Copyright (c) 2020 - 2021. caoccao.com Sam Cao
 * All rights reserved.
 */

package com.caoccao.javet.utils;

public final class OSDetectionUtils {
    public static final String OS_NAME = System.getProperty("os.name");
    public static final boolean IS_LINUX = OS_NAME.startsWith("Linux");
    public static final boolean IS_WINDOWS = OS_NAME.startsWith("Windows");

    private OSDetectionUtils() {}
}
