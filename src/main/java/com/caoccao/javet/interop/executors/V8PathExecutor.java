/*
 *   Copyright (c) 2021. caoccao.com Sam Cao
 *   All rights reserved.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.caoccao.javet.interop.executors;

import com.caoccao.javet.exceptions.JavetError;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.utils.SimpleMap;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class V8PathExecutor extends V8StringExecutor {
    protected Path scriptPath;

    public V8PathExecutor(V8Runtime v8Runtime, Path scriptPath) throws JavetException {
        super(v8Runtime);
        this.scriptPath = scriptPath;
        setResourceName(scriptPath.toFile().getAbsolutePath());
    }

    @Override
    public String getScriptString() throws JavetException {
        if (scriptString == null) {
            try {
                scriptString = new String(Files.readAllBytes(scriptPath), StandardCharsets.UTF_8);
            } catch (IOException e) {
                throw new JavetException(
                        JavetError.FailedToReadPath,
                        SimpleMap.of(JavetError.PARAMETER_PATH, scriptPath),
                        e);
            }
        }
        return scriptString;
    }

    public Path getScriptPath() {
        return scriptPath;
    }
}
