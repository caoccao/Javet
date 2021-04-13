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
import java.util.Objects;

public final class JavetLibLoader {
    static final String LIB_VERSION = "0.8.3";
    private static final String CHMOD = "chmod";
    private static final String XRR = "755";
    private static final String LIB_FILE_NAME_FORMAT = "libjavet-{0}-{1}-x86_64.v.{2}.{3}";
    private static final String RESOURCE_NAME_FORMAT = "/{0}";
    private static final String LIB_FILE_EXTENSION_LINUX = "so";
    private static final String LIB_FILE_EXTENSION_WINDOWS = "dll";
    private static final String OS_LINUX = "linux";
    private static final String OS_WINDOWS = "windows";
    private static final int BUFFER_LENGTH = 4096;
    private JSRuntimeType jsRuntimeType;
    private boolean loaded;

    public JavetLibLoader(JSRuntimeType jsRuntimeType) {
        Objects.requireNonNull(jsRuntimeType);
        this.jsRuntimeType = jsRuntimeType;
        loaded = false;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public JSRuntimeType getJSRuntimeType() {
        return jsRuntimeType;
    }

    private void deployLibFile(File libFile) {
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
                if (JavetOSUtils.IS_LINUX) {
                    try {
                        Runtime.getRuntime().exec(new String[]{CHMOD, XRR, libFile.getAbsolutePath()}).waitFor();
                    } catch (Throwable e) {
                    }
                }
            } catch (Exception e) {
                // Lib file is locked.
            }
        }
    }

    public File getLibFile(String rootDirectory)
            throws JavetOSNotSupportedException, JavetLibraryNotFoundException {
        String fileName, resourceFileName, osName, fileExtension;
        if (JavetOSUtils.IS_WINDOWS) {
            fileExtension = LIB_FILE_EXTENSION_WINDOWS;
            osName = OS_WINDOWS;
        } else if (JavetOSUtils.IS_LINUX) {
            fileExtension = LIB_FILE_EXTENSION_LINUX;
            osName = OS_LINUX;
        } else {
            throw new JavetOSNotSupportedException(JavetOSUtils.OS_NAME);
        }
        fileName = MessageFormat.format(LIB_FILE_NAME_FORMAT,
                jsRuntimeType.getName(), osName, LIB_VERSION, fileExtension);
        resourceFileName = MessageFormat.format(RESOURCE_NAME_FORMAT, fileName);
        if (JavetLibLoader.class.getResource(resourceFileName) != null) {
            return new File(rootDirectory, fileName);
        }
        throw new JavetLibraryNotFoundException(resourceFileName);
    }

    public void load()
            throws JavetOSNotSupportedException, JavetIOException, JavetLibraryNotFoundException {
        if (!loaded) {
            File tempDirectoryLibFile = getLibFile(JavetOSUtils.TEMP_DIRECTORY);
            try {
                deployLibFile(tempDirectoryLibFile);
                System.load(tempDirectoryLibFile.getAbsolutePath());
                loaded = true;
            } catch (Throwable t) {
                t.printStackTrace(System.err);
                throw JavetIOException.failedToReadPath(tempDirectoryLibFile.toPath(), t);
            }
        }
    }
}
