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
import com.caoccao.javet.utils.JavetOSUtils;

import java.io.File;
import java.nio.file.Path;

/**
 * The type Javet lib loading listener is the default one.
 *
 * @since 1.0.1
 */
public final class JavetLibLoadingListener implements IJavetLibLoadingListener {
    private static final String TEMP_ROOT_NAME = "javet";

    @Override
    public Path getLibPath(JSRuntimeType jsRuntimeType) {
        return new File(JavetOSUtils.TEMP_DIRECTORY, TEMP_ROOT_NAME).toPath();
    }

    @Override
    public boolean isDeploy(JSRuntimeType jsRuntimeType) {
        if (JavetOSUtils.IS_ANDROID) {
            return false;
        }
        return IJavetLibLoadingListener.super.isDeploy(jsRuntimeType);
    }

    @Override
    public boolean isLibInSystemPath(JSRuntimeType jsRuntimeType) {
        if (JavetOSUtils.IS_ANDROID) {
            return true;
        }
        return IJavetLibLoadingListener.super.isLibInSystemPath(jsRuntimeType);
    }
}
