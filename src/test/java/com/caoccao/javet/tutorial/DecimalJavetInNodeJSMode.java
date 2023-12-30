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

package com.caoccao.javet.tutorial;

import com.caoccao.javet.enums.JSRuntimeType;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interfaces.IJavetClosable;
import com.caoccao.javet.interfaces.IJavetLogger;
import com.caoccao.javet.interop.NodeRuntime;
import com.caoccao.javet.interop.engine.IJavetEngine;
import com.caoccao.javet.interop.engine.IJavetEnginePool;
import com.caoccao.javet.interop.engine.JavetEnginePool;
import com.caoccao.javet.node.modules.NodeModuleModule;
import com.caoccao.javet.utils.JavetOSUtils;

import java.io.File;
import java.nio.file.Path;

public class DecimalJavetInNodeJSMode implements IJavetClosable {
    private IJavetEngine<NodeRuntime> iJavetEngine;
    private IJavetEnginePool<NodeRuntime> iJavetEnginePool;

    public DecimalJavetInNodeJSMode() throws JavetException {
        iJavetEnginePool = new JavetEnginePool<>();
        iJavetEnginePool.getConfig().setJSRuntimeType(JSRuntimeType.Node);
        iJavetEngine = iJavetEnginePool.getEngine();
    }

    public static void main(String[] args) throws JavetException {
        DecimalJavetInNodeJSMode decimalJavetInNodeJSMode = new DecimalJavetInNodeJSMode();
        try {
            decimalJavetInNodeJSMode.test();
        } catch (Throwable t) {
            decimalJavetInNodeJSMode.getLogger().error(t.getMessage(), t);
        } finally {
            decimalJavetInNodeJSMode.close();
        }
    }

    @Override
    public void close() throws JavetException {
        if (iJavetEngine != null) {
            iJavetEngine.close();
        }
        if (iJavetEnginePool != null) {
            iJavetEnginePool.close();
        }
    }

    public IJavetLogger getLogger() {
        return iJavetEnginePool.getConfig().getJavetLogger();
    }

    @Override
    public boolean isClosed() {
        return iJavetEngine.isClosed();
    }

    public void test() throws JavetException {
        NodeRuntime nodeRuntime = iJavetEngine.getV8Runtime();
        File workingDirectory = new File(JavetOSUtils.WORKING_DIRECTORY, "scripts/node/test-node");
        // Set the require root directory so that Node.js is able to locate node_modules.
        nodeRuntime.getNodeModule(NodeModuleModule.class).setRequireRootDirectory(workingDirectory);
        getLogger().logInfo("1.23 + 2.34 = {0}", nodeRuntime.getExecutor(
                "const Decimal = require('decimal.js');" +
                        "const a = new Decimal(1.23);" +
                        "const b = new Decimal(2.34);" +
                        "a.add(b).toString();").executeString());
    }
}
