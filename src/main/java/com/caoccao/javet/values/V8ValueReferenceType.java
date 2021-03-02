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

package com.caoccao.javet.values;

public final class V8ValueReferenceType {
    public static final int Invalid = 0;
    public static final int Object = 1;
    public static final int Error = 2;
    public static final int RegExp = 3;
    public static final int Promise = 4;
    public static final int Proxy = 5;
    public static final int Symbol = 6;
    public static final int Arguments = 7;
    public static final int Map = 8;
    public static final int Set = 9;
    public static final int Array = 10;
    public static final int Function = 11;
    public static final int Iterator = 12;
    public static final int WeakMap = 13;
    public static final int WeakSet = 14;
    public static final int DataView = 30;
    public static final int ArrayBuffer = 31;
    public static final int Int8Array = 32; // -128 to 127 	1 	8-bit two's complement signed integer 	byte 	int8_t
    public static final int Uint8Array = 33; // 0 to 255 	1 	8-bit unsigned integer 	octet 	uint8_t
    public static final int Uint8ClampedArray = 34; // 0 to 255 	1 	8-bit unsigned integer (clamped) 	octet 	uint8_t
    public static final int Int16Array = 35; // -32768 to 32767 	2 	16-bit two's complement signed integer 	short 	int16_t
    public static final int Uint16Array = 36; //  	0 to 65535 	2 	16-bit unsigned integer 	unsigned short 	uint16_t
    public static final int Int32Array = 37; // -2147483648 to 2147483647 	4 	32-bit two's complement signed integer 	long 	int32_t
    public static final int Uint32Array = 38; // 0 to 4294967295 	4 	32-bit unsigned integer 	unsigned long 	uint32_t
    public static final int Float32Array = 39; // 1.2×10^-38 to 3.4×10^38 	4 	32-bit IEEE floating point number (7 significant digits e.g., 1.234567) 	unrestricted float 	float
    public static final int Float64Array = 40; // 5.0×10^-324 to 1.8×10^308 	8 	64-bit IEEE floating point number (16 significant digits e.g., 1.23456789012345) 	unrestricted double 	double
    public static final int BigInt64Array = 41; // -2^63 to 2^63-1 	8 	64-bit two's complement signed integer 	bigint 	int64_t (signed long long)
    public static final int BigUint64Array = 42; // 0 to 2^64-1 	8 	64-bit unsigned integer 	bigint 	uint64_t (unsigned long long)
}
