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

package com.caoccao.javet.interop.callback;

import com.caoccao.javet.exceptions.JavetError;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.converters.IJavetConverter;
import com.caoccao.javet.interop.converters.JavetConverterConfig;
import com.caoccao.javet.utils.JavetReflectionUtils;
import com.caoccao.javet.utils.JavetResourceUtils;
import com.caoccao.javet.utils.JavetTypeUtils;
import com.caoccao.javet.utils.SimpleMap;
import com.caoccao.javet.values.IV8Value;
import com.caoccao.javet.values.V8Value;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

/**
 * The type V8 function callback.
 *
 * @since 0.8.3
 */
public final class V8FunctionCallback {

    private static final String NULL = "null";

    private static Object convert(IJavetConverter converter, Class<?> expectedClass, V8Value v8Value)
            throws JavetException {
        if (v8Value == null) {
            // This check is for null safety.
            if (expectedClass.isPrimitive()) {
                /*
                 * The following test is based on statistical analysis
                 * so that the performance can be maximized.
                 */
                JavetConverterConfig<?> config = converter.getConfig();
                if (expectedClass == int.class) {
                    return config.getDefaultInt();
                } else if (expectedClass == boolean.class) {
                    return config.getDefaultBoolean();
                } else if (expectedClass == double.class) {
                    return config.getDefaultDouble();
                } else if (expectedClass == float.class) {
                    return config.getDefaultFloat();
                } else if (expectedClass == long.class) {
                    return config.getDefaultLong();
                } else if (expectedClass == short.class) {
                    return config.getDefaultShort();
                } else if (expectedClass == byte.class) {
                    return config.getDefaultByte();
                } else if (expectedClass == char.class) {
                    return config.getDefaultChar();
                }
            } else if (expectedClass == Optional.class) {
                return Optional.empty();
            } else if (expectedClass == OptionalInt.class) {
                return OptionalInt.empty();
            } else if (expectedClass == OptionalDouble.class) {
                return OptionalDouble.empty();
            } else if (expectedClass == OptionalLong.class) {
                return OptionalLong.empty();
            } else if (expectedClass == Stream.class) {
                return Stream.empty();
            } else if (expectedClass == IntStream.class) {
                return IntStream.empty();
            } else if (expectedClass == DoubleStream.class) {
                return DoubleStream.empty();
            } else if (expectedClass == LongStream.class) {
                return LongStream.empty();
            }
        } else if (expectedClass.isAssignableFrom(v8Value.getClass())) {
            // Skip assignable
        } else {
            Object convertedObject = converter.toObject(v8Value);
            try {
                if (convertedObject == null) {
                    return convert(converter, expectedClass, null);
                } else {
                    Class<?> convertedObjectClass = convertedObject.getClass();
                    if (expectedClass.isAssignableFrom(convertedObjectClass)) {
                        return convertedObject;
                    } else if (expectedClass.isPrimitive()) {
                        /*
                         * The following test is based on statistical analysis
                         * so that the performance can be maximized.
                         */
                        if (expectedClass == int.class) {
                            if (convertedObjectClass == Integer.class) {
                                //noinspection UnnecessaryUnboxing
                                return ((Integer) convertedObject).intValue();
                            } else if (convertedObjectClass == Long.class) {
                                return ((Long) convertedObject).intValue();
                            } else if (convertedObjectClass == Short.class) {
                                return ((Short) convertedObject).intValue();
                            } else if (convertedObjectClass == Byte.class) {
                                return ((Byte) convertedObject).intValue();
                            }
                        } else if (expectedClass == boolean.class && convertedObjectClass == Boolean.class) {
                            //noinspection UnnecessaryUnboxing
                            return ((Boolean) convertedObject).booleanValue();
                        } else if (expectedClass == double.class) {
                            if (convertedObjectClass == Double.class) {
                                //noinspection UnnecessaryUnboxing
                                return ((Double) convertedObject).doubleValue();
                            } else if (convertedObjectClass == Float.class) {
                                return ((Float) convertedObject).doubleValue();
                            } else if (convertedObjectClass == Integer.class) {
                                return ((Integer) convertedObject).doubleValue();
                            } else if (convertedObjectClass == Long.class) {
                                return ((Long) convertedObject).doubleValue();
                            } else if (convertedObjectClass == Short.class) {
                                return ((Short) convertedObject).doubleValue();
                            } else if (convertedObjectClass == Byte.class) {
                                return ((Byte) convertedObject).doubleValue();
                            }
                        } else if (expectedClass == float.class) {
                            if (convertedObjectClass == Double.class) {
                                return ((Double) convertedObject).floatValue();
                            } else if (convertedObjectClass == Float.class) {
                                //noinspection UnnecessaryUnboxing
                                return ((Float) convertedObject).floatValue();
                            } else if (convertedObjectClass == Integer.class) {
                                return ((Integer) convertedObject).floatValue();
                            } else if (convertedObjectClass == Long.class) {
                                return ((Long) convertedObject).floatValue();
                            } else if (convertedObjectClass == Short.class) {
                                return ((Short) convertedObject).floatValue();
                            } else if (convertedObjectClass == Byte.class) {
                                return ((Byte) convertedObject).floatValue();
                            }
                        } else if (expectedClass == long.class) {
                            if (convertedObjectClass == Long.class) {
                                //noinspection UnnecessaryUnboxing
                                return ((Long) convertedObject).longValue();
                            } else if (convertedObjectClass == Integer.class) {
                                return ((Integer) convertedObject).longValue();
                            } else if (convertedObjectClass == Short.class) {
                                return ((Short) convertedObject).longValue();
                            } else if (convertedObjectClass == Byte.class) {
                                return ((Byte) convertedObject).longValue();
                            }
                        } else if (expectedClass == short.class) {
                            if (convertedObjectClass == Short.class) {
                                //noinspection UnnecessaryUnboxing
                                return ((Short) convertedObject).shortValue();
                            } else if (convertedObjectClass == Integer.class) {
                                return ((Integer) convertedObject).shortValue();
                            } else if (convertedObjectClass == Long.class) {
                                return ((Long) convertedObject).shortValue();
                            } else if (convertedObjectClass == Byte.class) {
                                return ((Byte) convertedObject).shortValue();
                            }
                        } else if (expectedClass == byte.class) {
                            if (convertedObjectClass == Byte.class) {
                                //noinspection UnnecessaryUnboxing
                                return ((Byte) convertedObject).byteValue();
                            } else if (convertedObjectClass == Integer.class) {
                                return ((Integer) convertedObject).byteValue();
                            } else if (convertedObjectClass == Long.class) {
                                return ((Long) convertedObject).byteValue();
                            } else if (convertedObjectClass == Short.class) {
                                return ((Short) convertedObject).byteValue();
                            }
                        } else if (expectedClass == char.class) {
                            if (convertedObjectClass == Character.class) {
                                //noinspection UnnecessaryUnboxing
                                return ((Character) convertedObject).charValue();
                            } else if (convertedObjectClass == String.class) {
                                String convertedString = (String) convertedObject;
                                return convertedString.length() > 0 ?
                                        convertedString.charAt(0) : converter.getConfig().getDefaultChar();
                            }
                        }
                    } else if (expectedClass == Integer.class) {
                        if (convertedObjectClass == Long.class) {
                            return ((Long) convertedObject).intValue();
                        } else if (convertedObjectClass == Short.class) {
                            return ((Short) convertedObject).intValue();
                        } else if (convertedObjectClass == Byte.class) {
                            return ((Byte) convertedObject).intValue();
                        }
                    } else if (expectedClass == Double.class) {
                        if (convertedObjectClass == Float.class) {
                            return ((Float) convertedObject).doubleValue();
                        } else if (convertedObjectClass == Integer.class) {
                            return ((Integer) convertedObject).doubleValue();
                        } else if (convertedObjectClass == Long.class) {
                            return ((Long) convertedObject).doubleValue();
                        } else if (convertedObjectClass == Short.class) {
                            return ((Short) convertedObject).doubleValue();
                        } else if (convertedObjectClass == Byte.class) {
                            return ((Byte) convertedObject).doubleValue();
                        }
                    } else if (expectedClass == Float.class) {
                        if (convertedObjectClass == Double.class) {
                            return ((Double) convertedObject).floatValue();
                        } else if (convertedObjectClass == Integer.class) {
                            return ((Integer) convertedObject).floatValue();
                        } else if (convertedObjectClass == Long.class) {
                            return ((Long) convertedObject).floatValue();
                        } else if (convertedObjectClass == Short.class) {
                            return ((Short) convertedObject).floatValue();
                        } else if (convertedObjectClass == Byte.class) {
                            return ((Byte) convertedObject).floatValue();
                        }
                    } else if (expectedClass == Long.class) {
                        if (convertedObjectClass == Integer.class) {
                            return ((Integer) convertedObject).longValue();
                        } else if (convertedObjectClass == Short.class) {
                            return ((Short) convertedObject).longValue();
                        } else if (convertedObjectClass == Byte.class) {
                            return ((Byte) convertedObject).longValue();
                        }
                    } else if (expectedClass == Short.class) {
                        if (convertedObjectClass == Integer.class) {
                            return ((Integer) convertedObject).shortValue();
                        } else if (convertedObjectClass == Long.class) {
                            return ((Long) convertedObject).shortValue();
                        } else if (convertedObjectClass == Byte.class) {
                            return ((Byte) convertedObject).shortValue();
                        }
                    } else if (expectedClass == Byte.class) {
                        if (convertedObjectClass == Integer.class) {
                            return ((Integer) convertedObject).byteValue();
                        } else if (convertedObjectClass == Long.class) {
                            return ((Long) convertedObject).byteValue();
                        } else if (convertedObjectClass == Short.class) {
                            return ((Short) convertedObject).byteValue();
                        }
                    } else if (expectedClass == Character.class) {
                        if (convertedObjectClass == String.class) {
                            String convertedString = (String) convertedObject;
                            return convertedString.length() > 0 ?
                                    convertedString.charAt(0) : converter.getConfig().getDefaultChar();
                        }
                    } else if (expectedClass == Optional.class) {
                        return Optional.of(convertedObject);
                    } else if (expectedClass == OptionalInt.class) {
                        if (convertedObject instanceof Integer) {
                            return OptionalInt.of((Integer) convertedObject);
                        }
                    } else if (expectedClass == OptionalDouble.class) {
                        if (convertedObject instanceof Double) {
                            return OptionalDouble.of((Double) convertedObject);
                        }
                    } else if (expectedClass == OptionalLong.class) {
                        if (convertedObject instanceof Long) {
                            return OptionalLong.of((Long) convertedObject);
                        }
                    } else if (expectedClass == Stream.class) {
                        Stream<?> stream = JavetTypeUtils.toStream(convertedObject);
                        if (stream != null) {
                            return stream;
                        }
                    } else if (expectedClass == IntStream.class) {
                        IntStream intStream = JavetTypeUtils.toIntStream(convertedObject);
                        if (intStream != null) {
                            return intStream;
                        }
                    } else if (expectedClass == LongStream.class) {
                        LongStream longStream = JavetTypeUtils.toLongStream(convertedObject);
                        if (longStream != null) {
                            return longStream;
                        }
                    } else if (expectedClass == DoubleStream.class) {
                        DoubleStream doubleStream = JavetTypeUtils.toDoubleStream(convertedObject);
                        if (doubleStream != null) {
                            return doubleStream;
                        }
                    }
                }
            } catch (Throwable ignored) {
            }
            throw new JavetException(
                    JavetError.CallbackSignatureParameterTypeMismatch,
                    SimpleMap.of(
                            JavetError.PARAMETER_EXPECTED_PARAMETER_TYPE, expectedClass,
                            JavetError.PARAMETER_ACTUAL_PARAMETER_TYPE, convertedObject == null ? NULL : convertedObject.getClass()));
        }
        return v8Value;
    }

    /**
     * Receive callback and return the V8 value.
     *
     * @param v8Runtime            the V8 runtime
     * @param javetCallbackContext the javet callback context
     * @param thisObject           this object
     * @param args                 the args
     * @return the V8 value
     * @throws Throwable the throwable
     * @since 0.8.3
     */
    public static V8Value receiveCallback(
            V8Runtime v8Runtime,
            JavetCallbackContext javetCallbackContext,
            V8Value thisObject,
            V8Value[] args) throws Throwable {
        if (javetCallbackContext != null) {
            Object resultObject = null;
            try {
                /*
                 * Converter is the key to automatic type conversion.
                 * If the call doesn't want automatic type conversion,
                 * it's better to inject V8Runtime via @V8RuntimeSetter
                 * to the receiver so that the receiver can create reference V8Value.
                 */
                IJavetConverter converter = v8Runtime.getConverter();
                if (javetCallbackContext.getCallbackType() == JavetCallbackType.Reflection) {
                    /*
                     * Javet doesn't check whether callback method is static or not.
                     * If the callback receiver is null, that's a static method.
                     */
                    Method method = javetCallbackContext.getCallbackMethod();
                    JavetReflectionUtils.safeSetAccessible(method);
                    Object callbackReceiver = javetCallbackContext.getCallbackReceiver();
                    List<Object> values = new ArrayList<>();
                    if (javetCallbackContext.isThisObjectRequired()) {
                        values.add(thisObject);
                    }
                    if (args != null && args.length > 0) {
                        Collections.addAll(values, args);
                    }
                    if (values.isEmpty()) {
                        if (method.isVarArgs()) {
                            Class<?>[] parameterTypes = method.getParameterTypes();
                            Class<?> parameterClass = parameterTypes[parameterTypes.length - 1];
                            Object varObject = Array.newInstance(parameterClass.getComponentType(), 0);
                            resultObject = method.invoke(callbackReceiver, varObject);
                        } else {
                            resultObject = method.invoke(callbackReceiver);
                        }
                    } else {
                        final int length = values.size();
                        List<Object> objectValues = new ArrayList<>();
                        Class<?>[] parameterTypes = method.getParameterTypes();
                        if (method.isVarArgs()) {
                            for (int i = 0; i < parameterTypes.length; ++i) {
                                Class<?> parameterClass = parameterTypes[i];
                                if (parameterClass.isArray() && i == parameterTypes.length - 1) {
                                    // VarArgs is special. It requires special API to manipulate the array.
                                    Class<?> componentType = parameterClass.getComponentType();
                                    Object varObject = Array.newInstance(componentType, length - i);
                                    for (int j = i; j < length; ++j) {
                                        Array.set(varObject, j - i,
                                                convert(converter, componentType, (V8Value) values.get(j)));
                                    }
                                    objectValues.add(varObject);
                                } else {
                                    objectValues.add(convert(converter, parameterClass, (V8Value) values.get(i)));
                                }
                            }
                        } else {
                            for (int i = 0; i < parameterTypes.length; ++i) {
                                /*
                                 * Virtual varargs support.
                                 * Redundant parameters will be dropped.
                                 * Absent parameters will be filled by the default values.
                                 */
                                V8Value v8Value = i < length ? (V8Value) values.get(i) : null;
                                objectValues.add(convert(converter, parameterTypes[i], v8Value));
                            }
                        }
                        resultObject = method.invoke(callbackReceiver, objectValues.toArray());
                    }
                } else {
                    switch (javetCallbackContext.getCallbackType()) {
                        case DirectCallGetterAndNoThis:
                            IJavetDirectCallable.GetterAndNoThis<?> directCallGetterAndNoThis =
                                    javetCallbackContext.getCallbackMethod();
                            resultObject = directCallGetterAndNoThis.get();
                            break;
                        case DirectCallGetterAndThis:
                            IJavetDirectCallable.GetterAndThis<?> directCallGetterAndThis =
                                    javetCallbackContext.getCallbackMethod();
                            resultObject = directCallGetterAndThis.get(thisObject);
                            break;
                        case DirectCallSetterAndNoThis:
                            IJavetDirectCallable.SetterAndNoThis<?> directCallSetterAndNoThis =
                                    javetCallbackContext.getCallbackMethod();
                            if (args == null || args.length < 1) {
                                throw new JavetException(JavetError.CallbackSignatureParameterSizeMismatch,
                                        SimpleMap.of(
                                                JavetError.PARAMETER_METHOD_NAME,
                                                javetCallbackContext.getName(),
                                                JavetError.PARAMETER_EXPECTED_PARAMETER_SIZE, 1,
                                                JavetError.PARAMETER_ACTUAL_PARAMETER_SIZE, args == null ? 0 : args.length));
                            }
                            directCallSetterAndNoThis.set(args[0]);
                            break;
                        case DirectCallSetterAndThis:
                            IJavetDirectCallable.SetterAndThis<?> directCallSetterAndThis =
                                    javetCallbackContext.getCallbackMethod();
                            if (args == null || args.length != 1) {
                                throw new JavetException(JavetError.CallbackSignatureParameterSizeMismatch,
                                        SimpleMap.of(
                                                JavetError.PARAMETER_METHOD_NAME,
                                                javetCallbackContext.getName(),
                                                JavetError.PARAMETER_EXPECTED_PARAMETER_SIZE, 1,
                                                JavetError.PARAMETER_ACTUAL_PARAMETER_SIZE, args == null ? 0 : args.length));
                            }
                            directCallSetterAndThis.set(thisObject, args[0]);
                            break;
                        case DirectCallThisAndNoResult:
                            IJavetDirectCallable.ThisAndNoResult<?> directCallThisAndNoResult =
                                    javetCallbackContext.getCallbackMethod();
                            directCallThisAndNoResult.call(thisObject, args);
                            break;
                        case DirectCallThisAndResult:
                            IJavetDirectCallable.ThisAndResult<?> directCallThisAndResult =
                                    javetCallbackContext.getCallbackMethod();
                            resultObject = directCallThisAndResult.call(thisObject, args);
                            break;
                        case DirectCallNoThisAndNoResult:
                            IJavetDirectCallable.NoThisAndNoResult<?> directCallNoThisAndNoResult =
                                    javetCallbackContext.getCallbackMethod();
                            directCallNoThisAndNoResult.call(args);
                            break;
                        case DirectCallNoThisAndResult:
                            IJavetDirectCallable.NoThisAndResult<?> directCallNoThisAndResult =
                                    javetCallbackContext.getCallbackMethod();
                            resultObject = directCallNoThisAndResult.call(args);
                            break;
                        default:
                            throw new JavetException(JavetError.CallbackTypeNotSupported,
                                    SimpleMap.of(
                                            JavetError.PARAMETER_CALLBACK_TYPE,
                                            javetCallbackContext.getCallbackType().name()));
                    }
                }
                if (javetCallbackContext.isReturnResult()) {
                    if (!(resultObject instanceof IV8Value)) {
                        resultObject = v8Runtime.toV8Value(resultObject);
                    }
                    // The lifecycle of the result is handed over to JNI native implementation.
                    // So, close() or setWeak() must not be called.
                    return (V8Value) resultObject;
                } else {
                    JavetResourceUtils.safeClose(resultObject);
                }
            } catch (InvocationTargetException e) {
                throw e.getTargetException();
            } finally {
                // Result object must be excluded because it will be closed in JNI.
                if (javetCallbackContext.isThisObjectRequired()) {
                    if (thisObject != resultObject) {
                        JavetResourceUtils.safeClose(thisObject);
                    }
                }
                if (args != null) {
                    for (V8Value value : args) {
                        if (value != resultObject) {
                            JavetResourceUtils.safeClose(value);
                        }
                    }
                }
            }
        }
        return v8Runtime.createV8ValueUndefined();
    }
}
