/*
 * Copyright (c) 2021-2026. caoccao.com Sam Cao
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

package com.caoccao.javet.values.reference.builtin;

import com.caoccao.javet.annotations.CheckReturnValue;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interfaces.IJavetSupplier;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.values.reference.V8ValueFunction;
import com.caoccao.javet.values.reference.V8ValueSymbol;

import java.util.HashMap;
import java.util.Map;

/**
 * The type V8 value built-in Symbol, providing access to well-known JavaScript symbols.
 */
@SuppressWarnings("unchecked")
public class V8ValueBuiltInSymbol extends V8ValueFunction {
    /**
     * The constant NAME.
     */
    public static final String NAME = "Symbol";

    /**
     * The property name for Symbol.asyncIterator.
     */
    public static final String PROPERTY_ASYNC_ITERATOR = "asyncIterator";
    /**
     * The property name for Symbol.hasInstance.
     */
    public static final String PROPERTY_HAS_INSTANCE = "hasInstance";
    /**
     * The property name for Symbol.isConcatSpreadable.
     */
    public static final String PROPERTY_IS_CONCAT_SPREADABLE = "isConcatSpreadable";
    /**
     * The property name for Symbol.iterator.
     */
    public static final String PROPERTY_ITERATOR = "iterator";
    /**
     * The property name for Symbol.match.
     */
    public static final String PROPERTY_MATCH = "match";
    /**
     * The property name for Symbol.matchAll.
     */
    public static final String PROPERTY_MATCH_ALL = "matchAll";
    /**
     * The property name for Symbol.replace.
     */
    public static final String PROPERTY_REPLACE = "replace";
    /**
     * The property name for Symbol.search.
     */
    public static final String PROPERTY_SEARCH = "search";
    /**
     * The property name for Symbol.species.
     */
    public static final String PROPERTY_SPECIES = "species";
    /**
     * The property name for Symbol.split.
     */
    public static final String PROPERTY_SPLIT = "split";
    /**
     * The property name for Symbol.toPrimitive.
     */
    public static final String PROPERTY_TO_PRIMITIVE = "toPrimitive";
    /**
     * The property name for Symbol.toStringTag.
     */
    public static final String PROPERTY_TO_STRING_TAG = "toStringTag";
    /**
     * The property name for Symbol.unscopables.
     */
    public static final String PROPERTY_UNSCOPABLES = "unscopables";

    /**
     * The full symbol property string for Symbol.asyncIterator.
     */
    public static final String SYMBOL_PROPERTY_ASYNC_ITERATOR = "Symbol.asyncIterator";
    /**
     * The full symbol property string for Symbol.hasInstance.
     */
    public static final String SYMBOL_PROPERTY_HAS_INSTANCE = "Symbol.hasInstance";
    /**
     * The full symbol property string for Symbol.isConcatSpreadable.
     */
    public static final String SYMBOL_PROPERTY_IS_CONCAT_SPREADABLE = "Symbol.isConcatSpreadable";
    /**
     * The full symbol property string for Symbol.iterator.
     */
    public static final String SYMBOL_PROPERTY_ITERATOR = "Symbol.iterator";
    /**
     * The full symbol property string for Symbol.match.
     */
    public static final String SYMBOL_PROPERTY_MATCH = "Symbol.match";
    /**
     * The full symbol property string for Symbol.matchAll.
     */
    public static final String SYMBOL_PROPERTY_MATCH_ALL = "Symbol.matchAll";
    /**
     * The full symbol property string for Symbol.replace.
     */
    public static final String SYMBOL_PROPERTY_REPLACE = "Symbol.replace";
    /**
     * The full symbol property string for Symbol.search.
     */
    public static final String SYMBOL_PROPERTY_SEARCH = "Symbol.search";
    /**
     * The full symbol property string for Symbol.species.
     */
    public static final String SYMBOL_PROPERTY_SPECIES = "Symbol.species";
    /**
     * The full symbol property string for Symbol.split.
     */
    public static final String SYMBOL_PROPERTY_SPLIT = "Symbol.split";
    /**
     * The full symbol property string for Symbol.toPrimitive.
     */
    public static final String SYMBOL_PROPERTY_TO_PRIMITIVE = "Symbol.toPrimitive";
    /**
     * The full symbol property string for Symbol.toStringTag.
     */
    public static final String SYMBOL_PROPERTY_TO_STRING_TAG = "Symbol.toStringTag";
    /**
     * The full symbol property string for Symbol.unscopables.
     */
    public static final String SYMBOL_PROPERTY_UNSCOPABLES = "Symbol.unscopables";

    /**
     * The constant FUNCTION_FOR.
     */
    protected static final String FUNCTION_FOR = "for";
    /**
     * The constant FUNCTION_KEY_FOR.
     */
    protected static final String FUNCTION_KEY_FOR = "keyFor";

    /**
     * The map of built-in symbol suppliers keyed by property name.
     */
    protected Map<String, IJavetSupplier<V8ValueSymbol, Throwable>> builtInSymbolMap;

    /**
     * Instantiates a new V8 value built-in Symbol.
     *
     * @param v8Runtime the V8 runtime
     * @param handle    the native handle
     * @throws JavetException the javet exception
     */
    public V8ValueBuiltInSymbol(V8Runtime v8Runtime, long handle) throws JavetException {
        super(v8Runtime, handle);
        builtInSymbolMap = new HashMap<>();
        builtInSymbolMap.put(PROPERTY_ASYNC_ITERATOR, this::getAsyncIterator);
        builtInSymbolMap.put(PROPERTY_HAS_INSTANCE, this::getHasInstance);
        builtInSymbolMap.put(PROPERTY_IS_CONCAT_SPREADABLE, this::getIsConcatSpreadable);
        builtInSymbolMap.put(PROPERTY_ITERATOR, this::getIterator);
        builtInSymbolMap.put(PROPERTY_MATCH, this::getMatch);
        builtInSymbolMap.put(PROPERTY_MATCH_ALL, this::getMatchAll);
        builtInSymbolMap.put(PROPERTY_REPLACE, this::getReplace);
        builtInSymbolMap.put(PROPERTY_SEARCH, this::getSearch);
        builtInSymbolMap.put(PROPERTY_SPECIES, this::getSpecies);
        builtInSymbolMap.put(PROPERTY_SPLIT, this::getSplit);
        builtInSymbolMap.put(PROPERTY_TO_PRIMITIVE, this::getToPrimitive);
        builtInSymbolMap.put(PROPERTY_TO_STRING_TAG, this::getToStringTag);
        builtInSymbolMap.put(PROPERTY_UNSCOPABLES, this::getUnscopables);
    }

    /**
     * Calls Symbol.for() to search for existing symbols in the global symbol registry with the given description.
     *
     * @param description the symbol description
     * @return the V8 value symbol
     * @throws JavetException the javet exception
     */
    @CheckReturnValue
    public V8ValueSymbol _for(String description) throws JavetException {
        return invoke(FUNCTION_FOR, description);
    }

    /**
     * Gets the well-known Symbol.asyncIterator symbol.
     *
     * @return the V8 value symbol
     * @throws JavetException the javet exception
     */
    @CheckReturnValue
    public V8ValueSymbol getAsyncIterator() throws JavetException {
        return get(PROPERTY_ASYNC_ITERATOR);
    }

    /**
     * Gets a built-in symbol by its property name.
     *
     * @param description the symbol property name
     * @return the V8 value symbol, or null if not found
     * @throws JavetException the javet exception
     */
    @CheckReturnValue
    public V8ValueSymbol getBuiltInSymbol(String description) throws JavetException {
        IJavetSupplier<V8ValueSymbol, Throwable> iJavetSupplier = builtInSymbolMap.get(description);
        if (iJavetSupplier != null) {
            try {
                return iJavetSupplier.get();
            } catch (JavetException e) {
                throw e;
            } catch (Throwable ignored) {
            }
        }
        return null;
    }

    /**
     * Gets the well-known Symbol.hasInstance symbol.
     *
     * @return the V8 value symbol
     * @throws JavetException the javet exception
     */
    @CheckReturnValue
    public V8ValueSymbol getHasInstance() throws JavetException {
        return get(PROPERTY_HAS_INSTANCE);
    }

    /**
     * Gets the well-known Symbol.isConcatSpreadable symbol.
     *
     * @return the V8 value symbol
     * @throws JavetException the javet exception
     */
    @CheckReturnValue
    public V8ValueSymbol getIsConcatSpreadable() throws JavetException {
        return get(PROPERTY_IS_CONCAT_SPREADABLE);
    }

    /**
     * Gets the well-known Symbol.iterator symbol.
     *
     * @return the V8 value symbol
     * @throws JavetException the javet exception
     */
    @CheckReturnValue
    public V8ValueSymbol getIterator() throws JavetException {
        return get(PROPERTY_ITERATOR);
    }

    /**
     * Gets the well-known Symbol.match symbol.
     *
     * @return the V8 value symbol
     * @throws JavetException the javet exception
     */
    @CheckReturnValue
    public V8ValueSymbol getMatch() throws JavetException {
        return get(PROPERTY_MATCH);
    }

    /**
     * Gets the well-known Symbol.matchAll symbol.
     *
     * @return the V8 value symbol
     * @throws JavetException the javet exception
     */
    @CheckReturnValue
    public V8ValueSymbol getMatchAll() throws JavetException {
        return get(PROPERTY_MATCH_ALL);
    }

    /**
     * Gets the well-known Symbol.replace symbol.
     *
     * @return the V8 value symbol
     * @throws JavetException the javet exception
     */
    @CheckReturnValue
    public V8ValueSymbol getReplace() throws JavetException {
        return get(PROPERTY_REPLACE);
    }

    /**
     * Gets the well-known Symbol.search symbol.
     *
     * @return the V8 value symbol
     * @throws JavetException the javet exception
     */
    @CheckReturnValue
    public V8ValueSymbol getSearch() throws JavetException {
        return get(PROPERTY_SEARCH);
    }

    /**
     * Gets the well-known Symbol.species symbol.
     *
     * @return the V8 value symbol
     * @throws JavetException the javet exception
     */
    @CheckReturnValue
    public V8ValueSymbol getSpecies() throws JavetException {
        return get(PROPERTY_SPECIES);
    }

    /**
     * Gets the well-known Symbol.split symbol.
     *
     * @return the V8 value symbol
     * @throws JavetException the javet exception
     */
    @CheckReturnValue
    public V8ValueSymbol getSplit() throws JavetException {
        return get(PROPERTY_SPLIT);
    }

    /**
     * Gets the well-known Symbol.toPrimitive symbol.
     *
     * @return the V8 value symbol
     * @throws JavetException the javet exception
     */
    @CheckReturnValue
    public V8ValueSymbol getToPrimitive() throws JavetException {
        return get(PROPERTY_TO_PRIMITIVE);
    }

    /**
     * Gets the well-known Symbol.toStringTag symbol.
     *
     * @return the V8 value symbol
     * @throws JavetException the javet exception
     */
    @CheckReturnValue
    public V8ValueSymbol getToStringTag() throws JavetException {
        return get(PROPERTY_TO_STRING_TAG);
    }

    /**
     * Gets the well-known Symbol.unscopables symbol.
     *
     * @return the V8 value symbol
     * @throws JavetException the javet exception
     */
    @CheckReturnValue
    public V8ValueSymbol getUnscopables() throws JavetException {
        return get(PROPERTY_UNSCOPABLES);
    }

    /**
     * Calls Symbol.keyFor() to retrieve a shared symbol key from the global symbol registry.
     *
     * @param v8ValueSymbol the V8 value symbol
     * @return the key string, or null if the symbol is not in the global registry
     * @throws JavetException the javet exception
     */
    @CheckReturnValue
    public String keyFor(V8ValueSymbol v8ValueSymbol) throws JavetException {
        return invokeString(FUNCTION_KEY_FOR, v8ValueSymbol);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public V8ValueBuiltInSymbol toClone() throws JavetException {
        return this;
    }
}
