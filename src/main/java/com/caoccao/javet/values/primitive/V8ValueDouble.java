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

@SuppressWarnings("unchecked")
public class V8ValueDouble extends V8ValuePrimitive<Double> {
    public V8ValueDouble() {
        this(0D);
    }

    public V8ValueDouble(double value) {
        super(value);
    }

    // Javet Android Ignore Begin
    public boolean isFinite() {
        return Double.isFinite(value);
    }

    public boolean isInfinite() {
        return Double.isInfinite(value);
    }
    // Javet Android Ignore End

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
