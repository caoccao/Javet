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

package com.caoccao.javet.values.reference;

import com.caoccao.javet.annotations.CheckReturnValue;
import com.caoccao.javet.enums.V8ContextType;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.primitive.V8ValueNull;
import com.caoccao.javet.values.primitive.V8ValuePrimitive;
import com.caoccao.javet.values.primitive.V8ValueUndefined;

import java.math.BigInteger;

/**
 * The interface V8 context.
 *
 * @since 2.0.1
 */
@SuppressWarnings("unchecked")
public interface IV8Context extends IV8ValueReference {

    /**
     * Gets element by index.
     *
     * @param <T>   the type parameter
     * @param index the index
     * @return the element
     * @throws JavetException the javet exception
     * @since 2.0.1
     */
    @CheckReturnValue
    <T extends V8Value> T get(int index) throws JavetException;

    /**
     * Gets element as big integer by index.
     *
     * @param index the index
     * @return the element as big integer
     * @throws JavetException the javet exception
     * @since 2.0.1
     */
    default BigInteger getBigInteger(int index) throws JavetException {
        return getPrimitive(index);
    }

    /**
     * Gets element as boolean by key object.
     *
     * @param index the index
     * @return the element as boolean
     * @throws JavetException the javet exception
     * @since 2.0.1
     */
    default Boolean getBoolean(int index) throws JavetException {
        return getPrimitive(index);
    }

    /**
     * Gets element as double by key object.
     *
     * @param index the index
     * @return the element as double
     * @throws JavetException the javet exception
     * @since 2.0.1
     */
    default Double getDouble(int index) throws JavetException {
        return getPrimitive(index);
    }

    /**
     * Gets element as float by key object.
     *
     * @param index the index
     * @return the element as float
     * @throws JavetException the javet exception
     * @since 2.0.1
     */
    default Float getFloat(int index) throws JavetException {
        Double result = getDouble(index);
        return result == null ? null : result.floatValue();
    }

    /**
     * Gets element as integer by key object.
     *
     * @param index the index
     * @return the element as integer
     * @throws JavetException the javet exception
     * @since 2.0.1
     */
    default Integer getInteger(int index) throws JavetException {
        return getPrimitive(index);
    }

    /**
     * Gets the element length.
     *
     * @return the element length
     * @throws JavetException the javet exception
     * @since 2.0.1
     */
    int getLength() throws JavetException;

    /**
     * Gets element as long by key object.
     *
     * @param index the index
     * @return the element as long
     * @throws JavetException the javet exception
     * @since 2.0.1
     */
    default Long getLong(int index) throws JavetException {
        return getPrimitive(index);
    }

    /**
     * Gets element as null by key object.
     *
     * @param index the index
     * @return the element as null
     * @throws JavetException the javet exception
     * @since 2.0.1
     */
    default V8ValueNull getNull(int index) throws JavetException {
        return get(index);
    }

    /**
     * Gets element as object by key object.
     *
     * @param <T>   the type parameter
     * @param index the index
     * @return the element as object
     * @throws JavetException the javet exception
     * @since 2.0.1
     */
    default <T> T getObject(int index) throws JavetException {
        try {
            return getV8Runtime().toObject(get(index), true);
        } catch (JavetException e) {
            throw e;
        } catch (Throwable t) {
            return null;
        }
    }

    /**
     * Gets element as primitive by index.
     *
     * @param <R>   the type parameter
     * @param <T>   the type parameter
     * @param index the index
     * @return the element as primitive
     * @throws JavetException the javet exception
     * @since 2.0.1
     */
    default <R, T extends V8ValuePrimitive<R>> R getPrimitive(int index) throws JavetException {
        try (V8Value v8Value = get(index)) {
            return ((T) v8Value).getValue();
        } catch (JavetException e) {
            throw e;
        } catch (Throwable ignored) {
        }
        return null;
    }

    /**
     * Gets string by index.
     *
     * @param index the index
     * @return the string
     * @throws JavetException the javet exception
     * @since 2.0.1
     */
    default String getString(int index) throws JavetException {
        return getPrimitive(index);
    }

    /**
     * Gets undefined by index.
     *
     * @param index the index
     * @return the undefined
     * @throws JavetException the javet exception
     * @since 2.0.1
     */
    default V8ValueUndefined getUndefined(int index) throws JavetException {
        return get(index);
    }

    /**
     * Is Await context.
     *
     * @return true : yes, false : no
     * @throws JavetException the javet exception
     * @since 2.0.1
     */
    default boolean isAwaitContext() throws JavetException {
        return isContextType(V8ContextType.Await);
    }

    /**
     * Is Block context.
     *
     * @return true : yes, false : no
     * @throws JavetException the javet exception
     * @since 2.0.1
     */
    default boolean isBlockContext() throws JavetException {
        return isContextType(V8ContextType.Block);
    }

    /**
     * Is Catch context.
     *
     * @return true : yes, false : no
     * @throws JavetException the javet exception
     * @since 2.0.1
     */
    default boolean isCatchContext() throws JavetException {
        return isContextType(V8ContextType.Catch);
    }

    /**
     * Is the given context type.
     *
     * @param v8ContextType the V8 context type
     * @return true : yes, false : no
     * @throws JavetException the javet exception
     * @since 2.0.1
     */
    boolean isContextType(V8ContextType v8ContextType) throws JavetException;

    /**
     * Is DebugEvaluate context.
     *
     * @return true : yes, false : no
     * @throws JavetException the javet exception
     * @since 2.0.1
     */
    default boolean isDebugEvaluateContext() throws JavetException {
        return isContextType(V8ContextType.DebugEvaluate);
    }

    /**
     * Is Declaration context.
     *
     * @return true : yes, false : no
     * @throws JavetException the javet exception
     * @since 2.0.1
     */
    default boolean isDeclarationContext() throws JavetException {
        return isContextType(V8ContextType.Declaration);
    }

    /**
     * Is Eval context.
     *
     * @return true : yes, false : no
     * @throws JavetException the javet exception
     * @since 2.0.1
     */
    default boolean isEvalContext() throws JavetException {
        return isContextType(V8ContextType.Eval);
    }

    /**
     * Is Function context.
     *
     * @return true : yes, false : no
     * @throws JavetException the javet exception
     * @since 2.0.1
     */
    default boolean isFunctionContext() throws JavetException {
        return isContextType(V8ContextType.Function);
    }

    /**
     * Is Module context.
     *
     * @return true : yes, false : no
     * @throws JavetException the javet exception
     * @since 2.0.1
     */
    default boolean isModuleContext() throws JavetException {
        return isContextType(V8ContextType.Module);
    }

    /**
     * Is Script context.
     *
     * @return true : yes, false : no
     * @throws JavetException the javet exception
     * @since 2.0.1
     */
    default boolean isScriptContext() throws JavetException {
        return isContextType(V8ContextType.Script);
    }

    /**
     * Is With context.
     *
     * @return true : yes, false : no
     * @throws JavetException the javet exception
     * @since 2.0.1
     */
    default boolean isWithContext() throws JavetException {
        return isContextType(V8ContextType.With);
    }

    /**
     * Sets the element length.
     *
     * @param length the element length
     * @return true : success, false: failure
     * @throws JavetException the javet exception
     * @since 2.0.1
     */
    boolean setLength(int length) throws JavetException;
}
