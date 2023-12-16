/*
 * Copyright (c) 2022. caoccao.com Sam Cao
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

import com.caoccao.javet.interfaces.IJavetLogger;
import com.caoccao.javet.utils.JavetDefaultLogger;
import com.caoccao.javet.utils.JavetResourceUtils;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.reference.V8ValueFunction;
import com.caoccao.javet.values.reference.V8ValueObject;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import net.bytebuddy.matcher.ElementMatchers;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;
import java.util.concurrent.Callable;

/**
 * The type Javet dynamic object factory.
 *
 * @since 2.0.1
 */
public final class JavetReflectionObjectFactory implements IJavetReflectionObjectFactory {
    private static final JavetReflectionObjectFactory instance = new JavetReflectionObjectFactory();
    private final IJavetLogger logger;

    private JavetReflectionObjectFactory() {
        logger = new JavetDefaultLogger(getClass().getName());
    }

    /**
     * Gets instance.
     *
     * @return the instance
     * @since 2.0.1
     */
    public static JavetReflectionObjectFactory getInstance() {
        return instance;
    }

    @Override
    public Object toObject(Class<?> type, V8Value v8Value) {
        if (v8Value instanceof V8ValueObject) {
            V8ValueObject v8ValueObject = null;
            try {
                DynamicObjectAutoCloseableInvocationHandler invocationHandler;
                v8ValueObject = v8Value.toClone();
                if (AutoCloseable.class.isAssignableFrom(type)) {
                    invocationHandler = new DynamicObjectAutoCloseableInvocationHandler(type, v8ValueObject);
                } else {
                    invocationHandler = new DynamicObjectForceCloseableInvocationHandler(type, v8ValueObject);
                }
                invocationHandler.initialize();
                return invocationHandler.getDynamicObject();
            } catch (Throwable t) {
                logger.logError(t, "Failed to create dynamic object for {0}.", type.getName());
                JavetResourceUtils.safeClose(v8ValueObject);
            }
        }
        return null;
    }

    /**
     * The type Dynamic object auto closeable invocation handler.
     *
     * @since 2.0.1
     */
    public static class DynamicObjectAutoCloseableInvocationHandler {
        /**
         * The constant CONSTRUCTOR_STRATEGY.
         *
         * @since 2.0.1
         */
        protected static final ConstructorStrategy CONSTRUCTOR_STRATEGY =
                new ConstructorStrategy.ForDefaultConstructor();
        /**
         * The constant METHOD_CLOSE.
         *
         * @since 2.0.1
         */
        protected static final String METHOD_CLOSE = "close";
        /**
         * The Dynamic object.
         *
         * @since 2.0.1
         */
        protected Object dynamicObject;
        /**
         * The Type.
         *
         * @since 2.0.1
         */
        protected Class<?> type;
        /**
         * The V8 value object.
         *
         * @since 2.0.1
         */
        protected V8ValueObject v8ValueObject;

        /**
         * Instantiates a new Dynamic object auto closeable invocation handler.
         *
         * @param type          the type
         * @param v8ValueObject the V8 value object
         * @since 2.0.1
         */
        public DynamicObjectAutoCloseableInvocationHandler(Class<?> type, V8ValueObject v8ValueObject) {
            this.type = type;
            this.v8ValueObject = v8ValueObject;
        }

        /**
         * Close.
         *
         * @throws Exception the exception
         * @since 2.0.1
         */
        public void close() throws Exception {
            JavetResourceUtils.safeClose(v8ValueObject);
            dynamicObject = null;
            v8ValueObject = null;
        }

        @Override
        protected void finalize() throws Throwable {
            close();
        }

        /**
         * Gets dynamic object.
         *
         * @return the dynamic object
         * @since 2.0.1
         */
        public Object getDynamicObject() {
            return dynamicObject;
        }

        /**
         * Initialize.
         *
         * @throws IOException               the io exception
         * @throws NoSuchMethodException     the no such method exception
         * @throws InvocationTargetException the invocation target exception
         * @throws InstantiationException    the instantiation exception
         * @throws IllegalAccessException    the illegal access exception
         * @since 2.0.1
         */
        public void initialize()
                throws IOException, NoSuchMethodException, InvocationTargetException,
                InstantiationException, IllegalAccessException {
            Class<?> objectClass;
            try (DynamicType.Unloaded<?> unloadedType = new ByteBuddy()
                    .subclass(type, CONSTRUCTOR_STRATEGY)
                    .method(ElementMatchers.isPublic())
                    .intercept(MethodDelegation.to(this))
                    .make()) {
                objectClass = unloadedType.load(getClass().getClassLoader()).getLoaded();
            }
            dynamicObject = objectClass.getConstructor().newInstance();
        }

        /**
         * Intercept object.
         *
         * @param method    the method
         * @param arguments the arguments
         * @param callable  the callable
         * @return the object
         * @throws Exception the exception
         * @since 2.0.1
         */
        @RuntimeType
        public Object intercept(
                @Origin Method method,
                @AllArguments Object[] arguments,
                @SuperCall Callable<Object> callable) throws Exception {
            if (v8ValueObject != null) {
                String methodName = method.getName();
                final int argumentLength = arguments.length;
                if (METHOD_CLOSE.equals(methodName) && argumentLength == 0) {
                    close();
                    return callable.call();
                } else if (v8ValueObject.has(methodName)) {
                    // Function or Property
                    try (V8Value v8ValueProperty = v8ValueObject.get(methodName)) {
                        if (v8ValueProperty instanceof V8ValueFunction) {
                            // Function
                            V8ValueFunction v8ValueFunction = (V8ValueFunction) v8ValueProperty;
                            return v8ValueFunction.callObject(null, arguments);
                        } else if (argumentLength == 0) {
                            // Property
                            return v8ValueObject.getV8Runtime().toObject(v8ValueProperty);
                        }
                    }
                } else if (argumentLength == 0) {
                    // Getter
                    String propertyName = null;
                    if (methodName.startsWith(V8ValueObject.METHOD_PREFIX_IS)) {
                        propertyName = methodName.substring(V8ValueObject.METHOD_PREFIX_IS.length());
                    } else if (methodName.startsWith(V8ValueObject.METHOD_PREFIX_GET)) {
                        propertyName = methodName.substring(V8ValueObject.METHOD_PREFIX_GET.length());
                    }
                    if (propertyName != null && propertyName.length() > 0) {
                        propertyName = propertyName.substring(0, 1).toLowerCase(Locale.ROOT)
                                + propertyName.substring(1);
                        if (v8ValueObject.has(propertyName)) {
                            return v8ValueObject.getObject(propertyName);
                        }
                    }
                } else if (argumentLength == 1) {
                    // Setter
                    String propertyName = null;
                    if (methodName.startsWith(V8ValueObject.METHOD_PREFIX_SET)) {
                        propertyName = methodName.substring(V8ValueObject.METHOD_PREFIX_SET.length());
                    }
                    if (propertyName != null && propertyName.length() > 0) {
                        propertyName = propertyName.substring(0, 1).toLowerCase(Locale.ROOT)
                                + propertyName.substring(1);
                        if (v8ValueObject.has(propertyName)) {
                            return v8ValueObject.set(propertyName, arguments[0]);
                        }
                    }
                }
            }
            return callable.call();
        }
    }

    /**
     * The type Dynamic object force closeable invocation handler.
     *
     * @since 2.0.1
     */
    public static class DynamicObjectForceCloseableInvocationHandler
            extends DynamicObjectAutoCloseableInvocationHandler
            implements AutoCloseable {

        /**
         * Instantiates a new Dynamic object force closeable invocation handler.
         *
         * @param type          the type
         * @param v8ValueObject the V8 value object
         * @since 2.0.1
         */
        public DynamicObjectForceCloseableInvocationHandler(Class<?> type, V8ValueObject v8ValueObject) {
            super(type, v8ValueObject);
        }

        @RuntimeType
        @Override
        public void close() throws Exception {
            super.close();
        }

        @Override
        public void initialize()
                throws IOException, NoSuchMethodException, InvocationTargetException,
                InstantiationException, IllegalAccessException {
            Class<?> objectClass;
            try (DynamicType.Unloaded<?> unloadedType = new ByteBuddy()
                    .subclass(type, CONSTRUCTOR_STRATEGY)
                    .implement(AutoCloseable.class)
                    .method(ElementMatchers.isPublic())
                    .intercept(MethodDelegation.to(this))
                    .make()) {
                objectClass = unloadedType.load(getClass().getClassLoader()).getLoaded();
            }
            dynamicObject = objectClass.getConstructor().newInstance();
        }

        @RuntimeType
        @Override
        public Object intercept(
                @Origin Method method,
                @AllArguments Object[] arguments,
                @SuperCall Callable<Object> callable) throws Exception {
            return super.intercept(method, arguments, callable);
        }
    }
}
