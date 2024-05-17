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

package com.caoccao.javet.values.primitive;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.utils.StringUtils;
import com.caoccao.javet.values.IV8ValuePrimitiveValue;
import com.caoccao.javet.values.reference.V8ValueDoubleObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The type V8 value double.
 *
 * @since 0.7.0
 */
@SuppressWarnings("unchecked")
public final class V8ValueDouble
        extends V8ValueNumber<Double>
        implements IV8ValuePrimitiveValue<V8ValueDoubleObject> {
    public static final String INFINITY = "Infinity";
    private static final int MAX_EXPONENT = 308;
    private static final Pattern PATTERN_DECIMAL_ZEROS =
            Pattern.compile("^([\\+\\-]?)(\\d+)\\.0*$", Pattern.CASE_INSENSITIVE);
    private static final Pattern PATTERN_SCIENTIFIC_NOTATION_WITHOUT_FRACTION =
            Pattern.compile("^([\\+\\-]?)(\\d+)e([\\+\\-]?)(\\d+)$", Pattern.CASE_INSENSITIVE);
    private static final Pattern PATTERN_SCIENTIFIC_NOTATION_WITH_FRACTION =
            Pattern.compile("^([\\+\\-]?)(\\d+)\\.(\\d*)e([\\+\\-]?)(\\d+)$", Pattern.CASE_INSENSITIVE);
    private String cachedToString;

    /**
     * Instantiates a new V8 value double.
     *
     * @param v8Runtime the V8 runtime
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    public V8ValueDouble(V8Runtime v8Runtime) throws JavetException {
        this(v8Runtime, 0D);
    }

    /**
     * Instantiates a new V8 value double.
     *
     * @param v8Runtime the V8 runtime
     * @param value     the value
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    public V8ValueDouble(V8Runtime v8Runtime, double value) throws JavetException {
        super(v8Runtime, value);
        cachedToString = null;
    }

    private static String normalize(String raw) {
        Matcher matcher = PATTERN_SCIENTIFIC_NOTATION_WITH_FRACTION.matcher(raw);
        if (matcher.matches()) {
            String sign = "-".equals(matcher.group(1)) ? "-" : "";
            String exponentSign = StringUtils.isEmpty(matcher.group(4)) ? "+" : matcher.group(4);
            String integer = matcher.group(2);
            String fraction = matcher.group(3);
            int additionalExponent = 0;
            while (fraction.endsWith("0")) {
                fraction = fraction.substring(0, fraction.length() - 1);
            }
            if (integer.length() > 1) {
                additionalExponent += integer.length() - 1;
                fraction = integer.substring(1) + fraction;
                integer = integer.substring(0, 1);
            }
            if (StringUtils.isNotEmpty(fraction)) {
                fraction = "." + fraction;
            }
            long exponent = Long.parseLong(matcher.group(5)) + additionalExponent;
            if (exponent > MAX_EXPONENT) {
                return sign + INFINITY;
            }
            return sign + integer + fraction + "e" + exponentSign + exponent;
        }
        matcher = PATTERN_SCIENTIFIC_NOTATION_WITHOUT_FRACTION.matcher(raw);
        if (matcher.matches()) {
            String sign = "-".equals(matcher.group(1)) ? "-" : "";
            String exponentSign = StringUtils.isEmpty(matcher.group(3)) ? "+" : matcher.group(3);
            String integer = matcher.group(2);
            String fraction = "";
            int additionalExponent = 0;
            while (integer.endsWith("0")) {
                ++additionalExponent;
                integer = integer.substring(0, integer.length() - 1);
            }
            if (integer.length() > 1) {
                additionalExponent += integer.length() - 1;
                fraction = "." + integer.substring(1);
                integer = integer.substring(0, 1);
            }
            long exponent = Long.parseLong(matcher.group(4)) + additionalExponent;
            if (exponent > MAX_EXPONENT) {
                return sign + INFINITY;
            }
            return sign + integer + fraction + "e" + exponentSign + exponent;
        }
        matcher = PATTERN_DECIMAL_ZEROS.matcher(raw);
        if (matcher.matches()) {
            String sign = "-".equals(matcher.group(1)) ? "-" : "";
            return sign + matcher.group(2);
        }
        return raw;
    }

    @Override
    public boolean asBoolean() {
        // 0, -0, and NaN turn into false; other numbers turn into true.
        return value != 0D && !Double.isNaN(value) && Double.isFinite(value);
    }

    @Override
    public double asDouble() throws JavetException {
        return value;
    }

    @Override
    public int asInt() {
        return value.intValue();
    }

    @Override
    public long asLong() throws JavetException {
        return value.longValue();
    }

    /**
     * Is finite.
     *
     * @return true : finite, false: infinite
     * @since 0.7.0
     */
    public boolean isFinite() {
        return Double.isFinite(value);
    }

    /**
     * Is infinite.
     *
     * @return true : infinite, false: finite
     * @since 0.7.0
     */
    public boolean isInfinite() {
        return Double.isInfinite(value);
    }

    /**
     * Is NaN.
     *
     * @return true : NaN, false: not NaN
     * @since 0.7.0
     */
    public boolean isNaN() {
        return Double.isNaN(value);
    }

    @Override
    public V8ValueDouble toClone(boolean referenceCopy) throws JavetException {
        return this;
    }

    @Override
    public V8ValueDoubleObject toObject() throws JavetException {
        return checkV8Runtime().createV8ValueDoubleObject(value);
    }

    /**
     * To primitive double.
     *
     * @return the double
     * @since 0.7.0
     */
    public double toPrimitive() {
        return value;
    }

    @Override
    public String toString() {
        if (cachedToString == null) {
            cachedToString = normalize(value.toString());
        }
        return cachedToString;
    }
}
