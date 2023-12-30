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

package com.caoccao.javet.interop.proxy;

import com.caoccao.javet.annotations.*;
import com.caoccao.javet.enums.V8ConversionMode;
import com.caoccao.javet.exceptions.JavetError;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.binding.ClassDescriptor;
import com.caoccao.javet.utils.*;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.primitive.V8ValueString;
import com.caoccao.javet.values.reference.V8ValueObject;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The type Base javet reflection proxy handler.
 *
 * @param <T> the type parameter
 * @param <E> the type parameter
 * @since 0.9.6
 */
public abstract class BaseJavetReflectionProxyHandler<T, E extends Exception>
        extends BaseJavetProxyHandler<T, E> {
    /**
     * The constant GETTER_PREFIX_ARRAY.
     *
     * @since 0.9.6
     */
    protected static final String[] GETTER_PREFIX_ARRAY = new String[]{"get", "is"};
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
     * The Class descriptor.
     *
     * @since 1.1.7
     */
    protected ClassDescriptor classDescriptor;
    /**
     * The Reflection object factory.
     *
     * @since 2.0.1
     */
    protected IJavetReflectionObjectFactory reflectionObjectFactory;

    /**
     * Instantiates a new Base javet reflection proxy handler.
     *
     * @param v8Runtime               the V8 runtime
     * @param reflectionObjectFactory the reflection object factory
     * @param targetObject            the target object
     * @since 0.9.6
     */
    public BaseJavetReflectionProxyHandler(
            V8Runtime v8Runtime,
            IJavetReflectionObjectFactory reflectionObjectFactory,
            T targetObject) {
        super(v8Runtime, targetObject);
        this.reflectionObjectFactory = reflectionObjectFactory;
        initialize();
    }

    /**
     * Execute.
     *
     * @param <E>                     the type parameter
     * @param reflectionObjectFactory the reflection object factory
     * @param targetObject            the target object
     * @param thisObject              this object
     * @param executables             the executables
     * @param javetVirtualObjects     the javet virtual objects
     * @return the object
     * @throws Throwable the throwable
     * @since 0.9.10
     */
    protected static <E extends AccessibleObject> Object execute(
            IJavetReflectionObjectFactory reflectionObjectFactory,
            Object targetObject,
            V8ValueObject thisObject,
            List<E> executables,
            JavetVirtualObject[] javetVirtualObjects) throws Throwable {
        List<ScoredExecutable<E>> scoredExecutables = new ArrayList<>();
        for (E executable : executables) {
            ScoredExecutable<E> scoredExecutable = new ScoredExecutable<>(
                    reflectionObjectFactory, targetObject, thisObject, executable, javetVirtualObjects);
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
                    aliasMethodName = methodName.substring(
                            startIndex, startIndex + capitalizedPrefixLength).toLowerCase(Locale.ROOT)
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

    /**
     * Gets class descriptor cache.
     *
     * @return the thread safe map
     * @since 1.1.7
     */
    public abstract ThreadSafeMap<Class<?>, ClassDescriptor> getClassDescriptorCache();

    /**
     * Gets from field.
     *
     * @param property the property
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 1.1.7
     */
    protected V8Value getFromField(V8Value property) throws JavetException {
        if (!classDescriptor.getFieldMap().isEmpty() && property instanceof V8ValueString) {
            String propertyName = ((V8ValueString) property).toPrimitive();
            Field field = classDescriptor.getFieldMap().get(propertyName);
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
        return null;
    }

    /**
     * Gets from getter.
     *
     * @param property the property
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 1.1.7
     */
    protected V8Value getFromGetter(V8Value property) throws JavetException {
        if (!classDescriptor.getGenericGetters().isEmpty()) {
            try {
                Object propertyObject = v8Runtime.toObject(property);
                if (propertyObject != null && !(propertyObject instanceof V8Value)) {
                    for (Method method : classDescriptor.getGenericGetters()) {
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
        return null;
    }

    /**
     * Gets from method.
     *
     * @param target   the target
     * @param property the property
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 1.1.7
     */
    protected V8Value getFromMethod(V8Value target, V8Value property) throws JavetException {
        if (property instanceof V8ValueString) {
            String propertyName = ((V8ValueString) property).toPrimitive();
            List<Method> methods = classDescriptor.getMethodsMap().get(propertyName);
            if (methods != null && !methods.isEmpty()) {
                JavetReflectionProxyInterceptor reflectionProxyInterceptor = new JavetReflectionProxyInterceptor(
                        reflectionObjectFactory, targetObject, propertyName, methods);
                return v8Runtime.createV8ValueFunction(reflectionProxyInterceptor.getCallbackContext());
            }
            methods = classDescriptor.getGettersMap().get(propertyName);
            if (methods != null && !methods.isEmpty()) {
                JavetReflectionProxyInterceptor reflectionProxyInterceptor = new JavetReflectionProxyInterceptor(
                        reflectionObjectFactory, targetObject, propertyName, methods);
                return v8Runtime.toV8Value(reflectionProxyInterceptor.invoke((V8ValueObject) target));
            }
            if (FUNCTION_NAME_TO_V8_VALUE.equals(propertyName)) {
                return new JavetProxySymbolToPrimitiveConverter<>(v8Runtime, targetObject).getV8ValueFunction();
            }
        }
        return null;
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

    /**
     * Has from generic.
     *
     * @param property the property
     * @return the boolean
     * @throws JavetException the javet exception
     * @since 1.1.7
     */
    protected boolean hasFromGeneric(V8Value property) throws JavetException {
        if (!classDescriptor.getGenericGetters().isEmpty()) {
            try {
                Object propertyObject = v8Runtime.toObject(property);
                if (propertyObject != null && !(propertyObject instanceof V8Value)) {
                    for (Method method : classDescriptor.getGenericGetters()) {
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
                                return method.invoke(callee, key) != null;
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
        return false;
    }

    /**
     * Has from regular.
     *
     * @param property the property
     * @return true : has, false: not has
     * @since 1.1.7
     */
    protected boolean hasFromRegular(V8Value property) {
        if (property instanceof V8ValueString) {
            String propertyName = ((V8ValueString) property).toPrimitive();
            return classDescriptor.getFieldMap().containsKey(propertyName)
                    || classDescriptor.getMethodsMap().containsKey(propertyName)
                    || classDescriptor.getGettersMap().containsKey(propertyName)
                    || classDescriptor.getSettersMap().containsKey(propertyName);
        }
        return false;
    }

    /**
     * Initialize.
     *
     * @since 1.1.7
     */
    protected abstract void initialize();

    /**
     * Initialize constructors.
     *
     * @param currentClass   the current class
     * @param conversionMode the conversion mode
     * @since 1.1.7
     */
    protected void initializeConstructors(Class<?> currentClass, V8ConversionMode conversionMode) {
        for (Constructor<?> constructor : currentClass.getConstructors()) {
            if (isAllowed(conversionMode, constructor)) {
                classDescriptor.getConstructors().add(constructor);
            }
        }
    }

    /**
     * Initialize public fields.
     *
     * @param currentClass   the current class
     * @param conversionMode the conversion mode
     * @param staticMode     the static mode
     * @since 1.1.7
     */
    protected void initializePublicFields(
            Class<?> currentClass, V8ConversionMode conversionMode, boolean staticMode) {
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
            if (classDescriptor.getFieldMap().containsKey(fieldName)) {
                continue;
            }
            JavetReflectionUtils.safeSetAccessible(field);
            classDescriptor.getFieldMap().put(fieldName, field);
            if (!classDescriptor.isTargetTypeMap() && !classDescriptor.isTargetTypeSet()) {
                classDescriptor.getUniqueKeySet().add(fieldName);
            }
        }
    }

    /**
     * Initialize public methods.
     *
     * @param currentClass   the current class
     * @param conversionMode the conversion mode
     * @param staticMode     the static mode
     * @since 1.1.7
     */
    protected void initializePublicMethods(
            Class<?> currentClass, V8ConversionMode conversionMode, boolean staticMode) {
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
                classDescriptor.getGenericGetters().add(method);
            } else if (isGenericSetter(method)) {
                classDescriptor.getGenericSetters().add(method);
            } else if (isApplyFunction(method)) {
                classDescriptor.getApplyFunctions().add(method);
            } else {
                final int getterPrefixLength = getGetterPrefixLength(method);
                if (getterPrefixLength > 0) {
                    addMethod(method, getterPrefixLength, classDescriptor.getGettersMap());
                    if (!classDescriptor.isTargetTypeMap() && !classDescriptor.isTargetTypeSet()) {
                        String aliasMethodName = method.getName().substring(getterPrefixLength);
                        Matcher matcher = PATTERN_CAPITALIZED_PREFIX.matcher(aliasMethodName);
                        if (matcher.find()) {
                            final int capitalizedPrefixLength = matcher.group().length();
                            if (capitalizedPrefixLength == 1) {
                                classDescriptor.getUniqueKeySet().add(
                                        aliasMethodName.substring(0, capitalizedPrefixLength).toLowerCase(Locale.ROOT)
                                                + aliasMethodName.substring(capitalizedPrefixLength));
                            } else {
                                classDescriptor.getUniqueKeySet().add(
                                        aliasMethodName.substring(0, capitalizedPrefixLength - 1).toLowerCase(Locale.ROOT)
                                                + aliasMethodName.substring(capitalizedPrefixLength - 1));
                            }
                        }
                    }
                } else {
                    final int setterPrefixLength = getSetterPrefixLength(method);
                    if (setterPrefixLength > 0) {
                        addMethod(method, setterPrefixLength, classDescriptor.getSettersMap());
                    }
                }
            }
            addMethod(method, 0, classDescriptor.getMethodsMap());
        }
    }

    /**
     * Is allowed.
     *
     * @param conversionMode   the conversion mode
     * @param accessibleObject the accessible object
     * @return true : allowed, false : disallowed
     * @since 1.1.7
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
     * Is apply function.
     *
     * @param method the method
     * @return true : yes, false: no
     * @since 1.1.7
     */
    protected boolean isApplyFunction(Method method) {
        return method.isAnnotationPresent(V8ProxyFunctionApply.class);
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
     * Sets to field.
     *
     * @param propertyKey   the property key
     * @param propertyValue the property value
     * @return true : set, false: not set
     * @throws JavetException the javet exception
     * @since 1.1.7
     */
    protected boolean setToField(V8Value propertyKey, V8Value propertyValue) throws JavetException {
        if (!classDescriptor.getFieldMap().isEmpty() && propertyKey instanceof V8ValueString) {
            String propertyName = ((V8ValueString) propertyKey).toPrimitive();
            Field field = classDescriptor.getFieldMap().get(propertyName);
            if (field != null) {
                final int fieldModifiers = field.getModifiers();
                if (!Modifier.isFinal(fieldModifiers)) {
                    try {
                        Object callee = Modifier.isStatic(fieldModifiers) ? null : targetObject;
                        field.set(callee, v8Runtime.toObject(propertyValue));
                        return true;
                    } catch (JavetException e) {
                        throw e;
                    } catch (Throwable t) {
                        throw new JavetException(JavetError.CallbackUnknownFailure,
                                SimpleMap.of(JavetError.PARAMETER_MESSAGE, t.getMessage()), t);
                    }
                }
            }
        }
        return false;
    }

    /**
     * Sets to setter.
     *
     * @param target        the target
     * @param propertyKey   the property key
     * @param propertyValue the property value
     * @return true : set, false: not set
     * @throws JavetException the javet exception
     * @since 1.1.7
     */
    protected boolean setToSetter(V8Value target, V8Value propertyKey, V8Value propertyValue) throws JavetException {
        if (!classDescriptor.getGenericSetters().isEmpty() || !classDescriptor.getSettersMap().isEmpty()) {
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
            if (!classDescriptor.getGenericSetters().isEmpty() && keyObject != null && !(keyObject instanceof V8Value)) {
                try {
                    for (Method method : classDescriptor.getGenericSetters()) {
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
                            return true;
                        }
                    }
                } catch (Throwable t) {
                    throw new JavetException(JavetError.CallbackUnknownFailure,
                            SimpleMap.of(JavetError.PARAMETER_MESSAGE, t.getMessage()), t);
                }
            }
            if (keyObject instanceof String) {
                String propertyName = (String) keyObject;
                List<Method> methods = classDescriptor.getSettersMap().get(propertyName);
                if (methods != null) {
                    JavetReflectionProxyInterceptor reflectionProxyInterceptor = new JavetReflectionProxyInterceptor(
                            reflectionObjectFactory, targetObject, propertyName, methods);
                    reflectionProxyInterceptor.invoke((V8ValueObject) target, propertyValue);
                    return true;
                }
            }
        }
        return false;
    }
}
