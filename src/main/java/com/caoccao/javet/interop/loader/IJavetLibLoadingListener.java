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

import java.io.File;

/**
 * The interface Javet lib loading listener.
 *
 * @since 1.0.1
 */
public interface IJavetLibLoadingListener {
    /**
     * Gets lib path.
     * If the lib is in system path, this function will not be called.
     * <p>
     * Note: lib file name is decided by Javet.
     *
     * @param jsRuntimeType the JS runtime type
     * @return the lib path
     * @since 1.0.1
     */
    default File getLibPath(JSRuntimeType jsRuntimeType) {
        return null;
    }

    /**
     * Is deploy.
     *
     * @param jsRuntimeType the JS runtime type
     * @return true : yes, false : no
     * @since 1.0.1
     */
    default boolean isDeploy(JSRuntimeType jsRuntimeType) {
        return true;
    }

    /**
     * Is lib in system path.
     *
     * @param jsRuntimeType the JS runtime type
     * @return true : yes, false : no
     * @since 1.0.1
     */
    default boolean isLibInSystemPath(JSRuntimeType jsRuntimeType) {
        return false;
    }

    /**
     * Is suppressing error.
     *
     * @param jsRuntimeType the JS runtime type
     * @return true : yes, false : no
     * @since 1.0.6
     */
    default boolean isSuppressingError(JSRuntimeType jsRuntimeType) {
        return false;
    }
}
