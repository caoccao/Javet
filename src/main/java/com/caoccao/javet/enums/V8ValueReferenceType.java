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

package com.caoccao.javet.enums;

public enum V8ValueReferenceType {
    Invalid(0, "Invalid"),
    Object(1, "Object"),
    Error(2, "Error"),
    RegExp(3, "RegExp"),
    Promise(4, "Promise"),
    Proxy(5, "Proxy"),
    Symbol(6, "Symbol"),
    SymbolObject(7, "SymbolObject"),
    Arguments(8, "Arguments"),
    Map(9, "Map"),
    Set(10, "Set"),
    Array(11, "Array"),
    Function(12, "Function"),
    Iterator(13, "Iterator"),
    WeakMap(14, "WeakMap"),
    WeakSet(15, "WeakSet"),
    Script(20, "Script"),
    Module(21, "Module"),
    Context(22, "Context"),
    DataView(30, "DataView"),
    ArrayBuffer(31, "ArrayBuffer"),
    SharedArrayBuffer(32, "SharedArrayBuffer"),
    Int8Array(33, "Int8Array"), // -128 to 127 	1 	8-bit two's complement signed integer 	byte 	int8_t
    Uint8Array(34, "Uint8Array"), // 0 to 255 	1 	8-bit unsigned integer 	octet 	uint8_t
    Uint8ClampedArray(35, "Uint8ClampedArray"), // 0 to 255 	1 	8-bit unsigned integer (clamped) 	octet 	uint8_t
    Int16Array(36, "Int16Array"), // -32768 to 32767 	2 	16-bit two's complement signed integer 	short 	int16_t
    Uint16Array(37, "Uint16Array"), //  	0 to 65535 	2 	16-bit unsigned integer 	unsigned short 	uint16_t
    Int32Array(38, "Int32Array"), // -2147483648 to 2147483647 	4 	32-bit two's complement signed integer 	long 	int32_t
    Uint32Array(39, "Uint32Array"), // 0 to 4294967295 	4 	32-bit unsigned integer 	unsigned long 	uint32_t
    Float32Array(40, "Float32Array"), // 1.2×10^-38 to 3.4×10^38 	4 	32-bit IEEE floating point number (7 significant digits e.g., 1.234567) 	unrestricted float 	float
    Float64Array(41, "Float64Array"), // 5.0×10^-324 to 1.8×10^308 	8 	64-bit IEEE floating point number (16 significant digits e.g., 1.23456789012345) 	unrestricted double 	double
    BigInt64Array(42, "BigInt64Array"), // -2^63 to 2^63-1 	8 	64-bit two's complement signed integer 	bigint 	int64_t (signed long long)
    BigUint64Array(43, "BigUint64Array"); // 0 to 2^64-1 	8 	64-bit unsigned integer 	bigint 	uint64_t (unsigned long long)

    private static final V8ValueReferenceType[] ALL_TYPES = new V8ValueReferenceType[44];

    static {
        for (V8ValueReferenceType type : values()) {
            ALL_TYPES[type.getId()] = type;
        }
    }

    private final int id;
    private final String name;

    V8ValueReferenceType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static V8ValueReferenceType parse(int id) {
        return ALL_TYPES[id];
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
