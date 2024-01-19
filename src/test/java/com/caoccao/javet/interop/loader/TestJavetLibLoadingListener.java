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

package com.caoccao.javet.interop.loader;

import com.caoccao.javet.enums.JSRuntimeType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestJavetLibLoadingListener {
    @Test
    public void testGetProperty() {
        assertNull(System.getProperty(JavetLibLoadingListener.PROPERTY_KEY_JAVET_LIB_LOADING_PATH));
        assertNull(System.getProperty(JavetLibLoadingListener.PROPERTY_KEY_JAVET_LIB_LOADING_TYPE));

        assertEquals("javet", new JavetLibLoadingListener().getLibPath(JSRuntimeType.V8).getName());
        System.setProperty(JavetLibLoadingListener.PROPERTY_KEY_JAVET_LIB_LOADING_PATH, "/abc");
        assertEquals("abc", new JavetLibLoadingListener().getLibPath(JSRuntimeType.V8).getName());
        System.clearProperty(JavetLibLoadingListener.PROPERTY_KEY_JAVET_LIB_LOADING_PATH);

        assertFalse(new JavetLibLoadingListener().isLibInSystemPath(JSRuntimeType.V8));
        assertTrue(new JavetLibLoadingListener().isDeploy(JSRuntimeType.V8));
        System.setProperty(
                JavetLibLoadingListener.PROPERTY_KEY_JAVET_LIB_LOADING_TYPE,
                JavetLibLoadingListener.JAVET_LIB_LOADING_TYPE_SYSTEM);
        assertTrue(new JavetLibLoadingListener().isLibInSystemPath(JSRuntimeType.V8));
        assertFalse(new JavetLibLoadingListener().isDeploy(JSRuntimeType.V8));
        System.setProperty(
                JavetLibLoadingListener.PROPERTY_KEY_JAVET_LIB_LOADING_TYPE,
                JavetLibLoadingListener.JAVET_LIB_LOADING_TYPE_CUSTOM);
        assertFalse(new JavetLibLoadingListener().isLibInSystemPath(JSRuntimeType.V8));
        assertFalse(new JavetLibLoadingListener().isDeploy(JSRuntimeType.V8));
        System.clearProperty(JavetLibLoadingListener.PROPERTY_KEY_JAVET_LIB_LOADING_TYPE);
    }
}
