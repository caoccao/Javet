/*
 * Copyright (c) 2024. caoccao.com Sam Cao
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

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.NodeRuntime;
import com.caoccao.javet.interop.V8Host;
import com.caoccao.javet.interop.options.NodeRuntimeOptions;
import com.caoccao.javet.utils.JavetOSUtils;

import java.io.File;

public class TestI18nInNode {
    public static void main(String[] args) throws JavetException {
        File icuDataDir = new File(JavetOSUtils.WORKING_DIRECTORY)
                .toPath()
                .resolve("../node/deps/icu-tmp")
                .normalize()
                .toFile();
        NodeRuntimeOptions.NODE_FLAGS.setIcuDataDir(icuDataDir.getAbsolutePath());
        try (NodeRuntime nodeRuntime = V8Host.getNodeI18nInstance().createV8Runtime()) {
            System.out.println(nodeRuntime.getExecutor("const a = 123456; a.toLocaleString('en-US');").executeString());
            // 123,456
            System.out.println(nodeRuntime.getExecutor("const us = new Intl.Locale('en-US'); us.language;").executeString());
            // en
            System.out.println(nodeRuntime.getExecutor("JSON.stringify(['Z', 'a', 'z', 'ä'].sort(new Intl.Collator('de').compare));").executeString());
            // ["a","ä","z","Z"]
        }
    }
}
