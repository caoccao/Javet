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

package com.caoccao.javet.interop;

import com.caoccao.javet.exceptions.JavetIOException;
import com.caoccao.javet.exceptions.JavetLibraryNotFoundException;
import com.caoccao.javet.exceptions.JavetOSNotSupportedException;
import com.caoccao.javet.utils.JavetOSUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.MessageFormat;

final class JavetLibLoader {
    static final String LIB_VERSION = "0.8.0";
    // Node takes higher priority over V8.
    private static final JSRuntimeType[] SUPPORTED_JS_RUNTIME_TYPES =
            new JSRuntimeType[]{JSRuntimeType.Node, JSRuntimeType.V8};
    private static final String CHMOD = "chmod";
    private static final String XRR = "755";
    private static final String LIB_FILE_NAME_FORMAT = "libjavet-{0}-{1}-x86_64.v.{2}.{3}";
    private static final String RESOURCE_NAME_FORMAT = "/{0}";
    private static final String LIB_FILE_EXTENSION_LINUX = "so";
    private static final String LIB_FILE_EXTENSION_WINDOWS = "dll";
    private static final String OS_LINUX = "linux";
    private static final String OS_WINDOWS = "windows";
    private static final int BUFFER_LENGTH = 4096;
    private static JSRuntimeType JS_RUNTIME_TYPE = null;
    private static Object lockObject = new Object();
    private static boolean javetLibLoaded = false;

    private JavetLibLoader() {
    }

    public static JSRuntimeType getJSRuntimeType() {
        return JS_RUNTIME_TYPE;
    }

    static boolean load()
            throws JavetOSNotSupportedException, JavetIOException, JavetLibraryNotFoundException {
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
            if (isDeployed && JavetOSUtils.IS_LINUX) {
                try {
                    Runtime.getRuntime().exec(new String[]{CHMOD, XRR, libFile.getAbsolutePath()}).waitFor();
                } catch (Throwable e) {
                }
            }
        }
        return isDeployed;
    }

    private static File getLibFile(String rootDirectory)
            throws JavetOSNotSupportedException, JavetLibraryNotFoundException {
        String fileName, osName, fileExtension;
        if (JavetOSUtils.IS_WINDOWS) {
            fileExtension = LIB_FILE_EXTENSION_WINDOWS;
            osName = OS_WINDOWS;
        } else if (JavetOSUtils.IS_LINUX) {
            fileExtension = LIB_FILE_EXTENSION_LINUX;
            osName = OS_LINUX;
        } else {
            throw new JavetOSNotSupportedException(JavetOSUtils.OS_NAME);
        }
        for (final JSRuntimeType jsRuntimeType : SUPPORTED_JS_RUNTIME_TYPES) {
            fileName = MessageFormat.format(LIB_FILE_NAME_FORMAT,
                    jsRuntimeType.getName(), osName, LIB_VERSION, fileExtension);
            if (JavetLibLoader.class.getResource(MessageFormat.format(RESOURCE_NAME_FORMAT, fileName)) != null) {
                JS_RUNTIME_TYPE = jsRuntimeType;
                return new File(rootDirectory, fileName);
            }
        }
        throw new JavetLibraryNotFoundException();
    }

    private static void internalLoad()
            throws JavetOSNotSupportedException, JavetIOException, JavetLibraryNotFoundException {
        File tempDirectoryLibFile = getLibFile(JavetOSUtils.TEMP_DIRECTORY);
        try {
            deployLibFile(tempDirectoryLibFile);
            System.load(tempDirectoryLibFile.getAbsolutePath());
        } catch (Throwable t) {
            throw JavetIOException.failedToReadPath(tempDirectoryLibFile.toPath(), t);
        }
    }
}
