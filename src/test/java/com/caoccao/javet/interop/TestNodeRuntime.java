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

package com.caoccao.javet.interop;

import com.caoccao.javet.BaseTestJavet;
import com.caoccao.javet.enums.JSRuntimeType;
import com.caoccao.javet.enums.V8AwaitMode;
import com.caoccao.javet.exceptions.JavetError;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.converters.JavetProxyConverter;
import com.caoccao.javet.interop.options.NodeRuntimeOptions;
import com.caoccao.javet.node.modules.NodeModuleAny;
import com.caoccao.javet.node.modules.NodeModuleProcess;
import com.caoccao.javet.utils.JavetOSUtils;
import com.caoccao.javet.values.reference.V8ValueArray;
import com.caoccao.javet.values.reference.V8ValueError;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.MessageFormat;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

public class TestNodeRuntime extends BaseTestJavet {
    protected NodeModuleProcess nodeModuleProcess;
    protected NodeRuntime nodeRuntime;

    public TestNodeRuntime() {
        super(JSRuntimeType.Node);
    }

    @AfterEach
    public void afterEach() throws JavetException {
        nodeModuleProcess.setWorkingDirectory(new File(JavetOSUtils.WORKING_DIRECTORY));
        assertEquals(nodeRuntime.getNodeModuleCount(), nodeRuntime.getReferenceCount(),
                "Reference count should be equal to node module count after test case is ended.");
        nodeRuntime.close();
        assertTrue(nodeRuntime.isClosed());
        assertEquals(0, v8Host.getV8RuntimeCount());
    }

    @BeforeEach
    public void beforeEach() throws JavetException {
        nodeRuntime = v8Host.createV8Runtime();
        assertFalse(nodeRuntime.isPooled());
        assertEquals(0, nodeRuntime.getReferenceCount(),
                "Reference count should be 0 before test case is started.");
        try {
            nodeModuleProcess = nodeRuntime.getNodeModule(NodeModuleProcess.class);
            nodeModuleProcess.setWorkingDirectory(
                    new File(JavetOSUtils.WORKING_DIRECTORY, "scripts/node/test-node"));
        } catch (JavetException e) {
            fail(e.getMessage());
        }
    }

    protected File getScriptFile(String relativePath) throws JavetException {
        return nodeModuleProcess.getWorkingDirectory().toPath().resolve(relativePath).toFile();
    }

    protected void internalTest(String fileName, String expectedJsonString) throws JavetException {
        try (V8ValueArray v8ValueArray = nodeRuntime.getExecutor(getScriptFile(
                fileName)).execute()) {
            assertEquals(expectedJsonString, v8ValueArray.toJsonString());
        }
    }

    @Test
    public void testAwaitRunNoWait() {
        try {
            nodeRuntime.setConverter(new JavetProxyConverter());
            AtomicInteger counter = new AtomicInteger();
            nodeRuntime.getGlobalObject().set("counter", counter);
            nodeRuntime.getExecutor("let timeoutIDs = [];" +
                    "function a() {\n" +
                    "  counter.incrementAndGet();\n" +
                    "  if (counter.get() < 10) {\n" +
                    "    timeoutIDs.push(setTimeout(a, 1000));\n" +
                    "  }\n" +
                    "};\n" +
                    "a();").executeVoid();
            assertTrue(nodeRuntime.await(V8AwaitMode.RunNoWait));
            nodeRuntime.getExecutor("timeoutIDs.forEach(id => clearTimeout(id));").executeVoid();
            assertEquals(1, counter.get());
            nodeRuntime.getGlobalObject().delete("counter");
        } catch (Throwable t) {
            fail(t);
        } finally {
            nodeRuntime.lowMemoryNotification();
        }
    }

    @Test
    public void testAwaitRunOnce() {
        try {
            nodeRuntime.setConverter(new JavetProxyConverter());
            AtomicInteger counter = new AtomicInteger();
            nodeRuntime.getGlobalObject().set("counter", counter);
            nodeRuntime.getExecutor("let timeoutIDs = [];" +
                    "function a() {\n" +
                    "  counter.incrementAndGet();\n" +
                    "  if (counter.get() < 10) {\n" +
                    "    timeoutIDs.push(setTimeout(a, 0));\n" +
                    "  }\n" +
                    "};\n" +
                    "a();").executeVoid();
            while (counter.get() < 2) {
                assertTrue(nodeRuntime.await(V8AwaitMode.RunOnce));
            }
            nodeRuntime.getExecutor("timeoutIDs.forEach(id => clearTimeout(id));").executeVoid();
            assertEquals(2, counter.get());
            nodeRuntime.getGlobalObject().delete("counter");
        } catch (Throwable t) {
            fail(t);
        } finally {
            nodeRuntime.lowMemoryNotification();
        }
    }

    @Test
    public void testAwaitRunTillNoMoreTasks() {
        try {
            nodeRuntime.setConverter(new JavetProxyConverter());
            AtomicInteger counter = new AtomicInteger();
            nodeRuntime.getGlobalObject().set("counter", counter);
            nodeRuntime.getExecutor("function a() {\n" +
                    "  counter.incrementAndGet();\n" +
                    "  if (counter.get() < 10) {\n" +
                    "    setTimeout(a, 0);\n" +
                    "  }\n" +
                    "};\n" +
                    "a();").executeVoid();
            assertFalse(nodeRuntime.await(V8AwaitMode.RunTillNoMoreTasks));
            assertEquals(10, counter.get());
            nodeRuntime.getGlobalObject().delete("counter");
        } catch (Throwable t) {
            fail(t);
        } finally {
            nodeRuntime.lowMemoryNotification();
        }
    }

    @Test
    public void testConsoleArguments() throws JavetException {
        NodeRuntimeOptions runtimeOptions = new NodeRuntimeOptions();
        runtimeOptions.setConsoleArguments(new String[]{"--version"});
        try (NodeRuntime testNodeRuntime = v8Host.createV8Runtime(runtimeOptions)) {
            assertNotNull(testNodeRuntime);
        }
    }

    @Test
    public void testDynamicImport() throws JavetException {
        nodeRuntime.getExecutor("const { Script, constants } = require('node:vm');\n" +
                        "const script = new Script(\n" +
                        "  'import(\"node:fs\").then(({readFile}) => readFile instanceof Function)',\n" +
                        "  { importModuleDynamically: constants.USE_MAIN_CONTEXT_DEFAULT_LOADER });\n" +
                        "globalThis.a = null;" +
                        "script.runInNewContext().then((x) => { globalThis.a = x; });")
                .setModule(true)
                .executeVoid();
        nodeRuntime.await();
        assertFalse(nodeRuntime.getGlobalObject().getBoolean("a"));
    }

    @Test
    public void testModuleAny() throws JavetException {
        NodeModuleAny nodeModuleFS = nodeRuntime.getNodeModule("fs", NodeModuleAny.class);
        assertTrue(nodeModuleFS.getModuleObject().invokeBoolean(
                "existsSync",
                getScriptFile("test-node-module-fs.js").getAbsolutePath()));
    }

    @Test
    public void testModuleFS() throws JavetException {
        internalTest(
                "test-node-module-fs.js",
                "[false,true,true,false]");
    }

    @Test
    public void testModuleProcess() throws JavetException {
        Path path1 = nodeModuleProcess.getWorkingDirectory().toPath();
        Path path2 = path1.resolve("../");
        nodeModuleProcess.setWorkingDirectory(path2.toFile());
        Path path3 = nodeModuleProcess.getWorkingDirectory().toPath();
        nodeModuleProcess.setWorkingDirectory(path1.toFile());
        Path path4 = nodeModuleProcess.getWorkingDirectory().toPath();
        assertNotEquals(path1.toAbsolutePath().toString(), path3.toAbsolutePath().toString());
        assertEquals(path1.toAbsolutePath().toString(), path4.toAbsolutePath().toString());
        assertEquals("v20.17.0", nodeModuleProcess.getVersion());
    }

    @Test
    public void testModuleVM() throws JavetException {
        internalTest(
                "test-node-module-vm.js",
                "[{\"a\":\"x\",\"b\":3},\"undefined\",\"undefined\"]");
    }

    @Test
    public void testPurgeEventLoopBeforeClose() throws JavetException {
        nodeRuntime.getExecutor(
                "const log = () => console.log('Event loop is purged before close.');" +
                        "setTimeout(log, 10);").executeVoid();
    }

    @Test
    public void testSWC() throws JavetException {
        File swcCoreFile = getScriptFile("../node_modules/@swc/core/index.js");
        if (swcCoreFile.exists()) {
            File scriptFile = getScriptFile("test-node-module-swc-sync.js");
            try {
                nodeRuntime.getExecutor(scriptFile).executeVoid();
            } catch (Throwable t) {
                t.printStackTrace(System.err);
                fail(MessageFormat.format("{0} should pass.", scriptFile.getAbsolutePath()));
            }
        }
    }

    @Test
    public void testSqlite3InRootDirectoryWithDoubleDots() throws JavetException, IOException {
        File sqlite3File = getScriptFile("../node_modules/sqlite3/sqlite3.js");
        if (sqlite3File.exists()) {
            File scriptFile = getScriptFile("test-node-module-sqlite3-sync.js");
            File testModuleModeFile = getScriptFile("../test-module-mode.js");
            try {
                if (testModuleModeFile.exists()) {
                    assertTrue(testModuleModeFile.delete());
                }
                Files.write(
                        testModuleModeFile.toPath(),
                        Files.readAllBytes(scriptFile.toPath()),
                        StandardOpenOption.CREATE);
                // It should pass in module mode.
                try {
                    nodeRuntime.getExecutor(testModuleModeFile).executeVoid();
                } catch (Throwable t) {
                    t.printStackTrace(System.err);
                    fail(MessageFormat.format(
                            "{0} should pass in module mode.",
                            testModuleModeFile.getAbsolutePath()));
                }
            } finally {
                assertTrue(testModuleModeFile.delete());
            }
        }
    }

    @Test
    public void testSqlite3InRootDirectoryWithoutDoubleDots() throws JavetException, IOException {
        File sqlite3File = getScriptFile("../node_modules/sqlite3/sqlite3.js");
        if (sqlite3File.exists()) {
            File scriptFile = getScriptFile("test-node-module-sqlite3-sync.js");
            File testModuleModeFile = new File(JavetOSUtils.WORKING_DIRECTORY, "scripts/node/test-module-mode.js");
            try {
                if (testModuleModeFile.exists()) {
                    assertTrue(testModuleModeFile.delete());
                }
                Files.write(
                        testModuleModeFile.toPath(),
                        Files.readAllBytes(scriptFile.toPath()),
                        StandardOpenOption.CREATE);
                // It should fail in module mode.
                try {
                    nodeRuntime.getExecutor(testModuleModeFile).executeVoid();
                    fail(MessageFormat.format(
                            "{0} should fail in module mode.",
                            testModuleModeFile.getAbsolutePath()));
                } catch (JavetException e) {
                    assertEquals(JavetError.ExecutionFailure, e.getError());
                    assertTrue(e.getMessage().startsWith("Error: Cannot find module 'sqlite3'"));
                }
            } finally {
                assertTrue(testModuleModeFile.delete());
            }
        }
    }

    @Test
    public void testSqlite3InSubDirectory() throws JavetException {
        File sqlite3File = getScriptFile("../node_modules/sqlite3/sqlite3.js");
        if (sqlite3File.exists()) {
            File scriptFile = getScriptFile("test-node-module-sqlite3-sync.js");
            try {
                nodeRuntime.getExecutor(scriptFile).executeVoid();
            } catch (Throwable t) {
                t.printStackTrace(System.err);
                fail(MessageFormat.format("{0} should pass.", scriptFile.getAbsolutePath()));
            }
        }
    }

    @Test
    public void testStopping() throws JavetException {
        IntStream.range(0, 5).forEach(i -> assertFalse(nodeRuntime.isStopping()));
        IntStream.range(0, 5).forEach(i -> {
            nodeRuntime.setStopping(false);
            assertFalse(nodeRuntime.isStopping());
            nodeRuntime.setStopping(true);
            assertTrue(nodeRuntime.isStopping());
        });
        nodeRuntime.getExecutor("let count = 0; setInterval(()=> console.log(count++), 1000);").executeVoid();
    }

    @Test
    public void testTimers() throws JavetException {
        nodeRuntime.getExecutor(getScriptFile("test-node-module-timers.js")).executeVoid();
        try (V8ValueArray v8ValueArray = nodeRuntime.getGlobalObject().get("results")) {
            assertEquals("[1]", v8ValueArray.toJsonString(),
                    "setTimeout() hasn't been executed before await().");
            nodeRuntime.await();
            assertEquals("[1,2]", v8ValueArray.toJsonString(),
                    "setTimeout() has been executed after await().");
        }
    }

    @Test
    public void testUncaughtException() throws JavetException {
        nodeRuntime.getExecutor("let a = [];\n" +
                "process.on('uncaughtException', (error, origin) => {\n" +
                "  a.push(origin, error);\n" +
                "});").executeVoid();
        nodeRuntime.getExecutor("setTimeout(() => { throw new Error('Error'); }, 0);").executeVoid();
        nodeRuntime.await();
        assertEquals("uncaughtException", nodeRuntime.getExecutor("a[0]").executeString());
        try (V8ValueError v8ValueError = nodeRuntime.getExecutor("a[1]").execute()) {
            assertEquals("Error", v8ValueError.getMessage());
        }
    }
}
