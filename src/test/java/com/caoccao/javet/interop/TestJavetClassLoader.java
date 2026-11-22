/*
 * Copyright (c) 2021-2026. caoccao.com Sam Cao
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

import com.caoccao.javet.enums.JSRuntimeType;
import com.caoccao.javet.interop.loader.JavetLibLoader;
import org.junit.jupiter.api.Test;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class TestJavetClassLoader {
    @Test
    public void testDefineClassFromStreamWithZeroAvailableBytes() throws IOException {
        for (Class<?> expectedClass : Arrays.asList(JavetLibLoader.class, NodeNative.class, V8Native.class)) {
            JavetClassLoader javetClassLoader = new JavetClassLoader(getClass().getClassLoader(), JSRuntimeType.V8);
            String classPath = "/" + expectedClass.getName().replace(".", "/") + ".class";
            try (InputStream inputStream = new ZeroAvailableInputStream(getClass().getResourceAsStream(classPath))) {
                Class<?> definedClass = javetClassLoader.defineClass(expectedClass.getName(), inputStream);
                assertEquals(expectedClass.getName(), definedClass.getName());
                assertSame(javetClassLoader, definedClass.getClassLoader());
                assertNotSame(expectedClass, definedClass);
            }
        }
    }

    @Test
    public void testLoadClass() throws ClassNotFoundException {
        JavetClassLoader javetClassLoader = new JavetClassLoader(getClass().getClassLoader(), JSRuntimeType.V8);
        Class<?> definedClass = javetClassLoader.loadClass(V8Native.class.getName());
        assertSame(javetClassLoader, definedClass.getClassLoader());
        assertTrue(IV8Native.class.isAssignableFrom(definedClass));
        // The other classes are loaded by the parent class loader.
        assertSame(IV8Native.class, javetClassLoader.loadClass(IV8Native.class.getName()));
    }

    /**
     * The input stream reports zero available bytes and returns at most a few bytes per read,
     * but still delivers all the bytes till EOF.
     */
    private static class ZeroAvailableInputStream extends FilterInputStream {
        private static final int MAX_READ_LENGTH = 100;

        public ZeroAvailableInputStream(InputStream inputStream) {
            super(Objects.requireNonNull(inputStream));
        }

        @Override
        public int available() {
            return 0;
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            return super.read(b, off, Math.min(len, MAX_READ_LENGTH));
        }
    }
}
