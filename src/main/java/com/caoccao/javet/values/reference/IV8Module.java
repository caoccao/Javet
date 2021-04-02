/*
 *    Copyright 2021. caoccao.com Sam Cao
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

package com.caoccao.javet.values.reference;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.IV8Executable;
import com.caoccao.javet.values.V8Value;

@SuppressWarnings("unchecked")
public interface IV8Module extends IV8ValueReference, IV8Executable {
    int Uninstantiated = 0;
    int Instantiating = 1;
    int Instantiated = 2;
    int Evaluating = 3;
    int Evaluated = 4;
    int Errored = 5;

    default <T extends V8Value> T evaluate() throws JavetException {
        return evaluate(true);
    }

    <T extends V8Value> T evaluate(boolean resultRequired) throws JavetException;

    @Override
    default <T extends V8Value> T execute(boolean resultRequired) throws JavetException {
        if (getStatus() == Uninstantiated) {
            if (!instantiate()) {
                return (T) getV8Runtime().createV8ValueUndefined();
            }
        }
        if (getStatus() == Instantiated) {
            return (T) evaluate(resultRequired);
        }
        return (T) getV8Runtime().createV8ValueUndefined();
    }

    V8ValueError getException() throws JavetException;

    String getResourceName();

    void setResourceName(String resourceName);

    /**
     * Gets script id.
     *
     * Note: This API is not supported by Node because the V8 version is too low.
     *
     * @return the script id
     * @throws JavetException the javet exception
     */
    int getScriptId() throws JavetException;

    int getStatus() throws JavetException;

    boolean instantiate() throws JavetException;
}
