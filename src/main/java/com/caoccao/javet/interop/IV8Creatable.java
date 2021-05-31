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

import com.caoccao.javet.annotations.CheckReturnValue;
import com.caoccao.javet.enums.V8ValueReferenceType;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.utils.JavetCallbackContext;
import com.caoccao.javet.values.primitive.*;
import com.caoccao.javet.values.reference.*;

import java.time.ZonedDateTime;

public interface IV8Creatable {
    @CheckReturnValue
    V8ValueArray createV8ValueArray() throws JavetException;

    @CheckReturnValue
    V8ValueArrayBuffer createV8ValueArrayBuffer(int length) throws JavetException;

    V8ValueBoolean createV8ValueBoolean(boolean booleanValue) throws JavetException;

    @CheckReturnValue
    V8ValueDataView createV8ValueDataView(V8ValueArrayBuffer v8ValueArrayBuffer) throws JavetException;

    V8ValueDouble createV8ValueDouble(double doubleValue) throws JavetException;

    @CheckReturnValue
    V8ValueFunction createV8ValueFunction(JavetCallbackContext javetCallbackContext) throws JavetException;

    V8ValueInteger createV8ValueInteger(int integerValue) throws JavetException;

    V8ValueLong createV8ValueLong(long longValue) throws JavetException;

    @CheckReturnValue
    V8ValueMap createV8ValueMap() throws JavetException;

    V8ValueNull createV8ValueNull();

    @CheckReturnValue
    V8ValueObject createV8ValueObject() throws JavetException;

    @CheckReturnValue
    V8ValueSet createV8ValueSet() throws JavetException;

    V8ValueString createV8ValueString(String str) throws JavetException;

    @CheckReturnValue
    V8ValueTypedArray createV8ValueTypedArray(V8ValueReferenceType type, int length) throws JavetException;

    V8ValueUndefined createV8ValueUndefined();

    V8ValueZonedDateTime createV8ValueZonedDateTime(long jsTimestamp) throws JavetException;

    V8ValueZonedDateTime createV8ValueZonedDateTime(ZonedDateTime zonedDateTime) throws JavetException;
}
