/*
 *   Copyright (c) 2021 caoccao.com Sam Cao
 *   All rights reserved.

 *   Licensed under the Apache License, Version 2.0 (the "License"),
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at

 *   http://www.apache.org/licenses/LICENSE-2.0

 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

#pragma once

namespace Javet {
	namespace Enums {
		enum NodeScriptMode {
			String = 0,
			File = 1,
		};

		enum V8ValueReferenceType {
			Invalid = 0,
			Object = 1,
			Error = 2,
			RegExp = 3,
			Promise = 4,
			Proxy = 5,
			Symbol = 6,
			Arguments = 7,
			Map = 8,
			Set = 9,
			Array = 10,
			Function = 11,
			Iterator = 12,
			WeakMap = 13,
			WeakSet = 14,
			Script = 20,
			Module = 21,
			DataView = 30,
			ArrayBuffer = 31,
			Int8Array = 32, // -128 to 127 	1 	8-bit two's complement signed integer 	byte 	int8_t
			Uint8Array = 33, // 0 to 255 	1 	8-bit unsigned integer 	octet 	uint8_t
			Uint8ClampedArray = 34, // 0 to 255 	1 	8-bit unsigned integer (clamped) 	octet 	uint8_t
			Int16Array = 35, // -32768 to 32767 	2 	16-bit two's complement signed integer 	short 	int16_t
			Uint16Array = 36, //  	0 to 65535 	2 	16-bit unsigned integer 	unsigned short 	uint16_t
			Int32Array = 37, // -2147483648 to 2147483647 	4 	32-bit two's complement signed integer 	long 	int32_t
			Uint32Array = 38, // 0 to 4294967295 	4 	32-bit unsigned integer 	unsigned long 	uint32_t
			Float32Array = 39, // 1.2��10^-38 to 3.4��10^38 	4 	32-bit IEEE floating point number (7 significant digits e.g., 1.234567) 	unrestricted float 	float
			Float64Array = 40, // 5.0��10^-324 to 1.8��10^308 	8 	64-bit IEEE floating point number (16 significant digits e.g., 1.23456789012345) 	unrestricted double 	double
			BigInt64Array = 41, // -2^63 to 2^63-1 	8 	64-bit two's complement signed integer 	bigint 	int64_t (signed long long)
			BigUint64Array = 42, // 0 to 2^64-1 	8 	64-bit unsigned integer 	bigint 	uint64_t (unsigned long long)
		};
	}
}