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
    protected boolean createSnapshotEnabled;
    /**
     * The Snapshot blob.
     *
     * @since 3.0.3
     */
    protected byte[] snapshotBlob;

    /**
     * Instantiates a new Runtime options.
     *
     * @since 1.0.0
     */
    public RuntimeOptions() {
        createSnapshotEnabled = false;
        snapshotBlob = null;
    }

    /**
     * Get snapshot blob in byte array.
     *
     * @return the byte array
     * @since 3.0.3
     */
    public byte[] getSnapshotBlob() {
        return snapshotBlob;
    }

    /**
     * Is create snapshot enabled.
     *
     * @return true : enabled, false : disabled
     * @since 3.0.3
     */
    public boolean isCreateSnapshotEnabled() {
        return createSnapshotEnabled;
    }

    /**
     * Sets create snapshot enabled.
     *
     * @param createSnapshotEnabled the create snapshot enabled
     * @return the self
     * @since 3.0.3
     */
    public RuntimeOptions<Options> setCreateSnapshotEnabled(boolean createSnapshotEnabled) {
        this.createSnapshotEnabled = createSnapshotEnabled;
        return this;
    }

    /**
     * Sets snapshot blob.
     *
     * @param snapshotBlob the snapshot blob
     * @return the self
     * @since 3.0.3
     */
    public RuntimeOptions<Options> setSnapshotBlob(byte[] snapshotBlob) {
        this.snapshotBlob = snapshotBlob;
        return this;
    }
}
