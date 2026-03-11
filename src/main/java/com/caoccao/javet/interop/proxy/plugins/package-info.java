/*
 * Copyright (c) 2024-2026. caoccao.com Sam Cao
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
 * Proxy plugins that teach the {@link com.caoccao.javet.interop.converters.JavetProxyConverter} how to
 * expose specific Java types as idiomatic JavaScript objects via {@code Proxy}.
 * <p>
 * Each plugin maps a Java type to JavaScript semantics (index access, iteration, {@code length}, spread, etc.):
 * <ul>
 *   <li>{@link com.caoccao.javet.interop.proxy.plugins.JavetProxyPluginDefault} - Default plugin for general Java objects;
 *       handles {@code String}, {@code BigInteger}, {@code ZonedDateTime}, and other common types.</li>
 *   <li>{@link com.caoccao.javet.interop.proxy.plugins.JavetProxyPluginArray} - Java arrays exposed as JavaScript Array-like
 *       objects with index access, {@code length}, {@code push}, {@code pop}, {@code forEach}, {@code map}, etc.</li>
 *   <li>{@link com.caoccao.javet.interop.proxy.plugins.JavetProxyPluginList} - {@code java.util.List} exposed as JavaScript Array-like
 *       objects with index access, {@code length}, {@code push}, {@code pop}, {@code splice}, {@code forEach}, etc.</li>
 *   <li>{@link com.caoccao.javet.interop.proxy.plugins.JavetProxyPluginMap} - {@code java.util.Map} exposed as JavaScript Map-like
 *       objects with {@code size}, {@code get}, {@code set}, {@code has}, {@code delete}, {@code forEach}, and iteration.</li>
 *   <li>{@link com.caoccao.javet.interop.proxy.plugins.JavetProxyPluginSet} - {@code java.util.Set} exposed as JavaScript Set-like
 *       objects with {@code size}, {@code add}, {@code has}, {@code delete}, {@code forEach}, and iteration.</li>
 *   <li>{@link com.caoccao.javet.interop.proxy.plugins.JavetProxyPluginClass} - {@code java.lang.Class} proxy for static member access.</li>
 * </ul>
 * <p>
 * Base classes for building custom plugins:
 * <ul>
 *   <li>{@link com.caoccao.javet.interop.proxy.plugins.BaseJavetProxyPlugin} - Base with {@code Symbol.toPrimitive} and error handling.</li>
 *   <li>{@link com.caoccao.javet.interop.proxy.plugins.BaseJavetProxyPluginSingle} - For plugins targeting a single Java type.</li>
 *   <li>{@link com.caoccao.javet.interop.proxy.plugins.BaseJavetProxyPluginMultiple} - For plugins targeting multiple Java types.</li>
 * </ul>
 *
 * @since 3.0.4
 * @author Sam Cao
 */
package com.caoccao.javet.interop.proxy.plugins;
