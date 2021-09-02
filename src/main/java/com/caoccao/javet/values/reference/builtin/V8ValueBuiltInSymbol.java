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

package com.caoccao.javet.values.reference.builtin;

import com.caoccao.javet.annotations.CheckReturnValue;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.values.reference.V8ValueFunction;
import com.caoccao.javet.values.reference.V8ValueSymbol;

@SuppressWarnings("unchecked")
public class V8ValueBuiltInSymbol extends V8ValueFunction {

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
    public static final String FUNCTION_FOR = "for";
    public static final String FUNCTION_KEY_FOR = "keyFor";

    public V8ValueBuiltInSymbol(long handle) {
        super(handle);
    }

    @CheckReturnValue
    public V8ValueSymbol _for(String name) throws JavetException {
        return invoke(FUNCTION_FOR, name);
    }

    public V8ValueSymbol getAsyncIterator() throws JavetException {
        return get(PROPERTY_ASYNC_ITERATOR);
    }

    public V8ValueSymbol getHasInstance() throws JavetException {
        return get(PROPERTY_HAS_INSTANCE);
    }

    public V8ValueSymbol getIsConcatSpreadable() throws JavetException {
        return get(PROPERTY_IS_CONCAT_SPREADABLE);
    }

    public V8ValueSymbol getIterator() throws JavetException {
        return get(PROPERTY_ITERATOR);
    }

    public V8ValueSymbol getMatch() throws JavetException {
        return get(PROPERTY_MATCH);
    }

    public V8ValueSymbol getMatchAll() throws JavetException {
        return get(PROPERTY_MATCH_ALL);
    }

    public V8ValueSymbol getReplace() throws JavetException {
        return get(PROPERTY_REPLACE);
    }

    public V8ValueSymbol getSearch() throws JavetException {
        return get(PROPERTY_SEARCH);
    }

    public V8ValueSymbol getSpecies() throws JavetException {
        return get(PROPERTY_SPECIES);
    }

    public V8ValueSymbol getSplit() throws JavetException {
        return get(PROPERTY_SPLIT);
    }

    public V8ValueSymbol getToPrimitive() throws JavetException {
        return get(PROPERTY_TO_PRIMITIVE);
    }

    public V8ValueSymbol getToStringTag() throws JavetException {
        return get(PROPERTY_TO_STRING_TAG);
    }

    public V8ValueSymbol getUnscopables() throws JavetException {
        return get(PROPERTY_UNSCOPABLES);
    }

    public String keyFor(V8ValueSymbol v8ValueSymbol) throws JavetException {
        return invokeString(FUNCTION_KEY_FOR, v8ValueSymbol);
    }

    @Override
    public V8ValueBuiltInSymbol toClone() throws JavetException {
        return this;
    }
}
