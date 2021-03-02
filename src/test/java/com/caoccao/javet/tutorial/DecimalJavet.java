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

package com.caoccao.javet.tutorial;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interfaces.IJavetClosable;
import com.caoccao.javet.interfaces.IJavetLogger;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.engine.IJavetEngine;
import com.caoccao.javet.interop.engine.IJavetEnginePool;
import com.caoccao.javet.interop.engine.JavetEnginePool;
import com.caoccao.javet.utils.JavetOSUtils;
import com.caoccao.javet.values.primitive.V8ValueString;
import com.caoccao.javet.values.reference.V8ValueFunction;
import com.caoccao.javet.values.reference.V8ValueObject;

import java.io.File;
import java.math.BigDecimal;

public class DecimalJavet implements IJavetClosable {
    private IJavetEnginePool iJavetEnginePool;
    private IJavetEngine iJavetEngine;

    public DecimalJavet() {
        iJavetEnginePool = new JavetEnginePool();
        iJavetEngine = iJavetEnginePool.getEngine();
    }

    public static void main(String[] args) throws JavetException {
        DecimalJavet decimalJavet = new DecimalJavet();
        try {
            decimalJavet.loadJS();
            decimalJavet.test();
        } catch (Throwable t) {
            decimalJavet.getLogger().error(t.getMessage(), t);
        } finally {
            decimalJavet.close();
        }
    }

    public void loadJS() throws JavetException {
        File decimalJSFile = new File(
                JavetOSUtils.WORKING_DIRECTORY,
                "scripts/node/node_modules/decimal.js/decimal.js");
        if (decimalJSFile.exists() && decimalJSFile.canRead()) {
            getLogger().logInfo("Loading {0}.", decimalJSFile.getAbsolutePath());
            V8Runtime v8Runtime = iJavetEngine.getV8Runtime();
            v8Runtime.getExecutor(decimalJSFile).executeVoid();
        } else {
            getLogger().logError("{0} is not found.", decimalJSFile.getAbsolutePath());
            getLogger().logError("Please make sure NodeJS is installed, then visit script/node directory and run npm install.");
        }
    }

    public void test() throws JavetException {
        V8Runtime v8Runtime = iJavetEngine.getV8Runtime();
        getLogger().logInfo("1.23 + 2.34 = {0}", v8Runtime.getExecutor(
                "const a = new Decimal(1.23);" +
                        "const b = new Decimal(2.34);" +
                        "a.add(b).toString();").executeString());
        try (V8ValueFunction v8ValueFunctionDecimal = v8Runtime.getGlobalObject().get("Decimal")) {
            try (V8ValueObject v8ValueObjectDecimal = v8ValueFunctionDecimal.call(
                    null, new V8ValueString("123.45"))) {
                getLogger().logInfo(v8ValueObjectDecimal.toString());
                if (v8ValueObjectDecimal.has("constructor")) {
                    try (V8ValueFunction v8ValueFunction = v8ValueObjectDecimal.get("constructor")) {
                        String name = v8ValueFunction.getString("name");
                        if ("Decimal".equals(name)) {
                            BigDecimal bigDecimal = new BigDecimal(v8ValueObjectDecimal.toString());
                            getLogger().logInfo("BigDecimal: {0}", bigDecimal.toString());
                        }
                    }
                }
            }
        }
    }

    public IJavetLogger getLogger() {
        return iJavetEnginePool.getConfig().getJavetLogger();
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
}
