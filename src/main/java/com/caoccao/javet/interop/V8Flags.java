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

package com.caoccao.javet.interop;

public final class V8Flags {
    private boolean allowNativesSyntax;
    private boolean exposeGC;
    private boolean exposeInspectorScripts;
    private boolean sealed;
    private boolean trackRetainingPath;
    private boolean useStrict;

    V8Flags() {
        allowNativesSyntax = false;
        exposeGC = false;
        exposeInspectorScripts = false;
        sealed = false;
        trackRetainingPath = false;
        useStrict = true;
    }

    public boolean isAllowNativesSyntax() {
        return allowNativesSyntax;
    }

    public boolean isExposeGC() {
        return exposeGC;
    }

    public boolean isExposeInspectorScripts() {
        return exposeInspectorScripts;
    }

    public boolean isSealed() {
        return sealed;
    }

    public boolean isTrackRetainingPath() {
        return trackRetainingPath;
    }

    public boolean isUseStrict() {
        return useStrict;
    }

    public void seal() {
        if (!sealed) {
            sealed = true;
        }
    }

    public void setAllowNativesSyntax(boolean allowNativesSyntax) {
        if (!sealed) {
            this.allowNativesSyntax = allowNativesSyntax;
        }
    }

    public void setExposeGC(boolean exposeGC) {
        if (!sealed) {
            this.exposeGC = exposeGC;
        }
    }

    public void setExposeInspectorScripts(boolean exposeInspectorScripts) {
        if (!sealed) {
            this.exposeInspectorScripts = exposeInspectorScripts;
        }
    }

    public void setTrackRetainingPath(boolean trackRetainingPath) {
        if (!sealed) {
            this.trackRetainingPath = trackRetainingPath;
        }
    }

    public void setUseStrict(boolean useStrict) {
        if (!sealed) {
            this.useStrict = useStrict;
        }
    }
}
