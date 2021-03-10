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

package com.caoccao.javet.interop;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.utils.JavetCallbackContext;
import com.caoccao.javet.values.primitive.*;
import com.caoccao.javet.values.reference.*;

public interface IV8Creatable {
    V8ValueArray createV8ValueArray() throws JavetException;

    V8ValueArrayBuffer createV8ValueArrayBuffer(int length) throws JavetException;

    V8ValueBoolean createV8ValueBoolean(boolean booleanValue) throws JavetException;

    V8ValueDataView createV8ValueDataView(V8ValueArrayBuffer v8ValueArrayBuffer) throws JavetException;

    V8ValueFunction createV8ValueFunction(JavetCallbackContext javetCallbackContext) throws JavetException;

    V8ValueInteger createV8ValueInteger(int integerValue) throws JavetException;

    V8ValueLong createV8ValueLong(long longValue) throws JavetException;

    V8ValueMap createV8ValueMap() throws JavetException;

    V8ValueNull createV8ValueNull();

    V8ValueObject createV8ValueObject() throws JavetException;

    V8ValueSet createV8ValueSet() throws JavetException;

    V8ValueTypedArray createV8ValueTypedArray(int type, int length) throws JavetException;

    V8ValueUndefined createV8ValueUndefined();
}
