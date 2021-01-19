/*
 * Copyright (c) 2020 - 2021. caoccao.com Sam Cao
 * All rights reserved.
 */

package com.caoccao.javet.interop;

import com.caoccao.javet.exceptions.JavetOSNotSupportedException;
import com.caoccao.javet.utils.OSDetectionUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.MessageFormat;

final class JavetLibLoader {
    public static final String CHMOD = "chmod";
    public static final String XRR = "755";
    private static final String TEMP_DIRECTORY = System.getProperty("java.io.tmpdir");
    private static final String FILE_SEPARATOR = System.getProperty("file.separator");
    private static final String LIB_VERSION = "0.7.0";
    private static final String LIB_FILE_NAME_FORMAT = "javet-{0}-x86_64.v.{1}.{2}";
    private static final String RESOURCE_NAME_FORMAT = "/{0}";
    private static final String LIB_FILE_EXTENSION_LINUX = "so";
    private static final String LIB_FILE_EXTENSION_WINDOWS = "dll";
    private static final String OS_LINUX = "linux";
    private static final String OS_WINDOWS = "windows";
    private static int BUFFER_LENGTH = 4096;

    private static Object lockObject = new Object();
    private static boolean javetLibLoaded = false;

    private JavetLibLoader() {
    }

    static boolean load() throws JavetOSNotSupportedException {
        if (!javetLibLoaded) {
            synchronized (lockObject) {
                if (!javetLibLoaded) {
                    internalLoad();
                    javetLibLoaded = true;
                }
            }
        }
        return javetLibLoaded;
    }

    private static boolean deployLibFile(File libFile) {
        boolean isDeployed = false;
        boolean isLibFileLocked = false;
        if (libFile.exists()) {
            try {
                libFile.delete();
            } catch (Exception e) {
                isLibFileLocked = true;
            }
        }
        if (!isLibFileLocked) {
            byte[] buffer = new byte[BUFFER_LENGTH];
            String resourceName = MessageFormat.format(RESOURCE_NAME_FORMAT, libFile.getName());
            try (InputStream inputStream = JavetLibLoader.class.getResourceAsStream(resourceName);
                 FileOutputStream outputStream = new FileOutputStream(libFile.getAbsolutePath())) {
                while (true) {
                    int length = inputStream.read(buffer);
                    if (length == -1) {
                        break;
                    }
                    outputStream.write(buffer, 0, length);
                }
                isDeployed = true;
            } catch (Exception e) {
                // Lib file is locked.
            }
            if (isDeployed && OSDetectionUtils.IS_LINUX) {
                try {
                    Runtime.getRuntime().exec(new String[]{CHMOD, XRR, libFile.getAbsolutePath()}).waitFor();
                } catch (Throwable e) {
                }
            }
        }
        return isDeployed;
    }

    private static File getLibFile() throws JavetOSNotSupportedException {
        if (OSDetectionUtils.IS_WINDOWS) {
            return new File(TEMP_DIRECTORY, MessageFormat.format(LIB_FILE_NAME_FORMAT,
                    OS_WINDOWS, LIB_VERSION, LIB_FILE_EXTENSION_WINDOWS));
        } else if (OSDetectionUtils.IS_LINUX) {
            return new File(TEMP_DIRECTORY, MessageFormat.format(LIB_FILE_NAME_FORMAT,
                    OS_LINUX, LIB_VERSION, LIB_FILE_EXTENSION_LINUX));
        } else {
            throw new JavetOSNotSupportedException(OSDetectionUtils.OS_NAME);
        }
    }

    private static void internalLoad() throws JavetOSNotSupportedException {
        File libFile = getLibFile();
        deployLibFile(libFile);
        System.load(libFile.getAbsolutePath());
    }
}
