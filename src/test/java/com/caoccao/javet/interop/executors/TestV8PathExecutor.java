/*
 * Copyright (c) 2021-2026. caoccao.com Sam Cao
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

import com.caoccao.javet.BaseTestJavetRuntime;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.NodeRuntime;
import com.caoccao.javet.node.modules.NodeModuleProcess;
import com.caoccao.javet.utils.JavetOSUtils;
import com.caoccao.javet.utils.SimpleMap;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestV8PathExecutor extends BaseTestJavetRuntime {
    @TempDir
    protected Path tempDirPath;
    protected File workingDirectory;

    @AfterEach
    public void afterEachTest() throws JavetException {
        // The executor changes the working directory in Node.js mode, so it is restored
        // before the temporary directory is deleted.
        if (workingDirectory != null) {
            ((NodeRuntime) v8Runtime).getNodeModule(NodeModuleProcess.class).setWorkingDirectory(workingDirectory);
        }
    }

    @BeforeEach
    public void beforeEachTest() throws JavetException {
        workingDirectory = isNode()
                ? ((NodeRuntime) v8Runtime).getNodeModule(NodeModuleProcess.class).getWorkingDirectory()
                : null;
    }

    @Test
    public void testZipFileSystem() throws IOException, JavetException {
        Path hostScriptPath = tempDirPath.resolve("main.js");
        Files.write(hostScriptPath, "'host'".getBytes(StandardCharsets.UTF_8));
        Path zipFilePath = tempDirPath.resolve("scripts.zip");
        URI zipFileUri = URI.create("jar:" + zipFilePath.toUri());
        try (FileSystem zipFileSystem = FileSystems.newFileSystem(zipFileUri, SimpleMap.of("create", "true"))) {
            // The ZIP entry mirrors the host file path so that both paths share the same string.
            StringBuilder stringBuilder = new StringBuilder();
            for (Path name : hostScriptPath) {
                stringBuilder.append('/').append(name);
            }
            Path zipScriptPath = zipFileSystem.getPath(stringBuilder.toString());
            if (!JavetOSUtils.IS_WINDOWS) {
                assertEquals(hostScriptPath.toString(), zipScriptPath.toString());
            }
            Files.createDirectories(zipScriptPath.getParent());
            Files.write(zipScriptPath, "'zip'".getBytes(StandardCharsets.UTF_8));
            assertEquals("zip", v8Runtime.getExecutor(zipScriptPath).executeString());
            assertEquals("host", v8Runtime.getExecutor(hostScriptPath).executeString());
        }
    }
}
