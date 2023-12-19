/*
 * Copyright (c) 2021-2023. caoccao.com Sam Cao
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

/**
 * The type Runtime options.
 *
 * @param <Options> the type parameter
 * @since 1.0.0
 */
public abstract class RuntimeOptions<Options extends RuntimeOptions<Options>> {
    /**
     * The Snapshot enabled flag indicates whether the snapshot feature is enabled or not.
     * It is disabled by default.
     *
     * @since 3.0.3
     */
    protected boolean snapshotEnabled;

    /**
     * Instantiates a new Runtime options.
     *
     * @since 1.0.0
     */
    public RuntimeOptions() {
        snapshotEnabled = false;
    }

    /**
     * Is snapshot enabled.
     *
     * @return true : enabled, false : disabled
     * @since 3.0.3
     */
    public boolean isSnapshotEnabled() {
        return snapshotEnabled;
    }

    /**
     * Sets snapshot enabled.
     *
     * @param snapshotEnabled the snapshot enabled
     * @return the self
     * @since 3.0.3
     */
    public RuntimeOptions<Options> setSnapshotEnabled(boolean snapshotEnabled) {
        this.snapshotEnabled = snapshotEnabled;
        return this;
    }
}
