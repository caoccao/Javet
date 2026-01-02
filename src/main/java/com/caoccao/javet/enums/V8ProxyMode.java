/*
 * Copyright (c) 2022-2026. caoccao.com Sam Cao
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

package com.caoccao.javet.enums;

/**
 * The enum V8 proxy mode.
 *
 * @since 1.1.7
 */
public enum V8ProxyMode {
    /**
     * Class mode.
     *
     * @since 1.1.7
     */
    Class,
    /**
     * Function mode.
     *
     * @since 1.1.7
     */
    Function,
    /**
     * Object mode.
     *
     * @since 1.1.7
     */
    Object;

    /**
     * Is class mode.
     *
     * @param objectClass the object class
     * @return true: class mode, false: not class mode
     * @since 1.1.7
     */
    public static boolean isClassMode(Class<?> objectClass) {
        return !(objectClass.isPrimitive() || objectClass.isAnnotation()
                || objectClass.isInterface() || objectClass.isArray());
    }
}
