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

package com.caoccao.javet.interop.proxy;

import com.caoccao.javet.annotations.V8BindEnabler;
import com.caoccao.javet.annotations.V8Function;
import com.caoccao.javet.exceptions.JavetError;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.callback.JavetCallbackContext;
import com.caoccao.javet.utils.*;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.primitive.V8ValueBoolean;
import com.caoccao.javet.values.primitive.V8ValueString;
import com.caoccao.javet.values.reference.V8ValueArray;

import java.lang.reflect.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The type Javet universal proxy handler.
 *
 * @param <T> the type parameter
 * @since 0.9.6
 */
@SuppressWarnings("unchecked")
public class JavetUniversalProxyHandler<T> extends BaseJavetProxyHandler<T> {

    /**
     * The constant GETTER_PREFIX_ARRAY.
     *
     * @since 0.9.6
     */
    protected static final String[] GETTER_PREFIX_ARRAY = new String[]{"get", "is"};
    /**
     * The constant METHOD_NAME_CONSTRUCT.
     *
     * @since 0.9.9
     */
    protected static final String METHOD_NAME_CONSTRUCT = "construct";
    /**
     * The constant METHOD_NAME_CONSTRUCTOR.
     *
     * @since 0.9.8
     */
    protected static final String METHOD_NAME_CONSTRUCTOR = "constructor";
    /**
     * The constant PATTERN_CAPITALIZED_PREFIX.
     *
     * @since 0.9.7
     */
    protected static final Pattern PATTERN_CAPITALIZED_PREFIX = Pattern.compile("^[A-Z]+");
    /**
     * The constant SETTER_PREFIX_ARRAY.
     *
     * @since 0.9.6
     */
    protected static final String[] SETTER_PREFIX_ARRAY = new String[]{"set", "put"};
    /**
     * The Class mode.
     *
     * @since 0.9.9
     */
    protected boolean classMode;
    /**
     * The Constructors.
     *
     * @since 0.9.8
     */
    protected List<Constructor> constructors;
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
     * The Is target type map.
     *
     * @since 0.9.7
     */
    protected boolean isTargetTypeMap;
    /**
     * The Is target type set.
     *
     * @since 0.9.7
     */
    protected boolean isTargetTypeSet;
    /**
     * The Methods map.
     *
     * @since 0.9.6
     */
    protected Map<String, List<Method>> methodsMap;
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
    protected Class<T> targetClass;
    /**
     * The Unique key set.
     *
     * @since 0.9.7
     */
    protected Set<String> uniqueKeySet;

    /**
     * Instantiates a new Javet universal proxy handler.
     *
     * @param v8Runtime    the V8 runtime
     * @param targetObject the target object
     * @since 0.9.6
     */
    public JavetUniversalProxyHandler(V8Runtime v8Runtime, T targetObject) {
        super(v8Runtime, Objects.requireNonNull(targetObject));
        this.targetClass = (Class<T>) targetObject.getClass();
        initialize();
    }

    /**
     * Execute.
     *
     * @param <E>                 the type parameter
     * @param v8Runtime           the V8 runtime
     * @param targetObject        the target object
     * @param executables         the executables
     * @param javetVirtualObjects the javet virtual objects
     * @return the object
     * @throws Throwable the throwable
     * @since 0.9.10
     */
    protected static <E extends Executable> Object execute(
            V8Runtime v8Runtime, Object targetObject, List<E> executables, JavetVirtualObject[] javetVirtualObjects)
            throws Throwable {
        List<ScoredExecutable<E>> scoredExecutables = new ArrayList<>();
        for (E executable : executables) {
            ScoredExecutable scoredExecutable = new ScoredExecutable(
                    v8Runtime, targetObject, executable, javetVirtualObjects);
            scoredExecutable.calculateScore();
            double score = scoredExecutable.getScore();
            if (score > 0) {
                scoredExecutables.add(scoredExecutable);
            }
        }
        if (!scoredExecutables.isEmpty()) {
            scoredExecutables.sort((o1, o2) -> Double.compare(o2.getScore(), o1.getScore()));
            Throwable lastException = null;
            for (ScoredExecutable<E> scoredExecutable : scoredExecutables) {
                try {
                    return scoredExecutable.execute();
                } catch (Throwable t) {
                    lastException = t;
                }
            }
            throw lastException;
        }
        return null;
    }

    /**
     * Is class mode.
     *
     * @param objectClass the object class
     * @return the boolean
     */
    public static boolean isClassMode(Class objectClass) {
        return !(objectClass.isPrimitive() || objectClass.isAnnotation()
                || objectClass.isInterface() || objectClass.isArray());
    }

    /**
     * Add method.
     *
     * @param method     the method
     * @param startIndex the start index
     * @param map        the map
     * @since 0.9.6
     */
    protected void addMethod(Method method, int startIndex, Map<String, List<Method>> map) {
        String methodName = method.getName();
        String aliasMethodName = methodName.substring(startIndex);
        Matcher matcher = PATTERN_CAPITALIZED_PREFIX.matcher(aliasMethodName);
        if (matcher.find()) {
            final int capitalizedPrefixLength = matcher.group().length();
            if (capitalizedPrefixLength == 1) {
                aliasMethodName = methodName.substring(startIndex, startIndex + capitalizedPrefixLength).toLowerCase(Locale.ROOT)
                        + methodName.substring(startIndex + capitalizedPrefixLength);
                map.computeIfAbsent(aliasMethodName, key -> new ArrayList<>()).add(method);
            } else {
                for (int i = 1; i < capitalizedPrefixLength; ++i) {
                    aliasMethodName = methodName.substring(startIndex, startIndex + i).toLowerCase(Locale.ROOT)
                            + methodName.substring(startIndex + i);
                    map.computeIfAbsent(aliasMethodName, key -> new ArrayList<>()).add(method);
                }
            }
        } else {
            map.computeIfAbsent(aliasMethodName, key -> new ArrayList<>()).add(method);
        }
    }

    @V8Function
    @Override
    public V8Value construct(V8Value target, V8ValueArray arguments, V8Value newTarget) throws JavetException {
        try {
            return v8Runtime.toV8Value(execute(
                    v8Runtime, null, constructors, V8ValueUtils.convertToVirtualObjects(arguments.toArray())));
        } catch (JavetException e) {
            throw e;
        } catch (Throwable t) {
            throw new JavetException(JavetError.CallbackMethodFailure,
                    SimpleMap.of(
                            JavetError.PARAMETER_METHOD_NAME, METHOD_NAME_CONSTRUCTOR,
                            JavetError.PARAMETER_MESSAGE, t.getMessage()), t);
        }
    }

    @V8Function
    @Override
    public V8Value get(V8Value target, V8Value property, V8Value receiver) throws JavetException {
        if (!fieldMap.isEmpty() && property instanceof V8ValueString) {
            String propertyName = ((V8ValueString) property).toPrimitive();
            Field field = fieldMap.get(propertyName);
            if (field != null) {
                try {
                    Object callee = Modifier.isStatic(field.getModifiers()) ? null : targetObject;
                    Object value = field.get(callee);
                    if (value != null) {
                        return v8Runtime.toV8Value(value);
                    }
                } catch (JavetException e) {
                    throw e;
                } catch (Throwable t) {
                    throw new JavetException(JavetError.CallbackUnknownFailure,
                            SimpleMap.of(JavetError.PARAMETER_MESSAGE, t.getMessage()), t);
                }
            }
        }
        if (!genericGetters.isEmpty()) {
            try {
                Object propertyObject = v8Runtime.toObject(property);
                if (propertyObject != null && !(propertyObject instanceof V8Value)) {
                    for (Method method : genericGetters) {
                        Class<?>[] parameterTypes = method.getParameterTypes();
                        Class<?> parameterType = parameterTypes[0];
                        Object key = propertyObject;
                        boolean isCallable = parameterType.isAssignableFrom(propertyObject.getClass());
                        if (!isCallable && parameterType.isPrimitive()) {
                            key = JavetPrimitiveUtils.toExactPrimitive(parameterType, propertyObject);
                            isCallable = key != null;
                        }
                        if (isCallable) {
                            Object callee = Modifier.isStatic(method.getModifiers()) ? null : targetObject;
                            Object value = method.invoke(callee, key);
                            if (value != null) {
                                return v8Runtime.toV8Value(value);
                            }
                        }
                    }
                }
            } catch (JavetException e) {
                throw e;
            } catch (Throwable t) {
                throw new JavetException(JavetError.CallbackUnknownFailure,
                        SimpleMap.of(JavetError.PARAMETER_MESSAGE, t.getMessage()), t);
            }
        }
        if (property instanceof V8ValueString) {
            String propertyName = ((V8ValueString) property).toPrimitive();
            List<Method> methods = gettersMap.get(propertyName);
            if (methods != null) {
                JavetUniversalInterceptor javetUniversalInterceptor =
                        new JavetUniversalInterceptor(v8Runtime, targetObject, propertyName, methods);
                return v8Runtime.toV8Value(javetUniversalInterceptor.invoke());
            }
            methods = methodsMap.get(propertyName);
            if (methods != null) {
                JavetUniversalInterceptor javetUniversalInterceptor =
                        new JavetUniversalInterceptor(v8Runtime, targetObject, propertyName, methods);
                return v8Runtime.createV8ValueFunction(javetUniversalInterceptor.getCallbackContext());
            }
        }
        return v8Runtime.createV8ValueUndefined();
    }

    /**
     * Gets getter prefix length.
     *
     * @param method the method
     * @return the getter prefix length
     * @since 0.9.6
     */
    protected int getGetterPrefixLength(Method method) {
        String methodName = method.getName();
        for (String prefix : GETTER_PREFIX_ARRAY) {
            if (methodName.startsWith(prefix) && methodName.length() > prefix.length()
                    && method.getParameterCount() == 0) {
                return prefix.length();
            }
        }
        return 0;
    }

    /**
     * Gets setter prefix length.
     *
     * @param method the method
     * @return the setter prefix length
     * @since 0.9.6
     */
    protected int getSetterPrefixLength(Method method) {
        String methodName = method.getName();
        for (String prefix : SETTER_PREFIX_ARRAY) {
            if (methodName.startsWith(prefix) && methodName.length() > prefix.length()
                    && method.getParameterCount() == 1) {
                return prefix.length();
            }
        }
        return 0;
    }

    @V8Function
    @Override
    public V8ValueBoolean has(V8Value target, V8Value property) throws JavetException {
        boolean isFound = false;
        if (!classMode) {
            if (isTargetTypeMap) {
                isFound = ((Map) targetObject).containsKey(v8Runtime.toObject(property));
            } else if (isTargetTypeSet) {
                isFound = ((Set) targetObject).contains(v8Runtime.toObject(property));
            }
        }
        if (!isFound && (property instanceof V8ValueString)) {
            String propertyName = ((V8ValueString) property).toPrimitive();
            isFound = fieldMap.containsKey(propertyName)
                    || methodsMap.containsKey(propertyName)
                    || gettersMap.containsKey(propertyName)
                    || settersMap.containsKey(propertyName);
        }
        if (!isFound && !genericGetters.isEmpty()) {
            try {
                Object propertyObject = v8Runtime.toObject(property);
                if (propertyObject != null && !(propertyObject instanceof V8Value)) {
                    for (Method method : genericGetters) {
                        Class<?>[] parameterTypes = method.getParameterTypes();
                        Class<?> parameterType = parameterTypes[0];
                        Object parameter = propertyObject;
                        boolean isCallable = parameterType.isAssignableFrom(propertyObject.getClass());
                        if (!isCallable && parameterType.isPrimitive()) {
                            parameter = JavetPrimitiveUtils.toExactPrimitive(parameterType, propertyObject);
                            isCallable = parameter != null;
                        }
                        if (isCallable) {
                            Object callee = Modifier.isStatic(method.getModifiers()) ? null : targetObject;
                            try {
                                isFound = method.invoke(callee, parameter) != null;
                                if (isFound) {
                                    break;
                                }
                            } catch (Throwable t) {
                            }
                        }
                    }
                }
            } catch (JavetException e) {
                throw e;
            } catch (Throwable t) {
            }
        }
        return v8Runtime.createV8ValueBoolean(isFound);
    }

    /**
     * Has constructors.
     *
     * @return the boolean
     * @since 0.9.9
     */
    public boolean hasConstructors() {
        return !constructors.isEmpty();
    }

    /**
     * Initialize.
     *
     * @since 0.9.7
     */
    protected void initialize() {
        constructors = new ArrayList<>();
        fieldMap = new LinkedHashMap<>();
        genericGetters = new ArrayList<>();
        genericSetters = new ArrayList<>();
        methodsMap = new LinkedHashMap<>();
        gettersMap = new LinkedHashMap<>();
        settersMap = new LinkedHashMap<>();
        uniqueKeySet = new LinkedHashSet<>();
        isTargetTypeMap = Map.class.isAssignableFrom(targetClass);
        isTargetTypeSet = Set.class.isAssignableFrom(targetClass);
        classMode = false;
        if (targetObject instanceof Class) {
            Class objectClass = (Class) targetObject;
            classMode = isClassMode(objectClass);
            initializeFieldsAndMethods(objectClass, true);
        }
        initializeFieldsAndMethods(targetClass, false);
    }

    /**
     * Initialize fields and methods.
     *
     * @param currentClass the current class
     * @param staticMode   the static mode
     * @since 0.9.6
     */
    protected void initializeFieldsAndMethods(Class currentClass, boolean staticMode) {
        if (!classMode) {
            if (isTargetTypeMap) {
                uniqueKeySet.addAll(((Map) targetObject).keySet());
            } else if (isTargetTypeSet) {
                uniqueKeySet.addAll((Set) targetObject);
            }
        }
        while (true) {
            if (classMode) {
                for (Constructor constructor : currentClass.getConstructors()) {
                    constructors.add(constructor);
                }
            }
            // All public fields are in the scope.
            for (Field field : currentClass.getFields()) {
                final int fieldModifiers = field.getModifiers();
                if (staticMode && !Modifier.isStatic(fieldModifiers)) {
                    continue;
                }
                if (!(Modifier.isPublic(fieldModifiers))) {
                    continue;
                }
                String fieldName = field.getName();
                if (fieldMap.containsKey(fieldName)) {
                    continue;
                }
                JavetReflectionUtils.safeSetAccessible(field);
                fieldMap.put(fieldName, field);
                if (!isTargetTypeMap && !isTargetTypeSet) {
                    uniqueKeySet.add(fieldName);
                }
            }
            // All public methods are in the scope.
            for (Method method : currentClass.getMethods()) {
                final int methodModifiers = method.getModifiers();
                if (staticMode && !Modifier.isStatic(methodModifiers)) {
                    continue;
                }
                if (!(Modifier.isPublic(methodModifiers))) {
                    continue;
                }
                JavetReflectionUtils.safeSetAccessible(method);
                if (isGenericGetter(method)) {
                    genericGetters.add(method);
                } else if (isGenericSetter(method)) {
                    genericSetters.add(method);
                } else {
                    final int getterPrefixLength = getGetterPrefixLength(method);
                    if (getterPrefixLength > 0) {
                        addMethod(method, getterPrefixLength, gettersMap);
                        if (!isTargetTypeMap && !isTargetTypeSet) {
                            String aliasMethodName = method.getName().substring(getterPrefixLength);
                            Matcher matcher = PATTERN_CAPITALIZED_PREFIX.matcher(aliasMethodName);
                            if (matcher.find()) {
                                final int capitalizedPrefixLength = matcher.group().length();
                                if (capitalizedPrefixLength == 1) {
                                    uniqueKeySet.add(aliasMethodName.substring(0, capitalizedPrefixLength).toLowerCase(Locale.ROOT)
                                            + aliasMethodName.substring(capitalizedPrefixLength));
                                } else {
                                    uniqueKeySet.add(aliasMethodName.substring(0, capitalizedPrefixLength - 1).toLowerCase(Locale.ROOT)
                                            + aliasMethodName.substring(capitalizedPrefixLength - 1));
                                }
                            }
                        }
                    } else {
                        final int setterPrefixLength = getSetterPrefixLength(method);
                        if (setterPrefixLength > 0) {
                            addMethod(method, setterPrefixLength, settersMap);
                        }
                    }
                }
                addMethod(method, 0, methodsMap);
            }
            if (currentClass == Object.class) {
                break;
            }
            currentClass = currentClass.getSuperclass();
            if (currentClass == null) {
                break;
            }
        }
    }

    /**
     * Is class mode.
     *
     * @return the boolean
     * @since 0.9.9
     */
    public boolean isClassMode() {
        return classMode;
    }

    /**
     * Is generic getter boolean.
     *
     * @param method the method
     * @return the boolean
     * @since 0.9.6
     */
    protected boolean isGenericGetter(Method method) {
        String methodName = method.getName();
        for (String prefix : GETTER_PREFIX_ARRAY) {
            if (methodName.equals(prefix) && method.getParameterCount() == 1 && !method.isVarArgs()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Is generic setter boolean.
     *
     * @param method the method
     * @return the boolean
     * @since 0.9.6
     */
    protected boolean isGenericSetter(Method method) {
        String methodName = method.getName();
        for (String prefix : SETTER_PREFIX_ARRAY) {
            if (methodName.equals(prefix) && method.getParameterCount() == 2 && !method.isVarArgs()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Is V8 bind enabled.
     *
     * @param methodName the method name
     * @return the boolean
     */
    @V8BindEnabler
    public boolean isV8BindEnabled(String methodName) {
        if (JavetUniversalProxyHandler.METHOD_NAME_CONSTRUCT.equals(methodName)) {
            return hasConstructors();
        }
        return true;
    }

    @V8Function
    @Override
    public V8Value ownKeys(V8Value target) throws JavetException {
        if (!classMode) {
            if (isTargetTypeMap) {
                return v8Runtime.toV8Value(((Map) targetObject).keySet().toArray());
            } else if (isTargetTypeSet) {
                return v8Runtime.toV8Value(((Set) targetObject).toArray());
            }
        }
        return v8Runtime.toV8Value(uniqueKeySet.toArray());
    }

    @V8Function
    @Override
    public V8ValueBoolean set(V8Value target, V8Value propertyKey, V8Value propertyValue, V8Value receiver) throws JavetException {
        boolean isSet = false;
        if (!fieldMap.isEmpty() && propertyKey instanceof V8ValueString) {
            String propertyName = ((V8ValueString) propertyKey).toPrimitive();
            Field field = fieldMap.get(propertyName);
            if (field != null) {
                final int fieldModifiers = field.getModifiers();
                if (!Modifier.isFinal(fieldModifiers)) {
                    try {
                        Object callee = Modifier.isStatic(fieldModifiers) ? null : targetObject;
                        field.set(callee, v8Runtime.toObject(propertyValue));
                        isSet = true;
                    } catch (JavetException e) {
                        throw e;
                    } catch (Throwable t) {
                        throw new JavetException(JavetError.CallbackUnknownFailure,
                                SimpleMap.of(JavetError.PARAMETER_MESSAGE, t.getMessage()), t);
                    }
                }
            }
        }
        if (!isSet && (!genericSetters.isEmpty() || !settersMap.isEmpty())) {
            Object propertyObject, valueObject;
            try {
                propertyObject = v8Runtime.toObject(propertyKey);
                valueObject = v8Runtime.toObject(propertyValue);
            } catch (JavetException e) {
                throw e;
            } catch (Throwable t) {
                throw new JavetException(JavetError.CallbackUnknownFailure,
                        SimpleMap.of(JavetError.PARAMETER_MESSAGE, t.getMessage()), t);
            }
            if (!genericSetters.isEmpty() && propertyObject != null && !(propertyObject instanceof V8Value)) {
                try {
                    for (Method method : genericSetters) {
                        Class<?>[] parameterTypes = method.getParameterTypes();
                        Class<?> keyParameterType = parameterTypes[0];
                        Class<?> valueParameterType = parameterTypes[1];
                        Object key = propertyObject;
                        Object value = valueObject;
                        boolean isCallable = keyParameterType.isAssignableFrom(propertyObject.getClass());
                        if (!isCallable && keyParameterType.isPrimitive()) {
                            key = JavetPrimitiveUtils.toExactPrimitive(keyParameterType, propertyObject);
                            isCallable = key != null;
                        }
                        if (isCallable) {
                            isCallable = valueParameterType.isAssignableFrom(valueObject.getClass());
                            if (!isCallable && valueParameterType.isPrimitive()) {
                                value = JavetPrimitiveUtils.toExactPrimitive(valueParameterType, valueObject);
                                isCallable = value != null;
                            }
                        }
                        if (isCallable) {
                            Object callee = Modifier.isStatic(method.getModifiers()) ? null : targetObject;
                            method.invoke(callee, key, value);
                            isSet = true;
                            break;
                        }
                    }
                } catch (Throwable t) {
                    throw new JavetException(JavetError.CallbackUnknownFailure,
                            SimpleMap.of(JavetError.PARAMETER_MESSAGE, t.getMessage()), t);
                }
            }
            if (!isSet && (propertyObject instanceof String)) {
                String propertyName = (String) propertyObject;
                List<Method> methods = settersMap.get(propertyName);
                if (methods != null) {
                    JavetUniversalInterceptor javetUniversalInterceptor =
                            new JavetUniversalInterceptor(v8Runtime, targetObject, propertyName, methods);
                    javetUniversalInterceptor.invoke(propertyValue);
                    isSet = true;
                }
            }
        }
        return v8Runtime.createV8ValueBoolean(isSet);
    }

    /**
     * The type Javet universal interceptor.
     *
     * @since 0.9.6
     */
    public static class JavetUniversalInterceptor {
        private static final String METHOD_NAME_INVOKE = "invoke";
        private String jsMethodName;
        private List<Method> methods;
        private Object targetObject;
        private V8Runtime v8Runtime;

        /**
         * Instantiates a new Javet universal interceptor.
         *
         * @param v8Runtime    the V8 runtime
         * @param targetObject the target object
         * @param jsMethodName the JS method name
         * @param methods      the methods
         * @since 0.9.6
         */
        public JavetUniversalInterceptor(
                V8Runtime v8Runtime, Object targetObject, String jsMethodName, List<Method> methods) {
            this.jsMethodName = jsMethodName;
            this.methods = methods;
            this.targetObject = targetObject;
            this.v8Runtime = v8Runtime;
        }


        /**
         * Gets callback context.
         *
         * @return the callback context
         * @since 0.9.6
         */
        public JavetCallbackContext getCallbackContext() {
            try {
                return new JavetCallbackContext(
                        this,
                        getClass().getMethod(METHOD_NAME_INVOKE, V8Value[].class));
            } catch (NoSuchMethodException e) {
            }
            return null;
        }

        /**
         * Gets JS method name.
         *
         * @return the JS method name
         * @since 0.9.6
         */
        public String getJSMethodName() {
            return jsMethodName;
        }

        /**
         * Gets methods.
         *
         * @return the methods
         * @since 0.9.6
         */
        public List<Method> getMethods() {
            return methods;
        }

        /**
         * Gets target object.
         *
         * @return the target object
         * @since 0.9.6
         */
        public Object getTargetObject() {
            return targetObject;
        }

        /**
         * Invoke.
         *
         * @param v8Values the V8 values
         * @return the object
         * @throws JavetException the javet exception
         * @since 0.9.6
         */
        public Object invoke(V8Value... v8Values) throws JavetException {
            try {
                return execute(v8Runtime, targetObject, methods, V8ValueUtils.convertToVirtualObjects(v8Values));
            } catch (JavetException e) {
                throw e;
            } catch (Throwable t) {
                throw new JavetException(JavetError.CallbackMethodFailure,
                        SimpleMap.of(
                                JavetError.PARAMETER_METHOD_NAME, jsMethodName,
                                JavetError.PARAMETER_MESSAGE, t.getMessage()), t);
            }
        }
    }

    /**
     * The type Scored executable.
     *
     * @param <E> the type parameter
     * @since 0.9.6
     */
    public static class ScoredExecutable<E extends Executable> {
        private E executable;
        private JavetVirtualObject[] javetVirtualObjects;
        private double score;
        private Object targetObject;
        private V8Runtime v8Runtime;

        /**
         * Instantiates a new Scored executable.
         *
         * @param v8Runtime           the V8 runtime
         * @param targetObject        the target object
         * @param executable          the executable
         * @param javetVirtualObjects the javet virtual objects
         * @since 0.9.10
         */
        public ScoredExecutable(
                V8Runtime v8Runtime, Object targetObject, E executable, JavetVirtualObject[] javetVirtualObjects) {
            this.executable = executable;
            this.javetVirtualObjects = javetVirtualObjects;
            this.score = 0;
            this.targetObject = targetObject;
            this.v8Runtime = v8Runtime;
        }

        /**
         * Calculate score double.
         *
         * @throws JavetException the javet exception
         * @since 0.9.10
         */
        public void calculateScore() throws JavetException {
            // Max score is 1. Min score is 0.
            final int parameterCount = executable.getParameterCount();
            Class<?>[] parameterTypes = executable.getParameterTypes();
            boolean isExecutableVarArgs = executable.isVarArgs();
            score = 0;
            final int length = javetVirtualObjects.length;
            if (length == 0) {
                if (isExecutableVarArgs) {
                    if (parameterCount == 1) {
                        score = 0.99;
                    }
                } else {
                    if (parameterCount == 0) {
                        score = 1;
                    }
                }
            } else {
                boolean isVarArgs = isExecutableVarArgs && length >= parameterCount - 1;
                boolean isFixedArgs = !isExecutableVarArgs && length == parameterCount;
                if (isVarArgs || isFixedArgs) {
                    double totalScore = 0;
                    final int fixedParameterCount = isExecutableVarArgs ? parameterCount - 1 : parameterCount;
                    for (int i = 0; i < fixedParameterCount; i++) {
                        Class<?> parameterType = parameterTypes[i];
                        Object object = javetVirtualObjects[i].getObject();
                        if (object == null) {
                            if (parameterType.isPrimitive()) {
                                totalScore = 0;
                                break;
                            } else {
                                totalScore += 1;
                            }
                        } else if (parameterType.isAssignableFrom(object.getClass())) {
                            totalScore += 1;
                        } else if (parameterType.isPrimitive()
                                && JavetPrimitiveUtils.toExactPrimitive(parameterType, object) != null) {
                            totalScore += 0.9;
                        } else {
                            totalScore = 0;
                            break;
                        }
                    }
                    if ((fixedParameterCount == 0 || (fixedParameterCount > 0 && totalScore > 0)) && isVarArgs) {
                        Class<?> componentType = parameterTypes[fixedParameterCount].getComponentType();
                        for (int i = fixedParameterCount; i < length; ++i) {
                            Object object = javetVirtualObjects[i].getObject();
                            if (object == null) {
                                if (componentType.isPrimitive()) {
                                    totalScore = 0;
                                    break;
                                } else {
                                    totalScore += 1;
                                }
                            } else if (componentType.isAssignableFrom(object.getClass())) {
                                totalScore += 1;
                            } else if (componentType.isPrimitive()
                                    && JavetPrimitiveUtils.toExactPrimitive(componentType, object) != null) {
                                totalScore += 0.8;
                            } else {
                                totalScore = 0;
                                break;
                            }
                        }
                    }
                    if (totalScore > 0) {
                        score = totalScore / length;
                    }
                }
            }
        }

        /**
         * Execute.
         *
         * @return the object
         * @throws Throwable the throwable
         * @since 0.9.10
         */
        public Object execute() throws Throwable {
            final int length = javetVirtualObjects.length;
            Object callee = Modifier.isStatic(executable.getModifiers()) ? null : targetObject;
            final int parameterCount = executable.getParameterCount();
            Class<?>[] parameterTypes = executable.getParameterTypes();
            boolean isExecutableVarArgs = executable.isVarArgs();
            if (length == 0) {
                if (isExecutableVarArgs) {
                    Class<?> componentType = parameterTypes[parameterCount - 1];
                    Object varObject = Array.newInstance(componentType, 0);
                    if (executable instanceof Constructor) {
                        return ((Constructor) executable).newInstance(varObject);
                    } else {
                        return ((Method) executable).invoke(callee, varObject);
                    }
                } else {
                    if (executable instanceof Constructor) {
                        return ((Constructor) executable).newInstance();
                    } else {
                        return ((Method) executable).invoke(callee);
                    }
                }
            } else {
                List<Object> parameters = new ArrayList<>();
                final int fixedParameterCount = isExecutableVarArgs ? parameterCount - 1 : parameterCount;
                for (int i = 0; i < fixedParameterCount; i++) {
                    Class<?> parameterType = parameterTypes[i];
                    Object parameter = javetVirtualObjects[i].getObject();
                    if (parameter != null && !parameterType.isAssignableFrom(parameter.getClass())
                            && parameterType.isPrimitive()) {
                        parameter = JavetPrimitiveUtils.toExactPrimitive(parameterType, parameter);
                    }
                    parameters.add(parameter);
                }
                if (isExecutableVarArgs) {
                    Class<?> parameterType = parameterTypes[fixedParameterCount];
                    Class<?> componentType = parameterType.getComponentType();
                    Object varObject = Array.newInstance(componentType, length - fixedParameterCount);
                    for (int i = fixedParameterCount; i < length; ++i) {
                        Object parameter = javetVirtualObjects[i].getObject();
                        if (parameter != null && !componentType.isAssignableFrom(parameter.getClass())
                                && componentType.isPrimitive()) {
                            parameter = JavetPrimitiveUtils.toExactPrimitive(componentType, parameter);
                        }
                        Array.set(varObject, i - fixedParameterCount, parameter);
                    }
                    parameters.add(varObject);
                }
                if (executable instanceof Constructor) {
                    return ((Constructor) executable).newInstance(parameters.toArray());
                } else {
                    return ((Method) executable).invoke(callee, parameters.toArray());
                }
            }
        }

        /**
         * Gets score.
         *
         * @return the score
         * @since 0.9.6
         */
        public double getScore() {
            return score;
        }
    }
}
