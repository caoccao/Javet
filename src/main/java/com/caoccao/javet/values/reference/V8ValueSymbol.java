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
import com.caoccao.javet.values.V8ValueReferenceType;

import java.text.MessageFormat;

public class V8ValueSymbol extends V8ValueObject {

    public static final String DESCRIPTION = "description";
    public static final String SYMBOL_0 = "Symbol({0})";

    public V8ValueSymbol(long handle) {
        super(handle);
    }

    @Override
    public int getType() {
        return V8ValueReferenceType.Symbol;
    }

    public String getDescription() throws JavetException {
        return getPropertyString(DESCRIPTION);
    }

    @Override
    public String toString() {
        try {
            return MessageFormat.format(SYMBOL_0, getDescription());
        } catch (JavetException e) {
            return e.getMessage();
        }
    }
}
