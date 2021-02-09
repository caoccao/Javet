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

package com.caoccao.javet.values.reference;

import com.caoccao.javet.exceptions.JavetException;

@SuppressWarnings("unchecked")
public final class V8ValueGlobalObject extends V8ValueObject {
    public V8ValueGlobalObject(long handle) {
        super(handle);
    }

    @Override
    protected void addReference() {
        // Global object lives as long as V8 runtime lives.
    }

    @Override
    public void clearWeak() throws JavetException {
    }

    @Override
    public void close(boolean forceClose) throws JavetException {
        // Global object lives as long as V8 runtime lives.
    }

    @Override
    public boolean isWeak() throws JavetException {
        return false;
    }

    @Override
    protected void removeReference() {
        // Global object lives as long as V8 runtime lives.
    }

    @Override
    public void setWeak() throws JavetException {
    }

    @Override
    public V8ValueGlobalObject toClone() {
        return this;
    }
}
