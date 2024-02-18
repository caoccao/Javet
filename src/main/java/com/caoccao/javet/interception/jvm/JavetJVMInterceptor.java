/*
 * Copyright (c) 2023-2024. caoccao.com Sam Cao
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

package com.caoccao.javet.interception.jvm;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interception.BaseJavetDirectCallableInterceptor;
import com.caoccao.javet.interfaces.IJavetUniFunction;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.callback.JavetCallbackContext;
import com.caoccao.javet.interop.callback.JavetCallbackType;
import com.caoccao.javet.interop.converters.JavetProxyConverter;
import com.caoccao.javet.interop.proxy.IJavetDirectProxyHandler;
import com.caoccao.javet.utils.JavetResourceUtils;
import com.caoccao.javet.utils.StringUtils;
import com.caoccao.javet.utils.V8ValueUtils;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.primitive.V8ValueString;
import com.caoccao.javet.values.reference.IV8ValueObject;
import com.caoccao.javet.values.reference.V8ValueObject;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The Javet JVM interceptor exposes the whole JVM as <code>javet</code> in V8.
 * It must be accompanied by {@link JavetProxyConverter}.
 * <p>
 * Usages:
 * <code>
 * let sb = new javet.package.java.util.StringBuilder();
 * sb.append(123).append('abc');
 * sb.toString(); // 123abc
 * sb = undefined;
 * javet.v8.gc();
 * </code>
 *
 * @since 3.0.3
 */
public class JavetJVMInterceptor extends BaseJavetDirectCallableInterceptor {
    /**
     * The constant DEFAULT_NAME.
     *
     * @since 3.0.3
     */
    public static final String DEFAULT_NAME = "javet";
    /**
     * The constant ERROR_THE_CONVERTER_MUST_BE_INSTANCE_OF_JAVET_PROXY_CONVERTER.
     *
     * @since 3.0.3
     */
    protected static final String ERROR_THE_CONVERTER_MUST_BE_INSTANCE_OF_JAVET_PROXY_CONVERTER =
            "The converter must be instance of JavetProxyConverter.";
    /**
     * The constant JAVET_PROXY_CONVERTER.
     *
     * @since 3.0.3
     */
    protected static final JavetProxyConverter JAVET_PROXY_CONVERTER = new JavetProxyConverter();
    /**
     * The constant JS_PROPERTY_PACKAGE.
     *
     * @since 3.0.3
     */
    protected static final String JS_PROPERTY_PACKAGE = "package";
    /**
     * The constant JS_PROPERTY_V8.
     *
     * @since 3.0.3
     */
    protected static final String JS_PROPERTY_V8 = "v8";
    /**
     * The Name injected in V8.
     *
     * @since 3.0.3
     */
    protected String name;

    /**
     * Instantiates a new Javet JVM interceptor.
     *
     * @param v8Runtime the V8 runtime
     * @since 3.0.3
     */
    public JavetJVMInterceptor(V8Runtime v8Runtime) {
        super(v8Runtime);
        assert v8Runtime.getConverter() instanceof JavetProxyConverter : ERROR_THE_CONVERTER_MUST_BE_INSTANCE_OF_JAVET_PROXY_CONVERTER;
        name = DEFAULT_NAME;
    }

    @Override
    public JavetCallbackContext[] getCallbackContexts() {
        return new JavetCallbackContext[]{
                new JavetCallbackContext(
                        JS_PROPERTY_V8,
                        this, JavetCallbackType.DirectCallGetterAndNoThis,
                        (GetterAndNoThis<Exception>) () -> new JavetV8(v8Runtime).toV8Value()),
                new JavetCallbackContext(
                        JS_PROPERTY_PACKAGE,
                        this, JavetCallbackType.DirectCallGetterAndNoThis,
                        (GetterAndNoThis<Exception>) () -> new JavetVirtualPackage(v8Runtime, StringUtils.EMPTY).toV8Value()),
        };
    }

    /**
     * Gets name.
     *
     * @return the name
     * @since 3.0.3
     */
    public String getName() {
        return name;
    }

    @Override
    public boolean register(IV8ValueObject... iV8ValueObjects) throws JavetException {
        boolean successful = true;
        try (V8ValueObject v8ValueObject = v8Runtime.createV8ValueObject()) {
            v8ValueObject.bind(this);
            for (IV8ValueObject iV8ValueObject : iV8ValueObjects) {
                successful = iV8ValueObject.set(DEFAULT_NAME, v8ValueObject) & successful;
            }
            return successful;
        }
    }

    /**
     * Sets name.
     *
     * @param name the name
     * @since 3.0.3
     */
    public void setName(String name) {
        this.name = Objects.requireNonNull(name);
    }

    @Override
    public boolean unregister(IV8ValueObject... iV8ValueObjects) throws JavetException {
        boolean successful = true;
        for (IV8ValueObject iV8ValueObject : iV8ValueObjects) {
            successful = iV8ValueObject.delete(DEFAULT_NAME) & successful;
        }
        return successful;
    }

    /**
     * The type Base javet package.
     *
     * @since 3.0.3
     */
    abstract static class BaseJavetPackage implements IJavetDirectProxyHandler<Exception> {
        /**
         * The String getter map.
         *
         * @since 3.0.3
         */
        protected Map<String, IJavetUniFunction<String, ? extends V8Value, Exception>> stringGetterMap;
        /**
         * The V8 runtime.
         *
         * @since 3.0.3
         */
        protected V8Runtime v8Runtime;

        /**
         * Instantiates a new Base javet package.
         *
         * @param v8Runtime the V8 runtime
         * @since 3.0.3
         */
        public BaseJavetPackage(V8Runtime v8Runtime) {
            this.v8Runtime = v8Runtime;
        }

        /**
         * Gets name.
         *
         * @return the name
         * @since 3.0.3
         */
        public abstract String getName();

        @Override
        public V8Runtime getV8Runtime() {
            return v8Runtime;
        }

        /**
         * Is valid.
         *
         * @return true : valid, false : invalid
         * @since 3.0.3
         */
        public abstract boolean isValid();

        @Override
        public V8Value proxyGet(V8Value target, V8Value property, V8Value receiver) throws JavetException, Exception {
            V8Value v8Value = IJavetDirectProxyHandler.super.proxyGet(target, property, receiver);
            if (v8Value == null) {
                if (property instanceof V8ValueString) {
                    String childName = ((V8ValueString) property).getValue();
                    if (!StringUtils.isEmpty(childName)) {
                        String name;
                        if (StringUtils.isEmpty(getName())) {
                            name = childName;
                        } else {
                            name = getName() + "." + childName;
                        }
                        try {
                            Class<?> clazz = Class.forName(name);
                            v8Value = JAVET_PROXY_CONVERTER.toV8Value(v8Runtime, clazz);
                        } catch (Throwable ignored) {
                            Package namedPackage = Package.getPackage(name);
                            if (namedPackage != null) {
                                v8Value = new JavetPackage(v8Runtime, namedPackage).toV8Value();
                            } else {
                                v8Value = new JavetVirtualPackage(v8Runtime, name).toV8Value();
                            }
                        }
                    }

                }
            }
            return v8Value;
        }

        @Override
        public Map<String, IJavetUniFunction<String, ? extends V8Value, Exception>> proxyGetStringGetterMap() {
            if (stringGetterMap == null) {
                stringGetterMap = new HashMap<>();
                stringGetterMap.put(".getPackages", (propertyName) ->
                        v8Runtime.createV8ValueFunction(
                                new JavetCallbackContext(
                                        propertyName,
                                        this, JavetCallbackType.DirectCallNoThisAndResult,
                                        (NoThisAndResult<Exception>) (v8Values) -> {
                                            final String prefix = getName() + ".";
                                            final List<Package> packages = Stream.of(Package.getPackages())
                                                    .filter((p) -> p.getName().startsWith(prefix))
                                                    .filter((p) -> p.getName().substring(prefix.length()).contains("."))
                                                    .collect(Collectors.toList());
                                            final List<V8Value> results = new ArrayList<>(packages.size());
                                            try {
                                                for (Package p : packages) {
                                                    results.add(new JavetPackage(v8Runtime, p).toV8Value());
                                                }
                                                return V8ValueUtils.createV8ValueArray(v8Runtime, results.toArray());
                                            } finally {
                                                JavetResourceUtils.safeClose(results);
                                            }
                                        })));
                stringGetterMap.put(".name", (propertyName) -> v8Runtime.createV8ValueString(getName()));
                stringGetterMap.put(".valid", (propertyName) -> v8Runtime.createV8ValueBoolean(isValid()));
            }
            return stringGetterMap;
        }

        /**
         * To V8 value V8 value.
         *
         * @return the V8 value
         * @throws JavetException the javet exception
         * @since 3.0.3
         */
        public V8Value toV8Value() throws JavetException {
            return JAVET_PROXY_CONVERTER.toV8Value(v8Runtime, this);
        }
    }

    /**
     * The type Javet package.
     *
     * @since 3.0.3
     */
    static class JavetPackage extends BaseJavetPackage {
        /**
         * The Named package.
         *
         * @since 3.0.3
         */
        protected Package namedPackage;

        /**
         * Instantiates a new Javet package.
         *
         * @param v8Runtime    the V8 runtime
         * @param namedPackage the named package
         * @since 3.0.3
         */
        public JavetPackage(V8Runtime v8Runtime, Package namedPackage) {
            super(v8Runtime);
            this.namedPackage = namedPackage;
            stringGetterMap = null;
        }

        @Override
        public String getName() {
            return namedPackage.getName();
        }

        @Override
        public boolean isValid() {
            return true;
        }

        @Override
        public Map<String, IJavetUniFunction<String, ? extends V8Value, Exception>> proxyGetStringGetterMap() {
            if (stringGetterMap == null) {
                stringGetterMap = super.proxyGetStringGetterMap();
                stringGetterMap.put(
                        ".implementationTitle",
                        (propertyName) -> v8Runtime.createV8ValueString(namedPackage.getImplementationTitle()));
                stringGetterMap.put(
                        ".implementationVersion",
                        (propertyName) -> v8Runtime.createV8ValueString(namedPackage.getImplementationVersion()));
                stringGetterMap.put(
                        ".implementationVendor",
                        (propertyName) -> v8Runtime.createV8ValueString(namedPackage.getImplementationVendor()));
                stringGetterMap.put(
                        ".sealed",
                        (propertyName) -> v8Runtime.createV8ValueBoolean(namedPackage.isSealed()));
                stringGetterMap.put(
                        ".specificationTitle",
                        (propertyName) -> v8Runtime.createV8ValueString(namedPackage.getSpecificationTitle()));
                stringGetterMap.put(
                        ".specificationVersion",
                        (propertyName) -> v8Runtime.createV8ValueString(namedPackage.getSpecificationVersion()));
                stringGetterMap.put(
                        ".specificationVendor",
                        (propertyName) -> v8Runtime.createV8ValueString(namedPackage.getSpecificationVendor()));
            }
            return stringGetterMap;
        }
    }

    /**
     * The type Javet V8.
     *
     * @since 3.0.3
     */
    static class JavetV8 implements IJavetDirectProxyHandler<Exception> {
        /**
         * The String getter map.
         *
         * @since 3.0.3
         */
        protected Map<String, IJavetUniFunction<String, ? extends V8Value, Exception>> stringGetterMap;
        /**
         * The V8 runtime.
         *
         * @since 3.0.3
         */
        protected V8Runtime v8Runtime;

        /**
         * Instantiates a new Javet V8.
         *
         * @param v8Runtime the V8 runtime
         * @since 3.0.3
         */
        public JavetV8(V8Runtime v8Runtime) {
            this.v8Runtime = v8Runtime;
        }

        @Override
        public V8Runtime getV8Runtime() {
            return v8Runtime;
        }

        @Override
        public Map<String, IJavetUniFunction<String, ? extends V8Value, Exception>> proxyGetStringGetterMap() {
            if (stringGetterMap == null) {
                stringGetterMap = new HashMap<>();
                stringGetterMap.put("gc", (propertyName) ->
                        v8Runtime.createV8ValueFunction(
                                new JavetCallbackContext(
                                        propertyName,
                                        this, JavetCallbackType.DirectCallNoThisAndNoResult,
                                        (NoThisAndNoResult<Exception>) (v8Values) -> v8Runtime.lowMemoryNotification())
                        ));
            }
            return stringGetterMap;
        }

        /**
         * To V8 value V8 value.
         *
         * @return the V8 value
         * @throws JavetException the javet exception
         * @since 3.0.3
         */
        public V8Value toV8Value() throws JavetException {
            return JAVET_PROXY_CONVERTER.toV8Value(v8Runtime, this);
        }
    }

    /**
     * The type Javet virtual package.
     *
     * @since 3.0.3
     */
    static class JavetVirtualPackage extends BaseJavetPackage {
        /**
         * The Package name.
         *
         * @since 3.0.3
         */
        protected String packageName;

        /**
         * Instantiates a new Javet virtual package.
         *
         * @param v8Runtime   the V8 runtime
         * @param packageName the package name
         * @since 3.0.3
         */
        public JavetVirtualPackage(V8Runtime v8Runtime, String packageName) {
            super(v8Runtime);
            this.packageName = packageName;
        }

        @Override
        public String getName() {
            return packageName;
        }

        @Override
        public boolean isValid() {
            return false;
        }
    }
}
