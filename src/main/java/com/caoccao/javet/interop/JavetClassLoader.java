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

import com.caoccao.javet.enums.JSRuntimeType;
import com.caoccao.javet.exceptions.JavetError;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.loader.IJavetLibLoadingListener;
import com.caoccao.javet.interop.loader.JavetLibLoader;
import com.caoccao.javet.utils.JavetOSUtils;
import com.caoccao.javet.utils.JavetReflectionUtils;
import com.caoccao.javet.utils.SimpleMap;

import java.io.DataInputStream;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.Objects;

/**
 * The type Javet class loader.
 *
 * @since 0.8.0
 */
final class JavetClassLoader extends ClassLoader {
    private static final String JAVET_LIB_LOADER_CLASS_NAME = JavetLibLoader.class.getName();
    private static final String METHOD_LOAD = "load";
    private static final String METHOD_SET_LIB_LOADING_LISTENER = "setLibLoadingListener";
    private static final String NODE_NATIVE_CLASS_NAME = NodeNative.class.getName();
    private static final String V8_NATIVE_CLASS_NAME = V8Native.class.getName();
    private final JSRuntimeType jsRuntimeType;

    /**
     * Instantiates a new Javet class loader.
     *
     * @param parent        the parent
     * @param jsRuntimeType the js runtime type
     * @since 0.8.0
     */
    JavetClassLoader(ClassLoader parent, JSRuntimeType jsRuntimeType) {
        super(parent);
        this.jsRuntimeType = Objects.requireNonNull(jsRuntimeType);
    }

    /**
     * Gets the V8 native interface.
     *
     * @return the V8 native interface
     * @throws JavetException the javet exception
     * @since 0.8.0
     */
    IV8Native getNative() throws JavetException {
        if (JavetOSUtils.IS_ANDROID) {
            if (jsRuntimeType.isNode()) {
                return new NodeNative();
            }
            return new V8Native();
        } else {
            try {
                Class<?> classNative = loadClass(jsRuntimeType.isNode() ? NODE_NATIVE_CLASS_NAME : V8_NATIVE_CLASS_NAME);
                Constructor<?> constructor = classNative.getDeclaredConstructor();
                JavetReflectionUtils.safeSetAccessible(constructor);
                return (IV8Native) constructor.newInstance();
            } catch (Exception e) {
                e.printStackTrace(System.err);
                throw new JavetException(
                        JavetError.LibraryNotLoaded,
                        SimpleMap.of(JavetError.PARAMETER_REASON, e.getMessage()),
                        e);
            }
        }
    }

    /**
     * Load the library.
     *
     * @throws JavetException the javet exception
     * @since 0.8.0
     */
    void load() throws JavetException {
        if (JavetOSUtils.IS_ANDROID) {
            JavetLibLoader javetLibLoader = new JavetLibLoader(jsRuntimeType);
            javetLibLoader.load();
        } else {
            try {
                Class<?> classJavetLibLoader = loadClass(JAVET_LIB_LOADER_CLASS_NAME);
                Constructor<?> constructor = classJavetLibLoader.getConstructor(JSRuntimeType.class);
                Object javetLibLoader = constructor.newInstance(jsRuntimeType);
                classJavetLibLoader.getMethod(METHOD_SET_LIB_LOADING_LISTENER, IJavetLibLoadingListener.class)
                        .invoke(null, JavetLibLoader.getLibLoadingListener());
                classJavetLibLoader.getMethod(METHOD_LOAD).invoke(javetLibLoader);
            } catch (Exception e) {
                e.printStackTrace(System.err);
                throw new JavetException(
                        JavetError.LibraryNotLoaded,
                        SimpleMap.of(JavetError.PARAMETER_REASON, e.getMessage()),
                        e);
            }
        }
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        if (JAVET_LIB_LOADER_CLASS_NAME.equals(name) ||
                NODE_NATIVE_CLASS_NAME.equals(name) ||
                V8_NATIVE_CLASS_NAME.equals(name)) {
            String classPath = "/" + name.replace(".", "/") + ".class";
            try (InputStream inputStream = getClass().getResourceAsStream(classPath)) {
                //noinspection ConstantConditions
                byte[] buffer = new byte[inputStream.available()];
                try (DataInputStream dataInputStream = new DataInputStream(inputStream)) {
                    dataInputStream.readFully(buffer);
                    Class<?> classJavetLibLoader = defineClass(name, buffer, 0, buffer.length);
                    resolveClass(classJavetLibLoader);
                    return classJavetLibLoader;
                }
            } catch (Throwable t) {
                t.printStackTrace(System.err);
                throw new ClassNotFoundException(name, t);
            }
        }
        return super.loadClass(name);
    }
}
