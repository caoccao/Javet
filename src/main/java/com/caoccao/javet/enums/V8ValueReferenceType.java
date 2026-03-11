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
 * The enum V8 value reference type.
 */
public enum V8ValueReferenceType {
    /** The V8 Invalid reference type. */
    Invalid(0, "Invalid"),
    /** The V8 Object reference type. */
    Object(1, "Object"),
    /** The V8 Error reference type. */
    Error(2, "Error"),
    /** The V8 RegExp reference type. */
    RegExp(3, "RegExp"),
    /** The V8 Promise reference type. */
    Promise(4, "Promise"),
    /** The V8 Proxy reference type. */
    Proxy(5, "Proxy"),
    /** The V8 Symbol reference type. */
    Symbol(6, "Symbol"),
    /** The V8 SymbolObject reference type. */
    SymbolObject(7, "SymbolObject"),
    /** The V8 Arguments reference type. */
    Arguments(8, "Arguments"),
    /** The V8 Map reference type. */
    Map(9, "Map"),
    /** The V8 Set reference type. */
    Set(10, "Set"),
    /** The V8 Array reference type. */
    Array(11, "Array"),
    /** The V8 Function reference type. */
    Function(12, "Function"),
    /** The V8 Iterator reference type. */
    Iterator(13, "Iterator"),
    /** The V8 WeakMap reference type. */
    WeakMap(14, "WeakMap"),
    /** The V8 WeakSet reference type. */
    WeakSet(15, "WeakSet"),
    /** The V8 Script reference type. */
    Script(20, "Script"),
    /** The V8 Module reference type. */
    Module(21, "Module"),
    /** The V8 Context reference type. */
    Context(22, "Context"),
    /** The V8 BigIntObject reference type. */
    BigIntObject(23, "BigIntObject"),
    /** The V8 BooleanObject reference type. */
    BooleanObject(24, "BooleanObject"),
    /** The V8 IntegerObject reference type. */
    IntegerObject(25, "IntegerObject"),
    /** The V8 DoubleObject reference type. */
    DoubleObject(26, "DoubleObject"),
    /** The V8 StringObject reference type. */
    StringObject(27, "StringObject"),
    /** The V8 DataView reference type. */
    DataView(30, "DataView"),
    /** The V8 ArrayBuffer reference type. */
    ArrayBuffer(31, "ArrayBuffer"),
    /** The V8 SharedArrayBuffer reference type. */
    SharedArrayBuffer(32, "SharedArrayBuffer"),
    /** The V8 Int8Array reference type. */
    Int8Array(33, "Int8Array"), // -128 to 127 	1 	8-bit two's complement signed integer 	byte 	int8_t
    /** The V8 Uint8Array reference type. */
    Uint8Array(34, "Uint8Array"), // 0 to 255 	1 	8-bit unsigned integer 	octet 	uint8_t
    /** The V8 Uint8ClampedArray reference type. */
    Uint8ClampedArray(35, "Uint8ClampedArray"), // 0 to 255 	1 	8-bit unsigned integer (clamped) 	octet 	uint8_t
    /** The V8 Int16Array reference type. */
    Int16Array(36, "Int16Array"), // -32768 to 32767 	2 	16-bit two's complement signed integer 	short 	int16_t
    /** The V8 Uint16Array reference type. */
    Uint16Array(37, "Uint16Array"), //  	0 to 65535 	2 	16-bit unsigned integer 	unsigned short 	uint16_t
    /** The V8 Int32Array reference type. */
    Int32Array(38, "Int32Array"), // -2147483648 to 2147483647 	4 	32-bit two's complement signed integer 	long 	int32_t
    /** The V8 Uint32Array reference type. */
    Uint32Array(39, "Uint32Array"), // 0 to 4294967295 	4 	32-bit unsigned integer 	unsigned long 	uint32_t
    /** The V8 Float16Array reference type. */
    Float16Array(40, "Float16Array"), // -65504 to 65504	2	N/A
    /** The V8 Float32Array reference type. */
    Float32Array(41, "Float32Array"), // -3.4e38 to 3.4e38 	4 	32-bit IEEE floating point number (7 significant digits e.g., 1.234567) 	unrestricted float 	float
    /** The V8 Float64Array reference type. */
    Float64Array(42, "Float64Array"), // -1.8e308 to 1.8e308 	8 	64-bit IEEE floating point number (16 significant digits e.g., 1.23456789012345) 	unrestricted double 	double
    /** The V8 BigInt64Array reference type. */
    BigInt64Array(43, "BigInt64Array"), // -2^63 to 2^63-1 	8 	64-bit two's complement signed integer 	bigint 	int64_t (signed long long)
    /** The V8 BigUint64Array reference type. */
    BigUint64Array(44, "BigUint64Array"); // 0 to 2^64-1 	8 	64-bit unsigned integer 	bigint 	uint64_t (unsigned long long)

    /** The total number of reference types. */
    private static final int LENGTH = 45;
    /** The array of reference types indexed by ID. */
    private static final V8ValueReferenceType[] TYPES = new V8ValueReferenceType[LENGTH];

    static {
        Stream.of(values()).forEach(v -> TYPES[v.getId()] = v);
    }

    /** The type ID. */
    private final int id;
    /** The type name. */
    private final String name;

    V8ValueReferenceType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Parse V8 value reference type by ID.
     *
     * @param id the id
     * @return the V8 value reference type
     */
    public static V8ValueReferenceType parse(int id) {
        return id >= 0 && id < LENGTH ? TYPES[id] : Invalid;
    }

    /**
     * Gets the type ID.
     *
     * @return the id
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
