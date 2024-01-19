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

package com.caoccao.javet.tutorial.cdt;

public final class CDTConfig {
    public static final String COMMAND_EXIT = ".exit";
    public static final String PATH_ROOT = "/";
    public static final String PATH_JSON = "/json";
    public static final String PATH_JSON_LIST = "/json/list";
    public static final String PATH_JSON_VERSION = "/json/version";
    public static final String PATH_JAVET = "/javet";
    private static int port = 9229;

    public static int getPort() {
        return port;
    }

    public static String getWebSocketUrl() {
        return "localhost:" + port + PATH_JAVET;
    }

    public static void setPort(int port) {
        CDTConfig.port = port;
    }
}
