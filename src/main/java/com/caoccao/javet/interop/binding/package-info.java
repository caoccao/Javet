/*
 * Copyright (c) 2021-2026. caoccao.com Sam Cao
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
 * Binding infrastructure for mapping Java classes and methods to JavaScript.
 * <p>
 * This package manages the metadata that drives proxy-based Java-to-JavaScript interop:
 * <ul>
 *   <li>{@link com.caoccao.javet.interop.binding.ClassDescriptor} - Comprehensive descriptor of a Java class for reflection-based proxying, including methods, fields, and annotations.</li>
 *   <li>{@link com.caoccao.javet.interop.binding.ClassDescriptorStore} - Centralized cache of class descriptors.</li>
 *   <li>{@link com.caoccao.javet.interop.binding.MethodDescriptor} - Describes a single method binding with its name, symbol, and invocation metadata.</li>
 *   <li>{@link com.caoccao.javet.interop.binding.BindingContext} / {@link com.caoccao.javet.interop.binding.BindingContextStore} - Stores and retrieves active method bindings per V8 value object.</li>
 *   <li>{@link com.caoccao.javet.interop.binding.IClassProxyPlugin} - Interface for customizing proxy behavior of Java classes.</li>
 * </ul>
 *
 * @since 0.7.0
 * @author Sam Cao
 */
package com.caoccao.javet.interop.binding;