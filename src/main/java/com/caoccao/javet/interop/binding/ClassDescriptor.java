/*
 * Copyright (c) 2022. caoccao.com Sam Cao
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

package com.caoccao.javet.interop.binding;

import com.caoccao.javet.annotations.V8Convert;
import com.caoccao.javet.enums.V8ConversionMode;
import com.caoccao.javet.enums.V8ProxyMode;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * The type Class descriptor.
 *
 * @since 1.1.7
 */
public class ClassDescriptor {
    /**
     * The Apply functions.
     *
     * @since 1.1.7
     */
    protected List<Method> applyFunctions;
    /**
     * The Constructors.
     *
     * @since 0.9.8
     */
    protected List<Constructor<?>> constructors;
    /**
     * The Field map.
     *
     * @since 0.9.7
     */
    protected Map<String, Field> fieldMap;
    /**
     * The Generic getters.
     *
     * @since 0.9.6
     */
    protected List<Method> genericGetters;
    /**
     * The Generic setters.
     *
     * @since 0.9.6
     */
    protected List<Method> genericSetters;
    /**
     * The Getters map.
     *
     * @since 0.9.6
     */
    protected Map<String, List<Method>> gettersMap;
    /**
     * The Methods map.
     *
     * @since 0.9.6
     */
    protected Map<String, List<Method>> methodsMap;
    /**
     * The Proxy mode.
     *
     * @since 0.9.9
     */
    protected V8ProxyMode proxyMode;
    /**
     * The Setters map.
     *
     * @since 0.9.6
     */
    protected Map<String, List<Method>> settersMap;
    /**
     * The Target class.
     *
     * @since 0.9.6
     */
    protected Class<?> targetClass;
    /**
     * The Target type list.
     *
     * @since 3.0.3
     */
    protected boolean targetTypeList;
    /**
     * The target type map.
     *
     * @since 0.9.7
     */
    protected boolean targetTypeMap;
    /**
     * The target type set.
     *
     * @since 0.9.7
     */
    protected boolean targetTypeSet;
    /**
     * The Unique key set.
     *
     * @since 0.9.7
     */
    protected Set<String> uniqueKeySet;

    /**
     * Instantiates a new Class descriptor.
     *
     * @param proxyMode   the proxy mode
     * @param targetClass the target class
     * @since 1.1.7
     */
    public ClassDescriptor(V8ProxyMode proxyMode, Class<?> targetClass) {
        applyFunctions = new ArrayList<>();
        constructors = new ArrayList<>();
        fieldMap = new LinkedHashMap<>();
        genericGetters = new ArrayList<>();
        genericSetters = new ArrayList<>();
        gettersMap = new LinkedHashMap<>();
        methodsMap = new LinkedHashMap<>();
        this.proxyMode = proxyMode;
        settersMap = new LinkedHashMap<>();
        this.targetClass = targetClass;
        targetTypeList = List.class.isAssignableFrom(targetClass);
        if (!targetTypeList) {
            targetTypeMap = Map.class.isAssignableFrom(targetClass);
            if (!targetTypeMap) {
                targetTypeSet = Set.class.isAssignableFrom(targetClass);
            }
        }
        uniqueKeySet = new LinkedHashSet<>();
    }

    /**
     * Gets apply functions.
     *
     * @return the apply functions
     * @since 1.1.7
     */
    public List<Method> getApplyFunctions() {
        return applyFunctions;
    }

    /**
     * Gets constructors.
     *
     * @return the constructors
     * @since 1.1.7
     */
    public List<Constructor<?>> getConstructors() {
        return constructors;
    }

    /**
     * Gets conversion mode.
     *
     * @return the conversion mode
     * @since 1.1.7
     */
    public V8ConversionMode getConversionMode() {
        return getTargetClass().isAnnotationPresent(V8Convert.class)
                ? getTargetClass().getAnnotation(V8Convert.class).mode()
                : V8ConversionMode.Transparent;
    }

    /**
     * Gets field map.
     *
     * @return the field map
     * @since 1.1.7
     */
    public Map<String, Field> getFieldMap() {
        return fieldMap;
    }

    /**
     * Gets generic getters.
     *
     * @return the generic getters
     * @since 1.1.7
     */
    public List<Method> getGenericGetters() {
        return genericGetters;
    }

    /**
     * Gets generic setters.
     *
     * @return the generic setters
     * @since 1.1.7
     */
    public List<Method> getGenericSetters() {
        return genericSetters;
    }

    /**
     * Gets getters map.
     *
     * @return the getters map
     * @since 1.1.7
     */
    public Map<String, List<Method>> getGettersMap() {
        return gettersMap;
    }

    /**
     * Gets methods map.
     *
     * @return the methods map
     * @since 1.1.7
     */
    public Map<String, List<Method>> getMethodsMap() {
        return methodsMap;
    }

    /**
     * Gets proxy mode.
     *
     * @return the proxy mode
     * @since 1.1.7
     */
    public V8ProxyMode getProxyMode() {
        return proxyMode;
    }

    /**
     * Gets setters map.
     *
     * @return the setters map
     * @since 1.1.7
     */
    public Map<String, List<Method>> getSettersMap() {
        return settersMap;
    }

    /**
     * Gets target class.
     *
     * @return the target class
     * @since 1.1.7
     */
    public Class<?> getTargetClass() {
        return targetClass;
    }

    /**
     * Gets unique key set.
     *
     * @return the unique key set
     * @since 1.1.7
     */
    public Set<String> getUniqueKeySet() {
        return uniqueKeySet;
    }

    /**
     * Is target type list.
     *
     * @return true: is a list, false: is not a list
     * @since 3.0.3
     */
    public boolean isTargetTypeList() {
        return targetTypeList;
    }

    /**
     * Is target type map.
     *
     * @return true: is a map, false: is not a map
     * @since 1.1.7
     */
    public boolean isTargetTypeMap() {
        return targetTypeMap;
    }

    /**
     * Is target type set.
     *
     * @return true: is a set, false: is not a set
     * @since 1.1.7
     */
    public boolean isTargetTypeSet() {
        return targetTypeSet;
    }
}
