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

package com.caoccao.javet.interop.loader;

import com.caoccao.javet.enums.JSRuntimeType;
import com.caoccao.javet.utils.JavetOSUtils;

import java.io.File;

/**
 * The type Javet lib loading listener is the default one.
 *
 * @since 1.0.1
 */
public final class JavetLibLoadingListener implements IJavetLibLoadingListener {
    /**
     * The constant JAVET_LIB_LOADING_TYPE_DEFAULT.
     *
     * @since 1.0.3
     */
    public static final String JAVET_LIB_LOADING_TYPE_DEFAULT = "default";
    /**
     * The constant JAVET_LIB_LOADING_TYPE_CUSTOM.
     *
     * @since 1.0.3
     */
    public static final String JAVET_LIB_LOADING_TYPE_CUSTOM = "custom";
    /**
     * The constant JAVET_LIB_LOADING_TYPE_SYSTEM.
     *
     * @since 1.0.3
     */
    public static final String JAVET_LIB_LOADING_TYPE_SYSTEM = "system";
    /**
     * The constant PROPERTY_KEY_JAVET_LIB_LOADING_PATH.
     *
     * @since 1.0.3
     */
    public static final String PROPERTY_KEY_JAVET_LIB_LOADING_PATH = "javet.lib.loading.path";
    /**
     * The constant PROPERTY_KEY_JAVET_LIB_LOADING_TYPE.
     *
     * @since 1.0.3
     */
    public static final String PROPERTY_KEY_JAVET_LIB_LOADING_TYPE = "javet.lib.loading.type";
    /**
     * The constant PROPERTY_KEY_JAVET_LIB_LOADING_SUPPRESS_ERROR.
     *
     * @since 1.0.6
     */
    public static final String PROPERTY_KEY_JAVET_LIB_LOADING_SUPPRESS_ERROR = "javet.lib.loading.suppress.error";
    private static final String TEMP_ROOT_NAME = "javet";
    private final String javetLibLoadingPath;
    private final String javetLibLoadingSuppressError;
    private final String javetLibLoadingType;

    /**
     * Instantiates a new Javet lib loading listener.
     */
    public JavetLibLoadingListener() {
        javetLibLoadingPath = System.getProperty(PROPERTY_KEY_JAVET_LIB_LOADING_PATH);
        javetLibLoadingSuppressError = System.getProperty(PROPERTY_KEY_JAVET_LIB_LOADING_SUPPRESS_ERROR, null);
        javetLibLoadingType = System.getProperty(PROPERTY_KEY_JAVET_LIB_LOADING_TYPE, JAVET_LIB_LOADING_TYPE_DEFAULT);
    }

    @Override
    public File getLibPath(JSRuntimeType jsRuntimeType) {
        if (javetLibLoadingPath == null) {
            return new File(JavetOSUtils.TEMP_DIRECTORY, TEMP_ROOT_NAME);
        }
        return new File(javetLibLoadingPath);
    }

    @Override
    public boolean isDeploy(JSRuntimeType jsRuntimeType) {
        if (JavetOSUtils.IS_ANDROID) {
            return false;
        }
        if (JAVET_LIB_LOADING_TYPE_SYSTEM.equals(javetLibLoadingType)) {
            return false;
        }
        if (JAVET_LIB_LOADING_TYPE_CUSTOM.equals(javetLibLoadingType)) {
            return false;
        }
        return true;
    }

    @Override
    public boolean isLibInSystemPath(JSRuntimeType jsRuntimeType) {
        if (JavetOSUtils.IS_ANDROID) {
            return true;
        }
        if (JAVET_LIB_LOADING_TYPE_SYSTEM.equals(javetLibLoadingType)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isSuppressingError(JSRuntimeType jsRuntimeType) {
        if (JavetOSUtils.IS_ANDROID) {
            return true;
        }
        return javetLibLoadingSuppressError != null;
    }
}
