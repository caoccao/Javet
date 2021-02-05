/*
 *   Copyright (c) 2021. caoccao.com Sam Cao
 *   All rights reserved.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.caoccao.javet.utils;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.primitive.*;

import java.time.ZonedDateTime;

public final class JavetConverterUtils {
    public static final IJavetConverter<Boolean> DEFAULT_BOOLEAN_CONVERTER = new IJavetConverter<>() {
        @Override
        public Boolean toObject(V8Value v8Value) throws JavetException {
            if (v8Value != null && v8Value instanceof V8ValueBoolean) {
                return ((V8ValueBoolean) v8Value).getValue();
            }
            return null;
        }

        @Override
        public V8Value toV8Value(V8Runtime v8Runtime, Boolean obj) throws JavetException {
            return v8Runtime.decorateV8Value(obj == null ? new V8ValueNull() : new V8ValueBoolean(obj));
        }
    };

    public static final IJavetConverter<Double> DEFAULT_DOUBLE_CONVERTER = new IJavetConverter<>() {
        @Override
        public Double toObject(V8Value v8Value) throws JavetException {
            if (v8Value != null && v8Value instanceof V8ValueDouble) {
                return ((V8ValueDouble) v8Value).getValue();
            }
            return null;
        }

        @Override
        public V8Value toV8Value(V8Runtime v8Runtime, Double obj) throws JavetException {
            return v8Runtime.decorateV8Value(obj == null ? new V8ValueNull() : new V8ValueDouble(obj));
        }
    };

    public static final IJavetConverter<Integer> DEFAULT_INTEGER_CONVERTER = new IJavetConverter<>() {
        @Override
        public Integer toObject(V8Value v8Value) throws JavetException {
            if (v8Value != null && v8Value instanceof V8ValueInteger) {
                return ((V8ValueInteger) v8Value).getValue();
            }
            return null;
        }

        @Override
        public V8Value toV8Value(V8Runtime v8Runtime, Integer obj) throws JavetException {
            return v8Runtime.decorateV8Value(obj == null ? new V8ValueNull() : new V8ValueInteger(obj));
        }
    };

    public static final IJavetConverter<Long> DEFAULT_LONG_CONVERTER = new IJavetConverter<>() {
        @Override
        public Long toObject(V8Value v8Value) throws JavetException {
            if (v8Value != null && v8Value instanceof V8ValueLong) {
                return ((V8ValueLong) v8Value).getValue();
            }
            return null;
        }

        @Override
        public V8Value toV8Value(V8Runtime v8Runtime, Long obj) throws JavetException {
            return v8Runtime.decorateV8Value(obj == null ? new V8ValueNull() : new V8ValueLong(obj));
        }
    };

    public static final IJavetConverter<Object> DEFAULT_NULL_CONVERTER = new IJavetConverter<>() {
        @Override
        public Object toObject(V8Value v8Value) throws JavetException {
            return null;
        }

        @Override
        public V8Value toV8Value(V8Runtime v8Runtime, Object obj) throws JavetException {
            return v8Runtime.decorateV8Value(new V8ValueNull());
        }
    };

    public static final IJavetConverter<String> DEFAULT_STRING_CONVERTER = new IJavetConverter<>() {
        @Override
        public String toObject(V8Value v8Value) throws JavetException {
            if (v8Value != null && v8Value instanceof V8ValueString) {
                return ((V8ValueString) v8Value).getValue();
            }
            return null;
        }

        @Override
        public V8Value toV8Value(V8Runtime v8Runtime, String obj) throws JavetException {
            return v8Runtime.decorateV8Value(obj == null ? new V8ValueNull() : new V8ValueString(obj));
        }
    };

    public static final IJavetConverter<Object> DEFAULT_UNDEFINED_CONVERTER = new IJavetConverter<>() {
        @Override
        public Object toObject(V8Value v8Value) throws JavetException {
            return null;
        }

        @Override
        public V8Value toV8Value(V8Runtime v8Runtime, Object obj) throws JavetException {
            return v8Runtime.decorateV8Value(new V8ValueUndefined());
        }
    };

    public static final IJavetConverter<ZonedDateTime> DEFAULT_ZONED_DATE_TIME_CONVERTER = new IJavetConverter<>() {
        @Override
        public ZonedDateTime toObject(V8Value v8Value) throws JavetException {
            if (v8Value != null && v8Value instanceof V8ValueZonedDateTime) {
                return ((V8ValueZonedDateTime) v8Value).getValue();
            }
            return null;
        }

        @Override
        public V8Value toV8Value(V8Runtime v8Runtime, ZonedDateTime obj) throws JavetException {
            return v8Runtime.decorateV8Value(obj == null ? new V8ValueNull() : new V8ValueZonedDateTime(obj));
        }
    };

    protected IJavetConverter<Boolean> booleanConverter;
    protected IJavetConverter<Double> doubleConverter;
    protected IJavetConverter<Integer> integerConverter;
    protected IJavetConverter<Long> longConverter;
    protected IJavetConverter<Object> nullConverter;
    protected IJavetConverter<String> stringConverter;
    protected IJavetConverter<Object> undefinedConverter;
    protected IJavetConverter<ZonedDateTime> zonedDateTimeConverter;

    public JavetConverterUtils() {
        reset();
    }

    public IJavetConverter<Object> getNullConverter() {
        return nullConverter;
    }

    public void setNullConverter(IJavetConverter<Object> nullConverter) {
        this.nullConverter = nullConverter;
    }

    public IJavetConverter<Object> getUndefinedConverter() {
        return undefinedConverter;
    }

    public void setUndefinedConverter(IJavetConverter<Object> undefinedConverter) {
        this.undefinedConverter = undefinedConverter;
    }

    public IJavetConverter<Boolean> getBooleanConverter() {
        return booleanConverter;
    }

    public void setBooleanConverter(IJavetConverter<Boolean> booleanConverter) {
        this.booleanConverter = booleanConverter;
    }

    public IJavetConverter<Double> getDoubleConverter() {
        return doubleConverter;
    }

    public void setDoubleConverter(IJavetConverter<Double> doubleConverter) {
        this.doubleConverter = doubleConverter;
    }

    public IJavetConverter<Integer> getIntegerConverter() {
        return integerConverter;
    }

    public void setIntegerConverter(IJavetConverter<Integer> integerConverter) {
        this.integerConverter = integerConverter;
    }

    public IJavetConverter<Long> getLongConverter() {
        return longConverter;
    }

    public void setLongConverter(IJavetConverter<Long> longConverter) {
        this.longConverter = longConverter;
    }

    public IJavetConverter<String> getStringConverter() {
        return stringConverter;
    }

    public void setStringConverter(IJavetConverter<String> stringConverter) {
        this.stringConverter = stringConverter;
    }

    public IJavetConverter<ZonedDateTime> getZonedDateTimeConverter() {
        return zonedDateTimeConverter;
    }

    public void setZonedDateTimeConverter(IJavetConverter<ZonedDateTime> zonedDateTimeConverter) {
        this.zonedDateTimeConverter = zonedDateTimeConverter;
    }

    public void reset() {
        resetBooleanConverter();
        resetDoubleConverter();
        resetIntegerConverter();
        resetLongConverter();
        resetNullConverter();
        resetStringConverter();
        resetUndefinedConverter();
        resetZonedDateTimeConverter();
    }

    public void resetBooleanConverter() {
        booleanConverter = DEFAULT_BOOLEAN_CONVERTER;
    }

    public void resetDoubleConverter() {
        doubleConverter = DEFAULT_DOUBLE_CONVERTER;
    }

    public void resetIntegerConverter() {
        integerConverter = DEFAULT_INTEGER_CONVERTER;
    }

    public void resetLongConverter() {
        longConverter = DEFAULT_LONG_CONVERTER;
    }

    public void resetNullConverter() {
        nullConverter = DEFAULT_NULL_CONVERTER;
    }

    public void resetStringConverter() {
        stringConverter = DEFAULT_STRING_CONVERTER;
    }

    public void resetUndefinedConverter() {
        undefinedConverter = DEFAULT_UNDEFINED_CONVERTER;
    }

    public void resetZonedDateTimeConverter() {
        zonedDateTimeConverter = DEFAULT_ZONED_DATE_TIME_CONVERTER;
    }

    public Object toObject(V8Runtime v8Runtime, V8Value v8Value) throws JavetException {
        if (v8Value == null || v8Value instanceof V8ValueNull) {
            return nullConverter.toObject(v8Value);
        } else if (v8Value instanceof V8ValueBoolean) {
            return booleanConverter.toObject(v8Value);
        } else if (v8Value instanceof V8ValueDouble) {
            return doubleConverter.toObject(v8Value);
        } else if (v8Value instanceof V8ValueInteger) {
            return integerConverter.toObject(v8Value);
        } else if (v8Value instanceof V8ValueLong) {
            return longConverter.toObject(v8Value);
        } else if (v8Value instanceof V8ValueString) {
            return stringConverter.toObject(v8Value);
        } else if (v8Value instanceof V8ValueUndefined) {
            return undefinedConverter.toObject(v8Value);
        } else if (v8Value instanceof V8ValueZonedDateTime) {
            return zonedDateTimeConverter.toObject(v8Value);
        }
        return v8Value;
    }

    public V8Value toV8Value(V8Runtime v8Runtime, Object obj) throws JavetException {
        if (obj == null) {
            return nullConverter.toV8Value(v8Runtime, obj);
        } else if (obj instanceof V8Value) {
            return v8Runtime.decorateV8Value((V8Value) obj);
        } else if (obj instanceof Boolean) {
            return booleanConverter.toV8Value(v8Runtime, (Boolean) obj);
        } else if (obj instanceof Double) {
            return doubleConverter.toV8Value(v8Runtime, (Double) obj);
        } else if (obj instanceof Integer) {
            return integerConverter.toV8Value(v8Runtime, (Integer) obj);
        } else if (obj instanceof Long) {
            return longConverter.toV8Value(v8Runtime, (Long) obj);
        } else if (obj instanceof String) {
            return stringConverter.toV8Value(v8Runtime, (String) obj);
        } else if (obj instanceof ZonedDateTime) {
            return zonedDateTimeConverter.toV8Value(v8Runtime, (ZonedDateTime) obj);
        }
        return undefinedConverter.toV8Value(v8Runtime, obj);
    }

    public interface IJavetConverter<T extends Object> {
        T toObject(V8Value v8Value) throws JavetException;

        V8Value toV8Value(V8Runtime v8Runtime, T obj) throws JavetException;
    }
}
