/*
 * Copyright (c) 2021-2023. caoccao.com Sam Cao
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
import com.caoccao.javet.utils.JavetStringUtils;
import com.caoccao.javet.utils.SimpleMap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
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
    public static final String LIB_VERSION = "3.0.1";
    private static final String ANDROID_ABI_ARM = "armeabi-v7a";
    private static final String ANDROID_ABI_ARM64 = "arm64-v8a";
    private static final String ANDROID_ABI_X86 = "x86";
    private static final String ANDROID_ABI_X86_64 = "x86_64";
    private static final String ARCH_ARM = "arm";
    private static final String ARCH_ARM64 = "arm64";
    private static final String ARCH_X86 = "x86";
    private static final String ARCH_X86_64 = "x86_64";
    private static final int BUFFER_LENGTH = 4096;
    private static final String CHMOD = "chmod";
    private static final String DOT = ".";
    private static final String LIB_FILE_EXTENSION_ANDROID = "so";
    private static final String LIB_FILE_EXTENSION_LINUX = "so";
    private static final String LIB_FILE_EXTENSION_MACOS = "dylib";
    private static final String LIB_FILE_EXTENSION_WINDOWS = "dll";
    private static final String LIB_FILE_NAME_FORMAT = "libjavet-{0}-{1}-{2}.v.{3}.{4}";
    private static final String LIB_FILE_NAME_FOR_ANDROID_FORMAT = "libjavet-{0}-{1}.v.{2}.{3}";
    private static final String LIB_FILE_NAME_PREFIX = "lib";
    private static final IJavetLogger LOGGER = new JavetDefaultLogger(JavetLibLoader.class.getName());
    private static final long MIN_LAST_MODIFIED_GAP_IN_MILLIS = 60L * 1000L; // 1 minute
    private static final String OS_ANDROID = "android";
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
                    if (JavetOSUtils.IS_LINUX || JavetOSUtils.IS_MACOS || JavetOSUtils.IS_ANDROID) {
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

    private String getAndroidABI() {
        if (JavetOSUtils.IS_ANDROID) {
            if (JavetOSUtils.IS_ARM) {
                return ANDROID_ABI_ARM;
            } else if (JavetOSUtils.IS_ARM64) {
                return ANDROID_ABI_ARM64;
            } else if (JavetOSUtils.IS_X86) {
                return ANDROID_ABI_X86;
            } else if (JavetOSUtils.IS_X86_64) {
                return ANDROID_ABI_X86_64;
            }
        }
        return null;
    }

    private String getFileExtension() {
        if (JavetOSUtils.IS_WINDOWS) {
            return LIB_FILE_EXTENSION_WINDOWS;
        } else if (JavetOSUtils.IS_LINUX) {
            return LIB_FILE_EXTENSION_LINUX;
        } else if (JavetOSUtils.IS_MACOS) {
            return LIB_FILE_EXTENSION_MACOS;
        } else if (JavetOSUtils.IS_ANDROID) {
            return LIB_FILE_EXTENSION_ANDROID;
        }
        return null;
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
        String fileExtension = getFileExtension();
        String osName = getOSName();
        if (fileExtension == null || osName == null) {
            throw new JavetException(
                    JavetError.OSNotSupported,
                    SimpleMap.of(JavetError.PARAMETER_OS, JavetOSUtils.OS_NAME));
        }
        if (JavetOSUtils.IS_ANDROID) {
            return MessageFormat.format(
                    LIB_FILE_NAME_FOR_ANDROID_FORMAT,
                    jsRuntimeType.getName(),
                    osName,
                    LIB_VERSION,
                    fileExtension);
        } else {
            String osArch = getOSArch();
            if (osArch == null) {
                throw new JavetException(
                        JavetError.OSNotSupported,
                        SimpleMap.of(JavetError.PARAMETER_OS, JavetOSUtils.OS_ARCH));
            }
            return MessageFormat.format(
                    LIB_FILE_NAME_FORMAT,
                    jsRuntimeType.getName(),
                    osName,
                    osArch,
                    LIB_VERSION,
                    fileExtension);
        }
    }

    private String getNormalizedLibFilePath(String libFilePath) {
        boolean prefixToBeNormalized = false;
        if (JavetOSUtils.IS_LINUX) {
            prefixToBeNormalized = true;
            if (libFilePath.endsWith(DOT + LIB_FILE_EXTENSION_LINUX)) {
                libFilePath = libFilePath.substring(
                        0, libFilePath.length() - DOT.length() - LIB_FILE_EXTENSION_LINUX.length());
            }
        } else if (JavetOSUtils.IS_ANDROID) {
            prefixToBeNormalized = true;
            if (libFilePath.endsWith(DOT + LIB_FILE_EXTENSION_ANDROID)) {
                libFilePath = libFilePath.substring(
                        0, libFilePath.length() - DOT.length() - LIB_FILE_EXTENSION_ANDROID.length());
            }
        } else if (JavetOSUtils.IS_MACOS) {
            prefixToBeNormalized = true;
            if (libFilePath.endsWith(DOT + LIB_FILE_EXTENSION_MACOS)) {
                libFilePath = libFilePath.substring(
                        0, libFilePath.length() - DOT.length() - LIB_FILE_EXTENSION_MACOS.length());
            }
        }
        if (prefixToBeNormalized && libFilePath.startsWith(LIB_FILE_NAME_PREFIX)) {
            libFilePath = libFilePath.substring(LIB_FILE_NAME_PREFIX.length());
        }
        return libFilePath;
    }

    private String getOSArch() {
        if (JavetOSUtils.IS_WINDOWS) {
            return ARCH_X86_64;
        } else if (JavetOSUtils.IS_LINUX) {
            return JavetOSUtils.IS_ARM64 ? ARCH_ARM64 : ARCH_X86_64;
        } else if (JavetOSUtils.IS_MACOS) {
            return JavetOSUtils.IS_ARM64 ? ARCH_ARM64 : ARCH_X86_64;
        } else if (JavetOSUtils.IS_ANDROID) {
            if (JavetOSUtils.IS_ARM) {
                return ARCH_ARM;
            } else if (JavetOSUtils.IS_ARM64) {
                return ARCH_ARM64;
            } else if (JavetOSUtils.IS_X86) {
                return ARCH_X86;
            } else if (JavetOSUtils.IS_X86_64) {
                return ARCH_X86_64;
            }
        }
        return null;
    }

    private String getOSName() {
        if (JavetOSUtils.IS_WINDOWS) {
            return OS_WINDOWS;
        } else if (JavetOSUtils.IS_LINUX) {
            return OS_LINUX;
        } else if (JavetOSUtils.IS_MACOS) {
            return OS_MACOS;
        } else if (JavetOSUtils.IS_ANDROID) {
            return OS_ANDROID;
        }
        return null;
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
        String resourceFileName = MessageFormat.format(RESOURCE_NAME_FORMAT, JavetOSUtils.IS_ANDROID
                ? JavetStringUtils.join("/", LIB_FILE_NAME_PREFIX, getAndroidABI(), getLibFileName())
                : getLibFileName());
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
                    File libPath = libLoadingListener.getLibPath(jsRuntimeType);
                    Objects.requireNonNull(libPath, "Lib path cannot be null");
                    String resourceFileName = getResourceFileName();
                    File rootLibPath;
                    if (JavetOSUtils.IS_ANDROID) {
                        rootLibPath = libPath;
                    } else {
                        rootLibPath = new File(libPath, Long.toString(JavetOSUtils.PROCESS_ID));
                    }
                    if (!rootLibPath.exists()) {
                        if (!rootLibPath.mkdirs()) {
                            LOGGER.logError("Failed to create {0}.", rootLibPath.getAbsolutePath());
                        }
                    }
                    purge(libPath);
                    File libFile = new File(rootLibPath, getLibFileName()).getAbsoluteFile();
                    deployLibFile(resourceFileName, libFile);
                    libFilePath = libFile.getAbsolutePath();
                } else {
                    File libPath = libLoadingListener.getLibPath(jsRuntimeType);
                    Objects.requireNonNull(libPath, "Lib path cannot be null");
                    libFilePath = new File(libPath, getLibFileName()).getAbsolutePath();
                }
                try {
                    if (isLibInSystemPath) {
                        System.loadLibrary(getNormalizedLibFilePath(libFilePath));
                    } else {
                        System.load(libFilePath);
                    }
                    loaded = true;
                } catch (Throwable t) {
                    if (libLoadingListener.isSuppressingError(jsRuntimeType)) {
                        LOGGER.warn(t.getMessage());
                        loaded = true;
                    } else {
                        throw t;
                    }
                }
            } catch (Throwable t) {
                LOGGER.logError(t, t.getMessage());
                throw new JavetException(
                        JavetError.FailedToReadPath,
                        SimpleMap.of(JavetError.PARAMETER_PATH, libFilePath),
                        t);
            }
        }
    }

    private void purge(File rootLibPath) {
        try {
            if (rootLibPath.exists()) {
                if (rootLibPath.isDirectory()) {
                    File[] files = rootLibPath.listFiles();
                    if (files != null && files.length > 0) {
                        for (File libFileOrPath : files) {
                            if (libFileOrPath.lastModified() + MIN_LAST_MODIFIED_GAP_IN_MILLIS > System.currentTimeMillis()) {
                                continue;
                            }
                            boolean toBeDeleted = false;
                            if (libFileOrPath.isDirectory()) {
                                try {
                                    File[] libFiles = libFileOrPath.listFiles();
                                    if (libFiles != null && libFiles.length > 0) {
                                        for (File libFile : libFiles) {
                                            if (libFile.delete()) {
                                                LOGGER.logDebug("Deleted {0}.", libFile.getAbsolutePath());
                                            } else {
                                                LOGGER.logDebug("{0} is locked.", libFile.getAbsolutePath());
                                                toBeDeleted = true;
                                                break;
                                            }
                                        }
                                    } else {
                                        toBeDeleted = true;
                                    }
                                } catch (Throwable t) {
                                    LOGGER.logError(t, "Failed to delete {0}.", libFileOrPath.getAbsolutePath());
                                }
                            } else if (libFileOrPath.isFile()) {
                                toBeDeleted = true;
                            }
                            if (toBeDeleted) {
                                if (libFileOrPath.delete()) {
                                    LOGGER.logDebug("Deleted {0}.", libFileOrPath.getAbsolutePath());
                                } else {
                                    LOGGER.logDebug("{0} is locked.", libFileOrPath.getAbsolutePath());
                                }
                            }
                        }
                    }
                } else {
                    if (!rootLibPath.delete()) {
                        LOGGER.logError("Failed to delete {0}.", rootLibPath.getAbsolutePath());
                    }
                }
            }
        } catch (Throwable t) {
            LOGGER.logError(t, "Failed to clean up {0}.", rootLibPath.getAbsolutePath());
        }
    }
}
