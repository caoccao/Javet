/*
 * Copyright (c) 2021-2025. caoccao.com Sam Cao
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
import com.caoccao.javet.enums.JSFunctionType;
import com.caoccao.javet.enums.JSScopeType;
import com.caoccao.javet.enums.V8ScopeType;
import com.caoccao.javet.enums.V8ValueInternalType;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interfaces.IJavetClosable;
import com.caoccao.javet.utils.StringUtils;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.primitive.V8ValueBigInteger;
import com.caoccao.javet.values.primitive.V8ValueZonedDateTime;

import java.math.BigInteger;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * The interface V8 value function.
 *
 * @since 0.7.0
 */
public interface IV8ValueFunction extends IV8Cacheable, IV8ValueObject {

    /**
     * Call a function by {@link Object}[] and return {@link V8Value}.
     *
     * @param <T>      the type parameter
     * @param receiver the receiver
     * @param objects  the objects
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 0.8.5
     */
    @CheckReturnValue
    default <T extends V8Value> T call(V8Value receiver, Object... objects) throws JavetException {
        return callExtended(receiver, true, objects);
    }

    /**
     * Call a function by {@link V8Value}[] and return {@link V8Value}.
     *
     * @param <T>      the type parameter
     * @param receiver the receiver
     * @param v8Values the V8 values
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 0.8.5
     */
    @CheckReturnValue
    default <T extends V8Value> T call(V8Value receiver, V8Value... v8Values) throws JavetException {
        return callExtended(receiver, true, (Object[]) v8Values);
    }

    /**
     * Call a function as a constructor by {@link Object}[].
     *
     * @param <T>     the type parameter
     * @param objects the objects
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 0.8.5
     */
    @CheckReturnValue
    <T extends V8Value> T callAsConstructor(Object... objects) throws JavetException;

    /**
     * Call a function as a constructor by {@link V8Value}[].
     *
     * @param <T>      the type parameter
     * @param v8Values the V8 values
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 0.7.2
     */
    @CheckReturnValue
    <T extends V8Value> T callAsConstructor(V8Value... v8Values) throws JavetException;

    /**
     * Call a function by objects and return {@link BigInteger}.
     *
     * @param receiver the receiver
     * @param objects  the objects
     * @return the BigInteger
     * @throws JavetException the javet exception
     * @since 1.1.5
     */
    default BigInteger callBigInteger(V8Value receiver, Object... objects) throws JavetException {
        try (V8Value v8Value = callExtended(receiver, true, objects)) {
            if (v8Value instanceof V8ValueBigInteger) {
                return ((V8ValueBigInteger) v8Value).getValue();
            }
        } catch (JavetException e) {
            throw e;
        } catch (Throwable ignored) {
        }
        return null;
    }

    /**
     * Call a function by objects and return {@link Boolean}.
     *
     * @param receiver the receiver
     * @param objects  the objects
     * @return the boolean
     * @throws JavetException the javet exception
     * @since 0.8.5
     */
    default Boolean callBoolean(V8Value receiver, Object... objects) throws JavetException {
        try (V8Value v8Value = callExtended(receiver, true, objects)) {
            return v8Value.asBoolean();
        } catch (JavetException e) {
            throw e;
        } catch (Throwable t) {
            return null;
        }
    }

    /**
     * Call a function by objects and return {@link Double}.
     *
     * @param receiver the receiver
     * @param objects  the objects
     * @return the double
     * @throws JavetException the javet exception
     * @since 0.8.5
     */
    default Double callDouble(V8Value receiver, Object... objects) throws JavetException {
        try (V8Value v8Value = callExtended(receiver, true, objects)) {
            return v8Value.asDouble();
        } catch (JavetException e) {
            throw e;
        } catch (Throwable t) {
            return null;
        }
    }

    /**
     * Call a function by objects and return {@link V8Value}.
     *
     * @param <T>          the type parameter
     * @param receiver     the receiver
     * @param returnResult the return result
     * @param objects      the objects
     * @return the t
     * @throws JavetException the javet exception
     * @since 0.8.5
     */
    @CheckReturnValue
    <T extends V8Value> T callExtended(V8Value receiver, boolean returnResult, Object... objects)
            throws JavetException;

    /**
     * Call a function by V8 values and return {@link V8Value}.
     *
     * @param <T>          the type parameter
     * @param receiver     the receiver
     * @param returnResult the return result
     * @param v8Values     the V8 values
     * @return the t
     * @throws JavetException the javet exception
     * @since 0.8.5
     */
    @CheckReturnValue
    <T extends V8Value> T callExtended(V8Value receiver, boolean returnResult, V8Value... v8Values)
            throws JavetException;

    /**
     * Call a function by objects and return {@link Float}.
     *
     * @param receiver the receiver
     * @param objects  the objects
     * @return the float
     * @throws JavetException the javet exception
     * @since 0.8.5
     */
    default Float callFloat(V8Value receiver, Object... objects) throws JavetException {
        Double result = callDouble(receiver, objects);
        return result == null ? null : result.floatValue();
    }

    /**
     * Call a function by objects and return {@link Integer}.
     *
     * @param receiver the receiver
     * @param objects  the objects
     * @return the integer
     * @throws JavetException the javet exception
     * @since 0.8.5
     */
    default Integer callInteger(V8Value receiver, Object... objects) throws JavetException {
        try (V8Value v8Value = callExtended(receiver, true, objects)) {
            return v8Value.asInt();
        } catch (JavetException e) {
            throw e;
        } catch (Throwable t) {
            return null;
        }
    }

    /**
     * Call a function by objects and return {@link Long}.
     *
     * @param receiver the receiver
     * @param objects  the objects
     * @return the long
     * @throws JavetException the javet exception
     * @since 0.8.5
     */
    default Long callLong(V8Value receiver, Object... objects) throws JavetException {
        try (V8Value v8Value = callExtended(receiver, true, objects)) {
            return v8Value.asLong();
        } catch (JavetException e) {
            throw e;
        } catch (Throwable t) {
            return null;
        }
    }

    /**
     * Call a function by objects and return an object.
     *
     * @param <T>      the type parameter
     * @param receiver the receiver
     * @param objects  the objects
     * @return the object
     * @throws JavetException the javet exception
     * @since 0.8.5
     */
    default <T> T callObject(V8Value receiver, Object... objects) throws JavetException {
        try {
            return getV8Runtime().toObject(callExtended(receiver, true, objects), true);
        } catch (JavetException e) {
            throw e;
        } catch (Throwable t) {
            return null;
        }
    }

    /**
     * Call a function by objects and return {@link String}.
     *
     * @param receiver the receiver
     * @param objects  the objects
     * @return the string
     * @throws JavetException the javet exception
     * @since 0.8.5
     */
    default String callString(V8Value receiver, Object... objects) throws JavetException {
        try (V8Value v8Value = callExtended(receiver, true, objects)) {
            if (v8Value.isNullOrUndefined()) {
                return null;
            }
            return v8Value.asString();
        } catch (JavetException e) {
            throw e;
        } catch (Throwable ignored) {
        }
        return null;
    }

    /**
     * Call a function by objects without a return.
     *
     * @param receiver the receiver
     * @param objects  the objects
     * @throws JavetException the javet exception
     * @since 0.8.5
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    default void callVoid(V8Value receiver, Object... objects) throws JavetException {
        callExtended(receiver, false, objects);
    }

    /**
     * Call a function by V8 values without a return.
     *
     * @param receiver the receiver
     * @param v8Values the V8 values
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    default void callVoid(V8Value receiver, V8Value... v8Values) throws JavetException {
        callExtended(receiver, false, (Object[]) v8Values);
    }

    /**
     * Call a function by objects and return {@link ZonedDateTime}.
     *
     * @param receiver the receiver
     * @param objects  the objects
     * @return the zoned date time
     * @throws JavetException the javet exception
     * @since 1.1.5
     */
    default ZonedDateTime callZonedDateTime(V8Value receiver, Object... objects) throws JavetException {
        try (V8Value v8Value = callExtended(receiver, true, objects)) {
            if (v8Value instanceof V8ValueZonedDateTime) {
                return ((V8ValueZonedDateTime) v8Value).getValue();
            }
        } catch (JavetException e) {
            throw e;
        } catch (Throwable ignored) {
        }
        return null;
    }

    /**
     * Can discard compiled byte code.
     *
     * @return true : yes, false : no
     * @throws JavetException the javet exception
     * @since 2.0.1
     */
    boolean canDiscardCompiled() throws JavetException;

    /**
     * Copy the context from the source V8 value function.
     * <p>
     * This allows changing the existing function context on the fly.
     * It is similar to the live edit in a JavaScript debug tool.
     *
     * @param sourceIV8ValueFunction the source V8 value function
     * @return true : copied, false : not copied
     * @throws JavetException the javet exception
     * @since 2.0.1
     */
    default boolean copyContextFrom(IV8ValueFunction sourceIV8ValueFunction) throws JavetException {
        try (V8Context v8Context = sourceIV8ValueFunction.getContext()) {
            return setContext(v8Context);
        }
    }

    /**
     * Copy the scope info from the source V8 value function.
     * <p>
     * This allows changing the existing function scope info on the fly.
     * It is similar to the live edit in a JavaScript debug tool.
     *
     * @param sourceIV8ValueFunction the source V8 value function
     * @return true : copied, false : not copied
     * @throws JavetException the javet exception
     * @since 2.0.1
     */
    boolean copyScopeInfoFrom(IV8ValueFunction sourceIV8ValueFunction) throws JavetException;

    /**
     * Discard compiled byte code.
     *
     * @return true : discarded, false : not discarded
     * @throws JavetException the javet exception
     * @since 2.0.1
     */
    boolean discardCompiled() throws JavetException;

    /**
     * Gets arguments.
     *
     * @return the arguments
     * @throws JavetException the javet exception
     * @since 2.0.3
     */
    String[] getArguments() throws JavetException;

    /**
     * Gets the V8 context.
     *
     * @return the V8 context
     * @throws JavetException the javet exception
     * @since 2.0.1
     */
    V8Context getContext() throws JavetException;

    /**
     * Gets internal properties.
     *
     * @return the internal properties
     * @throws JavetException the javet exception
     * @since 0.8.8
     */
    @CheckReturnValue
    IV8ValueArray getInternalProperties() throws JavetException;

    /**
     * Gets {@link JSFunctionType}.
     *
     * @return the JS function type
     * @throws JavetException the javet exception
     * @since 0.8.8
     */
    JSFunctionType getJSFunctionType() throws JavetException;

    /**
     * Gets {@link JSScopeType}.
     * <p>
     * Note: If a function is not properly initialized, this call may crash the whole JVM.
     *
     * @return the JS scope type
     * @throws JavetException the javet exception
     * @since 0.8.8
     */
    JSScopeType getJSScopeType() throws JavetException;

    /**
     * Gets {@link ScopeInfos}.
     *
     * @return the scope infos
     * @throws JavetException the javet exception
     * @since 2.0.2
     */
    @CheckReturnValue
    default ScopeInfos getScopeInfos() throws JavetException {
        return getScopeInfos(GetScopeInfosOptions.Default);
    }

    /**
     * Gets {@link ScopeInfos} by {@link GetScopeInfosOptions}.
     *
     * @param options the options
     * @return the scope infos
     * @throws JavetException the javet exception
     * @since 2.0.2
     */
    @CheckReturnValue
    ScopeInfos getScopeInfos(GetScopeInfosOptions options) throws JavetException;

    /**
     * Gets {@link ScriptSource}.
     * <p>
     * A user-defined JavaScript function is part of a script from start position to end position.
     * This method returns the source code of the whole script with the start position and end position.
     * If it is not a user-defined JavaScript function, the return value is null.
     *
     * @return the script source
     * @throws JavetException the javet exception
     * @since 2.0.1
     */
    ScriptSource getScriptSource() throws JavetException;

    /**
     * Gets source code.
     *
     * @return the source code
     * @throws JavetException the javet exception
     * @since 0.8.8
     */
    String getSourceCode() throws JavetException;

    /**
     * Is async function.
     *
     * @return true : yes, false: no
     * @throws JavetException the javet exception
     * @since 0.9.13
     */
    default boolean isAsyncFunction() throws JavetException {
        return hasInternalType(V8ValueInternalType.AsyncFunction);
    }

    /**
     * Is this function compiled.
     *
     * @return true : yes, false : no
     * @throws JavetException the javet exception
     * @since 2.0.1
     */
    boolean isCompiled() throws JavetException;

    /**
     * Is generator function.
     *
     * @return true : yes, false: no
     * @throws JavetException the javet exception
     * @since 0.9.13
     */
    default boolean isGeneratorFunction() throws JavetException {
        return hasInternalType(V8ValueInternalType.GeneratorFunction);
    }

    /**
     * Is wrapped function.
     * <p>
     * Wrapped function means the source code is wrapped in the function.
     *
     * @return true : yes, false : no
     * @throws JavetException the javet exception
     * @since 2.0.3
     */
    boolean isWrapped() throws JavetException;

    /**
     * Sets the V8 context.
     *
     * @param v8Context the V8 context
     * @return the V8 context
     * @throws JavetException the javet exception
     * @since 2.0.1
     */
    boolean setContext(V8Context v8Context) throws JavetException;

    /**
     * Sets script source.
     *
     * @param scriptSource the script source
     * @return true : success, false : failure
     * @throws JavetException the javet exception
     * @since 2.0.1
     */
    default boolean setScriptSource(ScriptSource scriptSource) throws JavetException {
        return setScriptSource(scriptSource, false);
    }

    /**
     * Sets script source.
     *
     * @param scriptSource the script source
     * @param cloneScript  the clone script
     * @return true : success, false : failure
     * @throws JavetException the javet exception
     * @since 2.0.1
     */
    boolean setScriptSource(ScriptSource scriptSource, boolean cloneScript) throws JavetException;

    /**
     * Sets source code with default options.
     * 1. Do not perform the position calculation at the native layer.
     * 2. Do not trim the tailing characters.
     * 3. Do not call GC before of after the call.
     *
     * @param sourceCodeString the source code string
     * @return true : success, false : failure
     * @throws JavetException the javet exception
     * @since 0.8.8
     */
    default boolean setSourceCode(String sourceCodeString) throws JavetException {
        return setSourceCode(sourceCodeString, SetSourceCodeOptions.DEFAULT);
    }

    /**
     * Sets source code with options.
     * <p>
     * Note 1: The source code is shared among all function objects.
     * So the caller is responsible for restoring the original source code,
     * otherwise the next function call will likely fail because the source code
     * of the next function call is incorrect.
     * Note 2: The source code must be verified by compile(). Malformed source
     * code will crash V8.
     * Note 3: Sometimes the source code must not end with any of ' ', ';', '\n',
     * though technically the source code is valid. Otherwise, V8 will crash.
     *
     * @param sourceCodeString the source code string
     * @param options          the options
     * @return the source code
     * @throws JavetException the javet exception
     * @since 2.0.1
     */
    boolean setSourceCode(String sourceCodeString, SetSourceCodeOptions options) throws JavetException;

    /**
     * The type Get scope infos options.
     *
     * @since 2.0.2
     */
    final class GetScopeInfosOptions implements Cloneable {
        /**
         * The constant Default.
         *
         * @since 2.0.2
         */
        public static final GetScopeInfosOptions Default = new GetScopeInfosOptions();
        private boolean includeGlobalVariables;
        private boolean includeScopeTypeGlobal;

        /**
         * Instantiates a new Get scope infos options.
         *
         * @since 2.0.2
         */
        public GetScopeInfosOptions() {
            setIncludeGlobalVariables(false);
            setIncludeScopeTypeGlobal(false);
        }

        @Override
        protected GetScopeInfosOptions clone() {
            return new GetScopeInfosOptions()
                    .setIncludeGlobalVariables(isIncludeGlobalVariables())
                    .setIncludeScopeTypeGlobal(isIncludeScopeTypeGlobal());
        }

        /**
         * Is include global variables boolean.
         *
         * @return true : yes, false : no
         * @since 2.0.2
         */
        public boolean isIncludeGlobalVariables() {
            return includeGlobalVariables;
        }

        /**
         * Is include scope type global boolean.
         *
         * @return true : yes, false : no
         * @since 2.0.2
         */
        public boolean isIncludeScopeTypeGlobal() {
            return includeScopeTypeGlobal;
        }

        private GetScopeInfosOptions setIncludeGlobalVariables(boolean includeGlobalVariables) {
            this.includeGlobalVariables = includeGlobalVariables;
            return this;
        }

        private GetScopeInfosOptions setIncludeScopeTypeGlobal(boolean includeScopeTypeGlobal) {
            this.includeScopeTypeGlobal = includeScopeTypeGlobal;
            return this;
        }

        /**
         * With include global variables.
         *
         * @param includeGlobalVariables the include global variables
         * @return the self
         * @since 2.0.2
         */
        public GetScopeInfosOptions withIncludeGlobalVariables(boolean includeGlobalVariables) {
            return clone().setIncludeGlobalVariables(includeGlobalVariables);
        }

        /**
         * With include scope type global.
         *
         * @param includeScopeTypeGlobal the include scope type global
         * @return the self
         * @since 2.0.2
         */
        public GetScopeInfosOptions withIncludeScopeTypeGlobal(boolean includeScopeTypeGlobal) {
            return clone().setIncludeScopeTypeGlobal(includeScopeTypeGlobal);
        }
    }

    /**
     * The type Scope info.
     *
     * @since 2.0.2
     */
    final class ScopeInfo implements IJavetClosable {
        private final boolean context;
        private final int endPosition;
        private final V8ValueObject scopeObject;
        private final int startPosition;
        private final V8ScopeType type;

        /**
         * Instantiates a new Scope info.
         *
         * @param type          the type
         * @param scopeObject   the scope object
         * @param context       the context
         * @param startPosition the start position
         * @param endPosition   the end position
         * @since 2.0.2
         */
        ScopeInfo(
                V8ScopeType type,
                V8ValueObject scopeObject,
                boolean context,
                int startPosition,
                int endPosition) {
            this.context = context;
            this.endPosition = endPosition;
            this.scopeObject = scopeObject;
            this.startPosition = startPosition;
            this.type = type;
        }

        @Override
        public void close() throws JavetException {
            scopeObject.close();
        }

        /**
         * Gets end position.
         *
         * @return the end position
         * @since 2.0.2
         */
        public int getEndPosition() {
            return endPosition;
        }

        /**
         * Gets scope object.
         *
         * @return the scope object
         * @since 2.0.2
         */
        public V8ValueObject getScopeObject() {
            return scopeObject;
        }

        /**
         * Gets start position.
         *
         * @return the start position
         * @since 2.0.2
         */
        public int getStartPosition() {
            return startPosition;
        }

        /**
         * Gets type.
         *
         * @return the type
         * @since 2.0.2
         */
        public V8ScopeType getType() {
            return type;
        }

        /**
         * Has context.
         *
         * @return true : yes, false : no
         * @since 2.0.2
         */
        public boolean hasContext() {
            return context;
        }

        @Override
        public boolean isClosed() {
            return scopeObject.isClosed();
        }
    }

    /**
     * The type Scope infos.
     *
     * @since 2.0.2
     */
    final class ScopeInfos implements IJavetClosable {
        private static final int INDEX_SCOPE_END_POSITION = 4;
        private static final int INDEX_SCOPE_HAS_CONTEXT = 2;
        private static final int INDEX_SCOPE_OBJECT = 1;
        private static final int INDEX_SCOPE_START_POSITION = 3;
        private static final int INDEX_SCOPE_TYPE = 0;
        private final List<ScopeInfo> scopeInfos;

        /**
         * Instantiates a new Scope infos.
         *
         * @param iV8ValueArray the V8 value array
         * @throws JavetException the javet exception
         * @since 2.0.2
         */
        ScopeInfos(IV8ValueArray iV8ValueArray) throws JavetException {
            this.scopeInfos = createFrom(iV8ValueArray);
        }

        private static List<ScopeInfo> createFrom(IV8ValueArray iV8ValueArray) throws JavetException {
            final List<ScopeInfo> values = new ArrayList<>();
            if (iV8ValueArray != null) {
                iV8ValueArray.forEach(v8Value -> {
                    if (v8Value instanceof V8ValueArray) {
                        V8ValueArray innerV8ValueArray = (V8ValueArray) v8Value;
                        ScopeInfo scopeInfo = new ScopeInfo(
                                V8ScopeType.parse(innerV8ValueArray.getInteger(INDEX_SCOPE_TYPE)),
                                innerV8ValueArray.get(INDEX_SCOPE_OBJECT),
                                innerV8ValueArray.getBoolean(INDEX_SCOPE_HAS_CONTEXT),
                                innerV8ValueArray.getInteger(INDEX_SCOPE_START_POSITION),
                                innerV8ValueArray.getInteger(INDEX_SCOPE_END_POSITION));
                        values.add(scopeInfo);
                    }
                });
            }
            return values;
        }

        @Override
        public void close() throws JavetException {
            for (ScopeInfo value : scopeInfos) {
                value.close();
            }
        }

        /**
         * Gets scope info by index.
         *
         * @param index the index
         * @return the scope info
         * @since 2.0.2
         */
        public ScopeInfo get(int index) {
            return scopeInfos.get(index);
        }

        /**
         * Gets variables in closure.
         *
         * @return the variables in closure
         * @throws JavetException the javet exception
         * @since 2.0.2
         */
        public List<List<String>> getVariablesInClosure() throws JavetException {
            List<List<String>> variablesList = new ArrayList<>();
            for (ScopeInfo scopeInfo : scopeInfos) {
                variablesList.add(scopeInfo.getScopeObject().getOwnPropertyNameStrings());
            }
            return variablesList;
        }

        /**
         * Has variables in closure.
         *
         * @return true : yes, false : no
         * @throws JavetException the javet exception
         * @since 2.0.2
         */
        public boolean hasVariablesInClosure() throws JavetException {
            for (V8ValueObject v8ValueObject : scopeInfos.stream()
                    .filter(scopeInfo -> scopeInfo.getType() == V8ScopeType.Closure)
                    .map(ScopeInfo::getScopeObject)
                    .collect(Collectors.toList())) {
                try (IV8ValueArray iV8ValueArray = v8ValueObject.getOwnPropertyNames()) {
                    if (iV8ValueArray.getLength() > 0) {
                        return true;
                    }
                }
            }
            return false;
        }

        @Override
        public boolean isClosed() {
            return scopeInfos.stream().allMatch(ScopeInfo::isClosed);
        }

        /**
         * Gets the size.
         *
         * @return the size
         * @since 2.0.2
         */
        public int size() {
            return scopeInfos.size();
        }
    }

    /**
     * The type Script source.
     * <p>
     * It is immutable.
     *
     * @since 2.0.1
     */
    final class ScriptSource {
        private final String code;
        private final int endPosition;
        private final int startPosition;

        /**
         * Instantiates a new Script source.
         *
         * @param code          the code
         * @param startPosition the start position
         * @param endPosition   the end position
         * @since 2.0.1
         */
        public ScriptSource(String code, int startPosition, int endPosition) {
            Objects.requireNonNull(code, "Code cannot be null.");
            assert startPosition >= 0 : "Start position must be no less than 0.";
            assert endPosition > startPosition : "End position must be greater than start position.";
            assert endPosition <= code.length() : "End position must be no greater than the length of the code.";
            this.code = code;
            this.startPosition = startPosition;
            this.endPosition = endPosition;
        }

        /**
         * Instantiates a new Script source.
         *
         * @param code the code
         * @since 2.0.1
         */
        public ScriptSource(String code) {
            this(Objects.requireNonNull(code), 0, code.length());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ScriptSource that = (ScriptSource) o;
            return getEndPosition() == that.getEndPosition()
                    && getStartPosition() == that.getStartPosition()
                    && getCode().equals(that.getCode());
        }

        /**
         * Gets code.
         *
         * @return the code
         * @since 2.0.1
         */
        public String getCode() {
            return code;
        }

        /**
         * Gets code snippet from the start position to the end position.
         *
         * @return the code snippet
         * @since 2.0.1
         */
        public String getCodeSnippet() {
            return code.substring(startPosition, endPosition);
        }

        /**
         * Gets end position.
         *
         * @return the end position
         * @since 2.0.1
         */
        public int getEndPosition() {
            return endPosition;
        }

        /**
         * Gets start position.
         *
         * @return the start position
         * @since 2.0.1
         */
        public int getStartPosition() {
            return startPosition;
        }

        @Override
        public int hashCode() {
            return Objects.hash(getCode(), getEndPosition(), getStartPosition());
        }

        /**
         * Returns a new script source with the code snippet replaced and positions re-calculated.
         *
         * @param codeSnippet the code snippet
         * @return a new script source
         * @since 2.0.1
         */
        public ScriptSource setCodeSnippet(String codeSnippet) {
            if (StringUtils.isNotEmpty(codeSnippet)) {
                final int originalCodeLength = code.length();
                final int codeSnippetLength = codeSnippet.length();
                final int newCodeLength = originalCodeLength - (endPosition - startPosition) + codeSnippetLength;
                StringBuilder sb = new StringBuilder(newCodeLength);
                sb.append(code, 0, startPosition);
                sb.append(codeSnippet);
                sb.append(code, endPosition, originalCodeLength);
                return new ScriptSource(
                        sb.toString(), startPosition, startPosition + codeSnippetLength);
            }
            return this;
        }
    }

    /**
     * The enum Set source code options.
     *
     * @since 2.0.1
     */
    final class SetSourceCodeOptions implements Cloneable {
        /**
         * The constant DEFAULT with all options disabled.
         *
         * @since 2.0.1
         */
        public static final SetSourceCodeOptions DEFAULT = new SetSourceCodeOptions();
        /**
         * The constant GC with PreGC and PostGC enabled.
         *
         * @since 2.0.1
         */
        public static final SetSourceCodeOptions GC = new SetSourceCodeOptions()
                .setPreGC(true).setPostGC(true);
        /**
         * The constant NATIVE_GC with PreGC, PostGC and NativeCalculation enabled.
         *
         * @since 2.0.1
         */
        public static final SetSourceCodeOptions NATIVE_GC = new SetSourceCodeOptions()
                .setPreGC(true).setPostGC(true).setNativeCalculation(true);
        private boolean cloneScript;
        private boolean nativeCalculation;
        private boolean postGC;
        private boolean preGC;
        private boolean trimTailingCharacters;

        private SetSourceCodeOptions() {
            setCloneScript(false).setPreGC(false).setPostGC(false);
            setNativeCalculation(false).setTrimTailingCharacters(false);
        }

        @Override
        protected SetSourceCodeOptions clone() {
            return new SetSourceCodeOptions()
                    .setCloneScript(isCloneScript())
                    .setNativeCalculation(isNativeCalculation())
                    .setPreGC(isPreGC())
                    .setPostGC(isPostGC())
                    .setTrimTailingCharacters(isTrimTailingCharacters());
        }

        /**
         * CloneScript: Clone the script so that the original script is not affected.
         * <p>
         * When this option is turned on, it is called LiveEdit in V8.
         *
         * @return true : enabled, false: disabled
         */
        public boolean isCloneScript() {
            return cloneScript;
        }

        /**
         * NativeCalculation: The position calculation is performed at the native layer.
         *
         * @return true : enabled, false: disabled
         * @since 2.0.1
         */
        public boolean isNativeCalculation() {
            return nativeCalculation;
        }

        /**
         * PostGC: The GC is called after the set call happens.
         *
         * @return true : enabled, false: disabled
         * @since 2.0.1
         */
        public boolean isPostGC() {
            return postGC;
        }

        /**
         * PreGC: The GC is called before the set call happens.
         *
         * @return true : enabled, false: disabled
         * @since 2.0.1
         */
        public boolean isPreGC() {
            return preGC;
        }

        /**
         * TrimTailingCharacters: Sometimes the source code must not end with ' ', '\n', '\r', 't', ';',
         * otherwise, V8 will crash immediately.
         *
         * @return true : enabled, false: disabled
         * @since 2.0.1
         */
        public boolean isTrimTailingCharacters() {
            return trimTailingCharacters;
        }

        private SetSourceCodeOptions setCloneScript(boolean cloneScript) {
            this.cloneScript = cloneScript;
            return this;
        }

        private SetSourceCodeOptions setNativeCalculation(boolean nativeCalculation) {
            this.nativeCalculation = nativeCalculation;
            return this;
        }

        private SetSourceCodeOptions setPostGC(boolean postGC) {
            this.postGC = postGC;
            return this;
        }

        private SetSourceCodeOptions setPreGC(boolean preGC) {
            this.preGC = preGC;
            return this;
        }

        private SetSourceCodeOptions setTrimTailingCharacters(boolean trimTailingCharacters) {
            this.trimTailingCharacters = trimTailingCharacters;
            return this;
        }

        /**
         * Returns a new immutable options with CloneScript set.
         *
         * @param cloneScript the clone script
         * @return the new immutable options
         * @since 2.0.1
         */
        public SetSourceCodeOptions withCloneScript(boolean cloneScript) {
            return clone().setCloneScript(cloneScript);
        }

        /**
         * Returns a new immutable options with NativeCalculation set.
         *
         * @param nativeCalculation the native calculation
         * @return the new immutable options
         * @since 2.0.1
         */
        public SetSourceCodeOptions withNativeCalculation(boolean nativeCalculation) {
            return clone().setNativeCalculation(nativeCalculation);
        }

        /**
         * Returns a new immutable options with PostGC set.
         *
         * @param postGC the post gc
         * @return the new immutable options
         * @since 2.0.1
         */
        public SetSourceCodeOptions withPostGC(boolean postGC) {
            return clone().setPostGC(postGC);
        }

        /**
         * Returns a new immutable options with PreGC set.
         *
         * @param preGC the pre gc
         * @return the new immutable options
         * @since 2.0.1
         */
        public SetSourceCodeOptions withPreGC(boolean preGC) {
            return clone().setPreGC(preGC);
        }

        /**
         * Returns a new immutable options with TrimTrailingCharacters set.
         *
         * @param trimTrailingCharacters the trim trailing characters
         * @return the new immutable options
         * @since 2.0.1
         */
        public SetSourceCodeOptions withTrimTailingCharacters(boolean trimTrailingCharacters) {
            return clone().setTrimTailingCharacters(trimTrailingCharacters);
        }
    }
}
