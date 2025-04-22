/*
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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * The method invoker interface.
 */
@FunctionalInterface
public interface IMethodInvoker {
    /**
     * Invoke a method on the given receiver, with the given args.
     *
     * @param method   the method to invoke
     * @param receiver the object to invoke the method on
     * @param args     the arguments to pass to the method
     * @return the result of the method invocation
     */
    public Object invoke(
            Method method,
            Object receiver, Object... args) throws IllegalAccessException, InvocationTargetException;
}
