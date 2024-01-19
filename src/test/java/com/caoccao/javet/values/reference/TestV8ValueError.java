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

package com.caoccao.javet.values.reference;

import com.caoccao.javet.BaseTestJavetRuntime;
import com.caoccao.javet.annotations.V8Function;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.exceptions.JavetExecutionException;
import com.caoccao.javet.exceptions.JavetScriptingError;
import com.caoccao.javet.interfaces.IJavetAnonymous;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class TestV8ValueError extends BaseTestJavetRuntime {
    @Test
    public void testError() throws JavetException {
        try (V8ValueError v8ValueError = v8Runtime.getExecutor("Error('test')").execute()) {
            assertNotNull(v8ValueError);
            assertEquals("test", v8ValueError.getMessage());
            assertEquals("Error: test\n    at <anonymous>:1:1", v8ValueError.getStack());
            try (IV8ValueArray iV8ValueArray = v8ValueError.getOwnPropertyNames()) {
                assertNotNull(iV8ValueArray);
                assertEquals(0, iV8ValueArray.getLength());
            }
        }
    }

    @Test
    public void testInvokeWithBuiltInError() {
        try {
            v8Runtime.getExecutor("var test = () => { throw new Error('test'); };").executeVoid();
            v8Runtime.getGlobalObject().invokeVoid("test");
            fail("Failed to catch the error.");
        } catch (JavetException e) {
            assertTrue(e instanceof JavetExecutionException);
            JavetExecutionException javetExecutionException = (JavetExecutionException) e;
            assertEquals("Error: test", javetExecutionException.getMessage());
        }
    }

    @Test
    public void testInvokeWithCustomError() {
        try {
            v8Runtime.getExecutor("function AssertionError(message) {" +
                    " this.message = message;" +
                    " this.stack = 'abc';" +
                    " this.x = 123;" +
                    " this.y = 'def';" +
                    " }").executeVoid();
            v8Runtime.getExecutor("var test = () => { throw new AssertionError('test'); };").executeVoid();
            v8Runtime.getGlobalObject().invokeVoid("test");
            fail("Failed to catch the assertion error.");
        } catch (JavetException e) {
            assertTrue(e instanceof JavetExecutionException);
            JavetExecutionException javetExecutionException = (JavetExecutionException) e;
            JavetScriptingError javetScriptingError = javetExecutionException.getScriptingError();
            assertEquals("test", javetExecutionException.getMessage());
            assertEquals("test", javetScriptingError.getMessage());
            assertEquals("test", javetScriptingError.getDetailedMessage());
            assertEquals("abc", javetScriptingError.getStack());
            Map<String, Object> context = javetScriptingError.getContext();
            assertEquals(2, context.size());
            assertEquals(123, context.get("x"));
            assertEquals("def", context.get("y"));
        }
    }

    @Test
    public void testProperty() throws JavetException {
        try (V8ValueError v8ValueError = v8Runtime.getExecutor("const e = Error('test'); e.a = 1; e;").execute()) {
            assertNotNull(v8ValueError);
            assertEquals("test", v8ValueError.getMessage());
            assertEquals("Error: test\n    at <anonymous>:1:11", v8ValueError.getStack());
            try (IV8ValueArray iV8ValueArray = v8ValueError.getOwnPropertyNames()) {
                assertNotNull(iV8ValueArray);
                assertEquals(1, iV8ValueArray.getLength());
                assertEquals("a", iV8ValueArray.getString(0));
            }
            assertEquals(1, v8ValueError.getInteger("a"));
        }
    }

    @Test
    public void testThrowInCallback() {
        IJavetAnonymous anonymous = new IJavetAnonymous() {
            @V8Function
            public int noError() {
                return 1;
            }

            @V8Function
            public void throwError() throws IOException {
                throw new IOException("throwError");
            }
        };
        try (V8ValueObject v8ValueObject = v8Runtime.createV8ValueObject()) {
            v8ValueObject.bind(anonymous);
            v8Runtime.getGlobalObject().set("a", v8ValueObject);
            v8Runtime.getExecutor("try { a.throwError(); } catch (e) {}").executeVoid();
            assertEquals(1, v8Runtime.getExecutor(
                    "try { a.throwError(); } catch (e) {}\n" +
                            "a.noError();").executeInteger());
            v8ValueObject.unbind(anonymous);
            v8Runtime.getGlobalObject().delete("a");
        } catch (JavetException e) {
            e.printStackTrace();
            fail("Failed to hide the error.");
        } finally {
            v8Runtime.lowMemoryNotification();
        }
    }

    @Test
    public void testThrowInCatch() {
        IJavetAnonymous anonymous = new IJavetAnonymous() {
            @V8Function
            public void throwError() throws IOException {
                throw new IOException("1");
            }
        };
        try (V8ValueObject v8ValueObject = v8Runtime.createV8ValueObject()) {
            v8ValueObject.bind(anonymous);
            v8Runtime.getGlobalObject().set("a", v8ValueObject);
            try {
                v8Runtime.getExecutor(
                        "try { a.throwError(); } catch (e) { throw new Error('2'); }").executeVoid();
                fail("Failed to catch the JS error.");
            } catch (JavetException e) {
                assertEquals("Error: 2", e.getMessage());
            }
            v8ValueObject.unbind(anonymous);
            v8Runtime.getGlobalObject().delete("a");
        } catch (JavetException e) {
            e.printStackTrace();
            fail("Failed to hide the error.");
        } finally {
            v8Runtime.lowMemoryNotification();
        }
    }

    @Test
    public void testThrowWithoutCatch() {
        try {
            v8Runtime.getExecutor("throw new Error('test');").executeVoid();
            fail("Failed to catch JavetExecutionException.");
        } catch (JavetException e) {
            assertTrue(e instanceof JavetExecutionException);
            JavetExecutionException javetExecutionException = (JavetExecutionException) e;
            assertEquals("Error: test", javetExecutionException.getMessage());
            assertEquals(301, javetExecutionException.getError().getCode());
            assertEquals(
                    "Error: test\n" +
                            "Resource: undefined\n" +
                            "Source Code: throw new Error('test');\n" +
                            "Line Number: 1\n" +
                            "Column: 0, 1\n" +
                            "Position: 0, 1",
                    javetExecutionException.getScriptingError().toString());
        }
    }
}
