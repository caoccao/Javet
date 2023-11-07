/*
 * Copyright (c) 2021-2023. caoccao.com Sam Cao
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

import com.caoccao.javet.annotations.V8Function;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.utils.JavetTypeUtils;
import com.caoccao.javet.utils.JavetVirtualObject;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.reference.V8ValueObject;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The type Scored executable.
 *
 * @param <E> the type parameter
 * @since 0.9.6
 */
final class ScoredExecutable<E extends AccessibleObject> {
    private static final Class<?> V8_VALUE_CLASS = V8Value.class;
    private final E executable;
    private final IJavetReflectionObjectFactory reflectionObjectFactory;
    private final Object targetObject;
    private final V8ValueObject thisObject;
    private JavetVirtualObject[] javetVirtualObjects;
    private double score;

    /**
     * Instantiates a new Scored executable.
     *
     * @param reflectionObjectFactory the reflection object factory
     * @param targetObject            the target object
     * @param thisObject              this object
     * @param executable              the executable
     * @param javetVirtualObjects     the javet virtual objects
     * @since 0.9.10
     */
    public ScoredExecutable(
            IJavetReflectionObjectFactory reflectionObjectFactory,
            Object targetObject,
            V8ValueObject thisObject,
            E executable,
            JavetVirtualObject[] javetVirtualObjects) {
        this.executable = executable;
        this.reflectionObjectFactory = reflectionObjectFactory;
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
        Class<?>[] parameterTypes = isConstructor
                ? ((Constructor<?>) executable).getParameterTypes()
                : ((Method) executable).getParameterTypes();
        boolean isExecutableVarArgs = isConstructor
                ? ((Constructor<?>) executable).isVarArgs()
                : ((Method) executable).isVarArgs();
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
                final JavetReflectionProxyFactory reflectionProxyFactory = JavetReflectionProxyFactory.getInstance();
                for (int i = 0; i < fixedParameterCount; i++) {
                    Class<?> parameterType = parameterTypes[i];
                    final V8Value v8Value = javetVirtualObjects[i].getV8Value();
                    final Object object = javetVirtualObjects[i].getObject();
                    if (v8Value != null) {
                        if (V8_VALUE_CLASS.isAssignableFrom(parameterType)
                                && parameterType.isAssignableFrom(v8Value.getClass())) {
                            totalScore += 1;
                            continue;
                        } else if (object != null && parameterType.isAssignableFrom(object.getClass())) {
                            totalScore += 0.9;
                            continue;
                        } else if (reflectionProxyFactory.isSupportedFunction(parameterType, v8Value)) {
                            totalScore += 0.95;
                            continue;
                        } else if (reflectionProxyFactory.isSupportedObject(parameterType, v8Value)) {
                            totalScore += 0.85;
                            continue;
                        } else if (reflectionObjectFactory != null && reflectionObjectFactory.isSupported(parameterType, v8Value)) {
                            totalScore += 0.5;
                            continue;
                        }
                    }
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
                        final Object object = javetVirtualObjects[i].getObject();
                        if (v8Value != null) {
                            if (V8_VALUE_CLASS.isAssignableFrom(componentType)
                                    && componentType.isAssignableFrom(v8Value.getClass())) {
                                totalScore += 0.95;
                                continue;
                            } else if (object != null && componentType.isAssignableFrom(object.getClass())) {
                                totalScore += 0.85;
                                continue;
                            } else if (reflectionProxyFactory.isSupportedFunction(componentType, v8Value)) {
                                totalScore += 0.95;
                                continue;
                            } else if (reflectionProxyFactory.isSupportedObject(componentType, v8Value)) {
                                totalScore += 0.85;
                                continue;
                            } else if (reflectionObjectFactory != null && reflectionObjectFactory.isSupported(componentType, v8Value)) {
                                totalScore += 0.5;
                                continue;
                            }
                        }
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
            final JavetReflectionProxyFactory reflectionProxyFactory = JavetReflectionProxyFactory.getInstance();
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
                    } else if (object != null && parameterType.isAssignableFrom(object.getClass())) {
                        conversionRequired = false;
                    } else if (reflectionProxyFactory.isSupportedFunction(parameterType, v8Value)
                            || reflectionProxyFactory.isSupportedObject(parameterType, v8Value)) {
                        parameter = reflectionProxyFactory.toObject(parameterType, v8Value);
                        conversionRequired = false;
                    } else if (reflectionObjectFactory != null && reflectionObjectFactory.isSupported(parameterType, v8Value)) {
                        parameter = reflectionObjectFactory.toObject(parameterType, v8Value);
                        conversionRequired = false;
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
                        } else if (object != null && componentType.isAssignableFrom(object.getClass())) {
                            conversionRequired = false;
                        } else if (reflectionProxyFactory.isSupportedFunction(componentType, v8Value)
                                || reflectionProxyFactory.isSupportedObject(componentType, v8Value)) {
                            parameter = reflectionProxyFactory.toObject(componentType, v8Value);
                            conversionRequired = false;
                        } else if (reflectionObjectFactory != null && reflectionObjectFactory.isSupported(componentType, v8Value)) {
                            parameter = reflectionObjectFactory.toObject(componentType, v8Value);
                            conversionRequired = false;
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
