/*
 * Copyright (c) 2023. caoccao.com Sam Cao
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

package com.caoccao.javet.interop.callback;

import com.caoccao.javet.BaseTestJavetRuntime;
import com.caoccao.javet.annotations.V8Function;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interfaces.IJavetAnonymous;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.primitive.V8ValueInteger;
import com.caoccao.javet.values.reference.V8ValueObject;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestV8FunctionCallback extends BaseTestJavetRuntime {
    @Test
    @Tag("performance")
    public void testReceiveCallback() throws JavetException {
        IJavetAnonymous anonymous = new IJavetAnonymous() {
            @V8Function
            public int test(V8Value... v8Values) {
                return v8Values.length;
            }
        };
        final int argumentCount = 20;
        final long loopCount = 200_000L;
        V8Value[] arguments = new V8Value[argumentCount];
        Arrays.fill(arguments, v8Runtime.createV8ValueInteger(1));
        try (V8ValueObject v8ValueObject = v8Runtime.createV8ValueObject()) {
            v8ValueObject.bind(anonymous);
            int count = 0;
            final long startTime = System.currentTimeMillis();
            for (long i = 0; i < loopCount; i++) {
                V8ValueInteger v8ValueInteger = v8ValueObject.invoke("test", arguments);
                count += v8ValueInteger.getValue();
            }
            final long stopTime = System.currentTimeMillis();
            assertEquals(argumentCount * loopCount, count, "Count should match.");
            final long tps = count * 1000L / (stopTime - startTime);
            logger.logInfo("{0} calls in {1}ms. TPS is {2}.", count, stopTime - startTime, tps);
        } finally {
            v8Runtime.lowMemoryNotification();
        }
    }
}
