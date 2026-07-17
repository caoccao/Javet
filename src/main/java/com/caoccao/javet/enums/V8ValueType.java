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

package com.caoccao.javet.enums;

import java.util.stream.Stream;

/**
 * The enum V8 value type.
 */
public enum V8ValueType {
    /** The V8 Invalid reference type. */
    Invalid(0, "Invalid"),
    /** The null V8 value type. */
    Null(1, "Null"),
    /** The undefined V8 value type. */
    Undefined(2, "Undefined"),
    /** The boolean V8 value type. */
    Boolean(3, "Boolean"),
    /** The integer V8 value type. */
    Integer(4, "Integer"),
    /** The double V8 value type. */
    Double(5, "Double"),
    /** The long V8 value type. */
    Long(6, "Long"),
    /** The big integer V8 value type. */
    BigInteger(7, "BigInteger"),
    /** The string V8 value type. */
    String(8, "String"),
    /** The zoned date time V8 value type. */
    ZonedDateTime(9, "ZonedDateTime"),
    /** The V8 Object reference type. */
    Object(10, "Object"),
    /** The V8 Error reference type. */
    Error(11, "Error"),
    /** The V8 RegExp reference type. */
    RegExp(12, "RegExp"),
    /** The V8 Promise reference type. */
    Promise(13, "Promise"),
    /** The V8 Proxy reference type. */
    Proxy(14, "Proxy"),
    /** The V8 Symbol reference type. */
    Symbol(15, "Symbol"),
    /** The V8 SymbolObject reference type. */
    SymbolObject(16, "SymbolObject"),
    /** The V8 Arguments reference type. */
    Arguments(17, "Arguments"),
    /** The V8 Map reference type. */
    Map(18, "Map"),
    /** The V8 Set reference type. */
    Set(19, "Set"),
    /** The V8 Array reference type. */
    Array(20, "Array"),
    /** The V8 Function reference type. */
    Function(21, "Function"),
    /** The V8 Iterator reference type. */
    Iterator(22, "Iterator"),
    /** The V8 WeakMap reference type. */
    WeakMap(23, "WeakMap"),
    /** The V8 WeakSet reference type. */
    WeakSet(24, "WeakSet"),
    /** The V8 Script reference type. */
    Script(29, "Script"),
    /** The V8 Module reference type. */
    Module(30, "Module"),
    /** The V8 Context reference type. */
    Context(31, "Context"),
    /** The V8 BigIntObject reference type. */
    BigIntObject(32, "BigIntObject"),
    /** The V8 BooleanObject reference type. */
    BooleanObject(33, "BooleanObject"),
    /** The V8 IntegerObject reference type. */
    IntegerObject(34, "IntegerObject"),
    /** The V8 DoubleObject reference type. */
    DoubleObject(35, "DoubleObject"),
    /** The V8 StringObject reference type. */
    StringObject(36, "StringObject"),
    /** The V8 DataView reference type. */
    DataView(39, "DataView"),
    /** The V8 ArrayBuffer reference type. */
    ArrayBuffer(40, "ArrayBuffer"),
    /** The V8 SharedArrayBuffer reference type. */
    SharedArrayBuffer(41, "SharedArrayBuffer"),
    /** The V8 Int8Array reference type. */
    Int8Array(42, "Int8Array"), // -128 to 127 	1 	8-bit two's complement signed integer 	byte 	int8_t
    /** The V8 Uint8Array reference type. */
    Uint8Array(43, "Uint8Array"), // 0 to 255 	1 	8-bit unsigned integer 	octet 	uint8_t
    /** The V8 Uint8ClampedArray reference type. */
    Uint8ClampedArray(44, "Uint8ClampedArray"), // 0 to 255 	1 	8-bit unsigned integer (clamped) 	octet 	uint8_t
    /** The V8 Int16Array reference type. */
    Int16Array(45, "Int16Array"), // -32768 to 32767 	2 	16-bit two's complement signed integer 	short 	int16_t
    /** The V8 Uint16Array reference type. */
    Uint16Array(46, "Uint16Array"), //  	0 to 65535 	2 	16-bit unsigned integer 	unsigned short 	uint16_t
    /** The V8 Int32Array reference type. */
    Int32Array(47, "Int32Array"), // -2147483648 to 2147483647 	4 	32-bit two's complement signed integer 	long 	int32_t
    /** The V8 Uint32Array reference type. */
    Uint32Array(48, "Uint32Array"), // 0 to 4294967295 	4 	32-bit unsigned integer 	unsigned long 	uint32_t
    /** The V8 Float16Array reference type. */
    Float16Array(49, "Float16Array"), // -65504 to 65504	2	N/A
    /** The V8 Float32Array reference type. */
    Float32Array(50, "Float32Array"), // -3.4e38 to 3.4e38 	4 	32-bit IEEE floating point number (7 significant digits e.g., 1.234567) 	unrestricted float 	float
    /** The V8 Float64Array reference type. */
    Float64Array(51, "Float64Array"), // -1.8e308 to 1.8e308 	8 	64-bit IEEE floating point number (16 significant digits e.g., 1.23456789012345) 	unrestricted double 	double
    /** The V8 BigInt64Array reference type. */
    BigInt64Array(52, "BigInt64Array"), // -2^63 to 2^63-1 	8 	64-bit two's complement signed integer 	bigint 	int64_t (signed long long)
    /** The V8 BigUint64Array reference type. */
    BigUint64Array(53, "BigUint64Array"); // 0 to 2^64-1 	8 	64-bit unsigned integer 	bigint 	uint64_t (unsigned long long)

    /** The total number of V8 value types. */
    private static final int LENGTH = 54;
    /** The array of V8 value types indexed by ID. */
    private static final V8ValueType[] TYPES = new V8ValueType[LENGTH];

    static {
        Stream.of(values()).forEach(v -> TYPES[v.getId()] = v);
    }

    /** The type ID. */
    private final int id;
    /** The type name. */
    private final String name;

    V8ValueType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Parses a V8 value type by ID.
     *
     * @param id the ID
     * @return the V8 value type
     */
    public static V8ValueType parse(int id) {
        V8ValueType type = id >= 0 && id < LENGTH ? TYPES[id] : null;
        return type == null ? Invalid : type;
    }

    /**
     * Gets the type ID.
     *
     * @return the ID
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the type name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }
}
