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

package com.caoccao.javet.values.reference.builtin;

import com.caoccao.javet.BaseTestJavetRuntime;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.values.reference.V8ValueSymbol;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestV8ValueBuiltInSymbol extends BaseTestJavetRuntime {
    @Test
    public void testProperties() throws JavetException {
        try (V8ValueBuiltInSymbol v8ValueBuiltInSymbol = v8Runtime.getGlobalObject().getBuiltInSymbol()) {
            try (V8ValueSymbol v8ValueSymbol = v8ValueBuiltInSymbol.getAsyncIterator()) {
                assertEquals("Symbol(Symbol.asyncIterator)", v8ValueSymbol.toString());
            }
            try (V8ValueSymbol v8ValueSymbol = v8ValueBuiltInSymbol.getHasInstance()) {
                assertEquals("Symbol(Symbol.hasInstance)", v8ValueSymbol.toString());
            }
            try (V8ValueSymbol v8ValueSymbol = v8ValueBuiltInSymbol.getIsConcatSpreadable()) {
                assertEquals("Symbol(Symbol.isConcatSpreadable)", v8ValueSymbol.toString());
            }
            try (V8ValueSymbol v8ValueSymbol = v8ValueBuiltInSymbol.getIterator()) {
                assertEquals("Symbol(Symbol.iterator)", v8ValueSymbol.toString());
            }
            try (V8ValueSymbol v8ValueSymbol = v8ValueBuiltInSymbol.getMatch()) {
                assertEquals("Symbol(Symbol.match)", v8ValueSymbol.toString());
            }
            try (V8ValueSymbol v8ValueSymbol = v8ValueBuiltInSymbol.getMatchAll()) {
                assertEquals("Symbol(Symbol.matchAll)", v8ValueSymbol.toString());
            }
            try (V8ValueSymbol v8ValueSymbol = v8ValueBuiltInSymbol.getReplace()) {
                assertEquals("Symbol(Symbol.replace)", v8ValueSymbol.toString());
            }
            try (V8ValueSymbol v8ValueSymbol = v8ValueBuiltInSymbol.getSearch()) {
                assertEquals("Symbol(Symbol.search)", v8ValueSymbol.toString());
            }
            try (V8ValueSymbol v8ValueSymbol = v8ValueBuiltInSymbol.getSplit()) {
                assertEquals("Symbol(Symbol.split)", v8ValueSymbol.toString());
            }
            try (V8ValueSymbol v8ValueSymbol = v8ValueBuiltInSymbol.getSpecies()) {
                assertEquals("Symbol(Symbol.species)", v8ValueSymbol.toString());
            }
            try (V8ValueSymbol v8ValueSymbol = v8ValueBuiltInSymbol.getToPrimitive()) {
                assertEquals("Symbol(Symbol.toPrimitive)", v8ValueSymbol.toString());
            }
            try (V8ValueSymbol v8ValueSymbol = v8ValueBuiltInSymbol.getToStringTag()) {
                assertEquals("Symbol(Symbol.toStringTag)", v8ValueSymbol.toString());
            }
            try (V8ValueSymbol v8ValueSymbol = v8ValueBuiltInSymbol.getUnscopables()) {
                assertEquals("Symbol(Symbol.unscopables)", v8ValueSymbol.toString());
            }
        }
    }
}
