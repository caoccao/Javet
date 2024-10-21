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

package com.caoccao.javet.interception.jvm;

import com.caoccao.javet.BaseTestJavetRuntime;
import com.caoccao.javet.buddy.interop.proxy.JavetReflectionObjectFactory;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.callback.IJavetDirectCallable;
import com.caoccao.javet.interop.callback.JavetCallbackContext;
import com.caoccao.javet.interop.callback.JavetCallbackType;
import com.caoccao.javet.interop.converters.JavetProxyConverter;
import com.caoccao.javet.utils.V8ValueUtils;
import com.caoccao.javet.values.reference.V8ValueObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestJavetJVMInterceptor extends BaseTestJavetRuntime {
    protected JavetJVMInterceptor javetJVMInterceptor;

    @AfterEach
    @Override
    public void afterEach() throws JavetException {
        assertTrue(javetJVMInterceptor.unregister(v8Runtime.getGlobalObject()));
        try {
            JavetReflectionObjectFactory.getInstance().clear();
        } catch (Exception e) {
            fail(e);
        }
        v8Runtime.lowMemoryNotification();
        super.afterEach();
    }

    @BeforeEach
    @Override
    public void beforeEach() throws JavetException {
        super.beforeEach();
        JavetProxyConverter javetProxyConverter = new JavetProxyConverter();
        javetProxyConverter.getConfig().setReflectionObjectFactory(JavetReflectionObjectFactory.getInstance());
        v8Runtime.setConverter(javetProxyConverter);
        javetJVMInterceptor = new JavetJVMInterceptor(v8Runtime);
        javetJVMInterceptor.addCallbackContexts(new JavetCallbackContext(
                "extend",
                this, JavetCallbackType.DirectCallNoThisAndResult,
                (IJavetDirectCallable.NoThisAndResult<Exception>) (v8Values) -> {
                    if (v8Values.length >= 2) {
                        Object object = v8Runtime.toObject(v8Values[0]);
                        if (object instanceof Class) {
                            Class<?> clazz = (Class<?>) object;
                            V8ValueObject v8ValueObject = V8ValueUtils.asV8ValueObject(v8Values, 1);
                            if (v8ValueObject != null) {
                                Class<?> childClass = JavetReflectionObjectFactory.getInstance()
                                        .extend(clazz, v8ValueObject);
                                return v8Runtime.toV8Value(childClass);
                            }
                        }
                    }
                    return v8Runtime.createV8ValueUndefined();
                }));
        assertTrue(javetJVMInterceptor.register(v8Runtime.getGlobalObject()));
    }

    @Test
    public void testExtendArrayList() throws JavetException {
        v8Runtime.getConverter().getConfig().setProxyListEnabled(true);
        String codeString = "let ChildArrayList = javet.extend(javet.package.java.util.ArrayList, {\n" +
                "  isEmpty: () => !$super.isEmpty(),\n" +
                "});\n" +
                "let list = new ChildArrayList([1, 2, 3]);\n" +
                "JSON.stringify([list.isEmpty(), list.size()]);";
        assertEquals("[true,3]", v8Runtime.getExecutor(codeString).executeString());
        v8Runtime.getExecutor("ChildArrayList = undefined; list = undefined;").executeVoid();
        v8Runtime.getConverter().getConfig().setProxyListEnabled(false);
    }

    @Test
    public void testGC() throws JavetException {
        int initialCallbackContextCount = v8Runtime.getCallbackContextCount();
        v8Runtime.getGlobalObject().set("test", String.class);
        // The proxy creates 7 callback contexts.
        assertEquals(initialCallbackContextCount + 7, v8Runtime.getCallbackContextCount());
        v8Runtime.getGlobalObject().delete("test");
        // The 7 callback contexts are not recycled.
        assertEquals(initialCallbackContextCount + 7, v8Runtime.getCallbackContextCount());
        v8Runtime.getExecutor("javet.v8.gc()").executeVoid();
        // javet.v8 creates another 8 callback contexts while the gc() collects 8 callback contexts.
        assertEquals(initialCallbackContextCount + 8, v8Runtime.getCallbackContextCount());
        v8Runtime.lowMemoryNotification();
        assertEquals(initialCallbackContextCount, v8Runtime.getCallbackContextCount());
    }

    @Test
    public void testPackage() throws JavetException {
        // Test java
        v8Runtime.getExecutor("let java = javet.package.java").executeVoid();
        assertFalse(v8Runtime.getExecutor("java['.valid']").executeBoolean());
        assertEquals("java", v8Runtime.getExecutor("java['.name']").executeString());
        // Test java.util
        v8Runtime.getExecutor("let javaUtil = java.util").executeVoid();
        assertTrue(v8Runtime.getExecutor("javaUtil['.valid']").executeBoolean());
        assertFalse(v8Runtime.getExecutor("javaUtil['.sealed']").executeBoolean());
        assertEquals("java.util", v8Runtime.getExecutor("javaUtil['.name']").executeString());
        // Test java.lang.Object
        assertEquals(Object.class, v8Runtime.getExecutor("java.lang.Object").executeObject());
        // Test invalid cases
        assertInstanceOf(
                JavetJVMInterceptor.JavetVirtualPackage.class,
                v8Runtime.getExecutor("javet.package.abc.def").executeObject());
        assertInstanceOf(
                JavetJVMInterceptor.JavetVirtualPackage.class,
                v8Runtime.getExecutor("java.lang.abcdefg").executeObject());
        // Clean up
        v8Runtime.getExecutor("java = undefined; javaUtil = undefined").executeVoid();
    }

    @Test
    public void testStringBuilder() throws JavetException {
        v8Runtime.getExecutor("let java = javet.package.java").executeVoid();
        assertEquals(
                "a1",
                v8Runtime.getExecutor("let sb = new java.lang.StringBuilder(); sb.append('a').append(1); sb.toString();").executeString());
        v8Runtime.getExecutor("java = undefined; sb = undefined;").executeVoid();
    }

    @Test
    public void testThread() throws JavetException, InterruptedException {
        Thread thread = v8Runtime.getExecutor(
                "let java = javet.package.java;" +
                        "let count = 0;" +
                        "let thread = new java.lang.Thread(() => { count++; });" +
                        "thread.start();" +
                        "thread; "
        ).executeObject();
        thread.join();
        assertEquals(1, v8Runtime.getExecutor("count").executeInteger());
        v8Runtime.getExecutor("java = undefined; thread = undefined;").executeVoid();
        runGC();
    }
}
