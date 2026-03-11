/*
 * Copyright (c) 2023-2026. caoccao.com Sam Cao
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

/**
 * JVM interceptor that exposes the entire Java runtime to JavaScript as the {@code javet} global object.
 * <ul>
 *   <li>{@link com.caoccao.javet.interception.jvm.JavetJVMInterceptor} - Registers a {@code javet} object in V8
 *       that provides access to Java packages, classes, and a {@code javet.v8.gc()} helper.
 *       Must be used with {@link com.caoccao.javet.interop.converters.JavetProxyConverter}.</li>
 * </ul>
 * <p>
 * Example usage from JavaScript:
 * <pre>{@code
 * let sb = new javet.package.java.util.StringBuilder();
 * sb.append(123).append('abc');
 * sb.toString(); // "123abc"
 * sb = undefined;
 * javet.v8.gc();
 * }</pre>
 *
 * @since 3.0.3
 * @author Sam Cao
 */
package com.caoccao.javet.interception.jvm;
