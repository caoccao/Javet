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

package com.caoccao.javet.interop.options;

import com.caoccao.javet.utils.ArrayUtils;
import com.caoccao.javet.utils.StringUtils;

import java.util.stream.Stream;

/**
 * The type Node runtime options.
 *
 * @since 1.0.0
 */
public final class NodeRuntimeOptions extends RuntimeOptions<NodeRuntimeOptions> {
    /**
     * The constant NODE_FLAGS.
     *
     * @since 4.0.0
     */
    public static final NodeFlags NODE_FLAGS = new NodeFlags();
    /**
     * The constant V8_FLAGS.
     *
     * @since 1.1.7
     */
    public static final V8Flags V8_FLAGS = new V8Flags();
    private String[] consoleArguments;

    /**
     * Instantiates a new Node runtime options.
     *
     * @since 1.0.0
     */
    public NodeRuntimeOptions() {
        super();
        consoleArguments = null;
    }

    /**
     * Get console arguments.
     *
     * @return the console arguments
     * @since 1.0.0
     */
    public String[] getConsoleArguments() {
        return consoleArguments;
    }

    /**
     * Sets console arguments.
     *
     * @param consoleArguments the console arguments
     * @return the self
     * @since 1.0.0
     */
    public NodeRuntimeOptions setConsoleArguments(String[] consoleArguments) {
        if (ArrayUtils.isEmpty(consoleArguments)) {
            this.consoleArguments = null;
        } else {
            this.consoleArguments = Stream.of(consoleArguments)
                    .filter(StringUtils::isNotBlank)
                    .toArray(String[]::new);
        }
        return this;
    }
}
