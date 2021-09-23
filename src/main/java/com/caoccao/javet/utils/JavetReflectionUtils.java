/*
 *    Copyright 2021. caoccao.com Sam Cao
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

package com.caoccao.javet.utils;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The type Javet reflection utils.
 *
 * @since 0.9.7
 */
public final class JavetReflectionUtils {

    private static final String METHOD_NAME_WRITE_REPLACE = "writeReplace";

    private JavetReflectionUtils() {
    }

    /**
     * Gets method name from lambda.
     *
     * @param lambda the lambda
     * @return the method name from lambda
     * @since 0.9.13
     */
    public static String getMethodNameFromLambda(Serializable lambda) {
        Objects.requireNonNull(lambda);
        try {
            Method method = lambda.getClass().getDeclaredMethod(METHOD_NAME_WRITE_REPLACE);
            method.setAccessible(true);
            SerializedLambda serializedLambda = (SerializedLambda) method.invoke(lambda);
            return serializedLambda.getImplMethodName();
        } catch (Throwable t) {
        }
        return null;
    }

    /**
     * Gets method name set from lambdas.
     *
     * @param lambdas the lambdas
     * @return the method name set
     */
    public static Set<String> getMethodNameSetFromLambdas(Serializable... lambdas) {
        return Stream.of(Objects.requireNonNull(lambdas))
                .filter(Objects::nonNull)
                .map(JavetReflectionUtils::getMethodNameFromLambda)
                .collect(Collectors.toSet());
    }

    /**
     * Safe set accessible.
     *
     * @param accessibleObject the accessible object
     * @since 0.9.7
     */
    public static void safeSetAccessible(AccessibleObject accessibleObject) {
        try {
            accessibleObject.setAccessible(true);
        } catch (Throwable t) {
        }
    }
}
