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

package com.caoccao.javet.values.primitive;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;

@SuppressWarnings("unchecked")
public class V8ValueDouble extends V8ValuePrimitive<Double> {
    public V8ValueDouble(V8Runtime v8Runtime) throws JavetException {
        this(v8Runtime, 0D);
    }

    public V8ValueDouble(V8Runtime v8Runtime, double value) throws JavetException {
        super(v8Runtime, value);
    }

    public boolean isFinite() {
        /* if defined ANDROID
        return Math.abs(value) <= Double.MAX_VALUE;
        /* end if */
        /* if not defined ANDROID */
        return Double.isFinite(value);
        /* end if */
    }

    public boolean isInfinite() {
        /* if defined ANDROID
        return value == Double.POSITIVE_INFINITY || value == Double.NEGATIVE_INFINITY;
        /* end if */
        /* if not defined ANDROID */
        return Double.isInfinite(value);
        /* end if */
    }

    public boolean isNaN() {
        return Double.isNaN(value);
    }

    @Override
    public V8ValueDouble toClone() throws JavetException {
        return this;
    }

    public double toPrimitive() {
        return value;
    }
}
