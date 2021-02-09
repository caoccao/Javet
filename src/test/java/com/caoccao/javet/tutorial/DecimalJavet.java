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
import com.caoccao.javet.interfaces.IJavetLoggable;
import com.caoccao.javet.interop.V8Host;
import com.caoccao.javet.interop.V8Runtime;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Logger;

public class DecimalJavet implements IJavetLoggable, IJavetClosable {
    private Logger logger;
    private V8Runtime v8Runtime;

    public DecimalJavet() {
        logger = Logger.getLogger(getClass().getName());
        v8Runtime = null;
    }

    public static void main(String[] args) throws JavetException {
        DecimalJavet decimalJavet = new DecimalJavet();
        try {
            decimalJavet.loadJS();
            decimalJavet.test();
        } catch (Throwable t) {
            decimalJavet.logError(t.getMessage());
        } finally {
            decimalJavet.close();
        }
    }

    public void loadJS() throws IOException, JavetException {
        File decimalJSFile = new File(System.getProperty("user.dir"), "scripts/node/node_modules/decimal.js/decimal.js");
        if (decimalJSFile.exists() && decimalJSFile.canRead()) {
            logInfo("Loading {0}.", decimalJSFile.getAbsolutePath());
            String decimalJSString = Files.readString(decimalJSFile.toPath());
            v8Runtime = V8Host.getInstance().createV8Runtime();
            v8Runtime.lock();
            v8Runtime.executeVoid(decimalJSString);
        } else {
            logError("{0} is not found.", decimalJSFile.getAbsolutePath());
            logError("Please make sure NodeJS is installed, then visit script/node directory and run npm install.");
        }
    }

    public void test() throws JavetException {
        logInfo("1.23 + 2.34 = {0}", v8Runtime.executeString(
                "const a = new Decimal(1.23);" +
                        "const b = new Decimal(2.34);" +
                        "a.add(b).toString();"
        ));
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    public void close() throws JavetException {
        if (v8Runtime != null) {
            v8Runtime.close();
        }
    }
}
