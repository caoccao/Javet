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
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

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
    public void testBareFileNameAfterAnotherDirectory() throws IOException, JavetException {
        assumeTrue(isNode());
        Path otherDirPath = Files.createDirectories(tempDirPath.resolve("other"));
        Path otherScriptPath = otherDirPath.resolve("other.js");
        Files.write(otherScriptPath, "__dirname".getBytes(StandardCharsets.UTF_8));
        assertEquals(otherDirPath.toString(), v8Runtime.getExecutor(otherScriptPath).executeString());
        // The bare file name replaces the globals set by the previous script, instead of keeping them.
        v8Runtime.getExecutor(Paths.get("main.js"));
        Path userDirPath = Paths.get("").toAbsolutePath();
        assertEquals(
                Arrays.asList(
                        userDirPath.toString(),
                        userDirPath.resolve("main.js").toString(),
                        userDirPath.resolve("node_modules").toString()),
                v8Runtime.getExecutor("[__dirname, __filename, require.resolve.paths('dep')[0]]").executeObject());
    }

    @Test
    public void testRelativeAndAbsolutePathsAfterAnotherDirectory() throws IOException, JavetException {
        assumeTrue(isNode());
        Path otherDirPath = Files.createDirectories(tempDirPath.resolve("other"));
        Path otherScriptPath = otherDirPath.resolve("other.js");
        Files.write(otherDirPath.resolve("dep.js"), "module.exports = 'other';".getBytes(StandardCharsets.UTF_8));
        Files.write(otherScriptPath, "require('./dep.js')".getBytes(StandardCharsets.UTF_8));
        Path mainDirPath = Files.createDirectories(tempDirPath.resolve("main"));
        Path mainScriptPath = mainDirPath.resolve("main.js");
        Files.write(mainDirPath.resolve("dep.js"), "module.exports = 'main';".getBytes(StandardCharsets.UTF_8));
        Files.write(
                mainScriptPath,
                "[require('path').resolve(__dirname), require('path').resolve(__filename), require('./dep.js')]"
                        .getBytes(StandardCharsets.UTF_8));
        List<Path> mainScriptPaths = new ArrayList<>();
        mainScriptPaths.add(mainScriptPath);
        // A relative path doesn't exist between different roots (e.g. different drives on Windows).
        Path userDirPath = Paths.get("").toAbsolutePath();
        if (userDirPath.getRoot().equals(mainScriptPath.getRoot())) {
            mainScriptPaths.add(userDirPath.relativize(mainScriptPath));
        }
        List<String> expectedValues = Arrays.asList(mainDirPath.toString(), mainScriptPath.toString(), "main");
        for (Path scriptPath : mainScriptPaths) {
            // The previous script changes the working directory to another directory.
            assertEquals("other", v8Runtime.getExecutor(otherScriptPath).executeString());
            assertEquals(expectedValues, v8Runtime.getExecutor(scriptPath).executeObject(), scriptPath.toString());
        }
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
