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

package com.caoccao.javet.interop.options;

import com.caoccao.javet.utils.ArrayUtils;
import com.caoccao.javet.utils.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

/**
 * The type Node flags defines some built-in Node command line options.
 * The complete command line options are at https://nodejs.org/docs/latest/api/cli.html#options
 *
 * @since 4.0.0
 */
public final class NodeFlags {
    /**
     * The constant ALLOW_FS_READ.
     *
     * @since 4.0.0
     */
    public static final String ALLOW_FS_READ = "--allow-fs-read";
    /**
     * The constant ALLOW_FS_WRITE.
     *
     * @since 4.0.0
     */
    public static final String ALLOW_FS_WRITE = "--allow-fs-write";
    /**
     * The constant EXPERIMENTAL_PERMISSION.
     *
     * @since 4.0.0
     */
    public static final String EXPERIMENTAL_PERMISSION = "--experimental-permission";
    /**
     * The constant NO_EXPERIMENTAL_REQUIRE_MODULE.
     *
     * @since 4.1.1
     */
    public static final String NO_EXPERIMENTAL_REQUIRE_MODULE = "--no-experimental-require-module";
    /**
     * The constant EXPERIMENTAL_SQLITE.
     *
     * @since 4.0.0
     */
    public static final String EXPERIMENTAL_SQLITE = "--experimental-sqlite";
    /**
     * The constant ICU_DATA_DIR.
     *
     * @since 4.0.0
     */
    public static final String ICU_DATA_DIR = "--icu-data-dir";
    /**
     * The constant JS_FLOAT_16_ARRAY.
     *
     * @since 4.1.0
     */
    public static final String JS_FLOAT_16_ARRAY = "--js-float16array";
    /**
     * The constant NO_WARNINGS.
     *
     * @since 4.0.0
     */
    public static final String NO_WARNINGS = "--no-warnings";
    private static final String EQUAL = "=";
    private static final String SPACE = " ";
    private String[] allowFsRead;
    private String[] allowFsWrite;
    private String[] customFlags;
    private boolean experimentalPermission;
    private boolean experimentalSqlite;
    private String icuDataDir;
    private boolean jsFloat16Array;
    private boolean noExperimentalRequireModule;
    private boolean noWarnings;
    private boolean sealed;

    /**
     * Instantiates a new Node flags.
     *
     * @since 4.0.0
     */
    NodeFlags() {
        allowFsRead = null;
        allowFsWrite = null;
        customFlags = null;
        experimentalPermission = false;
        noExperimentalRequireModule = false;
        experimentalSqlite = false;
        jsFloat16Array = false;
        noWarnings = false;
        sealed = false;
    }

    /**
     * This flag configures file system read permissions using the Permission Model.
     * <p>
     * The valid arguments for the --allow-fs-read flag are:
     * <p>
     * - To allow all FileSystemRead operations.
     * Multiple paths can be allowed using multiple --allow-fs-read flags.
     * Example --allow-fs-read=/folder1/ --allow-fs-read=/folder1/
     *
     * @return the string[]
     * @since 4.0.0
     */
    public String[] getAllowFsRead() {
        return allowFsRead;
    }

    /**
     * This flag configures file system write permissions using the Permission Model.
     * <p>
     * The valid arguments for the --allow-fs-write flag are:
     * <p>
     * - To allow all FileSystemWrite operations.
     * Multiple paths can be allowed using multiple --allow-fs-write flags.
     * Example --allow-fs-write=/folder1/ --allow-fs-write=/folder1/
     *
     * @return the string[]
     * @since 4.0.0
     */
    public String[] getAllowFsWrite() {
        return allowFsWrite;
    }

    /**
     * Gets custom flags.
     *
     * @return the custom flags
     * @since 4.0.0
     */
    public String[] getCustomFlags() {
        return customFlags;
    }

    /**
     * Gets icu data dir that contains icudt*.dat files.
     *
     * @return the icu data dir
     * @since 4.0.0
     */
    public String getIcuDataDir() {
        return icuDataDir;
    }

    /**
     * Enable the Permission Model for current process. When enabled, the following permissions are restricted:
     * <p>
     * File System - manageable through --allow-fs-read, --allow-fs-write flags
     * Child Process - manageable through --allow-child-process flag
     * Worker Threads - manageable through --allow-worker flag
     * WASI - manageable through --allow-wasi flag
     *
     * @return true : yes, false: no
     * @since 4.0.0
     */
    public boolean isExperimentalPermission() {
        return experimentalPermission;
    }

    /**
     * Is the experimental node:sqlite module enabled.
     *
     * @return true : yes, false: no
     * @since 4.0.0
     */
    public boolean isExperimentalSqlite() {
        return experimentalSqlite;
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
     * Does not support loading a synchronous ES module graph in require().
     *
     * @return true : yes, false: no
     * @since 4.1.1
     */
    public boolean isNoExperimentalRequireModule() {
        return noExperimentalRequireModule;
    }

    /**
     * Silence all process warnings (including deprecations).
     *
     * @return true : yes, false: no
     * @since 4.0.0
     */
    public boolean isNoWarnings() {
        return noWarnings;
    }

    /**
     * Is sealed.
     *
     * @return true : yes, false: no
     * @since 4.0.0
     */
    public boolean isSealed() {
        return sealed;
    }

    /**
     * Seal the Node flags so that it is read-only.
     *
     * @return the self
     * @since 4.0.0
     */
    public NodeFlags seal() {
        if (!sealed) {
            sealed = true;
        }
        return this;
    }

    /**
     * Sets allow fs read.
     *
     * @param allowFsRead the allow fs read
     * @return the self
     * @since 4.0.0
     */
    public NodeFlags setAllowFsRead(String[] allowFsRead) {
        if (!sealed) {
            if (ArrayUtils.isEmpty(allowFsRead)) {
                this.allowFsRead = null;
            } else {
                this.allowFsRead = Stream.of(allowFsRead)
                        .filter(StringUtils::isNotBlank)
                        .toArray(String[]::new);
                this.experimentalPermission = true;
            }
        }
        return this;
    }

    /**
     * Sets allow fs write.
     *
     * @param allowFsWrite the allow fs write
     * @return the self
     * @since 4.0.0
     */
    public NodeFlags setAllowFsWrite(String[] allowFsWrite) {
        if (!sealed) {
            if (ArrayUtils.isEmpty(allowFsWrite)) {
                this.allowFsWrite = null;
            } else {
                this.allowFsWrite = Stream.of(allowFsWrite)
                        .filter(StringUtils::isNotBlank)
                        .toArray(String[]::new);
                this.experimentalPermission = true;
            }
        }
        return this;
    }

    /**
     * Sets custom flags (space separated).
     *
     * @param customFlags the custom flags
     * @return the self
     * @since 4.0.0
     */
    public NodeFlags setCustomFlags(String[] customFlags) {
        if (!sealed) {
            if (ArrayUtils.isEmpty(customFlags)) {
                this.customFlags = null;
            } else {
                this.customFlags = Stream.of(customFlags)
                        .filter(StringUtils::isNotBlank)
                        .toArray(String[]::new);
            }
        }
        return this;
    }

    /**
     * Sets experimental permission.
     *
     * @param experimentalPermission the experimental permission
     * @return the self
     * @since 4.0.0
     */
    public NodeFlags setExperimentalPermission(boolean experimentalPermission) {
        if (!sealed) {
            this.experimentalPermission = experimentalPermission;
        }
        return this;
    }

    /**
     * Sets experimental sqlite.
     *
     * @param experimentalSqlite the experimental sqlite
     * @return the self
     * @since 4.0.0
     */
    public NodeFlags setExperimentalSqlite(boolean experimentalSqlite) {
        if (!sealed) {
            this.experimentalSqlite = experimentalSqlite;
        }
        return this;
    }

    /**
     * Sets icu data dir.
     *
     * @param icuDataDir the icu data dir
     * @return the self
     * @since 4.0.0
     */
    public NodeFlags setIcuDataDir(String icuDataDir) {
        if (!sealed) {
            this.icuDataDir = icuDataDir;
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
    public NodeFlags setJsFloat16Array(boolean jsFloat16Array) {
        if (!sealed) {
            this.jsFloat16Array = jsFloat16Array;
        }
        return this;
    }

    /**
     * Sets no experimental require module.
     *
     * @param noExperimentalRequireModule the no experimental require module
     * @return the self
     * @since 4.1.1
     */
    public NodeFlags setNoExperimentalRequireModule(boolean noExperimentalRequireModule) {
        if (!sealed) {
            this.noExperimentalRequireModule = noExperimentalRequireModule;
        }
        return this;
    }

    /**
     * Sets no warnings.
     *
     * @param noWarnings the no warnings
     * @return the self
     * @since 4.0.0
     */
    public NodeFlags setNoWarnings(boolean noWarnings) {
        if (!sealed) {
            this.noWarnings = noWarnings;
        }
        return this;
    }

    /**
     * To string array.
     *
     * @return the string array
     */
    public String[] toArray() {
        List<String> tokens = new ArrayList<>();
        if (ArrayUtils.isNotEmpty(allowFsRead)) {
            Stream.of(allowFsRead)
                    .filter(StringUtils::isNotBlank)
                    .map(path -> ALLOW_FS_READ + EQUAL + path.trim())
                    .forEach(tokens::add);
        }
        if (ArrayUtils.isNotEmpty(allowFsWrite)) {
            Stream.of(allowFsWrite)
                    .filter(StringUtils::isNotBlank)
                    .map(path -> ALLOW_FS_WRITE + EQUAL + path.trim())
                    .forEach(tokens::add);
        }
        if (experimentalPermission) {
            tokens.add(EXPERIMENTAL_PERMISSION);
        }
        if (noExperimentalRequireModule) {
            tokens.add(NO_EXPERIMENTAL_REQUIRE_MODULE);
        }
        if (experimentalSqlite) {
            tokens.add(EXPERIMENTAL_SQLITE);
        }
        if (StringUtils.isNotBlank(icuDataDir)) {
            tokens.add(ICU_DATA_DIR + EQUAL + icuDataDir.trim());
        }
        if (jsFloat16Array) {
            tokens.add(JS_FLOAT_16_ARRAY);
        }
        if (noWarnings) {
            tokens.add(NO_WARNINGS);
        }
        tokens.sort(String::compareTo);
        if (ArrayUtils.isNotEmpty(customFlags)) {
            Collections.addAll(tokens, customFlags);
        }
        return tokens.toArray(new String[0]);
    }

    @Override
    public String toString() {
        return StringUtils.join(SPACE, toArray());
    }
}
