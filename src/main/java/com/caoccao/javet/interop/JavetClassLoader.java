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

import com.caoccao.javet.exceptions.JavetLibraryNotLoadedException;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;

@SuppressWarnings("unchecked")
class JavetClassLoader extends ClassLoader {
    protected static final String JAVET_LIB_LOADER_CLASS_NAME = JavetLibLoader.class.getName();
    protected static final String NODE_NATIVE_CLASS_NAME = NodeNative.class.getName();
    protected static final String V8_NATIVE_CLASS_NAME = V8Native.class.getName();
    protected static final String METHOD_LOAD = "load";
    protected JSRuntimeType jsRuntimeType;

    public JavetClassLoader(ClassLoader parent, JSRuntimeType jsRuntimeType) {
        super(parent);
        this.jsRuntimeType = jsRuntimeType;
    }

    IV8Native getNative() throws JavetLibraryNotLoadedException {
        try {
            Class classNative = loadClass(jsRuntimeType.isNode() ? NODE_NATIVE_CLASS_NAME : V8_NATIVE_CLASS_NAME);
            return (IV8Native) classNative.newInstance();
        } catch (Exception e) {
            throw new JavetLibraryNotLoadedException(e.getMessage(), e);
        }
    }

    void load() throws JavetLibraryNotLoadedException {
        try {
            Class classJavetLibLoader = loadClass(JAVET_LIB_LOADER_CLASS_NAME);
            Constructor constructor = classJavetLibLoader.getConstructor(JSRuntimeType.class);
            Object javetLibLoader = constructor.newInstance(jsRuntimeType);
            classJavetLibLoader.getMethod(METHOD_LOAD).invoke(javetLibLoader);
        } catch (Exception e) {
            throw new JavetLibraryNotLoadedException(e.getMessage(), e);
        }
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        if (JAVET_LIB_LOADER_CLASS_NAME.equals(name) ||
                NODE_NATIVE_CLASS_NAME.equals(name) ||
                V8_NATIVE_CLASS_NAME.equals(name)) {
            String classPath = "/" + name.replace(".", "/") + ".class";
            try (InputStream inputStream = getClass().getResourceAsStream(classPath)) {
                byte[] buffer = new byte[inputStream.available()];
                try (DataInputStream dataInputStream = new DataInputStream(inputStream)) {
                    dataInputStream.readFully(buffer);
                    Class<?> classJavetLibLoader = defineClass(name, buffer, 0, buffer.length);
                    resolveClass(classJavetLibLoader);
                    return classJavetLibLoader;
                }
            } catch (IOException e) {
                e.printStackTrace(System.err);
            }
        }
        return super.loadClass(name);
    }
}
