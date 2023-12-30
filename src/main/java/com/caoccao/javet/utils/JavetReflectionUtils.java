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
     * <p>
     * Usage:
     * <p>
     * Suppose there is a test() method.
     * <pre>
     * public String test() { ... }
     * </pre>
     * Let's convert the lambda of test() to string which represents the method name.
     * <pre>
     * String methodName = JavetReflectionUtils.getMethodNameFromLambda((Supplier &amp; Serializable) this::test);
     * </pre>
     *
     * @param lambda the lambda
     * @return the method name
     * @since 0.9.13
     */
    public static String getMethodNameFromLambda(Serializable lambda) {
        Objects.requireNonNull(lambda);
        try {
            Method method = lambda.getClass().getDeclaredMethod(METHOD_NAME_WRITE_REPLACE);
            method.setAccessible(true);
            SerializedLambda serializedLambda = (SerializedLambda) method.invoke(lambda);
            return serializedLambda.getImplMethodName();
        } catch (Throwable ignored) {
        }
        return null;
    }

    /**
     * Gets method name set from lambdas.
     * <p>
     * Usage:
     * <p>
     * Suppose there are a few methods.
     * <pre>
     * public String abc() { ... }
     * public int def() { ... }
     * </pre>
     * Let's convert the lambda of these methods to a set which contains the method names.
     * <pre>
     * Set&lt;String&gt; methodNameSet = JavetReflectionUtils.getMethodNameSetFromLambdas(
     *         (Supplier &amp; Serializable) this::abc,
     *         (Supplier &amp; Serializable) this::def);
     * </pre>
     *
     * @param lambdas the lambdas
     * @return the method name set
     * @since 0.9.13
     */
    public static Set<String> getMethodNameSetFromLambdas(Serializable... lambdas) {
        return Stream.of(Objects.requireNonNull(lambdas))
                .filter(Objects::nonNull)
                .map(JavetReflectionUtils::getMethodNameFromLambda)
                .filter(Objects::nonNull)
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
        } catch (Throwable ignored) {
        }
    }
}
