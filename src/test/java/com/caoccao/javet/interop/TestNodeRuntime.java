/*
 *    Copyright 2021. caoccao.com Sam Cao
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

package com.caoccao.javet.interop;

import com.caoccao.javet.BaseTestJavet;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.node.NodeModuleProcess;
import com.caoccao.javet.utils.JavetOSUtils;
import com.caoccao.javet.values.reference.V8ValueArray;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class TestNodeRuntime extends BaseTestJavet {
    protected NodeRuntime nodeRuntime;
    protected NodeModuleProcess nodeModuleProcess;

    public TestNodeRuntime() {
        super(JSRuntimeType.Node);
    }

    @AfterEach
    public void afterEach() throws JavetException {
        nodeModuleProcess.setWorkingDirectory(new File(JavetOSUtils.WORKING_DIRECTORY).toPath());
        nodeRuntime.getNodeObjectStore().close();
        assertEquals(0, nodeRuntime.getReferenceCount(),
                "Reference count should be 0 after test case is ended.");
        nodeRuntime.close();
        assertEquals(0, v8Host.getV8RuntimeCount());
    }

    @BeforeEach
    public void beforeEach() {
        nodeRuntime = (NodeRuntime) v8Host.createV8Runtime();
        assertFalse(nodeRuntime.isPooled());
        assertEquals(0, nodeRuntime.getReferenceCount(),
                "Reference count should be 0 before test case is started.");
        try {
            nodeModuleProcess = nodeRuntime.getNodeModuleProcess();
            nodeModuleProcess.setWorkingDirectory(
                    new File(JavetOSUtils.WORKING_DIRECTORY, "scripts/node/test-node").toPath());
        } catch (JavetException e) {
            fail(e.getMessage());
        }
    }

    protected File getScriptFile(String relativePath) throws JavetException {
        return nodeModuleProcess.getWorkingDirectory().resolve(relativePath).toFile();
    }

    protected void internalTest(String fileName, String expectedJsonString) throws JavetException {
        try (V8ValueArray v8ValueArray = nodeRuntime.getExecutor(getScriptFile(
                fileName)).execute()) {
            assertEquals(expectedJsonString, v8ValueArray.toJsonString());
        }
    }

    @Test
    public void testModuleFS() throws JavetException {
        internalTest(
                "test-node-module-fs.js",
                "[false,true,true,false]");
    }

    @Test
    public void testModuleProcess() throws JavetException {
        Path path1 = nodeModuleProcess.getWorkingDirectory();
        Path path2 = path1.resolve("../");
        nodeModuleProcess.setWorkingDirectory(path2);
        Path path3 = nodeModuleProcess.getWorkingDirectory();
        nodeModuleProcess.setWorkingDirectory(path1);
        Path path4 = nodeModuleProcess.getWorkingDirectory();
        assertNotEquals(path1.toAbsolutePath().toString(), path3.toAbsolutePath().toString());
        assertEquals(path1.toAbsolutePath().toString(), path4.toAbsolutePath().toString());
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
    public void testModuleVM() throws JavetException {
        internalTest(
                "test-node-module-vm.js",
                "[{\"a\":\"x\",\"b\":3},\"undefined\",\"undefined\"]");
    }
}
