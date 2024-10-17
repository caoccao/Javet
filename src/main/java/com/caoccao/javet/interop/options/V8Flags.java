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

package com.caoccao.javet.interop.options;

import com.caoccao.javet.utils.StringUtils;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * The type V8 flags defines some built-in V8 flags.
 * The complete flag definition is stored in v8/src/flags/flag-definitions.h
 *
 * @since 0.7.0
 */
public final class V8Flags {
    /**
     * The constant FLAG_ALLOW_NATIVES_SYNTAX.
     *
     * @since 0.9.13
     */
    public static final String FLAG_ALLOW_NATIVES_SYNTAX = "--allow-natives-syntax";
    /**
     * The constant FLAG_EXPOSE_GC.
     *
     * @since 0.9.13
     */
    public static final String FLAG_EXPOSE_GC = "--expose-gc";
    /**
     * The constant FLAG_EXPOSE_INSPECTOR_SCRIPTS.
     *
     * @since 0.9.13
     */
    public static final String FLAG_EXPOSE_INSPECTOR_SCRIPTS = "--expose-inspector-scripts";
    /**
     * The constant FLAG_INITIAL_HEAP_SIZE.
     *
     * @since 0.9.13
     */
    public static final String FLAG_INITIAL_HEAP_SIZE = "--initial-heap-size";
    /**
     * The constant FLAG_JS_FLOAT_16_ARRAY.
     *
     * @since 4.1.0
     */
    public static final String FLAG_JS_FLOAT_16_ARRAY = "--js-float16array";
    /**
     * The constant FLAG_MAX_OLD_SPACE_SIZE.
     *
     * @since 0.9.13
     */
    public static final String FLAG_MAX_OLD_SPACE_SIZE = "--max-old-space-size";
    /**
     * The constant FLAG_MAX_HEAP_SIZE.
     *
     * @since 0.9.13
     */
    public static final String FLAG_MAX_HEAP_SIZE = "--max-heap-size";
    /**
     * The constant FLAG_USE_STRICT.
     *
     * @since 0.9.13
     */
    public static final String FLAG_USE_STRICT = "--use-strict";
    private static final String SPACE = " ";
    private boolean allowNativesSyntax;
    private String customFlags;
    private boolean exposeGC;
    private boolean exposeInspectorScripts;
    private String icuDataFile;
    private int initialHeapSize;
    private boolean jsFloat16Array;
    private int maxHeapSize;
    private int maxOldSpaceSize;
    private boolean sealed;
    private boolean useStrict;

    /**
     * Instantiates a new V8 flags.
     *
     * @since 0.7.0
     */
    V8Flags() {
        allowNativesSyntax = false;
        customFlags = null;
        exposeGC = false;
        exposeInspectorScripts = false;
        icuDataFile = null;
        initialHeapSize = 0;
        jsFloat16Array = false;
        maxHeapSize = 0;
        maxOldSpaceSize = 0;
        sealed = false;
        useStrict = true;
    }

    private String fromInteger(String flagName, int flagValue) {
        return MessageFormat.format("{0}={1}", flagName, Integer.toString(flagValue));
    }

    /**
     * Gets custom flags.
     *
     * @return the custom flags
     * @since 0.9.13
     */
    public String getCustomFlags() {
        return customFlags;
    }

    /**
     * Gets icu data file.
     *
     * @return the icu data file
     * @since 4.0.0
     */
    public String getIcuDataFile() {
        return icuDataFile;
    }

    /**
     * Gets initial heap size.
     *
     * @return the initial heap size
     * @since 0.9.13
     */
    public int getInitialHeapSize() {
        return initialHeapSize;
    }

    /**
     * Gets max heap size.
     *
     * @return the max heap size
     * @since 0.9.13
     */
    public int getMaxHeapSize() {
        return maxHeapSize;
    }

    /**
     * Gets max old space size.
     *
     * @return the max old space size
     * @since 0.9.13
     */
    public int getMaxOldSpaceSize() {
        return maxOldSpaceSize;
    }

    /**
     * Is allow natives syntax.
     *
     * @return the boolean
     * @since 0.7.0
     */
    public boolean isAllowNativesSyntax() {
        return allowNativesSyntax;
    }

    /**
     * Is expose gc.
     *
     * @return the boolean
     * @since 0.7.0
     */
    public boolean isExposeGC() {
        return exposeGC;
    }

    /**
     * Is expose inspector scripts.
     *
     * @return the boolean
     * @since 0.7.0
     */
    public boolean isExposeInspectorScripts() {
        return exposeInspectorScripts;
    }

    /**
     * Is js float 16 array enabled.
     *
     * @return true : yes, false: no
     * @since 4.1.0
     */
    public boolean isJsFloat16Array() {
        return jsFloat16Array;
    }

    /**
     * Is sealed.
     *
     * @return the boolean
     * @since 0.7.0
     */
    public boolean isSealed() {
        return sealed;
    }

    /**
     * Is use strict.
     *
     * @return the boolean
     * @since 0.7.0
     */
    public boolean isUseStrict() {
        return useStrict;
    }

    /**
     * Seal the V8 flags so that it is read-only.
     *
     * @return the self
     * @since 0.7.0
     */
    public V8Flags seal() {
        if (!sealed) {
            sealed = true;
        }
        return this;
    }

    /**
     * Sets allow natives syntax.
     *
     * @param allowNativesSyntax allow natives syntax
     * @return the self
     * @since 0.7.0
     */
    public V8Flags setAllowNativesSyntax(boolean allowNativesSyntax) {
        if (!sealed) {
            this.allowNativesSyntax = allowNativesSyntax;
        }
        return this;
    }

    /**
     * Sets custom flags (space separated).
     *
     * @param customFlags the custom flags
     * @return the self
     * @since 0.9.13
     */
    public V8Flags setCustomFlags(String customFlags) {
        if (!sealed) {
            this.customFlags = customFlags == null ? null : customFlags.trim();
        }
        return this;
    }

    /**
     * Sets expose gc.
     *
     * @param exposeGC the expose gc
     * @return the self
     * @since 0.7.0
     */
    public V8Flags setExposeGC(boolean exposeGC) {
        if (!sealed) {
            this.exposeGC = exposeGC;
        }
        return this;
    }

    /**
     * Sets expose inspector scripts.
     *
     * @param exposeInspectorScripts the expose inspector scripts
     * @return the self
     * @since 0.7.0
     */
    public V8Flags setExposeInspectorScripts(boolean exposeInspectorScripts) {
        if (!sealed) {
            this.exposeInspectorScripts = exposeInspectorScripts;
        }
        return this;
    }

    /**
     * Sets icu data file.
     *
     * @param icuDataFile the icu data file
     * @return the icu data file
     * @since 4.0.0
     */
    public V8Flags setIcuDataFile(String icuDataFile) {
        if (!sealed) {
            this.icuDataFile = icuDataFile;
        }
        return this;
    }

    /**
     * Sets initial heap size.
     *
     * @param initialHeapSize the initial heap size
     * @return the self
     * @since 0.9.13
     */
    public V8Flags setInitialHeapSize(int initialHeapSize) {
        if (!sealed) {
            assert initialHeapSize >= 0 : "Initial heap size must be no less than 0";
            this.initialHeapSize = initialHeapSize;
        }
        return this;
    }

    /**
     * Sets js float 16 array.
     *
     * @param jsFloat16Array the js float 16 array
     * @return the self
     * @since 4.1.0
     */
    public V8Flags setJsFloat16Array(boolean jsFloat16Array) {
        if (!sealed) {
            this.jsFloat16Array = jsFloat16Array;
        }
        return this;
    }

    /**
     * Sets max heap size.
     * <p>
     * Currently, by default V8 has a memory limit of 512mb on 32-bit systems,
     * and 1gb on 64-bit systems.
     * The limit can be raised by setting --max-old-space-size to a maximum of
     * ~1gb (32-bit) and ~1.7gb (64-bit), but it is recommended that you split
     * your single process into several workers if you are hitting memory limits.
     * <p>
     * Please refer to <a href="https://v8.dev/blog/heap-size-limit">
     * One small step for Chrome, one giant heap for V8</a> for detail.
     *
     * @param maxHeapSize the max heap size
     * @return the self
     * @since 0.9.13
     */
    public V8Flags setMaxHeapSize(int maxHeapSize) {
        if (!sealed) {
            assert maxHeapSize >= 0 : "Max heap size must be no less than 0";
            this.maxHeapSize = maxHeapSize;
        }
        return this;
    }

    /**
     * Sets max old space size.
     *
     * @param maxOldSpaceSize the max old space size
     * @return the self
     * @since 0.9.13
     */
    public V8Flags setMaxOldSpaceSize(int maxOldSpaceSize) {
        if (!sealed) {
            assert maxOldSpaceSize >= 0 : "Max old space size must be no less than 0";
            this.maxOldSpaceSize = maxOldSpaceSize;
        }
        return this;
    }

    /**
     * Sets use strict.
     *
     * @param useStrict the use strict
     * @return the self
     * @since 0.7.0
     */
    public V8Flags setUseStrict(boolean useStrict) {
        if (!sealed) {
            this.useStrict = useStrict;
        }
        return this;
    }

    @Override
    public String toString() {
        List<String> tokens = new ArrayList<>();
        if (allowNativesSyntax) {
            tokens.add(FLAG_ALLOW_NATIVES_SYNTAX);
        }
        if (exposeGC) {
            tokens.add(FLAG_EXPOSE_GC);
        }
        if (exposeInspectorScripts) {
            tokens.add(FLAG_EXPOSE_INSPECTOR_SCRIPTS);
        }
        if (initialHeapSize > 0) {
            tokens.add(fromInteger(FLAG_INITIAL_HEAP_SIZE, initialHeapSize));
        }
        if (jsFloat16Array) {
            tokens.add(FLAG_JS_FLOAT_16_ARRAY);
        }
        if (maxHeapSize > 0) {
            tokens.add(fromInteger(FLAG_MAX_HEAP_SIZE, maxHeapSize));
        }
        if (maxOldSpaceSize > 0) {
            tokens.add(fromInteger(FLAG_MAX_OLD_SPACE_SIZE, maxOldSpaceSize));
        }
        if (useStrict) {
            tokens.add(FLAG_USE_STRICT);
        }
        tokens.sort(String::compareTo);
        if (StringUtils.isNotEmpty(customFlags)) {
            tokens.add(customFlags);
        }
        return StringUtils.join(SPACE, tokens);
    }
}
