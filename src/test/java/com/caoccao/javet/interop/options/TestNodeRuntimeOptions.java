/*
 * Copyright (c) 2024-2026. caoccao.com Sam Cao
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

package com.caoccao.javet.interop.options;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.NodeRuntime;
import com.caoccao.javet.interop.V8Host;
import com.caoccao.javet.utils.JavetOSUtils;
import com.caoccao.javet.utils.SimpleMap;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class TestNodeRuntimeOptions {
    protected static boolean testEnabled = false;

    @BeforeAll
    public static void beforeAll() {
        if (!NodeRuntimeOptions.NODE_FLAGS.isSealed()) {
            NodeRuntimeOptions.NODE_FLAGS
                    .setAllowFsRead(new String[]{"/a", "/b"})
                    .setExperimentalSqlite(true)
                    .seal();
            testEnabled = true;
        }
    }

    protected File getScriptFile(String relativePath) {
        return new File(JavetOSUtils.WORKING_DIRECTORY)
                .toPath()
                .resolve("scripts/node/test-node")
                .resolve(relativePath)
                .toFile();
    }

    @Test
    public void testExperimentalSqlite() throws JavetException {
        if (testEnabled) {
            try (NodeRuntime nodeRuntime = V8Host.getNodeInstance().createV8Runtime()) {
                File scriptFile = getScriptFile("test-node-module-sqlite-sync.js");
                List<Map<String, Object>> result = null;
                try {
                    result = nodeRuntime.getExecutor(scriptFile).executeObject();
                } catch (Throwable t) {
                    t.printStackTrace(System.err);
                    fail("The sqlite test should pass.");
                }
                assertEquals(2, result.size());
                assertEquals(SimpleMap.of("key", 1, "value", "a"), result.get(0));
                assertEquals(SimpleMap.of("key", 2, "value", "b"), result.get(1));
            }
        }
    }
}
