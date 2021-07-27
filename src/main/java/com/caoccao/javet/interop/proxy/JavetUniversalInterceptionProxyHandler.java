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

import com.caoccao.javet.annotations.V8Function;
import com.caoccao.javet.annotations.V8RuntimeSetter;
import com.caoccao.javet.exceptions.JavetError;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interfaces.IJavetLogger;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.callback.JavetCallbackContext;
import com.caoccao.javet.utils.JavetDefaultLogger;
import com.caoccao.javet.utils.JavetPrimitiveUtils;
import com.caoccao.javet.utils.SimpleMap;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.primitive.V8ValueBoolean;
import com.caoccao.javet.values.primitive.V8ValueString;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

@SuppressWarnings("unchecked")
public class JavetUniversalInterceptionProxyHandler<T> implements IJavetProxyHandler {
    protected static final String[] GETTER_PREFIX_ARRAY = new String[]{"get", "is"};
    protected static final String[] SETTER_PREFIX_ARRAY = new String[]{"set", "put"};
    protected List<Method> genericGetters;
    protected List<Method> genericSetters;
    protected Map<String, List<Method>> gettersMap;
    protected IJavetLogger logger;
    protected Map<String, List<Method>> methodsMap;
    protected Map<String, List<Method>> settersMap;
    protected Class<T> targetClass;
    protected T targetObject;
    protected V8Runtime v8Runtime;

    public JavetUniversalInterceptionProxyHandler(T targetObject) {
        this.targetObject = Objects.requireNonNull(targetObject);
        genericGetters = new ArrayList<>();
        genericSetters = new ArrayList<>();
        methodsMap = new HashMap<>();
        gettersMap = new HashMap<>();
        settersMap = new HashMap<>();
        targetClass = (Class<T>) targetObject.getClass();
        logger = new JavetDefaultLogger(targetClass.getName());
        v8Runtime = null;
        initializeMethods(targetClass);
    }

    protected void addMethod(Method method, int startIndex, Map<String, List<Method>> map) {
        String methodName = method.getName();
        final int length = methodName.length();
        for (int i = startIndex; i < length; i++) {
            char c = methodName.charAt(i);
            String aliasMethodName;
            if (i == 0) {
                aliasMethodName = methodName;
            } else if ((Character.isAlphabetic(c) && Character.isUpperCase(c)) || startIndex == i) {
                aliasMethodName = methodName.substring(startIndex, i + 1).toLowerCase(Locale.ROOT)
                        + methodName.substring(i + 1);
            } else {
                return;
            }
            map.computeIfAbsent(aliasMethodName, key -> new ArrayList<>()).add(method);
        }
    }

    @V8Function
    @Override
    public V8Value get(V8Value target, V8Value property, V8Value receiver) throws JavetException {
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
                        new JavetUniversalInterceptor(targetObject, propertyName, methods);
                return v8Runtime.toV8Value(javetUniversalInterceptor.invoke());
            }
            methods = methodsMap.get(propertyName);
            if (methods != null) {
                JavetUniversalInterceptor javetUniversalInterceptor =
                        new JavetUniversalInterceptor(targetObject, propertyName, methods);
                return v8Runtime.createV8ValueFunction(javetUniversalInterceptor.getCallbackContext());
            }
        }
        return v8Runtime.createV8ValueUndefined();
    }

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

    public IJavetLogger getLogger() {
        return logger;
    }

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

    public T getTargetObject() {
        return targetObject;
    }

    public V8Runtime getV8Runtime() {
        return v8Runtime;
    }

    @V8Function
    @Override
    public V8ValueBoolean has(V8Value target, V8Value property) throws JavetException {
        boolean isFound = false;
        if (property instanceof V8ValueString) {
            String propertyName = ((V8ValueString) property).toPrimitive();
            isFound = methodsMap.containsKey(propertyName)
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

    protected void initializeMethods(Class targetClass) {
        for (Method method : targetClass.getMethods()) {
            if (isGenericGetter(method)) {
                genericGetters.add(method);
                continue;
            }
            if (isGenericSetter(method)) {
                genericSetters.add(method);
                continue;
            }
            final int getterPrefixLength = getGetterPrefixLength(method);
            if (getterPrefixLength > 0) {
                addMethod(method, getterPrefixLength, gettersMap);
                continue;
            }
            final int setterPrefixLength = getSetterPrefixLength(method);
            if (setterPrefixLength > 0) {
                addMethod(method, setterPrefixLength, settersMap);
                continue;
            }
            addMethod(method, 0, methodsMap);
        }
        Class superclass = targetClass.getSuperclass();
        if (superclass != Object.class) {
            initializeMethods(superclass);
        }
    }

    protected boolean isGenericGetter(Method method) {
        String methodName = method.getName();
        for (String prefix : GETTER_PREFIX_ARRAY) {
            if (methodName.equals(prefix) && method.getParameterCount() == 1 && !method.isVarArgs()) {
                return true;
            }
        }
        return false;
    }

    protected boolean isGenericSetter(Method method) {
        String methodName = method.getName();
        for (String prefix : SETTER_PREFIX_ARRAY) {
            if (methodName.equals(prefix) && method.getParameterCount() == 2 && !method.isVarArgs()) {
                return true;
            }
        }
        return false;
    }

    @V8Function
    @Override
    public V8ValueBoolean set(V8Value target, V8Value propertyKey, V8Value propertyValue, V8Value receiver) throws JavetException {
        boolean isSet = false;
        if (!genericSetters.isEmpty() || !settersMap.isEmpty()) {
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
                            new JavetUniversalInterceptor(targetObject, propertyName, methods);
                    javetUniversalInterceptor.invoke(valueObject);
                    isSet = true;
                }
            }
        }
        return v8Runtime.createV8ValueBoolean(isSet);
    }

    public void setLogger(IJavetLogger logger) {
        this.logger = logger;
    }

    @V8RuntimeSetter
    public void setV8Runtime(V8Runtime v8Runtime) {
        this.v8Runtime = v8Runtime;
    }

    public static class JavetUniversalInterceptor {
        private static final String METHOD_NAME_INVOKE = "invoke";
        private String jsMethodName;
        private List<Method> methods;
        private Object targetObject;

        public JavetUniversalInterceptor(Object targetObject, String jsMethodName, List<Method> methods) {
            this.jsMethodName = jsMethodName;
            this.methods = methods;
            this.targetObject = targetObject;
        }


        public JavetCallbackContext getCallbackContext() {
            try {
                return new JavetCallbackContext(
                        this,
                        getClass().getMethod(METHOD_NAME_INVOKE, Object[].class));
            } catch (NoSuchMethodException e) {
            }
            return null;
        }

        public String getJSMethodName() {
            return jsMethodName;
        }

        public List<Method> getMethods() {
            return methods;
        }

        protected double getScore(Method method, Object... objects) {
            // Max score is 1. Min score is 0.
            final int parameterCount = method.getParameterCount();
            Class<?>[] parameterTypes = method.getParameterTypes();
            boolean isMethodVarArgs = method.isVarArgs();
            double score = 0;
            final int length = objects.length;
            if (length == 0) {
                if (isMethodVarArgs) {
                    if (parameterCount == 1) {
                        score = 0.99;
                    }
                } else {
                    if (parameterCount == 0) {
                        score = 1;
                    }
                }
            } else {
                boolean isVarArgs = isMethodVarArgs && length >= parameterCount - 1;
                boolean isFixedArgs = !isMethodVarArgs && length == parameterCount;
                if (isVarArgs || isFixedArgs) {
                    double totalScore = 0;
                    final int fixedParameterCount = isMethodVarArgs ? parameterCount - 1 : parameterCount;
                    for (int i = 0; i < fixedParameterCount; i++) {
                        Class<?> parameterType = parameterTypes[i];
                        Object object = objects[i];
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
                    if ((fixedParameterCount == 0 || (fixedParameterCount > 0 && totalScore > 0))
                            && isMethodVarArgs && length >= parameterCount) {
                        Class<?> componentType = parameterTypes[fixedParameterCount].getComponentType();
                        for (int i = fixedParameterCount; i < length; ++i) {
                            Object object = objects[i];
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
            return score;
        }

        public Object getTargetObject() {
            return targetObject;
        }

        public Object invoke(Object... objects) throws JavetException {
            final int length = objects.length;
            Object[] convertedObjects = new Object[length];
            for (int i = 0; i < length; i++) {
                Object object = objects[i];
                if (object instanceof V8Value) {
                    V8Value v8Value = (V8Value) object;
                    convertedObjects[i] = v8Value.getV8Runtime().toObject(v8Value);
                } else {
                    convertedObjects[i] = objects[i];
                }
            }
            objects = convertedObjects;
            List<ScoredMethod> scoredMethods = new ArrayList<>();
            for (Method method : methods) {
                double score = getScore(method, objects);
                if (score > 0) {
                    scoredMethods.add(new ScoredMethod(score, method));
                }
            }
            if (!scoredMethods.isEmpty()) {
                scoredMethods.sort((o1, o2) -> Double.compare(o2.getScore(), o1.getScore()));
                Throwable lastException = null;
                for (ScoredMethod scoredMethod : scoredMethods) {
                    Method method = scoredMethod.getMethod();
                    Object callee = Modifier.isStatic(method.getModifiers()) ? null : targetObject;
                    final int parameterCount = method.getParameterCount();
                    Class<?>[] parameterTypes = method.getParameterTypes();
                    boolean isMethodVarArgs = method.isVarArgs();
                    try {
                        if (length == 0) {
                            if (isMethodVarArgs) {
                                Class<?> componentType = parameterTypes[parameterCount - 1];
                                Object varObject = Array.newInstance(componentType, 0);
                                return method.invoke(callee, varObject);
                            } else {
                                return method.invoke(callee);
                            }
                        } else {
                            List<Object> parameters = new ArrayList<>();
                            if (isMethodVarArgs) {
                                for (int i = 0; i < parameterCount; i++) {
                                    Class<?> parameterType = parameterTypes[i];
                                    if (parameterType.isArray() && i == parameterCount - 1) {
                                        Class<?> componentType = parameterType.getComponentType();
                                        Object varObject = Array.newInstance(componentType, length - i);
                                        for (int j = i; j < length; ++j) {
                                            Object parameter = objects[j];
                                            if (parameter != null && !componentType.isAssignableFrom(parameter.getClass())
                                                    && componentType.isPrimitive()) {
                                                parameter = JavetPrimitiveUtils.toExactPrimitive(componentType, parameter);
                                            }
                                            Array.set(varObject, j - i, parameter);
                                        }
                                        parameters.add(varObject);
                                    } else {
                                        Object parameter = objects[i];
                                        if (parameter != null && !parameterType.isAssignableFrom(parameter.getClass())
                                                && parameterType.isPrimitive()) {
                                            parameter = JavetPrimitiveUtils.toExactPrimitive(parameterType, parameter);
                                        }
                                        parameters.add(parameter);
                                    }
                                }
                            } else {
                                for (int i = 0; i < length; i++) {
                                    Class<?> parameterType = parameterTypes[i];
                                    Object parameter = objects[i];
                                    if (parameter != null && !parameterType.isAssignableFrom(parameter.getClass())
                                            && parameterType.isPrimitive()) {
                                        parameter = JavetPrimitiveUtils.toExactPrimitive(parameterType, parameter);
                                    }
                                    parameters.add(parameter);
                                }
                            }
                            return method.invoke(callee, parameters.toArray());
                        }
                    } catch (Throwable t) {
                        lastException = t;
                    }
                }
                throw new JavetException(JavetError.CallbackMethodFailure,
                        SimpleMap.of(
                                JavetError.PARAMETER_METHOD_NAME, jsMethodName,
                                JavetError.PARAMETER_MESSAGE, lastException.getMessage()), lastException);
            }
            return null;
        }
    }

    public static class ScoredMethod {
        private Method method;
        private double score;

        public ScoredMethod(double score, Method method) {
            this.score = score;
            this.method = method;
        }

        public Method getMethod() {
            return method;
        }

        public double getScore() {
            return score;
        }
    }
}
