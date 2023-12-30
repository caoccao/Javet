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

package com.caoccao.javet.utils;

import com.caoccao.javet.interfaces.IJavetLogger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JavetDefaultLogger implements IJavetLogger {
    protected Logger logger;
    protected String name;

    public JavetDefaultLogger(String name) {
        logger = Logger.getLogger(name);
        this.name = name;
    }

    @Override
    public void debug(String message) {
        logger.log(Level.FINE, message);
    }

    @Override
    public void error(String message) {
        logger.log(Level.SEVERE, message);
    }

    @Override
    public void error(String message, Throwable cause) {
        logger.severe(message);
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            try (PrintStream printStream = new PrintStream(byteArrayOutputStream)) {
                cause.printStackTrace(printStream);
                logger.severe(byteArrayOutputStream.toString(StandardCharsets.UTF_8.name()));
            }
        } catch (IOException ignored) {
        }
    }

    public Logger getLogger() {
        return logger;
    }

    public String getName() {
        return name;
    }

    @Override
    public void info(String message) {
        logger.info(message);
    }

    @Override
    public void warn(String message) {
        logger.warning(message);
    }
}
