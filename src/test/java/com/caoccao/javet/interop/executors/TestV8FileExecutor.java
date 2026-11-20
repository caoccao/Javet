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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

public class TestV8FileExecutor extends BaseTestJavetRuntime {
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
    public void testFileLargerThanBuffer() throws IOException, JavetException {
        // The multi-byte characters cross the boundaries of the read buffer.
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 10000; ++i) {
            stringBuilder.append('中');
        }
        String expectedString = stringBuilder.toString();
        File scriptFile = tempDirPath.resolve("large.js").toFile();
        Files.write(scriptFile.toPath(), ("'" + expectedString + "'").getBytes(StandardCharsets.UTF_8));
        assertEquals(expectedString, v8Runtime.getExecutor(scriptFile).executeString());
    }

    @Test
    public void testNamedPipe() throws IOException, InterruptedException, JavetException {
        assumeFalse(JavetOSUtils.IS_WINDOWS);
        // A named pipe reports fewer available bytes than it eventually delivers.
        File scriptFile = tempDirPath.resolve("pipe.js").toFile();
        assertEquals(0, new ProcessBuilder("mkfifo", scriptFile.getAbsolutePath()).start().waitFor());
        Thread writerThread = new Thread(() -> {
            try (FileOutputStream fileOutputStream = new FileOutputStream(scriptFile)) {
                fileOutputStream.write("1 + ".getBytes(StandardCharsets.UTF_8));
                fileOutputStream.flush();
                TimeUnit.MILLISECONDS.sleep(100);
                fileOutputStream.write("2".getBytes(StandardCharsets.UTF_8));
            } catch (IOException | InterruptedException ignored) {
            }
        });
        writerThread.setDaemon(true);
        writerThread.start();
        try {
            assertEquals(3, v8Runtime.getExecutor(scriptFile).executeInteger());
        } finally {
            writerThread.join(TimeUnit.SECONDS.toMillis(5));
        }
        assertFalse(writerThread.isAlive());
    }
}
