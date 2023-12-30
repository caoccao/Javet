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

package com.caoccao.javet.interop.executors;

import com.caoccao.javet.exceptions.JavetError;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.utils.SimpleMap;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * The type V8 file executor.
 *
 * @since 1.0.3
 */
public class V8FileExecutor extends V8StringExecutor {
    /**
     * The Script file.
     *
     * @since 1.0.3
     */
    protected File scriptFile;

    /**
     * Instantiates a new V8 path executor.
     *
     * @param v8Runtime  the V8 runtime
     * @param scriptFile the script file
     * @throws JavetException the javet exception
     * @since 1.0.3
     */
    public V8FileExecutor(V8Runtime v8Runtime, File scriptFile) throws JavetException {
        super(v8Runtime);
        this.scriptFile = Objects.requireNonNull(scriptFile);
        setResourceName(scriptFile.getAbsolutePath());
    }

    /**
     * Gets script file.
     *
     * @return the script file
     * @since 1.0.3
     */
    public File getScriptFile() {
        return scriptFile;
    }

    @Override
    public String getScriptString() throws JavetException {
        if (scriptString == null) {
            try (FileInputStream fileInputStream = new FileInputStream(scriptFile)) {
                byte[] buffer = new byte[fileInputStream.available()];
                fileInputStream.read(buffer);
                scriptString = new String(buffer, StandardCharsets.UTF_8);
            } catch (IOException e) {
                throw new JavetException(
                        JavetError.FailedToReadPath,
                        SimpleMap.of(JavetError.PARAMETER_PATH, scriptFile),
                        e);
            }
        }
        return scriptString;
    }
}
