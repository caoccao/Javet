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

package com.caoccao.javet.interop;

import com.caoccao.javet.BaseTestJavetRuntime;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.values.V8Value;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestV8Scope extends BaseTestJavetRuntime {
    @Test
    public void testWithV8Runtime() throws JavetException {
        final AtomicInteger referenceCount = new AtomicInteger(0);
        Consumer<V8Value> consumer = v -> referenceCount.incrementAndGet();
        try (V8Scope v8Scope = v8Runtime.getV8Scope()) {
            Optional.ofNullable(v8Scope.createV8ValueArray()).ifPresent(consumer);
            Optional.ofNullable(v8Scope.createV8ValueArrayBuffer(16)).ifPresent(consumer);
            v8Scope.createV8ValueBoolean(true);
            v8Scope.createV8ValueDouble(1.23D);
            Optional.ofNullable(v8Scope.createV8ValueFunction("() => 1")).ifPresent(consumer);
            v8Scope.createV8ValueInteger(1);
            v8Scope.createV8ValueLong(1L);
            Optional.ofNullable(v8Scope.createV8ValueMap()).ifPresent(consumer);
            v8Scope.createV8ValueNull();
            Optional.ofNullable(v8Scope.createV8ValueObject()).ifPresent(consumer);
            Optional.ofNullable(v8Scope.createV8ValuePromise()).ifPresent(consumer);
            Optional.ofNullable(v8Scope.createV8ValueProxy()).ifPresent(consumer);
            Optional.ofNullable(v8Scope.createV8ValueSet()).ifPresent(consumer);
            v8Scope.createV8ValueString("abc");
            Optional.ofNullable(v8Scope.createV8ValueSymbol("abc")).ifPresent(consumer);
            v8Scope.createV8ValueUndefined();
            v8Scope.createV8ValueZonedDateTime(123L);
            v8Scope.createV8ValueZonedDateTime(ZonedDateTime.now());
            assertEquals(referenceCount.get(), v8Runtime.getReferenceCount());
        } finally {
            v8Runtime.lowMemoryNotification();
        }
    }

    @Test
    public void testWithoutV8Runtime() throws JavetException {
        final AtomicInteger referenceCount = new AtomicInteger(0);
        Consumer<V8Value> consumer = v -> referenceCount.incrementAndGet();
        try (V8Scope v8Scope = new V8Scope()) {
            Optional.ofNullable(v8Scope.add(v8Runtime.createV8ValueArray())).ifPresent(consumer);
            Optional.ofNullable(v8Scope.add(v8Runtime.createV8ValueArrayBuffer(16))).ifPresent(consumer);
            v8Scope.add(v8Runtime.createV8ValueBoolean(true));
            v8Scope.add(v8Runtime.createV8ValueDouble(1.23D));
            Optional.ofNullable(v8Scope.add(v8Runtime.createV8ValueFunction("() => 1"))).ifPresent(consumer);
            v8Scope.add(v8Runtime.createV8ValueInteger(1));
            v8Scope.add(v8Runtime.createV8ValueLong(1L));
            Optional.ofNullable(v8Scope.add(v8Runtime.createV8ValueMap())).ifPresent(consumer);
            v8Scope.add(v8Runtime.createV8ValueNull());
            Optional.ofNullable(v8Scope.add(v8Runtime.createV8ValueObject())).ifPresent(consumer);
            Optional.ofNullable(v8Scope.add(v8Runtime.createV8ValuePromise())).ifPresent(consumer);
            Optional.ofNullable(v8Scope.add(v8Runtime.createV8ValueProxy())).ifPresent(consumer);
            Optional.ofNullable(v8Scope.add(v8Runtime.createV8ValueSet())).ifPresent(consumer);
            v8Scope.add(v8Runtime.createV8ValueString("abc"));
            Optional.ofNullable(v8Scope.add(v8Runtime.createV8ValueSymbol("abc"))).ifPresent(consumer);
            v8Scope.add(v8Runtime.createV8ValueUndefined());
            v8Scope.add(v8Runtime.createV8ValueZonedDateTime(123L));
            v8Scope.add(v8Runtime.createV8ValueZonedDateTime(ZonedDateTime.now()));
            assertEquals(referenceCount.get(), v8Runtime.getReferenceCount());
        } finally {
            v8Runtime.lowMemoryNotification();
        }
    }
}
