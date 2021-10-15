/*
 * Copyright (c) 2021. caoccao.com Sam Cao
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

package com.caoccao.javet.interop.loader;

import com.caoccao.javet.enums.JSRuntimeType;
import com.caoccao.javet.exceptions.JavetError;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interfaces.IJavetLogger;
import com.caoccao.javet.utils.JavetDefaultLogger;
import com.caoccao.javet.utils.JavetOSUtils;
import com.caoccao.javet.utils.SimpleMap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.Objects;

/**
 * The type Javet lib loader is the one that actually loads the JNI library based on JS runtime type.
 * It is managed by a dedicated classloader (not the default classloader) so that unload is possible.
 * It is not supposed to be called directly by the applications via the default classloader.
 * <p>
 * It accepts a listener which can specify:
 * (1) To deploy the JNI library to a custom location to work around the permission issues.
 * (2) To skip the JNI library deployment to improve the performance and reduce the jar file size.
 *
 * @since 0.8.0
 */
public final class JavetLibLoader {
    /**
     * The constant LIB_VERSION.
     *
     * @since 0.8.0
     */
    public static final String LIB_VERSION = "1.0.1";
    private static final int BUFFER_LENGTH = 4096;
    private static final String CHMOD = "chmod";
    private static final String LIB_FILE_EXTENSION_LINUX = "so";
    private static final String LIB_FILE_EXTENSION_MACOS = "dylib";
    private static final String LIB_FILE_EXTENSION_WINDOWS = "dll";
    private static final String LIB_FILE_NAME_FORMAT = "libjavet-{0}-{1}-x86_64.v.{2}.{3}";
    private static final IJavetLogger LOGGER = new JavetDefaultLogger(JavetLibLoader.class.getName());
    private static final long MIN_LAST_MODIFIED_GAP_IN_MILLIS = 60L * 1000L; // 1 minute
    private static final String OS_LINUX = "linux";
    private static final String OS_MACOS = "macos";
    private static final String OS_WINDOWS = "windows";
    private static final String RESOURCE_NAME_FORMAT = "/{0}";
    private static final String XRR = "755";
    private static IJavetLibLoadingListener libLoadingListener = new JavetLibLoadingListener();
    private final JSRuntimeType jsRuntimeType;
    private volatile boolean loaded;

    /**
     * Instantiates a new Javet lib loader.
     *
     * @param jsRuntimeType the js runtime type
     * @since 0.8.0
     */
    public JavetLibLoader(JSRuntimeType jsRuntimeType) {
        Objects.requireNonNull(jsRuntimeType);
        this.jsRuntimeType = jsRuntimeType;
        loaded = false;
    }

    /**
     * Gets lib loading listener.
     *
     * @return the lib loading listener
     * @since 1.0.1
     */
    public static IJavetLibLoadingListener getLibLoadingListener() {
        return libLoadingListener;
    }

    /**
     * Sets lib loading listener.
     *
     * @param libLoadingListener the lib loading listener
     * @since 1.0.1
     */
    public static void setLibLoadingListener(IJavetLibLoadingListener libLoadingListener) {
        JavetLibLoader.libLoadingListener = Objects.requireNonNull(libLoadingListener);
    }

    private void deployLibFile(String resourceFileName, File libFile) {
        boolean isLibFileLocked = false;
        if (libFile.exists() && libFile.canWrite()) {
            try {
                //noinspection ResultOfMethodCallIgnored
                libFile.delete();
            } catch (Throwable t) {
                isLibFileLocked = true;
                LOGGER.logWarn("Failed to delete {0} because it is locked.", libFile.getAbsolutePath());
            }
        }
        if (!isLibFileLocked) {
            byte[] buffer = new byte[BUFFER_LENGTH];
            try (InputStream inputStream = JavetLibLoader.class.getResourceAsStream(resourceFileName);
                 FileOutputStream outputStream = new FileOutputStream(libFile.getAbsolutePath())) {
                if (inputStream != null) {
                    while (true) {
                        int length = inputStream.read(buffer);
                        if (length == -1) {
                            break;
                        }
                        outputStream.write(buffer, 0, length);
                    }
                    if (JavetOSUtils.IS_LINUX || JavetOSUtils.IS_MACOS) {
                        try {
                            Runtime.getRuntime().exec(new String[]{CHMOD, XRR, libFile.getAbsolutePath()}).waitFor();
                        } catch (Throwable ignored) {
                        }
                    }
                }
            } catch (Throwable t) {
                LOGGER.logWarn("Failed to write to {0} because it is locked.", libFile.getAbsolutePath());
            }
        }
    }

    /**
     * Gets js runtime type.
     *
     * @return the js runtime type
     * @since 0.8.0
     */
    public JSRuntimeType getJSRuntimeType() {
        return jsRuntimeType;
    }

    /**
     * Gets lib file name.
     *
     * @return the lib file name
     * @throws JavetException the javet exception
     * @since 1.0.1
     */
    public String getLibFileName() throws JavetException {
        String osName, fileExtension;
        if (JavetOSUtils.IS_WINDOWS) {
            fileExtension = LIB_FILE_EXTENSION_WINDOWS;
            osName = OS_WINDOWS;
        } else if (JavetOSUtils.IS_LINUX) {
            fileExtension = LIB_FILE_EXTENSION_LINUX;
            osName = OS_LINUX;
        } else if (JavetOSUtils.IS_MACOS) {
            fileExtension = LIB_FILE_EXTENSION_MACOS;
            osName = OS_MACOS;
        } else {
            throw new JavetException(
                    JavetError.OSNotSupported,
                    SimpleMap.of(JavetError.PARAMETER_OS, JavetOSUtils.OS_NAME));
        }
        return MessageFormat.format(LIB_FILE_NAME_FORMAT,
                jsRuntimeType.getName(), osName, LIB_VERSION, fileExtension);
    }

    /**
     * Gets resource file name.
     *
     * @return the resource file name
     * @throws JavetException the javet exception
     * @since 0.8.0
     */
    public String getResourceFileName()
            throws JavetException {
        String resourceFileName = MessageFormat.format(RESOURCE_NAME_FORMAT, getLibFileName());
        if (JavetLibLoader.class.getResource(resourceFileName) == null) {
            throw new JavetException(
                    JavetError.LibraryNotFound,
                    SimpleMap.of(JavetError.PARAMETER_PATH, resourceFileName));
        }
        return resourceFileName;
    }

    /**
     * Is loaded.
     *
     * @return true : loaded, false: not loaded
     * @since 0.8.0
     */
    public boolean isLoaded() {
        return loaded;
    }

    /**
     * Load.
     *
     * @throws JavetException the javet exception
     * @since 0.8.0
     */
    public void load() throws JavetException {
        if (!loaded) {
            String libFilePath = null;
            try {
                boolean isLibInSystemPath = libLoadingListener.isLibInSystemPath(jsRuntimeType);
                boolean isDeploy = libLoadingListener.isDeploy(jsRuntimeType);
                if (isLibInSystemPath) {
                    libFilePath = getLibFileName();
                } else if (isDeploy) {
                    Path libPath = libLoadingListener.getLibPath(jsRuntimeType);
                    Objects.requireNonNull(libPath, "Lib path cannot be null");
                    String resourceFileName = getResourceFileName();
                    File processIDPath = new File(libPath.toFile(), Long.toString(JavetOSUtils.PROCESS_ID));
                    if (!processIDPath.exists()) {
                        if (!processIDPath.mkdirs()) {
                            LOGGER.logError("Failed to create {0}.", processIDPath.getAbsolutePath());
                        }
                    }
                    purge(processIDPath);
                    File libFile = new File(processIDPath, getLibFileName()).getAbsoluteFile();
                    deployLibFile(resourceFileName, libFile);
                    libFilePath = libFile.getAbsolutePath();
                } else {
                    Path libPath = libLoadingListener.getLibPath(jsRuntimeType);
                    Objects.requireNonNull(libPath, "Lib path cannot be null");
                    libFilePath = new File(libPath.toFile(), getLibFileName()).getAbsolutePath();
                }
                if (isLibInSystemPath) {
                    System.loadLibrary(libFilePath);
                } else {
                    System.load(libFilePath);
                }
                loaded = true;
            } catch (Throwable t) {
                LOGGER.logError(t, t.getMessage());
                throw new JavetException(
                        JavetError.FailedToReadPath,
                        SimpleMap.of(JavetError.PARAMETER_PATH, libFilePath),
                        t);
            }
        }
    }

    private void purge(File libPath) {
        try {
            if (libPath.exists()) {
                if (libPath.isDirectory()) {
                    File[] files = libPath.listFiles();
                    if (files != null && files.length > 0) {
                        for (File tempProcessIDPath : files) {
                            if (tempProcessIDPath.isDirectory() &&
                                    tempProcessIDPath.lastModified() + MIN_LAST_MODIFIED_GAP_IN_MILLIS < System.currentTimeMillis()) {
                                try {
                                    boolean isLocked = false;
                                    File[] libFiles = tempProcessIDPath.listFiles();
                                    if (libFiles != null && libFiles.length > 0) {
                                        for (File libFile : libFiles) {
                                            if (libFile.delete()) {
                                                LOGGER.logDebug("Deleted {0}.", libFile.getAbsolutePath());
                                            } else {
                                                LOGGER.logDebug("{0} is locked.", libFile.getAbsolutePath());
                                                isLocked = true;
                                                break;
                                            }
                                        }
                                    }
                                    if (!isLocked) {
                                        if (tempProcessIDPath.delete()) {
                                            LOGGER.logDebug("Deleted {0}.", tempProcessIDPath.getAbsolutePath());
                                        } else {
                                            LOGGER.logDebug("{0} is locked.", tempProcessIDPath.getAbsolutePath());
                                        }
                                    }
                                } catch (Throwable t) {
                                    LOGGER.logError(t, "Failed to delete {0}.", tempProcessIDPath.getAbsolutePath());
                                }
                            }
                        }
                    }
                } else {
                    if (!libPath.delete()) {
                        LOGGER.logError("Failed to delete {0}.", libPath.getAbsolutePath());
                    }
                }
            }
        } catch (Throwable t) {
            LOGGER.logError(t, "Failed to clean up {0}.", libPath.getAbsolutePath());
        }
    }
}
