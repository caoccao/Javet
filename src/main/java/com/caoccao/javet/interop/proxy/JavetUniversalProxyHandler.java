/*
 * Copyright (c) 2021-2022. caoccao.com Sam Cao
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

package com.caoccao.javet.interop.proxy;

import com.caoccao.javet.annotations.*;
import com.caoccao.javet.enums.V8ConversionMode;
import com.caoccao.javet.exceptions.JavetError;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.V8Scope;
import com.caoccao.javet.interop.callback.JavetCallbackContext;
import com.caoccao.javet.utils.*;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.primitive.V8ValueBoolean;
import com.caoccao.javet.values.primitive.V8ValueString;
import com.caoccao.javet.values.reference.*;
import com.caoccao.javet.values.reference.builtin.V8ValueBuiltInSymbol;

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
     * The constant FUNCTION_NAME_TO_V8_VALUE.
     *
     * @since 1.0.4
     */
    public static final String FUNCTION_NAME_TO_V8_VALUE = "toV8Value";
    /**
     * The constant FUNCTION_NAME_LENGTH.
     *
     * @since 1.0.6
     */
    public static final String FUNCTION_NAME_LENGTH = "length";
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
     * The constant V8_VALUE_CLASS.
     *
     * @since 0.9.10
     */
    protected static final Class<?> V8_VALUE_CLASS = V8Value.class;
    /**
     * The constant V8_VALUE_FUNCTION_CLASS.
     *
     * @since 0.9.10
     */
    protected static final Class<?> V8_VALUE_FUNCTION_CLASS = V8ValueFunction.class;
    /**
     * The constant V8_VALUE_OBJECT_CLASS.
     *
     * @since 0.9.10
     */
    protected static final Class<?> V8_VALUE_OBJECT_CLASS = V8ValueObject.class;
    /**
     * The constant V8_VALUE_PROXY_CLASS.
     *
     * @since 0.9.10
     */
    protected static final Class<?> V8_VALUE_PROXY_CLASS = V8ValueProxy.class;
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
     * @param thisObject          this object
     * @param executables         the executables
     * @param javetVirtualObjects the javet virtual objects
     * @return the object
     * @throws Throwable the throwable
     * @since 0.9.10
     */
    protected static <E extends AccessibleObject> Object execute(
            V8Runtime v8Runtime,
            Object targetObject,
            V8ValueObject thisObject,
            List<E> executables,
            JavetVirtualObject[] javetVirtualObjects) throws Throwable {
        List<ScoredExecutable<E>> scoredExecutables = new ArrayList<>();
        for (E executable : executables) {
            ScoredExecutable<E> scoredExecutable = new ScoredExecutable<>(
                    v8Runtime, targetObject, thisObject, executable, javetVirtualObjects);
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
            if (lastException != null) {
                throw lastException;
            }
        }
        return null;
    }

    /**
     * Is class mode.
     *
     * @param objectClass the object class
     * @return the boolean
     */
    public static boolean isClassMode(Class<?> objectClass) {
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
        if (method.isAnnotationPresent(V8Function.class)) {
            String methodName = method.getName();
            String aliasMethodName = method.getAnnotation(V8Function.class).name();
            if (aliasMethodName.length() > 0) {
                methodName = aliasMethodName;
            }
            List<Method> methods = map.computeIfAbsent(methodName, k -> new ArrayList<>());
            methods.add(method);
        } else {
            String methodName = method.getName();
            String aliasMethodName = methodName.substring(startIndex);
            Matcher matcher = PATTERN_CAPITALIZED_PREFIX.matcher(aliasMethodName);
            if (matcher.find()) {
                final int capitalizedPrefixLength = matcher.group().length();
                if (capitalizedPrefixLength == 1) {
                    aliasMethodName = methodName.substring(startIndex, startIndex + capitalizedPrefixLength).toLowerCase(Locale.ROOT)
                            + methodName.substring(startIndex + capitalizedPrefixLength);
                    List<Method> methods = map.computeIfAbsent(aliasMethodName, k -> new ArrayList<>());
                    methods.add(method);
                } else {
                    for (int i = 1; i < capitalizedPrefixLength; ++i) {
                        aliasMethodName = methodName.substring(startIndex, startIndex + i).toLowerCase(Locale.ROOT)
                                + methodName.substring(startIndex + i);
                        List<Method> methods = map.computeIfAbsent(aliasMethodName, k -> new ArrayList<>());
                        methods.add(method);
                    }
                }
            } else {
                List<Method> methods = map.computeIfAbsent(aliasMethodName, k -> new ArrayList<>());
                methods.add(method);
            }
        }
    }

    @V8Function
    @Override
    public V8Value construct(V8Value target, V8ValueArray arguments, V8Value newTarget) throws JavetException {
        V8Value[] v8Values = null;
        try {
            v8Values = arguments.toArray();
            return v8Runtime.toV8Value(execute(
                    v8Runtime, null, (V8ValueObject) target, constructors, V8ValueUtils.convertToVirtualObjects(v8Values)));
        } catch (JavetException e) {
            throw e;
        } catch (Throwable t) {
            throw new JavetException(JavetError.CallbackMethodFailure,
                    SimpleMap.of(
                            JavetError.PARAMETER_METHOD_NAME, METHOD_NAME_CONSTRUCTOR,
                            JavetError.PARAMETER_MESSAGE, t.getMessage()), t);
        } finally {
            if (v8Values != null) {
                JavetResourceUtils.safeClose((Object[]) v8Values);
            }
        }
    }

    @V8Function
    @Override
    public V8Value get(V8Value target, V8Value property, V8Value receiver) throws JavetException {
        if (property instanceof V8ValueString) {
            String propertyString = ((V8ValueString) property).toPrimitive();
            if (JavetStringUtils.isDigital(propertyString)) {
                final int index = Integer.parseInt(propertyString);
                if (index >= 0) {
                    if (targetClass.isArray()) {
                        if (index < Array.getLength(targetObject)) {
                            return v8Runtime.toV8Value(Array.get(targetObject, index));
                        }
                    } else if (List.class.isAssignableFrom(targetClass)) {
                        List<?> list = (List<?>) targetObject;
                        if (index < list.size()) {
                            return v8Runtime.toV8Value(list.get(index));
                        }
                    }
                }
            } else if (targetClass.isArray() && FUNCTION_NAME_LENGTH.equals(propertyString)) {
                return v8Runtime.toV8Value(Array.getLength(targetObject));
            }
        }
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
        if (property instanceof V8ValueString) {
            String propertyName = ((V8ValueString) property).toPrimitive();
            List<Method> methods = methodsMap.get(propertyName);
            if (methods != null && !methods.isEmpty()) {
                JavetUniversalInterceptor javetUniversalInterceptor =
                        new JavetUniversalInterceptor(v8Runtime, targetObject, propertyName, methods);
                return v8Runtime.createV8ValueFunction(javetUniversalInterceptor.getCallbackContext());
            }
            methods = gettersMap.get(propertyName);
            if (methods != null && !methods.isEmpty()) {
                JavetUniversalInterceptor javetUniversalInterceptor =
                        new JavetUniversalInterceptor(v8Runtime, targetObject, propertyName, methods);
                return v8Runtime.toV8Value(javetUniversalInterceptor.invoke((V8ValueObject) target));
            }
            if (FUNCTION_NAME_TO_V8_VALUE.equals(propertyName)) {
                return new JavetProxySymbolToPrimitiveConverter<>(v8Runtime, targetObject).getV8ValueFunction();
            }
        } else if (property instanceof V8ValueSymbol) {
            V8ValueSymbol propertySymbol = (V8ValueSymbol) property;
            String description = propertySymbol.getDescription();
            if (V8ValueBuiltInSymbol.SYMBOL_PROPERTY_TO_PRIMITIVE.equals(description)) {
                return new JavetProxySymbolToPrimitiveConverter<>(v8Runtime, targetObject).getV8ValueFunction();
            } else if (V8ValueBuiltInSymbol.SYMBOL_PROPERTY_ITERATOR.equals(description)
                    && (targetObject instanceof Iterable || targetClass.isArray())) {
                return new JavetProxySymbolIterableConverter<>(v8Runtime, targetObject).getV8ValueFunction();
            }
        }
        if (!genericGetters.isEmpty()) {
            try {
                Object propertyObject = v8Runtime.toObject(property);
                if (propertyObject != null && !(propertyObject instanceof V8Value)) {
                    for (Method method : genericGetters) {
                        Class<?>[] parameterTypes = method.getParameterTypes();
                        Class<?> parameterType = parameterTypes[0];
                        Object key = null;
                        if (V8Value.class.isAssignableFrom(parameterType)
                                && parameterType.isAssignableFrom(property.getClass())) {
                            key = property;
                        }
                        if (key == null && parameterType.isAssignableFrom(propertyObject.getClass())) {
                            key = propertyObject;
                        }
                        if (key == null && parameterType.isPrimitive()) {
                            key = JavetTypeUtils.toExactPrimitive(parameterType, propertyObject);
                        }
                        if (key == null) {
                            key = JavetTypeUtils.toApproximatePrimitive(parameterType, propertyObject);
                        }
                        if (key != null) {
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
                isFound = ((Map<?, ?>) targetObject).containsKey(v8Runtime.toObject(property));
            } else if (isTargetTypeSet) {
                isFound = ((Set<?>) targetObject).contains(v8Runtime.toObject(property));
            } else if (property instanceof V8ValueString) {
                String indexString = ((V8ValueString) property).toPrimitive();
                if (JavetStringUtils.isDigital(indexString)) {
                    final int index = Integer.parseInt(indexString);
                    if (index >= 0) {
                        if (targetClass.isArray()) {
                            isFound = index < Array.getLength(targetObject);
                        } else if (List.class.isAssignableFrom(targetClass)) {
                            isFound = index < ((List<?>) targetObject).size();
                        }
                    }
                }
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
                        Object key = null;
                        if (V8Value.class.isAssignableFrom(parameterType)
                                && parameterType.isAssignableFrom(property.getClass())) {
                            key = property;
                        }
                        if (key == null && parameterType.isAssignableFrom(propertyObject.getClass())) {
                            key = propertyObject;
                        }
                        if (key == null && parameterType.isPrimitive()) {
                            key = JavetTypeUtils.toExactPrimitive(parameterType, propertyObject);
                        }
                        if (key == null) {
                            key = JavetTypeUtils.toApproximatePrimitive(parameterType, propertyObject);
                        }
                        if (key != null) {
                            Object callee = Modifier.isStatic(method.getModifiers()) ? null : targetObject;
                            try {
                                isFound = method.invoke(callee, key) != null;
                                if (isFound) {
                                    break;
                                }
                            } catch (Throwable ignored) {
                            }
                        }
                    }
                }
            } catch (JavetException e) {
                throw e;
            } catch (Throwable ignored) {
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
            Class<?> objectClass = (Class<?>) targetObject;
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
    protected void initializeFieldsAndMethods(Class<?> currentClass, boolean staticMode) {
        V8ConversionMode conversionMode = targetClass.isAnnotationPresent(V8Convert.class)
                ? targetClass.getAnnotation(V8Convert.class).mode()
                : V8ConversionMode.Transparent;
        if (!classMode) {
            if (isTargetTypeMap) {
                uniqueKeySet.addAll(((Map<String, ?>) targetObject).keySet());
            } else if (isTargetTypeSet) {
                uniqueKeySet.addAll((Set<String>) targetObject);
            }
        }
        do {
            if (classMode) {
                for (Constructor<?> constructor : currentClass.getConstructors()) {
                    if (isAllowed(conversionMode, constructor)) {
                        constructors.add(constructor);
                    }
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
                if (!isAllowed(conversionMode, field)) {
                    continue;
                }
                String fieldName = field.getName();
                if (field.isAnnotationPresent(V8Property.class)) {
                    String aliasFieldName = field.getAnnotation(V8Property.class).name();
                    if (aliasFieldName.length() > 0) {
                        fieldName = aliasFieldName;
                    }
                }
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
                if (!isAllowed(conversionMode, method)) {
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
        } while (currentClass != null);
    }

    /**
     * Is allowed.
     *
     * @param conversionMode   the conversion mode
     * @param accessibleObject the accessible object
     * @return true : allowed, false : disallowed
     */
    protected boolean isAllowed(V8ConversionMode conversionMode, AccessibleObject accessibleObject) {
        switch (conversionMode) {
            case AllowOnly:
                return accessibleObject.isAnnotationPresent(V8Allow.class);
            case BlockOnly:
                return !accessibleObject.isAnnotationPresent(V8Block.class);
            default:
                return true;
        }
    }

    /**
     * Is class mode.
     *
     * @return true : class mode, false : not class mode
     * @since 0.9.9
     */
    public boolean isClassMode() {
        return classMode;
    }

    /**
     * Is generic getter.
     *
     * @param method the method
     * @return true : yes, false : no
     * @since 0.9.6
     */
    protected boolean isGenericGetter(Method method) {
        if (method.isAnnotationPresent(V8Getter.class)) {
            return true;
        }
        String methodName = method.getName();
        for (String prefix : GETTER_PREFIX_ARRAY) {
            if (methodName.equals(prefix) && method.getParameterCount() == 1 && !method.isVarArgs()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Is generic setter.
     *
     * @param method the method
     * @return true : yes, false : no
     * @since 0.9.6
     */
    protected boolean isGenericSetter(Method method) {
        if (method.isAnnotationPresent(V8Setter.class)) {
            return true;
        }
        String methodName = method.getName();
        for (String prefix : SETTER_PREFIX_ARRAY) {
            if (methodName.equals(prefix) && method.getParameterCount() == 2 && !method.isVarArgs()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Is V8 binding enabled.
     *
     * @param methodName the method name
     * @return the boolean
     */
    @V8BindingEnabler
    public boolean isV8BindingEnabled(String methodName) {
        if (JavetUniversalProxyHandler.METHOD_NAME_CONSTRUCT.equals(methodName)) {
            return hasConstructors();
        }
        return true;
    }

    @V8Function
    @Override
    public V8Value ownKeys(V8Value target) throws JavetException {
        if (!classMode) {
            Object[] keys = null;
            if (isTargetTypeMap) {
                keys = ((Map<?, ?>) targetObject).keySet().toArray();
            } else if (isTargetTypeSet) {
                keys = ((Set<?>) targetObject).toArray();
            } else if (targetClass.isArray() || Collection.class.isAssignableFrom(targetClass)) {
                final int length = targetClass.isArray()
                        ? Array.getLength(targetObject)
                        : ((List<?>) targetObject).size();
                keys = new Object[length];
                for (int i = 0; i < length; ++i) {
                    keys[i] = i;
                }
            }
            if (keys != null && keys.length > 0) {
                try (V8Scope v8Scope = v8Runtime.getV8Scope()) {
                    V8ValueArray v8ValueArray = v8Scope.createV8ValueArray();
                    for (Object key : keys) {
                        if (key instanceof String) {
                            v8ValueArray.push(v8Runtime.createV8ValueString((String) key));
                        } else if (key instanceof V8ValueString || key instanceof V8ValueSymbol) {
                            v8ValueArray.push(key);
                        } else if (key != null) {
                            v8ValueArray.push(v8Runtime.createV8ValueString(key.toString()));
                        }
                    }
                    v8Scope.setEscapable();
                    return v8ValueArray;
                }
            }
        }
        return v8Runtime.toV8Value(uniqueKeySet.toArray());
    }

    @V8Function
    @Override
    public V8ValueBoolean set(V8Value target, V8Value propertyKey, V8Value propertyValue, V8Value receiver) throws JavetException {
        boolean isSet = false;
        if (propertyKey instanceof V8ValueString) {
            String indexString = ((V8ValueString) propertyKey).toPrimitive();
            if (JavetStringUtils.isDigital(indexString)) {
                final int index = Integer.parseInt(indexString);
                if (index >= 0) {
                    if (targetClass.isArray()) {
                        if (index < Array.getLength(targetObject)) {
                            Array.set(targetObject, index, v8Runtime.toObject(propertyValue));
                            isSet = true;
                        }
                    } else if (List.class.isAssignableFrom(targetClass)) {
                        List<?> list = (List<?>) targetObject;
                        if (index < list.size()) {
                            list.set(index, v8Runtime.toObject(propertyValue));
                            isSet = true;
                        }
                    }
                }
            }
        }
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
            Object keyObject, valueObject;
            try {
                keyObject = v8Runtime.toObject(propertyKey);
                valueObject = v8Runtime.toObject(propertyValue);
            } catch (JavetException e) {
                throw e;
            } catch (Throwable t) {
                throw new JavetException(JavetError.CallbackUnknownFailure,
                        SimpleMap.of(JavetError.PARAMETER_MESSAGE, t.getMessage()), t);
            }
            if (!genericSetters.isEmpty() && keyObject != null && !(keyObject instanceof V8Value)) {
                try {
                    for (Method method : genericSetters) {
                        Class<?>[] parameterTypes = method.getParameterTypes();
                        Class<?> keyParameterType = parameterTypes[0];
                        Class<?> valueParameterType = parameterTypes[1];
                        Object key = null;
                        Object value = null;
                        if (V8Value.class.isAssignableFrom(keyParameterType)
                                && keyParameterType.isAssignableFrom(propertyKey.getClass())) {
                            key = propertyKey;
                        }
                        if (V8Value.class.isAssignableFrom(valueParameterType)
                                && valueParameterType.isAssignableFrom(propertyValue.getClass())) {
                            value = propertyValue;
                        }
                        if (key == null && keyParameterType.isAssignableFrom(keyObject.getClass())) {
                            key = keyObject;
                        }
                        if (value == null && valueParameterType.isAssignableFrom(valueObject.getClass())) {
                            value = valueObject;
                        }
                        if (key == null && keyParameterType.isPrimitive()) {
                            key = JavetTypeUtils.toExactPrimitive(keyParameterType, keyObject);
                        }
                        if (value == null && valueParameterType.isPrimitive()) {
                            value = JavetTypeUtils.toExactPrimitive(valueParameterType, valueObject);
                        }
                        if (key == null) {
                            key = JavetTypeUtils.toApproximatePrimitive(keyParameterType, keyObject);
                        }
                        if (value == null) {
                            value = JavetTypeUtils.toApproximatePrimitive(valueParameterType, valueObject);
                        }
                        if (key != null) {
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
            if (!isSet && (keyObject instanceof String)) {
                String propertyName = (String) keyObject;
                List<Method> methods = settersMap.get(propertyName);
                if (methods != null) {
                    JavetUniversalInterceptor javetUniversalInterceptor =
                            new JavetUniversalInterceptor(v8Runtime, targetObject, propertyName, methods);
                    javetUniversalInterceptor.invoke((V8ValueObject) target, propertyValue);
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
        private final String jsMethodName;
        private final List<Method> methods;
        private final Object targetObject;
        private final V8Runtime v8Runtime;

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
                V8Runtime v8Runtime,
                Object targetObject,
                String jsMethodName,
                List<Method> methods) {
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
                        getClass().getMethod(METHOD_NAME_INVOKE, V8ValueObject.class, V8Value[].class),
                        true);
            } catch (NoSuchMethodException ignored) {
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
         * @param thisObject this object
         * @param v8Values   the V8 values
         * @return the object
         * @throws JavetException the javet exception
         * @since 0.9.6
         */
        public Object invoke(V8ValueObject thisObject, V8Value... v8Values) throws JavetException {
            try {
                return execute(v8Runtime, targetObject, thisObject, methods, V8ValueUtils.convertToVirtualObjects(v8Values));
            } catch (JavetException e) {
                throw e;
            } catch (InvocationTargetException e) {
                throw new JavetException(JavetError.CallbackMethodFailure,
                        SimpleMap.of(
                                JavetError.PARAMETER_METHOD_NAME, jsMethodName,
                                JavetError.PARAMETER_MESSAGE, e.getTargetException().getMessage()),
                        e.getTargetException());
            } catch (Throwable t) {
                throw new JavetException(JavetError.CallbackMethodFailure,
                        SimpleMap.of(
                                JavetError.PARAMETER_METHOD_NAME, jsMethodName,
                                JavetError.PARAMETER_MESSAGE, t.getMessage()),
                        t);
            }
        }
    }

    /**
     * The type Scored executable.
     *
     * @param <E> the type parameter
     * @since 0.9.6
     */
    public static class ScoredExecutable<E extends AccessibleObject> {
        private final E executable;
        private final Object targetObject;
        private final V8ValueObject thisObject;
        private JavetVirtualObject[] javetVirtualObjects;
        private double score;

        /**
         * Instantiates a new Scored executable.
         *
         * @param v8Runtime           the V8 runtime
         * @param targetObject        the target object
         * @param thisObject          this object
         * @param executable          the executable
         * @param javetVirtualObjects the javet virtual objects
         * @since 0.9.10
         */
        public ScoredExecutable(
                V8Runtime v8Runtime,
                Object targetObject,
                V8ValueObject thisObject,
                E executable,
                JavetVirtualObject[] javetVirtualObjects) {
            this.executable = executable;
            this.javetVirtualObjects = javetVirtualObjects;
            this.score = 0;
            this.targetObject = targetObject;
            this.thisObject = thisObject;
        }

        /**
         * Calculate score double.
         *
         * @throws JavetException the javet exception
         * @since 0.9.10
         */
        public void calculateScore() throws JavetException {
            final boolean isConstructor = executable instanceof Constructor;
            // Max score is 1. Min score is 0.
            Class<?>[] parameterTypes = isConstructor ?
                    ((Constructor<?>) executable).getParameterTypes() : ((Method) executable).getParameterTypes();
            boolean isExecutableVarArgs = isConstructor ?
                    ((Constructor<?>) executable).isVarArgs() : ((Method) executable).isVarArgs();
            boolean thisObjectRequired = false;
            if (!isConstructor) {
                Method method = (Method) executable;
                if (method.isAnnotationPresent(V8Function.class)) {
                    V8Function v8Function = method.getAnnotation(V8Function.class);
                    thisObjectRequired = v8Function.thisObjectRequired();
                }
            }
            if (thisObjectRequired) {
                JavetVirtualObject[] javetVirtualObjectsWithThis = new JavetVirtualObject[javetVirtualObjects.length + 1];
                javetVirtualObjectsWithThis[0] = new JavetVirtualObject(thisObject);
                System.arraycopy(javetVirtualObjects, 0, javetVirtualObjectsWithThis, 1, javetVirtualObjects.length);
                javetVirtualObjects = javetVirtualObjectsWithThis;
            }
            final int parameterCount = parameterTypes.length;
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
                        final V8Value v8Value = javetVirtualObjects[i].getV8Value();
                        if (v8Value != null) {
                            if (V8_VALUE_CLASS.isAssignableFrom(parameterType)
                                    && parameterType.isAssignableFrom(v8Value.getClass())) {
                                totalScore += 1;
                                continue;
                            } else if (parameterType.isInterface()) {
                                if (V8_VALUE_FUNCTION_CLASS.isAssignableFrom(v8Value.getClass())) {
                                    totalScore += 0.95;
                                    continue;
                                } else if (!V8_VALUE_PROXY_CLASS.isAssignableFrom(v8Value.getClass())
                                        && V8_VALUE_OBJECT_CLASS.isAssignableFrom(v8Value.getClass())) {
                                    totalScore += 0.85;
                                    continue;
                                }
                            }
                        }
                        final Object object = javetVirtualObjects[i].getObject();
                        if (object == null) {
                            if (parameterType.isPrimitive()) {
                                totalScore = 0;
                                break;
                            }
                            totalScore += 0.9;
                        } else if (parameterType.isAssignableFrom(object.getClass())) {
                            totalScore += 0.9;
                        } else if (parameterType.isPrimitive()
                                && JavetTypeUtils.toExactPrimitive(parameterType, object) != null) {
                            totalScore += 0.8;
                        } else if (JavetTypeUtils.toApproximatePrimitive(parameterType, object) != null) {
                            totalScore += 0.7;
                        } else {
                            totalScore = 0;
                            break;
                        }
                    }
                    if ((fixedParameterCount == 0 || (fixedParameterCount > 0 && totalScore > 0)) && isVarArgs) {
                        Class<?> componentType = parameterTypes[fixedParameterCount].getComponentType();
                        for (int i = fixedParameterCount; i < length; ++i) {
                            final V8Value v8Value = javetVirtualObjects[i].getV8Value();
                            if (v8Value != null) {
                                if (V8_VALUE_CLASS.isAssignableFrom(componentType)
                                        && componentType.isAssignableFrom(v8Value.getClass())) {
                                    totalScore += 0.95;
                                    continue;
                                } else if (componentType.isInterface()) {
                                    if (V8_VALUE_FUNCTION_CLASS.isAssignableFrom(v8Value.getClass())) {
                                        totalScore += 0.95;
                                        continue;
                                    } else if (!V8_VALUE_PROXY_CLASS.isAssignableFrom(v8Value.getClass())
                                            && V8_VALUE_OBJECT_CLASS.isAssignableFrom(v8Value.getClass())) {
                                        totalScore += 0.85;
                                        continue;
                                    }
                                }
                            }
                            final Object object = javetVirtualObjects[i].getObject();
                            if (object == null) {
                                if (componentType.isPrimitive()) {
                                    totalScore = 0;
                                    break;
                                } else {
                                    totalScore += 0.85;
                                }
                            } else if (componentType.isAssignableFrom(object.getClass())) {
                                totalScore += 0.85;
                            } else if (componentType.isPrimitive()
                                    && JavetTypeUtils.toExactPrimitive(componentType, object) != null) {
                                totalScore += 0.75;
                            } else if (JavetTypeUtils.toApproximatePrimitive(componentType, object) != null) {
                                totalScore += 0.65;
                            } else {
                                totalScore = 0;
                                break;
                            }
                        }
                    }
                    if (totalScore > 0) {
                        score = totalScore / length;
                        if (isConstructor) {
                            if (targetObject != null &&
                                    ((Constructor<?>) executable).getDeclaringClass() != targetObject.getClass()) {
                                score *= 0.9;
                            }
                        } else {
                            if (targetObject != null &&
                                    ((Method) executable).getDeclaringClass() != targetObject.getClass()) {
                                score *= 0.9;
                            }
                        }
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
            Object callee = Modifier.isStatic(((Member) executable).getModifiers()) ? null : targetObject;
            Class<?>[] parameterTypes = executable instanceof Constructor ?
                    ((Constructor<?>) executable).getParameterTypes() : ((Method) executable).getParameterTypes();
            final int parameterCount = parameterTypes.length;
            boolean isExecutableVarArgs = executable instanceof Constructor ?
                    ((Constructor<?>) executable).isVarArgs() : ((Method) executable).isVarArgs();
            if (length == 0) {
                if (isExecutableVarArgs) {
                    Class<?> componentType = parameterTypes[parameterCount - 1].getComponentType();
                    Object varObject = Array.newInstance(componentType, 0);
                    if (executable instanceof Constructor) {
                        return ((Constructor<?>) executable).newInstance(varObject);
                    } else {
                        return ((Method) executable).invoke(callee, varObject);
                    }
                } else {
                    if (executable instanceof Constructor) {
                        return ((Constructor<?>) executable).newInstance();
                    } else {
                        return ((Method) executable).invoke(callee);
                    }
                }
            } else {
                List<Object> parameters = new ArrayList<>();
                final int fixedParameterCount = isExecutableVarArgs ? parameterCount - 1 : parameterCount;
                for (int i = 0; i < fixedParameterCount; i++) {
                    Class<?> parameterType = parameterTypes[i];
                    final V8Value v8Value = javetVirtualObjects[i].getV8Value();
                    final Object object = javetVirtualObjects[i].getObject();
                    Object parameter = object;
                    boolean conversionRequired = true;
                    if (v8Value != null) {
                        if (V8_VALUE_CLASS.isAssignableFrom(parameterType)
                                && parameterType.isAssignableFrom(v8Value.getClass())) {
                            parameter = v8Value;
                            conversionRequired = false;
                        } else if (parameterType.isInterface()) {
                            if (V8_VALUE_FUNCTION_CLASS.isAssignableFrom(v8Value.getClass())) {
                                DynamicProxyV8ValueFunctionInvocationHandler invocationHandler =
                                        new DynamicProxyV8ValueFunctionInvocationHandler(v8Value.toClone());
                                parameter = Proxy.newProxyInstance(
                                        getClass().getClassLoader(),
                                        new Class[]{parameterType, AutoCloseable.class},
                                        invocationHandler);
                                conversionRequired = false;
                            } else if (!V8_VALUE_PROXY_CLASS.isAssignableFrom(v8Value.getClass())
                                    && V8_VALUE_OBJECT_CLASS.isAssignableFrom(v8Value.getClass())) {
                                DynamicProxyV8ValueObjectInvocationHandler invocationHandler =
                                        new DynamicProxyV8ValueObjectInvocationHandler(v8Value.toClone());
                                parameter = Proxy.newProxyInstance(
                                        getClass().getClassLoader(),
                                        new Class[]{parameterType, AutoCloseable.class},
                                        invocationHandler);
                                conversionRequired = false;
                            }
                        }
                    }
                    if (conversionRequired && object != null && !parameterType.isAssignableFrom(object.getClass())) {
                        boolean primitiveFound = false;
                        if (parameterType.isPrimitive()) {
                            Object primitiveObject = JavetTypeUtils.toExactPrimitive(parameterType, object);
                            if (primitiveObject != null) {
                                parameter = primitiveObject;
                                primitiveFound = true;
                            }
                        }
                        if (!primitiveFound) {
                            Object approximatePrimitiveValue = JavetTypeUtils.toApproximatePrimitive(parameterType, object);
                            if (approximatePrimitiveValue != null) {
                                parameter = approximatePrimitiveValue;
                            }
                        }
                    }
                    parameters.add(parameter);
                }
                if (isExecutableVarArgs) {
                    Class<?> componentType = parameterTypes[fixedParameterCount].getComponentType();
                    Object varObject = Array.newInstance(componentType, length - fixedParameterCount);
                    for (int i = fixedParameterCount; i < length; ++i) {
                        final V8Value v8Value = javetVirtualObjects[i].getV8Value();
                        final Object object = javetVirtualObjects[i].getObject();
                        Object parameter = object;
                        boolean conversionRequired = true;
                        if (v8Value != null) {
                            if (V8_VALUE_CLASS.isAssignableFrom(componentType)
                                    && componentType.isAssignableFrom(v8Value.getClass())) {
                                parameter = v8Value;
                                conversionRequired = false;
                            } else if (componentType.isInterface()) {
                                if (V8_VALUE_FUNCTION_CLASS.isAssignableFrom(v8Value.getClass())) {
                                    DynamicProxyV8ValueFunctionInvocationHandler invocationHandler =
                                            new DynamicProxyV8ValueFunctionInvocationHandler(v8Value.toClone());
                                    parameter = Proxy.newProxyInstance(
                                            getClass().getClassLoader(),
                                            new Class[]{componentType, AutoCloseable.class},
                                            invocationHandler);
                                    conversionRequired = false;
                                } else if (!V8_VALUE_PROXY_CLASS.isAssignableFrom(v8Value.getClass())
                                        && V8_VALUE_OBJECT_CLASS.isAssignableFrom(v8Value.getClass())) {
                                    DynamicProxyV8ValueObjectInvocationHandler invocationHandler =
                                            new DynamicProxyV8ValueObjectInvocationHandler(v8Value.toClone());
                                    parameter = Proxy.newProxyInstance(
                                            getClass().getClassLoader(),
                                            new Class[]{componentType, AutoCloseable.class},
                                            invocationHandler);
                                    conversionRequired = false;
                                }
                            }
                        }
                        if (conversionRequired && object != null && !componentType.isAssignableFrom(object.getClass())) {
                            boolean primitiveFound = false;
                            if (componentType.isPrimitive()) {
                                Object primitiveObject = JavetTypeUtils.toExactPrimitive(componentType, object);
                                if (primitiveObject != null) {
                                    parameter = primitiveObject;
                                    primitiveFound = true;
                                }
                            }
                            if (!primitiveFound) {
                                Object approximatePrimitiveValue = JavetTypeUtils.toApproximatePrimitive(componentType, object);
                                if (approximatePrimitiveValue != null) {
                                    parameter = approximatePrimitiveValue;
                                }
                            }
                        }
                        Array.set(varObject, i - fixedParameterCount, parameter);
                    }
                    parameters.add(varObject);
                }
                if (executable instanceof Constructor) {
                    return ((Constructor<?>) executable).newInstance(parameters.toArray());
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
