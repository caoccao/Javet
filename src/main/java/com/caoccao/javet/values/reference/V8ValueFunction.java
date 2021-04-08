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
import com.caoccao.javet.exceptions.JavetV8CallbackAlreadyRegisteredException;
import com.caoccao.javet.exceptions.JavetV8CallbackSignatureMismatchException;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.utils.JavetCallbackContext;
import com.caoccao.javet.utils.JavetResourceUtils;
import com.caoccao.javet.utils.converters.IJavetConverter;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.V8ValueReferenceType;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("unchecked")
public class V8ValueFunction extends V8ValueObject implements IV8ValueFunction {
    /*
     * The lifecycle of V8ValueFunction depends on V8CallbackContext which is
     * managed by JNI native implementation.
     */
    protected JavetCallbackContext javetCallbackContext;

    V8ValueFunction(long handle) {
        super(handle);
        javetCallbackContext = null;
    }

    public JavetCallbackContext getV8CallbackContext() {
        return javetCallbackContext;
    }

    public void setV8CallbackContext(JavetCallbackContext javetCallbackContext)
            throws JavetV8CallbackAlreadyRegisteredException {
        Objects.requireNonNull(javetCallbackContext);
        if (this.javetCallbackContext == null) {
            this.javetCallbackContext = javetCallbackContext;
        } else if (this.javetCallbackContext != javetCallbackContext) {
            throw new JavetV8CallbackAlreadyRegisteredException();
        }
    }

    @Override
    public <T extends V8Value> T call(IV8ValueObject receiver, boolean returnResult, V8Value... v8Values)
            throws JavetException {
        checkV8Runtime();
        return v8Runtime.call(this, receiver, returnResult, v8Values);
    }

    @Override
    public <T extends V8Value> T callAsConstructor(V8Value... v8Values) throws JavetException {
        checkV8Runtime();
        return v8Runtime.callAsConstructor(this, v8Values);
    }

    @Override
    public void close(boolean forceClose) throws JavetException {
        if (forceClose || !isWeak()) {
            if (javetCallbackContext != null) {
                v8Runtime.removeJNIGlobalRef(javetCallbackContext.getHandle());
                javetCallbackContext = null;
            } else {
                /*
                 * Function from V8 loses the callback context.
                 * So there is no need to recycle anything.
                 */
            }
            super.close(forceClose);
        }
    }

    @Override
    public int getType() {
        return V8ValueReferenceType.Function;
    }
}
