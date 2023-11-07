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

package com.caoccao.javet.values.reference;

import com.caoccao.javet.annotations.CheckReturnValue;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.IV8Executable;
import com.caoccao.javet.values.V8Value;

/**
 * The interface IV8Module.
 *
 * @since 0.8.0
 */
@SuppressWarnings("unchecked")
public interface IV8Module extends IV8Cacheable, IV8ValueReference, IV8Executable {
    /**
     * The constant Uninstantiated.
     *
     * @since 0.8.0
     */
    int Uninstantiated = 0;
    /**
     * The constant Instantiating.
     *
     * @since 0.8.0
     */
    int Instantiating = 1;
    /**
     * The constant Instantiated.
     *
     * @since 0.8.0
     */
    int Instantiated = 2;
    /**
     * The constant Evaluating.
     *
     * @since 0.8.0
     */
    int Evaluating = 3;
    /**
     * The constant Evaluated.
     *
     * @since 0.8.0
     */
    int Evaluated = 4;
    /**
     * The constant Errored.
     *
     * @since 0.8.0
     */
    int Errored = 5;

    /**
     * Evaluate the module.
     *
     * @param <T> the type parameter
     * @return the t
     * @throws JavetException the javet exception
     * @since 0.8.0
     */
    @CheckReturnValue
    default <T extends V8Value> T evaluate() throws JavetException {
        return evaluate(true);
    }

    /**
     * Evaluate the module.
     *
     * @param <T>            the type parameter
     * @param resultRequired the result required
     * @return the t
     * @throws JavetException the javet exception
     * @since 0.8.0
     */
    @CheckReturnValue
    <T extends V8Value> T evaluate(boolean resultRequired) throws JavetException;

    @Override
    @CheckReturnValue
    default <T extends V8Value> T execute(boolean resultRequired) throws JavetException {
        if (getStatus() == Uninstantiated) {
            if (!instantiate()) {
                return (T) getV8Runtime().createV8ValueUndefined();
            }
        }
        if (getStatus() == Instantiated) {
            return evaluate(resultRequired);
        }
        return (T) getV8Runtime().createV8ValueUndefined();
    }

    /**
     * Gets exception.
     *
     * @return the exception
     * @throws JavetException the javet exception
     * @since 0.8.0
     */
    @CheckReturnValue
    V8ValueError getException() throws JavetException;

    /**
     * Gets namespace.
     * <p>
     * The module's status must be at least kInstantiated. Otherwise, core dump will take place.
     *
     * @return the namespace
     * @throws JavetException the javet exception
     * @since 0.8.0
     */
    @CheckReturnValue
    V8ValueObject getNamespace() throws JavetException;

    /**
     * Gets resource name.
     *
     * @return the resource name
     * @since 0.8.0
     */
    String getResourceName();

    /**
     * Gets script id.
     * <p>
     * The module must be a SourceTextModule and must not have a kErrored status.
     *
     * @return the script id
     * @throws JavetException the javet exception
     * @since 0.8.0
     */
    int getScriptId() throws JavetException;

    /**
     * Gets status.
     *
     * @return the status
     * @throws JavetException the javet exception
     * @since 0.8.0
     */
    int getStatus() throws JavetException;

    /**
     * Instantiate.
     *
     * @return true : instantiated, false : not instantiated
     * @throws JavetException the javet exception
     * @since 0.8.0
     */
    boolean instantiate() throws JavetException;

    /**
     * Is source text module.
     *
     * @return true : yes, false : no
     * @throws JavetException the javet exception
     * @since 3.0.1
     */
    boolean isSourceTextModule() throws JavetException;

    /**
     * Is synthetic module.
     *
     * @return true : yes, false : no
     * @throws JavetException the javet exception
     * @since 3.0.1
     */
    boolean isSyntheticModule() throws JavetException;

    /**
     * Sets resource name.
     *
     * @param resourceName the resource name
     * @since 0.8.0
     */
    void setResourceName(String resourceName);

    @Override
    default <T, V extends V8Value> T toObject(V v8Value) throws JavetException {
        return getV8Runtime().toObject(v8Value);
    }

    @Override
    @CheckReturnValue
    default <T, V extends V8Value> V toV8Value(T object) throws JavetException {
        return getV8Runtime().toV8Value(object);
    }
}
