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

package com.caoccao.javet.interop.converters;

/**
 * The type Javet converter config.
 *
 * @since 0.9.4
 */
public class JavetConverterConfig {
    /**
     * The constant DEFAULT_MAX_DEPTH.
     *
     * @since 0.9.3
     */
    public static final int DEFAULT_MAX_DEPTH = 20;
    /**
     * The Extract function source code.
     *
     * @since 0.9.4
     */
    protected boolean extractFunctionSourceCode;
    /**
     * The Max depth.
     *
     * @since 0.9.3
     */
    protected int maxDepth;
    /**
     * This flag determines whether function should be skipped in object or not.
     *
     * @since 0.9.4
     */
    protected boolean skipFunctionInObject;

    /**
     * Instantiates a new Javet converter config.
     *
     * @since 0.9.4
     */
    public JavetConverterConfig() {
        extractFunctionSourceCode = false;
        maxDepth = DEFAULT_MAX_DEPTH;
        skipFunctionInObject = true;
    }

    /**
     * Gets max depth.
     *
     * @return the max depth
     * @since 0.9.3
     */
    public int getMaxDepth() {
        return maxDepth;
    }

    /**
     * Is extract function source code boolean.
     *
     * @return the boolean
     * @since 0.9.4
     */
    public boolean isExtractFunctionSourceCode() {
        return extractFunctionSourceCode;
    }

    /**
     * Is skip functions boolean.
     *
     * @return the boolean
     * @since 0.9.4
     */
    public boolean isSkipFunctionInObject() {
        return skipFunctionInObject;
    }

    /**
     * Sets extract function source code.
     *
     * @param extractFunctionSourceCode the extract function source code
     * @return the self
     * @since 0.9.4
     */
    public JavetConverterConfig setExtractFunctionSourceCode(boolean extractFunctionSourceCode) {
        this.extractFunctionSourceCode = extractFunctionSourceCode;
        return this;
    }

    /**
     * Sets max depth.
     *
     * @param maxDepth the max depth
     * @return the self
     * @since 0.9.3
     */
    public JavetConverterConfig setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
        return this;
    }

    /**
     * Sets skip functions.
     *
     * @param skipFunctionInObject the skip functions
     * @return the self
     * @since 0.9.4
     */
    public JavetConverterConfig setSkipFunctionInObject(boolean skipFunctionInObject) {
        this.skipFunctionInObject = skipFunctionInObject;
        return this;
    }
}
