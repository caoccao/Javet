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

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

/**
 * The type Javet type utils.
 *
 * @since 0.9.13
 */
public final class JavetTypeUtils {
    /**
     * To approximate primitive object.
     *
     * @param expectedClass the expected class
     * @param object        the object
     * @return the object
     * @since 1.0.1
     */
    public static Object toApproximatePrimitive(Class<?> expectedClass, Object object) {
        if (expectedClass == int.class || expectedClass == Integer.class) {
            if (object instanceof Integer) {
                return object;
            } else if (object instanceof Long) {
                return ((Long) object).intValue();
            } else if (object instanceof Short) {
                return ((Short) object).intValue();
            } else if (object instanceof Byte) {
                return ((Byte) object).intValue();
            }
        }
        if (expectedClass == boolean.class || expectedClass == Boolean.class) {
            if (object instanceof Boolean) {
                return object;
            }
        }
        if (expectedClass == double.class || expectedClass == Double.class) {
            if (object instanceof Double) {
                return object;
            } else if (object instanceof Float) {
                return ((Float) object).doubleValue();
            } else if (object instanceof Integer) {
                return ((Integer) object).doubleValue();
            } else if (object instanceof Long) {
                return ((Long) object).doubleValue();
            } else if (object instanceof Short) {
                return ((Short) object).doubleValue();
            } else if (object instanceof Byte) {
                return ((Byte) object).doubleValue();
            }
        }
        if (expectedClass == float.class || expectedClass == Float.class) {
            if (object instanceof Float) {
                return object;
            } else if (object instanceof Double) {
                return ((Double) object).floatValue();
            } else if (object instanceof Integer) {
                return ((Integer) object).floatValue();
            } else if (object instanceof Long) {
                return ((Long) object).floatValue();
            } else if (object instanceof Short) {
                return ((Short) object).floatValue();
            } else if (object instanceof Byte) {
                return ((Byte) object).floatValue();
            }
        }
        if (expectedClass == long.class || expectedClass == Long.class) {
            if (object instanceof Long) {
                return object;
            } else if (object instanceof Integer) {
                return ((Integer) object).longValue();
            } else if (object instanceof Short) {
                return ((Short) object).longValue();
            } else if (object instanceof Byte) {
                return ((Byte) object).longValue();
            }
        }
        if (expectedClass == short.class || expectedClass == Short.class) {
            if (object instanceof Short) {
                return object;
            } else if (object instanceof Integer) {
                return ((Integer) object).shortValue();
            } else if (object instanceof Long) {
                return ((Long) object).shortValue();
            } else if (object instanceof Byte) {
                return ((Byte) object).shortValue();
            }
        }
        if (expectedClass == byte.class || expectedClass == Byte.class) {
            if (object instanceof Byte) {
                return object;
            } else if (object instanceof Integer) {
                return ((Integer) object).byteValue();
            } else if (object instanceof Long) {
                return ((Long) object).byteValue();
            } else if (object instanceof Short) {
                return ((Short) object).byteValue();
            }
        }
        if (expectedClass == char.class || expectedClass == Character.class) {
            if (object instanceof Character) {
                return object;
            } else if (object instanceof String) {
                String objectString = (String) object;
                if (objectString.length() > 0) {
                    return objectString.charAt(0);
                }
            }
        }
        return null;
    }

    /**
     * Convert object to double stream.
     *
     * @param object the object
     * @return the double stream
     * @since 0.9.13
     */
    public static DoubleStream toDoubleStream(Object object) {
        if (object instanceof double[]) {
            return DoubleStream.of((double[]) object);
        } else if (object instanceof Collection) {
            Collection<?> collection = (Collection<?>) object;
            if (collection.stream().allMatch(i -> i instanceof Double)) {
                return collection.stream().mapToDouble(i -> (Double) i);
            }
        }
        return null;
    }

    /**
     * To exact primitive object.
     *
     * @param expectedClass the expected class
     * @param object        the object
     * @return the object
     * @since 0.9.13
     */
    public static Object toExactPrimitive(Class<?> expectedClass, Object object) {
        if (expectedClass == int.class && object instanceof Integer) {
            return ((Integer) object).intValue();
        }
        if (expectedClass == boolean.class && object instanceof Boolean) {
            return ((Boolean) object).booleanValue();
        }
        if (expectedClass == double.class && object instanceof Double) {
            return ((Double) object).doubleValue();
        }
        if (expectedClass == float.class && object instanceof Float) {
            return ((Float) object).floatValue();
        }
        if (expectedClass == long.class && object instanceof Long) {
            return ((Long) object).longValue();
        }
        if (expectedClass == short.class && object instanceof Short) {
            return ((Short) object).shortValue();
        }
        if (expectedClass == byte.class && object instanceof Byte) {
            return ((Byte) object).byteValue();
        }
        if (expectedClass == char.class && object instanceof Character) {
            return ((Character) object).charValue();
        }
        return null;
    }

    /**
     * Convert object to int stream.
     *
     * @param object the object
     * @return the int stream
     * @since 0.9.13
     */
    public static IntStream toIntStream(Object object) {
        if (object instanceof int[]) {
            return IntStream.of((int[]) object);
        } else if (object instanceof Collection) {
            Collection<?> collection = (Collection<?>) object;
            if (collection.stream().allMatch(i -> i instanceof Integer)) {
                return collection.stream().mapToInt(i -> (Integer) i);
            }
        }
        return null;
    }

    /**
     * Convert object to long stream.
     *
     * @param object the object
     * @return the long stream
     * @since 0.9.13
     */
    public static LongStream toLongStream(Object object) {
        if (object instanceof long[]) {
            return LongStream.of((long[]) object);
        } else if (object instanceof Collection) {
            Collection<?> collection = (Collection<?>) object;
            if (collection.stream().allMatch(i -> i instanceof Long)) {
                return collection.stream().mapToLong(i -> (Long) i);
            }
        }
        return null;
    }

    /**
     * Convert object to stream.
     *
     * @param object the object
     * @return the stream
     * @since 0.9.13
     */
    public static Stream<?> toStream(Object object) {
        if (object.getClass().isArray()) {
            if (object instanceof int[]) {
                return Arrays.stream((int[]) object).boxed();
            } else if (object instanceof long[]) {
                return Arrays.stream((long[]) object).boxed();
            } else if (object instanceof double[]) {
                return Arrays.stream((double[]) object).boxed();
            } else if (object instanceof boolean[]) {
                boolean[] booleanArray = (boolean[]) object;
                Object[] objects = new Object[booleanArray.length];
                for (int i = 0; i < booleanArray.length; ++i) {
                    objects[i] = booleanArray[i];
                }
                return Stream.of(objects);
            } else if (object instanceof float[]) {
                float[] floatArray = (float[]) object;
                Object[] objects = new Object[floatArray.length];
                for (int i = 0; i < floatArray.length; ++i) {
                    objects[i] = floatArray[i];
                }
                return Stream.of(objects);
            } else if (object instanceof byte[]) {
                byte[] byteArray = (byte[]) object;
                Object[] objects = new Object[byteArray.length];
                for (int i = 0; i < byteArray.length; ++i) {
                    objects[i] = byteArray[i];
                }
                return Stream.of(objects);
            } else if (object instanceof short[]) {
                short[] shortArray = (short[]) object;
                Object[] objects = new Object[shortArray.length];
                for (int i = 0; i < shortArray.length; ++i) {
                    objects[i] = shortArray[i];
                }
                return Stream.of(objects);
            } else if (object instanceof char[]) {
                char[] charArray = (char[]) object;
                Object[] objects = new Object[charArray.length];
                for (int i = 0; i < charArray.length; ++i) {
                    objects[i] = charArray[i];
                }
                return Stream.of(objects);
            } else {
                return Stream.of((Object[]) object);
            }
        } else if (object instanceof Collection) {
            return ((Collection<?>) object).stream();
        }
        return null;
    }
}
