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

package com.caoccao.javet.config;

public final class JavetConfig {
    private static boolean exposeGC;
    private static boolean sealed;
    private static boolean useStrict;

    private JavetConfig() {
        exposeGC = false;
        sealed = false;
        useStrict = true;
    }

    public static boolean isSealed() {
        return sealed;
    }

    public static void seal() {
        if (!sealed) {
            sealed = true;
        }
    }

    public static boolean isExposeGC() {
        return exposeGC;
    }

    public static void setExposeGC(boolean exposeGC) {
        if (!sealed) {
            JavetConfig.exposeGC = exposeGC;
        }
    }

    public static boolean isUseStrict() {
        return useStrict;
    }

    public static void setUseStrict(boolean useStrict) {
        if (!sealed) {
            JavetConfig.useStrict = useStrict;
        }
    }
}
