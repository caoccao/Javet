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

package com.caoccao.javet.values.reference.builtin;

import com.caoccao.javet.annotations.CheckReturnValue;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interfaces.IJavetSupplier;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.values.reference.V8ValueFunction;
import com.caoccao.javet.values.reference.V8ValueSymbol;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unchecked")
public class V8ValueBuiltInSymbol extends V8ValueFunction {
    public static final String NAME = "Symbol";

    public static final String PROPERTY_ASYNC_ITERATOR = "asyncIterator";
    public static final String PROPERTY_HAS_INSTANCE = "hasInstance";
    public static final String PROPERTY_IS_CONCAT_SPREADABLE = "isConcatSpreadable";
    public static final String PROPERTY_ITERATOR = "iterator";
    public static final String PROPERTY_MATCH = "match";
    public static final String PROPERTY_MATCH_ALL = "matchAll";
    public static final String PROPERTY_REPLACE = "replace";
    public static final String PROPERTY_SEARCH = "search";
    public static final String PROPERTY_SPECIES = "species";
    public static final String PROPERTY_SPLIT = "split";
    public static final String PROPERTY_TO_PRIMITIVE = "toPrimitive";
    public static final String PROPERTY_TO_STRING_TAG = "toStringTag";
    public static final String PROPERTY_UNSCOPABLES = "unscopables";

    public static final String SYMBOL_PROPERTY_ASYNC_ITERATOR = "Symbol.asyncIterator";
    public static final String SYMBOL_PROPERTY_HAS_INSTANCE = "Symbol.hasInstance";
    public static final String SYMBOL_PROPERTY_IS_CONCAT_SPREADABLE = "Symbol.isConcatSpreadable";
    public static final String SYMBOL_PROPERTY_ITERATOR = "Symbol.iterator";
    public static final String SYMBOL_PROPERTY_MATCH = "Symbol.match";
    public static final String SYMBOL_PROPERTY_MATCH_ALL = "Symbol.matchAll";
    public static final String SYMBOL_PROPERTY_REPLACE = "Symbol.replace";
    public static final String SYMBOL_PROPERTY_SEARCH = "Symbol.search";
    public static final String SYMBOL_PROPERTY_SPECIES = "Symbol.species";
    public static final String SYMBOL_PROPERTY_SPLIT = "Symbol.split";
    public static final String SYMBOL_PROPERTY_TO_PRIMITIVE = "Symbol.toPrimitive";
    public static final String SYMBOL_PROPERTY_TO_STRING_TAG = "Symbol.toStringTag";
    public static final String SYMBOL_PROPERTY_UNSCOPABLES = "Symbol.unscopables";

    protected static final String FUNCTION_FOR = "for";
    protected static final String FUNCTION_KEY_FOR = "keyFor";

    protected Map<String, IJavetSupplier<V8ValueSymbol, Throwable>> builtInSymbolMap;

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

    @CheckReturnValue
    public V8ValueSymbol _for(String description) throws JavetException {
        return invoke(FUNCTION_FOR, description);
    }

    @CheckReturnValue
    public V8ValueSymbol getAsyncIterator() throws JavetException {
        return get(PROPERTY_ASYNC_ITERATOR);
    }

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

    @CheckReturnValue
    public V8ValueSymbol getHasInstance() throws JavetException {
        return get(PROPERTY_HAS_INSTANCE);
    }

    @CheckReturnValue
    public V8ValueSymbol getIsConcatSpreadable() throws JavetException {
        return get(PROPERTY_IS_CONCAT_SPREADABLE);
    }

    @CheckReturnValue
    public V8ValueSymbol getIterator() throws JavetException {
        return get(PROPERTY_ITERATOR);
    }

    @CheckReturnValue
    public V8ValueSymbol getMatch() throws JavetException {
        return get(PROPERTY_MATCH);
    }

    @CheckReturnValue
    public V8ValueSymbol getMatchAll() throws JavetException {
        return get(PROPERTY_MATCH_ALL);
    }

    @CheckReturnValue
    public V8ValueSymbol getReplace() throws JavetException {
        return get(PROPERTY_REPLACE);
    }

    @CheckReturnValue
    public V8ValueSymbol getSearch() throws JavetException {
        return get(PROPERTY_SEARCH);
    }

    @CheckReturnValue
    public V8ValueSymbol getSpecies() throws JavetException {
        return get(PROPERTY_SPECIES);
    }

    @CheckReturnValue
    public V8ValueSymbol getSplit() throws JavetException {
        return get(PROPERTY_SPLIT);
    }

    @CheckReturnValue
    public V8ValueSymbol getToPrimitive() throws JavetException {
        return get(PROPERTY_TO_PRIMITIVE);
    }

    @CheckReturnValue
    public V8ValueSymbol getToStringTag() throws JavetException {
        return get(PROPERTY_TO_STRING_TAG);
    }

    @CheckReturnValue
    public V8ValueSymbol getUnscopables() throws JavetException {
        return get(PROPERTY_UNSCOPABLES);
    }

    @CheckReturnValue
    public String keyFor(V8ValueSymbol v8ValueSymbol) throws JavetException {
        return invokeString(FUNCTION_KEY_FOR, v8ValueSymbol);
    }

    @Override
    public V8ValueBuiltInSymbol toClone() throws JavetException {
        return this;
    }
}
